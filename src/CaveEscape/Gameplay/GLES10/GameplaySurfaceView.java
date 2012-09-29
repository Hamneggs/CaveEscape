package CaveEscape.Gameplay.GLES10;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * Created with IntelliJ IDEA.
 * User: Chuck Finley
 * Date: 7/11/12
 * Time: 8:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class GameplaySurfaceView extends GLSurfaceView{

    public GameplaySurfaceView(Context context, Renderer renderer){
        super(context);
        setRenderer(renderer);
        setRenderMode(RENDERMODE_CONTINUOUSLY);
    }
}
