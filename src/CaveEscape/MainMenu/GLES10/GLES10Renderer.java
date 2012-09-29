package CaveEscape.MainMenu.GLES10;

import CaveEscapeCore.Constants.Const;
import CaveEscapeCore.Terrain.PerlinTerrainMenu;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * The Renderer of the OpenGL geometry for
 * CaveEscape.CaveEscape.CaveEscape's main menu.
 * It consists of two PerlinTerrainMenus rotating
 * in the background with the geometry colored black
 * and the distance colored white.
 */
public class GLES10Renderer implements GLSurfaceView.Renderer{

    /**
     * The terrains that make up the background.
     */
    PerlinTerrainMenu top, bottom;

    /**
     * Constructs the renderer.
     */
    public GLES10Renderer(){

        //We create a random seed for the terrain, but we don't want
        //them to vary by much from each other, so we give the seeds
        //a much smaller deviation between each other.
        float randomSeed, deviation;
        //populate the seed and deviation.
        deviation = Const.mmDevSize*(float)Math.random();
        randomSeed = (float)Math.random();

        //Construct the top terrain.
        top = new PerlinTerrainMenu(
                0,  //Have it be centered around the point
                Const.mmVertOffset,   //(0, 3, 0) since it is the top
                0,  //terrain.
                Const.mmTerSize, //The terrain will be fifty units wide.
                Const.mmTerSize, //The terrain will be fifty units deep.
                Const.mmTerRes,  //The internal x resolution of the heightmap.
                Const.mmTerRes,  //The internal y (z) resolution of the heightmap.
                Const.mmRotSpeed,//How far the menu rotates every frame, in degrees.
                Const.mmFDensity,//The feature density scale.
                Const.mmHScale,  //The height factor.
                Const.mmSScale,  //The slope (exponential) factor.
                randomSeed+deviation, //The seed we made earlier.
                true, //Inverted since it is on top.
                false //We aren't using this for the ship menu.
        );

        randomSeed -= 2*deviation;
        bottom = new PerlinTerrainMenu(
                0,  //Have it be centered around the point
                -Const.mmVertOffset,  //(0, -3, 0) since it is the bottom
                0,  //terrain.
                Const.mmTerSize,  //The terrain will be fifty units wide.
                Const.mmTerSize,  //The terrain will be fifty units deep.
                Const.mmTerRes, //The internal x resolution of the heightmap.
                Const.mmTerRes, //The internal y (z) resolution of the heightmap.
                Const.mmRotSpeed,//How far the menu rotates every frame, in degrees.
                Const.mmFDensity,  //The feature density scale.
                Const.mmHScale,   //The height factor.
                Const.mmSScale,   //The slope (exponential) factor.
                randomSeed-deviation, //The seed.
                false,//Not inverted.
                false //Not for the ship menu.
        );

    }

    /**
     * Called when the surface is created, and before the
     * first frame is called to be rendered.
     * @param gl  The GL10 instance that the game is using.
     * @param config The EGLConfig that we could use if we wanted.
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        //Set the look at point.
        GLU.gluLookAt(gl, 0, 0, -5, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        //Set up the GL state.
        PerlinTerrainMenu.setupGLState(gl);

    }

    /**
     * Called when the GLSurfaceView changes shape or size.
     * @param gl The GL10 instance that the game is using.
     * @param width The new width of the GLSurfaceView.
     * @param height The new height of the GLSurfaceView.
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        // Set GL_MODELVIEW transformation mode
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();   // reset the matrix to its default state

        // When using GL_MODELVIEW, you must set the view point
        GLU.gluLookAt(gl, 0, 0, -5, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f, 1000.0f);
        gl.glViewport(0, 0, width, height);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    /**
     * Called every time we need to render a frame.
     * @param gl The GL10 instance that the game is using.
     */
    @Override
    public void onDrawFrame(GL10 gl) {

        //Clear the buffers.
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        //Draw the terrains.
        top.drawTerrain(gl);
        bottom.drawTerrain(gl);

    }

}
