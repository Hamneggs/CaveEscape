package CaveEscape.ShipSelectScreen.GLES10;

import CaveEscapeCore.Constants.Const;
import CaveEscapeCore.Player.ShipShowroom;
import CaveEscapeCore.Terrain.PerlinTerrainMenu;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * This GLES10Renderer is meant to be used only for the
 * Ship Selection Screen.
 */
public class GLES10Renderer implements GLSurfaceView.Renderer {

    /**
     * The showroom of the ships.
     */
    private ShipShowroom showroom;

    /**
     * The color of the fog and the clear color.
     */
    private float clearR, clearG, clearB;

    /**
     * Constructs this Renderer.
     * @param showroom The prepared Showroom instance to be be rendered.
     * @param clearR The clear (and fog) color for the OpenGL state.
     * @param clearG The clear (and fog) color for the OpenGL state.
     * @param clearB The clear (and fog) color for the OpenGL state.
     */
    public GLES10Renderer(ShipShowroom showroom, float clearR, float clearG, float clearB){

        this.showroom = showroom;
        this.clearR = clearR;
        this.clearG = clearG;
        this.clearB = clearB;


    }

    /**
     * Called when the Surface is created. This is all performed before the
     * first frame is rendered, so we set up the OpenGL state here.
     * @param gl The GL10 instance that the GLSurfaceView is using.
     * @param eglConfig The EGLConfig. Unused.
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig eglConfig) {

        //Set up (for the most part)
        //the OpenGL state.
        PerlinTerrainMenu.setupGLState(gl);

        //Move the eye to the center of the showroom.
        GLU.gluLookAt(gl,
                showroom.getCenterX(), showroom.getCenterY(), showroom.getCenterZ(),  //Eye location
                0f, 0f, 0f, //Center of vision.
                0f, 1.0f, 0.0f);//"Up" vector.

        //Set the clear color to that specified.
        gl.glClearColor(clearR, clearG, clearB, 1f);
        //Do the same with the fog.
        float[] fogColor = {clearR, clearG, clearB, 1f};

        //Apply these changes to the fog.
        gl.glFogfv(GL10.GL_FOG_COLOR, fogColor, 0);
        gl.glFogf(GL10.GL_FOG_DENSITY, Const.smFogDensity);
    }

    /**
     * Called when the surface changes shape. This should never happen
     * based on how we set up the Activity, but just in case we
     * reset everything here.
     * @param gl The GL10 instance that the GLSurfaceView is using.
     * @param width The new width of the GLSurfaceView.
     * @param height The new height of teh GLSurfaceView.
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // Set GL_MODELVIEW transformation mode
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();   // reset the matrix to its default state

        // When using GL_MODELVIEW, you must set the view point
        //Move the eye to the center of the showroom.
        GLU.gluLookAt(gl,
                showroom.getCenterX(), showroom.getCenterY(), showroom.getCenterZ(),  //Eye location
                0f, 0f, 0f, //Center of vision.
                0f, 1.0f, 0.0f);//"Up" vector.

        //Swap over to editing the Projection transformation matrix and its operation stack.
        gl.glMatrixMode(GL10.GL_PROJECTION);
        //clear said stack.
        gl.glLoadIdentity();
        //Set up the view frustum again.
        GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f, 1000.0f);
        //Resize OpenGL's viewport.
        gl.glViewport(0, 0, width, height);

        //Now for safety we leave with the matrix mode in ModelView...
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        //But not without first clearing its operation stack.
        gl.glLoadIdentity();
    }

    /**
     * Called every time the Renderer draws a frame. Not much happens here,
     * we just tell the showroom to draw itself. That's where the real
     * magic happens.
     * @param gl The GL10 instance that the GLSurfaceView is using.
     */
    @Override
    public void onDrawFrame(GL10 gl) {

        //Clear the buffers.
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        //Move the eye to the center of the showroom.
        GLU.gluLookAt(gl,
                showroom.getCenterX(), showroom.getCenterY(), showroom.getCenterZ(),  //Eye location
                0f, 0f, 0f, //Center of vision.
                0f, 1.0f, 0.0f);//"Up" vector.

        //Draw the showroom.
        showroom.drawShowroom(gl);


    }
}
