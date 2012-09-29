package CaveEscapeCore.Pickups;

import CaveEscapeCore.Player.Player;

import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Represents a health pickup. These change the player's health value.
 * Geometrically, the user will see them as a cube.
 *
 * As an addendum, I brain-farted and forgot how to draw a cube. Therefore,
 * most of the drawing and geometry portion of this Pickup is based on the
 * tutorial at:
 * www.intransitione.com/blog/create-a-spinning-cube-with-opengl-es-and-android/
 * This is also the best way to do it I think.
 */
public class HealthPickup extends Pickup {

    /**
     * The health increase represented by this HealthPickup.
     */
    int health;

    /**
     * The buffers the define the geometry of this pickup.
     */
    FloatBuffer colors, verts;
    ByteBuffer indices;

    /**
     * Constructs this HealthPickup.
     *
     * @param health The amount of health to give the player.
     * @param r  The intended red value.
     * @param g  The intended green value.
     * @param b  The intended blue value.
     * @param x  The intended x-coordinate of the location of this HealthPickup.
     * @param y  The intended y-coordinate of the location of this HealthPickup.
     * @param z  The intended z-coordinate of the location of this HealthPickup.
     * @param sx The intended x size of this HealthPickup.
     * @param sy The intended y size of this HealthPickup.
     * @param sz The intended z size of this HealthPickup.
     */
    public HealthPickup(
                       int health,
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

        super(PickupType.HEALTH, pClass, r, g, b, x, y, z, sx, sy, sz);
        this.health = health;
        initBuffers();

    }

    /**
     * Differently from the other pickups, we define, initialize and pack all the
     * geometry for the HealthPickup (through initBuffers()).
     */
    private void initBuffers(){

        //First we create our vertex coordinates based on the size and location of our cube.
        float vertices[] = {
                    0+sx,  0+sy,  0+sz,
                    0,     0+sy,  0+sz,
                    0,     0,     0+sz,
                    0+sx,  0,     0+sz,
                    0+sx,  0+sy,  0,
                    0,     0+sy,  0,
                    0,     0,     0,
                    0+sx,  0,     0
        };

        //Next we create vertex color terms. Note that yet again
        //we are not using an alpha value.
        float vertexColors[] = {
                    r, g, b, 1,
                    1, 1, 1, 0,
                    r, g, b, 1,
                    1, 1, 1, 0,
                    r, g, b, 0,
                    r, g, b, 1,
                    r, g, b, 0,
                    r, g, b, 1
        };

        //And here's the new one. Since a cube has several connecting faces, it is easiest
        //to define which vertices are connected directly than by using one of OpenGL's built-in
        //methods. By using an index buffer, we tell the computer to draw the triangles one at a
        //time.
        //If you would so kindly notice, this correlates with the vertices. And furthermore
        //notice that each row represents two triangles.
        byte vertexIndices[] = {
                    0, 4, 5, 0, 5, 1,
                    1, 5, 6, 1, 6, 2,
                    2, 6, 7, 2, 7, 3,
                    3, 7, 4, 3, 4, 0,
                    4, 7, 6, 4, 6, 5,
                    3, 0, 1, 3, 1, 2
        };

        //Now we pack the vertex elements into our FloatBuffer that we can give to the
        //OpenGL state directly.
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        verts = vbb.asFloatBuffer();
        verts.put(vertices);
        verts.position(0);

        //And now we do the same with the vertex colors.
        ByteBuffer cbb = ByteBuffer.allocateDirect(vertexColors.length * 4);
        cbb.order(ByteOrder.nativeOrder());
        colors = cbb.asFloatBuffer();
        colors.put(vertexColors);
        colors.position(0);

        //And yet again with the index buffer.
        indices = ByteBuffer.allocateDirect(vertexIndices.length);
        indices.put(vertexIndices);
        indices.position(0);


    }

    /**
     * When the Player "player" collects a HealthPickup, he gains the amount
     * of Health given by this pickup.
     * @param player The player that collects this pickup.
     */
    @Override
    public void collected(Player player){
        player.changeHealth(health);
    }

    /**
     * Checks to see if the player has collided with this HealthPickup. Note, however,
     * that the collision prism is twice as large in each direction than the pickup itself.
     * That's to be nice, and to easily account for various ship shapes.
     *
     * @param player The player to test collision with.
     * @return A boolean representing whether or not the Player "player" has collided
     * with and hence collected this pickup.
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
     * Draws the health pickup. Note how this uses glDrawElements().
     * @param gl The GL10 object that the game is using.
     */
    @Override
    public void draw(GL10 gl){

        //Set the front face to be that which is wound clockwise.
        //gl.glFrontFace(GL10.GL_CW);

        //Give the OpenGL state pointers to our shit.
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, verts);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, colors);

        //Since the visual effect for this pickup requires seeing the inside
        //of the cube, we disable backface culling.
        gl.glDisable(GL10.GL_CULL_FACE);
        //Enable blending.
        gl.glEnable(GL10.GL_BLEND);
        //Set up the blending equation.
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        //Translate to the location of the pickup.
        gl.glTranslatef(x, y, z);

        //Here's how this works:
        //GL_TRIANGLES:     The connectivity paradigm to use.
        //36:               How many of the indices in the index buffer to draw.
        //GL_UNSIGNED_BYTE: The data type to expect in the index buffer.
        //indices:          The index buffer itself.
        gl.glDrawElements(GL10.GL_TRIANGLES, 36, GL10.GL_UNSIGNED_BYTE, indices);

        //Translate back from the pickup's location so we don't fuck with the transformation stack.
        gl.glTranslatef(-x, -y, -z);

        gl.glEnable(GL10.GL_CULL_FACE);

    }
}