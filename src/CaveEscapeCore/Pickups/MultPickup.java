package CaveEscapeCore.Pickups;

import CaveEscapeCore.CoreGameplay.GameplayMode;
import CaveEscapeCore.Player.Player;

import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Represents a single MultPickup, a pickup that changes the score multiplier of the
 * player. These are also represented by octahedra, but are half the height of point
 * pickups. This means that the vertex packing and drawing methods are identical to
 * that of the PointPickup.
 */
public class MultPickup extends Pickup {

    /**
     * The multiplier represented by this pickup.
     */
     int mult;

    /**
     * The length of time this multiplier lasts--in frames.
     */
    int multFrames;

    /**
     * The vertex and color buffers of this pickup.
     */
    FloatBuffer colors, verts;

    /**
     * Constructs this MultPickup.
     *
     * @param mult The multiplier to give the player when this pickup is collected.
     * @param multFrames The number of frames this multiplier will last.
     * @param r  The intended red value.
     * @param g  The intended green value.
     * @param b  The intended blue value.
     * @param x  The intended x-coordinate of the location of this MultPickup.
     * @param y  The intended y-coordinate of the location of this MultPickup.
     * @param z  The intended z-coordinate of the location of this MultPickup.
     * @param sx The intended x size of this MultPickup.
     * @param sy The intended y size of this MultPickup.
     * @param sz The intended z size of this MultPickup.
     */
    public MultPickup(int mult,
                      int multFrames,
                      PickupClass pClass,
                      GameplayMode mode,
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
        
        super(PickupType.MULT, pClass, mode, r, g, b, x, y, z, sx, sy, sz);
        this.mult = mult;
        this.multFrames = multFrames;
        initBuffers();
        packBuffers();
    }

    /**
     * Initializes the geometry buffers of this MultPickup. Since the MultPickup's geometry is just a half tall
     * version of the PointPickup, this method is identical to that of the PointPickup.
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
     * Populates the geometry buffers with values. Since the MultPickup is just a smaller version of the
     * PointPickup, this call is nearly the same, albeit with different values.
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
        vertLocs[4]  = 0 + .5f*sy;
        vertLocs[5]  = 0;

        //East
        vertLocs[6]  = 0 +  1f*sx;
        vertLocs[7]  = 0 + .5f*sy;
        vertLocs[8]  = 0 + .5f*sz;

        //South
        vertLocs[9]  = 0 + .5f*sx;
        vertLocs[10] = 0 + .5f*sy;
        vertLocs[11] = 0 +  1f*sz;

        //West
        vertLocs[12] = 0;
        vertLocs[13] = 0 + .5f*sy;
        vertLocs[14] = 0 + .5f*sz;

        //Bottom
        vertLocs[15] = 0 + .5f*sx;
        vertLocs[16] = 0 +  1f*sy;
        vertLocs[17] = 0 + .5f*sz;

        //North
        vertLocs[18] = 0 + .5f*sx;
        vertLocs[19] = 0 + .5f*sy;
        vertLocs[20] = 0;

        //West
        vertLocs[21] = 0;
        vertLocs[22] = 0 + .5f*sy;
        vertLocs[23] = 0 + .5f*sz;

        //South
        vertLocs[24] = 0 + .5f*sx;
        vertLocs[25] = 0 + .5f*sy;
        vertLocs[26] = 0 +  1f*sz;

        //East
        vertLocs[27] = 0 +  1f*sx;
        vertLocs[28] = 0 + .5f*sy;
        vertLocs[29] = 0 + .5f*sz;

        //And now the color buffer.
        float[] vertCols = new float[40];

        vertCols[0]  = super.r;
        vertCols[1]  = super.g;
        vertCols[2]  = super.b;
        vertCols[3]  = 0f;

        vertCols[4]  = super.r;
        vertCols[5]  = super.g;
        vertCols[6]  = super.b;
        vertCols[7]  = 1f;

        vertCols[8]  = super.r;
        vertCols[9]  = super.g;
        vertCols[10] = super.b;
        vertCols[11] = 0f;

        vertCols[12] = super.r;
        vertCols[13] = super.g;
        vertCols[14] = super.b;
        vertCols[15] = 1f;

        vertCols[16] = super.r;
        vertCols[17] = super.g;
        vertCols[18] = super.b;
        vertCols[19] = 0f;

        vertCols[20] = super.r;
        vertCols[21] = super.g;
        vertCols[22] = super.b;
        vertCols[23] = 0f;

        vertCols[24] = super.r;
        vertCols[25] = super.g;
        vertCols[26] = super.b;
        vertCols[27] = 1f;

        vertCols[28] = super.r;
        vertCols[29] = super.g;
        vertCols[30] = super.b;
        vertCols[31] = 0f;

        vertCols[32] = super.r;
        vertCols[33] = super.g;
        vertCols[34] = super.b;
        vertCols[35] = 1f;

        vertCols[36] = super.r;
        vertCols[37] = super.g;
        vertCols[38] = super.b;
        vertCols[39] = 0f;

        //Now we pack these arrays into the FloatBuffers.
        verts.put(vertLocs);
        colors.put(vertCols);

        //We also need to reset the buffer positions of the FloatBuffers.
        verts.position(0);
        colors.position(0);

    }

    /**
     * When the player picks up this MultPickup, it sets the player's multiplier and gives it a time value.
     *
     * @param player The player that collects this pickup.
     */
    @Override
    public void collected(Player player){
        player.setMultiplier(mult, multFrames);
    }

    /**
     * Yet again, we find ourselves testing for collision between the ship
     * and an octahedral pickup. So the same guidelines still apply. We test
     * Z locations, then X locations, then Y locations, and we test within the
     * rectangular prism surrounding the octahedron.
     * There is one very important thing to note, however.
     *
     *    We still test for the same size prism, though MultPickups are smaller.
     *    This is because I merely wanted to make them harder to see, not harder to get.
     *    That's dickish.
     *
     * @param player The player to test collision with.
     * @return A boolean value representing whether or not the player collected
     *         this MultPickup.
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

        /*
        HOW IN THE FUCKING HELL DOES THE ABOVE WORK BUT NOT THIS?!?!?!?!?!?
        //First we need to get the location of the ship's nose.
        //Returns X, Y, and Z in a float array.
        float[] nose = player.getShipLocation();

        //Now we test the z locations of the two entities. We do this first
        //since it is the least likely to evaluate to true.
        if(nose[2] > z && nose[2] < (float)(z+sz)){
            //Since the cave will be wider than it is tall, we evaluate the
            //x locations of the entities next. They are the next least
            //likely to evaluate to colliding.
            if(nose[0] > x && nose[0] < x+sx){
                //Next we test the y locations of the two entities. we do this
                //last since they are the most likely to collide.
                if(nose[1] > y && nose[1] < y+sy){
                    return true;
                }
            }
        }
        return false;
         */

    }

    /**
     * Draws this MultPickup.
     * @param gl The GL10 object that the game is using.
     */
    @Override
    public void draw(GL10 gl){

        //Cull the back faces of this MultPickup.
        gl.glCullFace(GL10.GL_BACK);

        //Give the OpenGL state pointers to the vertex and color buffers of this MultPickup.
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, verts);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, colors);

        //Set up blending.
        gl.glEnable(GL10.GL_BLEND);

        //Set up the proper blending behaviour.
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        //Translate to the location of the pickup.
        gl.glTranslatef(x, y, z);

        //Draw the MultPickup.
        gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 5);
        gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 5, 5);

        //Translate back from the pickup's location so we don't fuck with the transformation stack.
        gl.glTranslatef(-x, -y, -z);

        //Return blending back to normal.
        gl.glDisable(GL10.GL_BLEND);
        
    }

}
