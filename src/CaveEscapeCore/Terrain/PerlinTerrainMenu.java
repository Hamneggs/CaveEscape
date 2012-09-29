package CaveEscapeCore.Terrain;

import android.opengl.GLU;

import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * This is another procedurally generated terrain class. However, this differs from the gameplay
 * oriented terrain the sense that it does not create new values, and is not updated. This is meant
 * merely for the background of the main menu.
 * Essentially, the terrain represented here will rotate behind a translucent menu, similar-ish
 * to the main menu of
 */
public class PerlinTerrainMenu {

    /**
     * The heightmap of the terrain.
     */
    private float[][] heightMap;

    /**
     * The physical location of the Terrain.
     */
    private float x, y, z;

    /**
     * The resolution of the heightmap.
     */
    private int resX, resY;

    /**
     * The physical unit size represented by a single element in the heightmap.
     */
    private float unitX, unitZ;

    /**
     * The physical dimensions of the terrain.
     */
    private float tWidth, tDepth;

    /**
     * The speed at which the terrain rotates.
     */
    private float rotSpeed;

    /**
     * If this PerlinTerrainMenu is being used for
     * the ship selection screen, then
     * we can't call it's own rotation because
     * it will corrupt OpenGL's
     * transformation matrix.
     */
    boolean isForShips;

    /**
     * The current amount of rotation.
     */
    private float curRot;

    /**
     * The geometry buffers of the terrain.
     */
    private FloatBuffer verts, color;

    /**
     * The feature density scale of the terrain. This is the value
     * by which we increment the noise function.
     */
    private float dScale;

    /**
     * The height scale of the terrain. We multiply the values returned by
     * the noise function by this value uniformly.
     */
    private float hScale;

    /**
     * The slope scale of the terrain. This completes our quadratic modification
     * of the noise function; we raise the value returned by the noise function to
     * this power.
     */
    private float sScale;

    /**
     * Since we only have x and y values to give the noise function, we can use the z
     * slot of the noise function as a seed. This is that seed.
     */
    private float seed;

    /**
     * If the terrain is inverted, the heightmap is subtracted from the base y-location.
     */
    private boolean inverted;

    /**
     * Constructs the MenuTerrain.
     *
     * @param x       The intended x location of the terrain.
     * @param y       The intended y location of the terrain.
     * @param z       The intended z location of the terrain.
     * @param tWidth  The intended physical width of the terrain.
     * @param tDepth  The intended physical depth of the terrain.
     * @param resX    The intended x resolution of the heightmap.
     * @param resY    The intended y resolution of the heightmap.
     * @param dScale  The intended feature density scale of the terrain.
     * @param hScale  The intended height scale of the terrain.
     * @param sScale  The intended slope scale of the terrain.
     * @param seed    The intended seed for the noise function.
     * @param inverted The intended inversion status of the terrain.
     */
    public PerlinTerrainMenu(
            float x,
            float y,
            float z,
            float tWidth,
            float tDepth,
            int resX,
            int resY,
            float rotSpeed,
            float dScale,
            float hScale,
            float sScale,
            float seed,
            boolean inverted,
            boolean isForShips
    ){

        this.x          = x;
        this.y          = y;
        this.z          = z;
        this.resX       = resX;
        this.resY       = resY;
        this.rotSpeed   = rotSpeed;
        this.tWidth     = tWidth;
        this.tDepth     = tDepth;
        this.dScale     = dScale;
        this.hScale     = hScale;
        this.sScale     = sScale;
        this.seed       = seed;
        this.inverted   = inverted;
        this.isForShips = isForShips;

        //Initialize unit values.
        unitX = tWidth/(float)resX;
        unitZ = tDepth/(float)resY;

        initHeightmap();
        initBuffers();

    }

    private void initHeightmap(){
        heightMap = new float[resX][resY];
        for(int x = 0; x < resX; x++){
            for(int y = 0; y < resY; y++){
                heightMap[x][y] = (float) ImprovedNoise.noise((unitX * x) / tWidth * dScale,
                        (unitZ * y) / tDepth * dScale,
                        seed);
                heightMap[x][y] = (float)Math.pow(heightMap[x][y], sScale) * hScale;
                if(inverted){ heightMap[x][y] *= -1f; }
            }
        }
    }

    /**
     * This method initializes the various FloatBuffers used for the geometry of the Terrain.
     */
    private void initBuffers(){

        //Nodes deep * nodes across * verts per node * floats per vert * bytes per float.

        //Create the first of the twin vertex buffers.
        ByteBuffer vbba = ByteBuffer.allocateDirect( 4*3 * 2*(resX-1)*(resY) ); //Allocate the appropriate num of bytes.
        vbba.order(ByteOrder.nativeOrder());  //Set the byte order to that currently employed by the phone.
        verts = vbba.asFloatBuffer();        //Cast it out to the first vertex buffer.

        //We also need to create the color buffer.
        ByteBuffer cbb = ByteBuffer.allocateDirect( 4*4 * 2*(resX-1)*(resY) );
        cbb.order(ByteOrder.nativeOrder());
        color = cbb.asFloatBuffer();

        //We now need to pack the vertices.
        packVerts(verts);
        packColors(0, 0, 0, 1f);
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
                vals[0]  = this.x + unitX*(x-(resX/2));
                vals[1]  = this.y + heightMap[x][y];
                vals[2]  = this.z - unitZ*(y-(resY/2));
                //Vertex 2:
                vals[3]  = this.x + unitX*(x-(resX/2) +1);
                vals[4]  = this.y + heightMap[(x+1)][y];
                vals[5]  = this.z - unitZ*(y-(resY/2));


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

        //gl.glLoadIdentity();
        gl.glMatrixMode(GL10.GL_MODELVIEW);

        //Make sure that we are culling the correct face.
        if(inverted) gl.glCullFace(GL10.GL_FRONT);
        else         gl.glCullFace(GL10.GL_BACK);

        //Point to our geometry buffers.
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, verts);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, color);

        //Do the rotation if the terrain is not being used
        //for the Ship Selection Screen.
        if(!isForShips) rotateGL(gl);

        //Draw one strip of triangles per x value in the height value table.
        for(int i = 0; i < resX-1; i++){
            gl.glDrawArrays( GL10.GL_TRIANGLE_STRIP, (i*(2*(resY))),  (2*(resY)) );
        }

    }

    /**
     * Performs the rotation of the gl state needed for the animation of the background.
     * @param gl The GL10 instance that the game is using.
     */
    private void rotateGL(GL10 gl){
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        curRot += rotSpeed;
        gl.glRotatef(curRot, 0f, 1f, 0f);
    }

    /**
     * Initializes the OpenGL state to the state needed to display
     * the terrain, ship, pickups--general gameplay--properly.
     * @param gl the GL10 state that the game is using.
     */
    public static void setupGLState(GL10 gl){

        //Initialize the fog.
        initFog(gl);

        //Enable depth masking.
        gl.glDepthMask(true);

        //And for the depth mask we must enable depth testing.
        gl.glEnable(GL10.GL_DEPTH_TEST);

        //Enable back-face culling.
        gl.glEnable(GL10.GL_CULL_FACE);

        //Flag the front face to be the one that is wound counter-clockwise.
        gl.glFrontFace(GL10.GL_CCW);

        //Make the fog shading nice.
        gl.glHint(GL10.GL_FOG_HINT, GL10.GL_NICEST);

        //Increase sampling count of whatever anti-aliasing the device performs.
        gl.glHint(GL10.GL_POLYGON_SMOOTH_HINT, GL10.GL_NICEST);

        //Smooth shading.
        gl.glShadeModel(GL10.GL_SMOOTH);

        //Make all shading be done per fragment.
        gl.glShadeModel(GL10.GL_SMOOTH);

        //Set the depth comparison to make a fragment in front of another
        //if it has a z less than or equal to another.
        gl.glDepthFunc(GL10.GL_LEQUAL);

        //Enable the use of vertex arrays.
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        //Enable the use of color arrays.
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        //Set the clear color to white.
        gl.glClearColor(1f, 1f, 1f, 1.0f);

        //
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

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

    /**
     * Initializes the fog functionality of the OpenGL state
     * that the game is using.
     * @param gl the GL10 object that the game is using.
     */
    private static void initFog(GL10 gl) {

            float[] fogColor = {1f, 1f, 1f, 1f};
            float fogDensity = .085f;
            gl.glEnable(GL10.GL_FOG);
            //gl.glFogx(GL10.GL_FOG_END, 20);
            gl.glFogx(GL10.GL_FOG_MODE, GL10.GL_EXP2);
            gl.glFogfv(GL10.GL_FOG_COLOR, fogColor, 0);
            gl.glFogf(GL10.GL_FOG_DENSITY, fogDensity);

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
     * Returns the X location of the Terrain.
     * @return The terrain's x coordinate.
     */
    public float getX() {
        return x;
    }

    /**
     * Returns the Y location of the Terrain.
     * @return The terrain's y coordinate.
     */
    public float getY() {
        return y;
    }

    /**
     * Returns the Z location of the Terrain.
     * @return The terrain's z coordinate.
     */
    public float getZ() {
        return z;
    }

    /**
     * Returns the X resolution of the heightmap.
     * @return the X resolution of the heightmap.
     */
    public int getResX() {
        return resX;
    }

    /**
     * Returns the Y resolution of the heightmap.
     * @return the Y resolution of the heightmap.
     */
    public int getResY() {
        return resY;
    }

    /**
     * Returns the rotational speed of the terrain.
     * @return the current rotational speed.
     */
    public float getRotSpeed() {
        return rotSpeed;
    }

    /**
     * Returns the current amount of rotation.
     * @return the current amount of rotation.
     */
    public float getCurrentRotation(){
        return curRot;
    }

    /**
     * Sets the speed at which the terrain spins.
     * @param rotSpeed The intended rotation speed.
     */
    public void setRotSpeed(float rotSpeed) {
        this.rotSpeed = rotSpeed;
    }

    /**
     * Returns whether or not the terrain is inverted.
     * @return you know what.
     */
    public boolean isInverted() {
        return inverted;
    }

}
