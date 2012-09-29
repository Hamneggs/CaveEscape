package CaveEscape.MainMenu.GLES10;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * The OpenGLSurfaceView used for the animated Terrain in the
 * background of CaveEscape.CaveEscape.CaveEscape's main menu.
 * Nothing is drawn until startRender() is called.
 */
public class MenuGLSurfaceView extends GLSurfaceView {

    /**
     * The Context of the reigning Activity.
     */
    Context context;

    /**
     * Constructs the MenuGLSurfaceView. This merely
     * initializes the superclass, and stores the
     * context for later.
     * @param context
     */
    public MenuGLSurfaceView(Context context){

        //Initialize the superclass.
        super(context);

        setRenderer(new GLES10Renderer());
        setRenderMode(RENDERMODE_CONTINUOUSLY);

    }





}
