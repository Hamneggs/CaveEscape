package CaveEscapeCore.Player;

import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * The basic model ship. It has middle of the road stats, and looks like an arrowhead that
 * is yellow on top, and blue on bottom. That's right, I'm subjecting you to the
 * yellow-blue contrast just like that.
 */
public class BasicShip extends Ship {

    /**
     * The buffers of the ship's geometry.
     */
    private FloatBuffer verts, colors;

    /**
     * Constructs the basic ship.
     * @param name
     * @param fSpeed
     * @param sSpeed
     * @param maxDamage
     * @param sx
     * @param sy
     * @param sz
     */
    public BasicShip(String name, float fSpeed, float sSpeed, int maxDamage, float sx, float sy, float sz, int topColor, int bottomColor, int leftColor, int rightColor, int noseColor, int backColor){

        //Initialize the statistics of this ship.
        super(name, fSpeed, sSpeed, maxDamage);

        //Since we don't need to store the size variables for collision, we simply pass them through
        //to the geometry creation, and let them get garbage-collected.
        initBuffers(sx, sy, sz, topColor, bottomColor, leftColor, rightColor, noseColor, backColor);

    }

    public void initBuffers(float sx, float sy, float sz, int topColor, int bottomColor, int leftColor, int rightColor, int noseColor, int backColor){

        float[] vertices = {
        //      x       y       z
                0,      0,    -.5f*sz,      //Nose   0
                0,-.5f*sy,     .5f*sz,      //Bottom 1
          -.5f*sx,      0,     .5f*sz,      //Left   3  //Cone
                0, .5f*sy,     .5f*sz,      //Top    2  //GL_TRIANGLE_FAN
           .5f*sx,      0,     .5f*sz,      //Right  4
                0,-.5f*sy,     .5f*sz,      //Bottom 1

          -.5f*sx,      0,     .5f*sz,      //Left   3
                0, .5f*sy,     .5f*sz,      //Top    2  //Back
                0,-.5f*sy,     .5f*sz,      //Bottom 1  //GL_TRIANGLE_STRIP
           .5f*sx,      0,     .5f*sz,      //Right  4


        };

        //Extract the color channels for OpenGL.
        float tr = (topColor >> 16) & 0xFF;
        float tg = (topColor >> 8) & 0xFF;
        float tb = topColor & 0xFF;
        float ta = topColor >>> 24;

        float br = (bottomColor >> 16) & 0xFF;
        float bg = (bottomColor >> 8) & 0xFF;
        float bb =  bottomColor & 0xFF;
        float ba =  bottomColor >>> 24;

        float lr = (leftColor >> 16) & 0xFF;
        float lg = (leftColor >> 8) & 0xFF;
        float lb =  leftColor & 0xFF;
        float la =  leftColor >>> 24;

        float rr = (rightColor >> 16) & 0xFF;
        float rg = (rightColor >> 8) & 0xFF;
        float rb =  rightColor & 0xFF;
        float ra =  rightColor >>> 24;

        float nr = (noseColor >> 16) & 0xFF;
        float ng = (noseColor >> 8) & 0xFF;
        float nb =  noseColor & 0xFF;
        float na =  noseColor >>> 24;

        float ar = (backColor >> 16) & 0xFF;
        float ag = (backColor >> 8) & 0xFF;
        float ab =  backColor & 0xFF;
        float aa =  backColor >>> 24;


        float[] vertexColors = {
        //      R   G   B   A
                nr/255f, ng/255f, nb/255f, na/255f, //Nose   0
                br/255f, bg/255f, bb/255f, ba/255f, //Bottom 1
                lr/255f, lg/255f, lb/255f, la/255f, //Left   3
                tr/255f, tg/255f, tb/255f, ta/255f, //Top    2
                rr/255f, rg/255f, rb/255f, ra/255f, //Right  4
                br/255f, bg/255f, bb/255f, ba/255f, //Bottom 1

                ar/255f, ag/255f, ab/255f, aa/255f, //Left   3
                ar/255f, ag/255f, ab/255f, aa/255f, //Top    2
                ar/255f, ag/255f, ab/255f, aa/255f, //Bottom 1
                ar/255f, ag/255f, ab/255f, aa/255f  //Right  4
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


    }

    /**
     * Draws the basic ship with nose at (x, y, z).
     *
     * @param gl The GL10 object that the game is using.
     * @param x The intended x location for the ship to be drawn at.
     * @param y The intended y location for the ship to be drawn at.
     * @param z The intended z location for the ship to be drawn at.
     */
    @Override
    public void drawAtLocation(GL10 gl, float x, float y, float z, float vx, float vy, ShipState state){

        //gl.glLoadIdentity();
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        //Translate to the location of the ship
        gl.glTranslatef(x, y, z);

        float rot;

        if(state == ShipState.menuRotate){
            //Rotate the ship by 1 degrees.
            rotateShip(1f);
            //Take those coordinates and rotate OpenGL's transformation
            //matrix accordingly.
            rot = getRotation();
            //Take the rotation, and rotate OpenGL around it, on the Y-axis.
            gl.glRotatef(rot, 0, 1, 0);
            //Set up the front face to be the one that is wound counter clockwise.
            gl.glFrontFace(GL10.GL_CCW);
            //Draw the ship.
            drawShip(gl);
            //Undo the rotation so the camera doesn't get all fucked up.
            gl.glRotatef(-rot, 0, 1, 0);
        }

        else{
            rot = (float)Math.sqrt(vx*vx+vy*vy)/((float)Math.pow(getStrafeSpeed(), 2)*2);
            gl.glRotatef(rot, vy/getStrafeSpeed(), 0, -vx/getStrafeSpeed());
            drawShip(gl);
            gl.glRotatef(-rot, vy/getStrafeSpeed(), 0, -vx/getStrafeSpeed());

        }


        //Translate back away from the ship so that the camera isn't fucked up.
        gl.glTranslatef(-x, -y, -z);
    }

    /**
     * Rotates the ship along the Z axis.
     * @param gl The GL10 instance that the game is using.
     * @param total The total possible number of degrees to rotate the ship.
     * @param vx The current X velocity of the ship.
     */
    private void rotateHorizontal(GL10 gl, float total, float vx){

        //Set the ship's rotation to that of total degrees.
        setShipRotation(total*-vx/getStrafeSpeed());
        //Take those coordinates and rotate OpenGL's transformation
        //matrix accordingly.
        float rot = getRotation();
        //Take the rotation, and rotate OpenGL around it, on the Z-axis.
        gl.glRotatef(rot, 0, 0, 1);
    }

    private void unRotateHorizontal(GL10 gl){


        //Take those coordinates and un-rotate OpenGL's transformation
        //matrix accordingly.
        float rot = getRotation();
        //Take the rotation, and rotate OpenGL around it, on the Z-axis.
        gl.glRotatef(-rot, 0, 0, 1);
    }

    private void rotateVertical(GL10 gl, float total, float vy){

        //Set the ship's rotation to that of total degrees.
        setShipRotation(total*-vy/getStrafeSpeed());
        //Take those coordinates and rotate OpenGL's transformation
        //matrix accordingly.
        float rot = getRotation();
        //Take the rotation, and rotate OpenGL around it, on the Z-axis.
        gl.glRotatef(rot, 1, 0, 0);
    }

    private void unRotateVertical(GL10 gl){


        //Take those coordinates and un-rotate OpenGL's transformation
        //matrix accordingly.
        float rot = getRotation();
        //Take the rotation, and rotate OpenGL around it, on the Z-axis.
        gl.glRotatef(-rot, 1, 0, 0);
    }

    /**
     * Draws the ship.
     * @param gl The GL10 instance that the game is using.
     */
    public void drawShip(GL10 gl){

        //Point OpenGL to our crap.
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, verts);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, colors);

        //Since we can't control the winding all that well with
        //GL_TRIANGLE_FAN we just turn it off and render the extra
        //four triangles. Oh well.
        gl.glDisable(GL10.GL_CULL_FACE);

        //Here's how this works:
        //GL_TRIANGLES:     The connectivity paradigm to use.
        //18:               How many of the indices in the index buffer to draw.
        //GL_UNSIGNED_BYTE: The data type to expect in the index buffer.
        //indices:          The index buffer itself.
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
        //gl.glFrontFace(GL10.GL_CW);
        gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 6);
        //gl.glFrontFace(GL10.GL_CCW);
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_SRC_ALPHA);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 6, 4);
        gl.glDisable(GL10.GL_BLEND);

        //Turn back-face culling back on.
        gl.glEnable(GL10.GL_CULL_FACE);

    }


}
