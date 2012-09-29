package CaveEscapeCore.Pickups;

import CaveEscapeCore.Player.Player;

import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Represents a single Point Pickup, a Pickup that gives the player a boost of points, on top of the
 * progression points. These are physically represented by octahedra.
 */
public class PointPickup extends Pickup {

    /**
     * The point value of this PointPickup.
     */
    int points;

    /**
     * The geometry buffers of the pickup.
     */
    FloatBuffer verts;
    FloatBuffer colors;

    /**
     * Constructs this PointPickup.
     *
     * @param points The amount of points to give the player.
     * @param r  The intended red value.
     * @param g  The intended green value.
     * @param b  The intended blue value.
     * @param x  The intended x-coordinate of the location of this PointPickup.
     * @param y  The intended y-coordinate of the location of this PointPickup.
     * @param z  The intended z-coordinate of the location of this PointPickup.
     * @param sx The intended x size of this PointPickup.
     * @param sy The intended y size of this PointPickup.
     * @param sz The intended z size of this PointPickup.
     */
    public PointPickup(int points,
                       PickupClass pClass,
                       float r,
                       float g,
                       float b,
                       float x,
                       float y,
                       float z,
                       float sx,
                       float sy,
                       float sz
                      ){

        super(PickupType.POINT, pClass, r, g, b, x, y, z, sx, sy, sz);
        this.points = points;
        initBuffers();
        packBuffers();
    }

    /**
     * Initializes the geometry buffers of this PointPickup. Note that the color buffer is allocated with only
     * three values per vertex, as opposed to four. This is done so that we do not store an alpha value, helping
     * a little with ram usage. This should be remembered when setting the OpenGL state's color pointer when
     * drawing this PointPickup.
     */
    private void initBuffers(){

        /*
        We will be using GL_TRIANGLE_FAN To draw the octahedron. That means we start with the
        top point, and draw the triangles around it by tracing the equator of the octahedron.
        Then we do the same for the bottom pyramid. That means our vertex budget looks like this:

            Top + four corners + bottom + four corners

        That means we have ten vertices. At 3 floats per vert, and four bytes per float, we have
        120 bytes.
         */
        ByteBuffer vbb = ByteBuffer.allocateDirect(10 * 3 * 4); //Allocate the proper number of bytes.
        vbb.order(ByteOrder.nativeOrder()); //Set the byte order of the vertex buffer to that of the phone.
        verts = vbb.asFloatBuffer(); //Now cast the byte buffer out to the vertex float buffer.

        /*
        Since we know we have 10 vertices going into the vertex buffer, creating and even packing
        the color buffer is very straight-forward. The only strange bit is that we will not be using
        an alpha value AT ALL. We do this by specifying the values-per-vertex parameter of glColorPointer
        as three, which accounts only for the red, green, and blue values.
         */
        ByteBuffer cbb = ByteBuffer.allocateDirect(10 * 4  * 4);
        cbb.order(ByteOrder.nativeOrder());
        colors = cbb.asFloatBuffer();



    }

    /**
     * Packs the geometry buffers of this PointPickup with values.
     */
    private void packBuffers(){

        //Create a float array to create the vertex location values in.
        float[] vertLocs = new float[30];

        //And now we type out the values. The rotation is opposite on the bottom to
        //prevent culling problems.

        //Top
        vertLocs[0]  = 0 + .5f*sx;
        vertLocs[1]  = 0;
        vertLocs[2]  = 0 + .5f*sz;

        //North
        vertLocs[3]  = 0 + .5f*sx;
        vertLocs[4]  = 0 + 1f*sy;
        vertLocs[5]  = 0;

        //East
        vertLocs[6]  = 0 +  1f*sx;
        vertLocs[7]  = 0 + 1f*sy;
        vertLocs[8]  = 0 + .5f*sz;

        //South
        vertLocs[9]  = 0 + .5f*sx;
        vertLocs[10] = 0 +  1f*sy;
        vertLocs[11] = 0 +  1f*sz;

        //West
        vertLocs[12] = 0;
        vertLocs[13] = 0 +  1f*sy;
        vertLocs[14] = 0 + .5f*sz;

        //Bottom
        vertLocs[15] = 0 + .5f*sx;
        vertLocs[16] = 0 +  2f*sy;
        vertLocs[17] = 0 + .5f*sz;

        //North
        vertLocs[18] = 0 + .5f*sx;
        vertLocs[19] = 0 + 1f*sy;
        vertLocs[20] = 0;

        //West
        vertLocs[21] = 0;
        vertLocs[22] = 0 +  1f*sy;
        vertLocs[23] = 0 + .5f*sz;

        //South
        vertLocs[24] = 0 + .5f*sx;
        vertLocs[25] = 0 +  1f*sy;
        vertLocs[26] = 0 +  1f*sz;

        //East
        vertLocs[27] = 0 +  1f*sx;
        vertLocs[28] = 0 +  1f*sy;
        vertLocs[29] = 0 + .5f*sz;

        //And now the color buffer.
        float[] vertCols = new float[40];

        vertCols[0]  = 1;
        vertCols[1]  = 1;
        vertCols[2]  = 1;
        vertCols[3]  = 0f;

        vertCols[4]  = super.r;
        vertCols[5]  = super.g;
        vertCols[6]  = super.b;
        vertCols[7]  = 1f;

        vertCols[8]  = super.r;
        vertCols[9]  = super.g;
        vertCols[10] = super.b;
        vertCols[11] = 1f;

        vertCols[12] = super.r;
        vertCols[13] = super.g;
        vertCols[14] = super.b;
        vertCols[15] = 1f;

        vertCols[16] = super.r;
        vertCols[17] = super.g;
        vertCols[18] = super.b;
        vertCols[19] = 1f;

        vertCols[20] = 1;
        vertCols[21] = 1;
        vertCols[22] = 1;
        vertCols[23] = 0f;

        vertCols[24] = super.r;
        vertCols[25] = super.g;
        vertCols[26] = super.b;
        vertCols[27] = 1f;

        vertCols[28] = super.r;
        vertCols[29] = super.g;
        vertCols[30] = super.b;
        vertCols[31] = 1f;

        vertCols[32] = super.r;
        vertCols[33] = super.g;
        vertCols[34] = super.b;
        vertCols[35] = 1f;

        vertCols[36] = super.r;
        vertCols[37] = super.g;
        vertCols[38] = super.b;
        vertCols[39] = 1f;

        //Now we pack these arrays into the FloatBuffers.
        verts.put(vertLocs);
        colors.put(vertCols);

        //We also need to reset the buffer positions of the FloatBuffers.
        verts.position(0);
        colors.position(0);

    }

    /**
     * When this PointPickup is collected, it changes the score by its "points" value.
     * @param player The player that collects this pickup.
     */
    public void collected(Player player){
        player.changeScore(points, Player.ScoreChangeType.BONUS);
    }

    /**
     * Tests to see if the nose point of the Player "player"'s ship is within a
     * rectangular prism surrounding the pickup. Note how this gives a volume larger
     * than that of the pickup itself. This is done for several reasons:
     *
     * 1: Testing if a point is within a rectangular prism is cutthroat simple.
     * 2: If the nose of the collides with an area within the prism but not within the
     *    octahedron, chances are that one of the wings did visibly collide with the
     *    octahedron.
     * 3: It makes collecting these octahedral slightly easier. And I think that's a
     *    good thing.
     *
     * @param player The player to test collision with.
     * @return A boolean value representing whether or not the Player "player"
     *         collected this PointPickup.
     */
    @Override
    public boolean collide(Player player) {

        //First we need to get the location of the ship's nose.
        float shipX = player.getX();
        float shipY = player.getY();
        float shipZ = player.getZ();

        //Now we test the z locations of the two entities. We do this first
        //since it is the least likely to evaluate to true.
        if(shipZ >= z-(.5*sz) && shipZ <= z+1.5*sz){
            //Since the cave will be wider than it is tall, we evaluate the
            //x locations of the entities next. They are the next least
            //likely to evaluate to colliding.
            if(shipX >= x-(.5*sx) && shipX <= x+1.5*sx){
                //Next we test the y locations of the two entities. we do this
                //last since they are the most likely to collide.
                if(shipY >= y-(.5*sy) && shipY <= y+1.5*sy){
                    return true;
                }
            }
        }
        return false;

    }

    /**
     * Draws this PointPickup.
     * @param gl The GL10 object that the game is using.
     */
    public void draw(GL10 gl){

        //Cull the back faces of this PointPickup.
        gl.glCullFace(GL10.GL_BACK);

        //Give the OpenGL state pointers to the vertex and color buffers of this pointPickup.
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, verts);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, colors);

        //Enable alpha blending for this pickup.
        gl.glEnable(GL10.GL_BLEND);

        //Set up a reasonable blending function.
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        //Translate to the location of the pickup.
        gl.glTranslatef(x, y, z);

        //Draw the pickup.
        gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 5);
        gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 5, 5);

        //Translate back from the pickup's location so we don't fuck with the transformation stack.
        gl.glTranslatef(-x, -y, -z);

        //Disable the blending for congruency.
        gl.glDisable(GL10.GL_BLEND);

    }

}
