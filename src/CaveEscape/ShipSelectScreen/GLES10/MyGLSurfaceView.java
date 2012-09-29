package CaveEscape.ShipSelectScreen.GLES10;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * Created with IntelliJ IDEA.
 * User: Chuck Finley
 * Date: 7/5/12
 * Time: 5:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyGLSurfaceView extends GLSurfaceView {

    public MyGLSurfaceView(Context context, Renderer renderer){
        super(context);
        setRenderer(renderer);
        setRenderMode(RENDERMODE_CONTINUOUSLY);
    }
}
