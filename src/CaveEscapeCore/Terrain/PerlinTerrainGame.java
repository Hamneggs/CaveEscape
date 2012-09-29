package CaveEscapeCore.Terrain;

import CaveEscapeCore.Constants.Const;
import CaveEscapeCore.CoreGameplay.GameplayMode;
import CaveEscapeCore.Pickups.PickupBag;
import CaveEscapeCore.Player.Player;
import android.opengl.GLU;
import android.view.HapticFeedbackConstants;
import android.view.View;

import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The representation of the terrain in caveEscape. It is a set of procedurally generated height values.
 * The terrain is incremented by translating the mesh, and once the distance it has covered is greater than
 * the distance between two height sample points, we reset the position of the terrain, move the height values through
 * their structure, dropping off one row, and replace the row at the other end of the structure with new height values.
 * This operation is threaded.
 *
 * At this stage in gameplay, the OpenGL paradigm is radically different from the menus at the start of the game.
 * This means ALL DRAWN GAMEPLAY OBJECTS MUST USE THE OPENGL PARADIGM OF THIS TERRAIN.
 *
 * Furthermore, the speed of the terrain should be determined by the Ship the player is using.
 * @see CaveEscapeCore.Player.Ship
 */
public class PerlinTerrainGame {

    /**
     * The x, y, and z coordinates of the front left corner of the mesh. This set of values are translated when moving
     * The terrain.
     */
    private float x, y, z;

    /**
     * The original x, y, and z coordinates. These should not be changed after construction.
     */
    private float ox,oy,oz;

    /**
     * Stores the height values of the terrain.
     */
    private float[][] heightVals;

    /**
     * Is the terrain inverted, or rather should the height values subtract
     * from the base height?
     */
    private boolean inverted;

    /**
     * The physical depth and width of the terrain.
     */
    private float tWidth, tDepth;

    /**
     * The physical x and z distance between each value in heightVals, as well as the hypotenusal distance.
     */
    private double unitDepth, unitWidth, unitH;

    /**
     * The density of features. This controls the resolution with which we sample the noise function.
     */
    private float dScale;

    /**
     * The height scale is the value by which we multiply the noise value. The current hScale is the one pow'd by
     * sScale.
     */
    private float hScale, chScale;

    /**
     * The slope scale. We raise each of the height values to this power, which should be an even value. This makes
     * tall features taller and short features shorter.
     */
    private float sScale;

    /**
     * The noise function takes three coordinates, but we only have two to give it. Hence we relegate the third to a
     * seed value.
     */
    private float seed;

    /**
     * The speed at which the terrain moves.
     */
    private float speed;

    /**
     * The resolution of the heightmap. These values are redundant to the size of heightVals[][].
     */
    private int resX, resY;

    /**
     * When we increment the terrain, we can't feed the noise function the same values over and over again, so we need
     * variable to represent how many times we've incremented the terrain.
     */
    private int increments;

    /**
     * The FloatBuffers for the vertices. We have multiple so we can edit one while displaying the other, eliminating
     * stutter.
     */
    FloatBuffer vertsA, vertsB, current;

    /**
     * A byte value for swifter testing of which vertex buffer is the current one.
     */
    byte curVerBuffer;

    /**
     * The FloatBuffer for the vertex colors.
     */
    FloatBuffer color;

    /**
     * We create a Timer object to handle the update thread. We schedule all tasks immediately so the timing
     * functionality of the Timer is moot.
     */
    Timer updater;

    /**
     * We store the Player in the Terrain so that we can better control the various OpenGL transformation
     * matrices.
     */
    Player player;

    /**
     * We need the player in order to give it it's transformations, and to
     * listen to its own, but do we really need to DRAW it twice?
     */
    boolean drawPlayer;
    private final PickupBag bag;
    private final boolean drawBag;

    /**
     * A view that provides haptic feedback during collision.
     */
    private View hapticFeedbackView;

    /**
     * Constructs the Terrain by initializing all of the geometry buffers, the OpenGL state, and
     * pre-creating height values.
     * @param x The x-location of the terrain.
     * @param y The y-location of the terrain.
     * @param z The z-location of the terrain.
     * @param tWidth The terrain width.
     * @param tDepth The terrain depth.
     * @param resX  The X resolution of the heightfield.
     * @param resY  The Y resolution of the heightfield.
     * @param dScale The intended density (multiplicative) scale.
     * @param hScale The intended height (constant) scale.
     * @param sScale The intended slope (exponential) scale.
     * @param seed Since we will only have two values to feed
     *             into the noise function, we will use the
     *             third as a seed.
     * @param inverted If the terrain is inverted, the height values are subtracted from the y location.
     * @param speed The speed at which the terrain moves.
     * @param player The Player instance that the user is using.
     */
    public PerlinTerrainGame(
            float x,
            float y,
            float z,
            float tWidth,
            float tDepth,
            int resX,
            int resY,
            float dScale,
            float hScale,
            float sScale,
            float seed,
            boolean inverted,
            float speed,
            Player player,
            boolean drawPlayer,
            PickupBag bag,
            boolean drawBag

    ){

        this.x        = x       ;
        this.y        = y       ;
        this.z        = z       ;
        this.ox       = x       ;
        this.oy       = y       ;
        this.oz       = z       ;
        this.tWidth   = tWidth  ;
        this.tDepth   = tDepth  ;
        this.resX     = resX    ;
        this.resY     = resY    ;
        this.dScale   = dScale  ;
        this.hScale   = hScale  ;
        this.sScale   = sScale  ;
        this.seed     = seed    ;
        this.inverted = inverted;
        this.speed    = speed   ;
        this.player   = player  ;
        this.drawPlayer = drawPlayer;
        this.bag = bag;
        this.drawBag = drawBag;

        //Create the unitDepth by dividing the dimensions of the terrain by its resolution.
        unitWidth = tWidth/(float)resX;
        unitDepth = tDepth/(float)resY;

        //Initialize the height-field array.
        initHeightfield();

        //Initialize the geometry buffers.
        initBuffers();

        //Set current to the first vertex buffer.


        //Create the Timer object. We name the thread it creates "Updater" and tell it to be a daemon thread.
        //This means that the thread will exit when the main execution thread ends.
        updater = new Timer("Updater", true);

        //Initialize the increments.
        increments = 0;
    }

    /**
     * This method initializes the heightfield. Remember that we need it to be guaranteed that the sides of the terrain are closed,
     * and hence the heightfield must be U-shaped.
     */
    private void initHeightfield(){
        heightVals = new float[resX][resY];
        for(int x = 0; x < resX; x++){
            for(int y = 0; y < resY; y++){
                heightVals[x][y] = (float) ImprovedNoise.noise((unitWidth * x) / tWidth * dScale,
                        (unitDepth * y) / tDepth * dScale,
                        seed);
                heightVals[x][y] = (float)Math.pow(heightVals[x][y], sScale) * hScale;
                if(inverted){
                    heightVals[x][y] *= -1f;
                }
                if(Const.ptgScaleInitialTerrain){
                    heightVals[x][y] *= ((float)(y)/(float)resY);
                }
                if(x == 0 || x == resX-1){
                    if(inverted)heightVals[x][y] = y - hScale;
                    else heightVals[x][y] = y + hScale;
                }

            }
        }
    }

    /**
     * This method initializes the various FloatBuffers used for the geometry of the Terrain. It does not initialize
     * the "current" FloatBuffer since we cast either of the other vertex buffers to it every update.
     */
    private void initBuffers(){

        //Nodes deep * nodes across * verts per node * floats per vert * bytes per float.

        //Create the first of the twin vertex buffers.
        ByteBuffer vbba = ByteBuffer.allocateDirect( 4*3 * 2*(resX-1)*(resY) ); //Allocate the appropriate num of bytes.
        vbba.order(ByteOrder.nativeOrder());  //Set the byte order to that currently employed by the phone.
        vertsA = vbba.asFloatBuffer();        //Cast it out to the first vertex buffer.

        //Do the same for the second of the twin vertex buffers.
        ByteBuffer vbbb = ByteBuffer.allocateDirect( 4*3 * 2*(resX-1)*(resY) );
        vbbb.order(ByteOrder.nativeOrder());
        vertsB = vbbb.asFloatBuffer();

        //We also need to create the color buffer.
        ByteBuffer cbb = ByteBuffer.allocateDirect( 4*4 * 2*(resX-1)*(resY) );
        cbb.order(ByteOrder.nativeOrder());
        color = cbb.asFloatBuffer();

        //We now need to pack the vertices.
        packVerts(vertsA);
        packVerts(vertsB);
        packColors(1f, 1f, 1f, 1f);
        current = vertsA;
    }

    /**
     * Packs the values of the height array into the vertex float buffers.
     * @param verts The vertex buffer to pack.
     */
    private void packVerts(FloatBuffer verts){

        //Create an array to store the x, y, and z coordinates of each vertex, two per iteration.
        float[] vals = new float[6];

        for(int x = 0; x < resX-1; x++){
            for(int y = 0; y < resY; y++){

                //Vertex 0:
                vals[0]  = (float)( this.x + unitWidth*(x-(.5*resX) ));
                vals[1]  = this.y + heightVals[x][y];
                vals[2]  = (float)(this.z - unitDepth*(y ) );
                //Vertex 2:
                vals[3]  = (float)(this.x + unitWidth*(x+1-(.5*resX)));
                vals[4]  = this.y + heightVals[(x+1)][y];
                vals[5]  = (float)(this.z - unitDepth*(y ));


                verts.put(vals);
            }
        }

        //Reset the position of the buffers to zero.
        verts.position(0);
    }

    /**
     * Packs the given color values into the vertex color buffer. Note that the colors should be normalized to [0-1].
     *
     * @param r Intended red value.
     * @param g Intended green value.
     * @param b Intended blue value.
     * @param a Intended alpha value.
     */
    private void packColors(float r, float g, float b, float a){

        //Create an array to pack into the vertex color buffer.
        float[] colors = { r, g, b, a, r, g, b, a};

        for(int x = 0; x < resX-1; x++){
            for(int y = 0; y < resY; y++){

                //Now pack the values over an over again.
                color.put(colors);
            }
        }

        //We again need to reset the buffer position to 0.
        color.position(0);
    }

    public void setHapticFeedbackView(View hapticFeedbackView){
        this.hapticFeedbackView = hapticFeedbackView;
    }

    /**
     * Tests for collision between the player's ship and the terrain. Possibly buggy.
     *
     * @param player The player object that might by colliding with the terrain.
     */
    public void testCollision(Player player){

        //We don't want the player to go outside of where the
        //terrain actually is in geometry space, if the player gets to the point
        //even where the edge of the terrain can be seen, we disallow further
        //movement.
        if(Const.ptgDoHorizBlanketColl){
            if(player.getX() < x-(tWidth*Const.ptgHorizFraction)
                    || player.getX() > x+(tWidth*Const.ptgHorizFraction)){
                player.setX(player.getX()-(Const.ptgBlanketJitterFactor*player.getXVelocity()));
                if(hapticFeedbackView != null && Const.performHaptics)hapticFeedbackView.performHapticFeedback(HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING, HapticFeedbackConstants.LONG_PRESS);
            }
        }

        //Furthermore, we also make sure the player doesn't go above (or below)
        //the Y location of the terrain.
        if(Const.ptgDoVertBlanketColl){
            if(inverted){
                if(player.getY() > y*Const.ptgVertFraction){
                    player.setY(player.getY()-(Const.ptgBlanketJitterFactor*player.getShip().getStrafeSpeed()));
                    if(hapticFeedbackView != null && Const.performHaptics)hapticFeedbackView.performHapticFeedback(HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING, HapticFeedbackConstants.LONG_PRESS);
                }
            }
            else{
                if(player.getY() < y*Const.ptgVertFraction){
                    player.setY(player.getY()+(Const.ptgBlanketJitterFactor*player.getShip().getStrafeSpeed()));
                    if(hapticFeedbackView != null && Const.performHaptics)hapticFeedbackView.performHapticFeedback(HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING, HapticFeedbackConstants.LONG_PRESS);
                }
            }
        }

        if(Const.ptgDoFineColl){

            //First we get the noseLocation
            float[] nose = player.getShipLocation();

            //Secondly we translate the location of the Player's nose to be relevant to
            //the terrain.
            nose[0]+=x;
            //nose[2]+=z;

            //Now we must force the Z value to to be positive.
            nose[2] = Math.abs(nose[2]);

            //Now we divide the x and z coordinates by the unit size of each node to
            //Get at which index we are at.
            nose[0]/=unitWidth;
            nose[2]/=unitDepth;

            //Since we center the terrain around its X location, we must
            //shift the nose location accordingly.
            nose[0]+=(.5*resX);

            //The technique above will still give us a float value, and thus if the
            //ship is between two nodes, the decimal is the fractional distance between
            //the nodes. We query this fraction by doing the following:
            float lerpDistanceX = nose[0] - (int)nose[0];
            float lerpDistanceZ = nose[2] - (int)nose[2];

            //We can now lerp these values to get the exact height of the terrain
            //where the ship is.
            float height = 0;
            if(  nose[0] < resX-1 && nose[0] > 1){   //We need to know if the ship is at a place with
               if(  nose[2] < resY-1 && nose[2] > 1){ //no adjacent node.
                   if(inverted){
                       height = y+(float)(
                               lerp(lerpDistanceX,
                                       heightVals[ (int)nose[0]  ] [ (int)nose[2] ],
                                       heightVals[ (int)nose[0]+1] [ (int)nose[2] ]) +
                               lerp(lerpDistanceZ,
                                       heightVals[ (int)nose[0] ] [ (int)nose[2]  ],
                                       heightVals[ (int)nose[0] ] [ (int)nose[2]+1]  )) /2f;
                   }
                   else{
                        height = y+(float)(
                               lerp(lerpDistanceX,
                                       heightVals[ (int)nose[0]  ] [ (int)nose[2] ],
                                       heightVals[ (int)nose[0]+1] [ (int)nose[2] ]) +
                               lerp(lerpDistanceZ,
                                       heightVals[ (int)nose[0] ] [ (int)nose[2]  ],
                                       heightVals[ (int)nose[0] ] [ (int)nose[2]+1]  )) /2f;
                   }
               }
            }

            //Now we can compare the height value we got to the actual height value of the ship.
            //If the ship is within the terrain, we do some stuff to it.
            if(inverted){
                if(nose[1] >= height *Const.ptgFineCollGraceFactor){
                    //First we change the health value of the Player.
                    player.changeHealth(-Const.ptgHealthPerColl);
                    //Next, we perform a vibration for effect.

                    //Next, if terrain damage is enabled, we deform the terrain.
                    if(Const.ptgDoTerrainDmg){
                        heightVals[(int)nose[0] ] [ (int)nose[2] ] = player.getY()-y;
                        heightVals[(int)nose[0]+1 ] [ (int)nose[2] ] = ((player.getY()-y)+heightVals[(int)nose[0]+1 ] [ (int)nose[2] ])/2f;
                        heightVals[(int)nose[0]-1 ] [ (int)nose[2] ] =((player.getY()-y)+heightVals[(int)nose[0]+1 ] [ (int)nose[2] ])/2f;
                    }
                    //Finally, we move the player back out of the terrain.
                    player.setY(height);
                    if(hapticFeedbackView != null && Const.performHaptics)hapticFeedbackView.performHapticFeedback(HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING, HapticFeedbackConstants.LONG_PRESS);
                    //TODO: Define what happens to the player when they hit the terrain.
                }
                else if(Const.ptgDoNearMissTest){
                    if(nose[1]>= height - Const.ptgNearMissDist){
                        player.changeScore(Const.ptgNearMissPtBonus, Player.ScoreChangeType.NEAR_MISS);
                    }
                }
            }
            else{
                if(nose[1] <= height *Const.ptgFineCollGraceFactor){
                    player.changeHealth(-Const.ptgHealthPerColl);
                    if(Const.ptgDoTerrainDmg){
                        heightVals[(int)nose[0] ] [ (int)nose[2] ] = player.getY()-y;
                        heightVals[(int)nose[0]+1 ] [ (int)nose[2] ] = ((player.getY()-y)+heightVals[(int)nose[0]+1 ] [ (int)nose[2] ])/2f;
                        heightVals[(int)nose[0]-1 ] [ (int)nose[2] ] =((player.getY()-y)+heightVals[(int)nose[0]+1 ] [ (int)nose[2] ])/2f;
                    }
                    player.setY(height);
                    if(hapticFeedbackView != null && Const.performHaptics)hapticFeedbackView.performHapticFeedback(HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING, HapticFeedbackConstants.LONG_PRESS);
                    //TODO: Define what happens to the player when they hit the terrain.
                }
                else if(Const.ptgDoNearMissTest){
                    if(nose[1]<= height + Const.ptgNearMissDist){
                        player.changeScore(Const.ptgNearMissPtBonus, Player.ScoreChangeType.NEAR_MISS);
                    }
                }
            }
        }
    }

    /**
     * Linearly interpolates a and b at t.
     * @param t t
     * @param a  a
     * @param b   b
     * @return The result of linearly interpolating a and b at t.
     */
    private float lerp(float t, float a, float b) {
        return a + t * (b - a);
    }

    public void setTerrainWidth(float newWidth){
        tWidth = newWidth;
    }

    public float getTerrainWidth(){
        return tWidth;
    }

    /**
     * Allows the changing of the density scale. Note that this will only affect future updates.
     *
     * @param newDensityScale The intended density scale.
     */
    public void setDensityScale(float newDensityScale){
        dScale = newDensityScale;
    }

    /**
     * Allows the changing of the density scale. Note that this will only affect future updates. Also, this
     * adapts to the current Slope Scale, hence when used in conjunction with setSlopeScale(), the call to
     * this method should always be second.
     *
     * @param newHeightScale The intended height scale.
     */
    public void setHeightScale(float newHeightScale){
        hScale = newHeightScale;
        chScale = (float)Math.pow(hScale, sScale);
    }

    /**
     * Allows the changing of the density scale, the power to which the height scale is raised. Note that
     * this will only affect future updates. Also, since this directly influences the height scale, when
     * used in conjunction with this method, setHeightScale() should always be called second.
     *
     * @param newSlopeScale The intended slope scale.
     */
    public void setSlopeScale(float newSlopeScale){
        sScale = newSlopeScale;
    }

    /**
     * Allows the changing of the traversal speed. These changes take place immediately.
     *
     * @param newSpeed The intended speed value.
     */
    public void setSpeed(float newSpeed){
        speed = newSpeed;
    }

    /**
     * Returns the speed the terrain is currently moving.
     * @return speed
     */
    public float getSpeed(){
        return speed;
    }

    /**
     * Allows the changing of the terrain color. These changes take place immediately.
     *
     * @param r Intended red value.
     * @param g Intended green value.
     * @param b Intended blue value.
     * @param a Intended alpha value.
     */
    public void setColor(float r, float g, float b, float a){
        packColors(r, g, b, a);
    }

    /**
     * Draws the terrain.
     * @param gl The GL10 object that the game is using.
     */
    public void drawTerrain(GL10 gl){

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        if(bag!= null && drawBag){
            //bag.drawBag(gl);
        }
        if(player != null){
            player.drawShip(gl, drawPlayer);
        }

        incrementTerrain(gl);

        //Make sure that we are culling the correct face.
        if(inverted) gl.glCullFace(GL10.GL_FRONT);
        else         gl.glCullFace(GL10.GL_BACK);

        //if(bag != null)bag.drawBag(gl, );

        //Point to our geometry buffers.
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, current);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, color);

        //Draw one strip of triangles per x value in the height value table.
        for(int i = 0; i < resX-1; i++){
            gl.glDrawArrays( GL10.GL_TRIANGLE_STRIP, (i*(2*(resY))),  (2*(resY)) );
        }






    }

    /**
     * Initializes the OpenGL state to the state needed to display
     * the terrain, ship, pickups--general gameplay--properly.
     * @param gl the GL10 state that the game is using.
     */
    public static void setupGLState(GL10 gl, GameplayMode mode){

        //Initialize the fog.
        initFog(gl, mode);

        //Enable depth masking.
        gl.glDepthMask(true);

        //And for the depth mask we must enable depth testing.
        gl.glEnable(GL10.GL_DEPTH_TEST);

        //Enable back-face culling.
        gl.glEnable(GL10.GL_CULL_FACE);

        //Flag the front face to be the one that is wound counter-clockwise.
        gl.glFrontFace(GL10.GL_CCW);

        //Make all shading be done per fragment.
        gl.glShadeModel(GL10.GL_SMOOTH);

        //Set the depth comparison to make a fragment in front of another
        //if it has a z less than or equal to another.
        gl.glDepthFunc(GL10.GL_LEQUAL);

        //Enable the use of vertex arrays.
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        //Enable the use of color arrays.
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        switch (mode) {
            case noneSelected:
                gl.glClearColor(1, 0, 1, 1);
                break;
            case TimeAttack:
                gl.glClearColor(1, 1, 1, 1);
                break;
            case Classic:
                gl.glClearColor(0, 0, 0, 1);
                break;
            case Survival:
                gl.glClearColor(0, 0, 0, 1);
                break;
        }

        // Set GL_MODELVIEW transformation mode
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();   // reset the matrix to its default state

        // When using GL_MODELVIEW, you must set the view point
        GLU.gluLookAt(gl, 0, 0, -5, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        gl.glViewport(0, 0, 16, 9);

        //Set up the projection matrix.
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 45.0f, (float) 16 / (float) 9, 0.1f, 1000.0f);
        gl.glViewport(0, 0, 16, 9);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public float gettWidth() {
        return tWidth;
    }

    public void settWidth(float tWidth) {
        this.tWidth = tWidth;
    }

    public float gettDepth() {
        return tDepth;
    }

    public void settDepth(float tDepth) {
        this.tDepth = tDepth;
    }

    public float getOx() {
        return ox;
    }

    public void setOx(float ox) {
        this.ox = ox;
    }

    public float getOy() {
        return oy;
    }

    public void setOy(float oy) {
        this.oy = oy;
    }

    public float getOz() {
        return oz;
    }

    public void setOz(float oz) {
        this.oz = oz;
    }

    public double getUnitDepth() {
        return unitDepth;
    }

    public void setUnitDepth(double unitDepth) {
        this.unitDepth = unitDepth;
    }

    public double getUnitWidth() {
        return unitWidth;
    }

    public void setUnitWidth(double unitWidth) {
        this.unitWidth = unitWidth;
    }

    public double getUnitH() {
        return unitH;
    }

    public void setUnitH(double unitH) {
        this.unitH = unitH;
    }

    public float getdScale() {
        return dScale;
    }

    public void setdScale(float dScale) {
        this.dScale = dScale;
    }

    public float gethScale() {
        return hScale;
    }

    public void sethScale(float hScale) {
        this.hScale = hScale;
    }

    public float getChScale() {
        return chScale;
    }

    public void setChScale(float chScale) {
        this.chScale = chScale;
    }

    public float getsScale() {
        return sScale;
    }

    public void setsScale(float sScale) {
        this.sScale = sScale;
    }

    public float getSeed() {
        return seed;
    }

    public void setSeed(float seed) {
        this.seed = seed;
    }

    public int getResX() {
        return resX;
    }

    public void setResX(int resX) {
        this.resX = resX;
    }

    public int getResY() {
        return resY;
    }

    public void setResY(int resY) {
        this.resY = resY;
    }

    public boolean isInverted() {
        return inverted;
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

    /**
        * Initializes the fog functionality of the OpenGL state
        * that the game is using.
        * @param gl the GL10 object that the game is using.
        */
    private static void initFog(GL10 gl, GameplayMode mode) {

        //Color the fog based on the GameplayMode.
        float[] fogColor = {0, 1, 0, 1};
        switch (mode) {
            case noneSelected:
                float[] colora = {0f, 1f, 0f, 1f};
                fogColor = colora;
                break;
            case TimeAttack:
                float[] colorb = {1f, 1f, 1f, 1f};
                fogColor = colorb;
                break;
            case Classic:
                float[] colorc = {0f, 0f, 0f, 1f};
                fogColor = colorc;
                break;
            case Survival:
                float[] colord = {0f, 0f, 0f, 1f};
                fogColor = colord;
                break;
        }



        //Make the fog look the best that it can on the device.
        gl.glHint(GL10.GL_FOG_HINT, GL10.GL_DONT_CARE);

        //Set it to be oh so dense.
        float fogDensity = .07f;

        //Enable the fog feature in the OpenGL state.
        gl.glEnable(GL10.GL_FOG);

        //Set the end of the fog to be 20 units away.
        //gl.glFogx(GL10.GL_FOG_END, 20);

        //Make the fog grow exponentially darker with distance.
        gl.glFogx(GL10.GL_FOG_MODE, GL10.GL_EXP2);

        //Apply the fog color.
        gl.glFogfv(GL10.GL_FOG_COLOR, fogColor, 0);

        //Apply the fog density.
        gl.glFogf(GL10.GL_FOG_DENSITY, fogDensity);

    }

    private void incrementTerrain(GL10 gl){

        /*
        We cannot simply increment the height array towards the camera each frame--that
        would make the world move far too fast. Rather, we move the geometry gradually
        over the distance of a single resolution, then we move the geometry back, and
        increment the height array.
        */

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        //Load the current transformation matrix.
        //gl.glLoadIdentity();

        //Translate to the location of the terrain.
        gl.glTranslatef(0, 0, z);


        //Increment the z value by speed.
        z += speed;


        //If we've moved more than the distance between each node, we need to do a crap-ton of stuff.
        if(Math.abs(oz-z)>= unitDepth){

            //First, we must retreat the terrain.
            //gl.glLoadIdentity();
            gl.glTranslatef(0, 0, (float)-unitDepth);

            //Also, on a related note, we need to negate the effects of that retreat on the pickups.
            if(drawBag && bag != null)
                bag.incrementPickupLocations(0, 0, (float)unitDepth);

            //Next we must reset the z value.
            z = oz;

            //And now we start scheduling updates.
            if(curVerBuffer == 0){//If the current vertex buffer is the first one...
                current = vertsB; //change it to the second one,
                updater.schedule(new VertUpdate(vertsA), 0); //and schedule an update on the first.
                curVerBuffer = 1; //Also change our flag.

            }
            else if(curVerBuffer == 1){//If the current vertex buffer is the second one...
                current = vertsA; //change it back to the first one,
                updater.schedule(new VertUpdate(vertsB), 0); //and schedule an update on the second.
                curVerBuffer = 0; //And again, change the flag.
            }

        }


    }

    /**
     * Encapsulates the task of updating one of the terrain's vertex buffers neatly within
     * a TimerTask.
     */
    public class VertUpdate extends TimerTask {

        //The FloatBuffer that we will be operating on this evening.
        FloatBuffer verts;

        /**
         * Constructs the VertUpdate.
         * @param verts The FloatBuffer that is the vertex buffer to be updated.
         */
        public VertUpdate(FloatBuffer verts){
            this.verts = verts;
        }

        public void run(){

            //Create a float array to hold the new values that we are going to create.
            float[] newRow = new float[resX];

            //Increment the increments.
            increments++;

            //Populate the new row with new noise values.
            //Create a single row of new Perlin noise height values.
            for(int i = 0; i < newRow.length; i++){
                if(Const.ptgDebugCollision){
                    if(inverted){
                        newRow[i] = y-hScale*(((newRow.length)-(float)i)/(float)newRow.length);
                    }
                    else{
                        newRow[i] = y+hScale*(((float)i)/(float)newRow.length);
                    }
                }
                else{
                    newRow[i] = (float) ImprovedNoise.noise((unitWidth * i) / tWidth * dScale * Const.gpTDVar,
                            (unitDepth * increments) / tDepth * dScale * Const.gpTDVar,
                            seed);
                    newRow[i] = (float)Math.pow(newRow[i], sScale) * hScale;
                    if(inverted){ newRow[i] *= -1f; }
                    if(i == 0 || i == resX-1){
                        if(inverted)newRow[i] += y - hScale;
                        else newRow[i] += y + hScale;
                    }
                }
            }

            //Move up all the heightValues by one, shifting it so that we don't perceive the map returning to normal
            //translation.
            for(int x = 0; x < resX; x++){
                for(int y = 1; y < resY; y++){
                    heightVals[x][y-1] = heightVals[x][y];
                }
            }

            //Append those new values to the far end of the heightfield.
            for(int i = 0; i < resX; i++){
                heightVals[i][resY-1] = newRow[i];
            }
            packVerts(verts);
        }

    }

}
