package CaveEscapeCore.GUIViews;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created with IntelliJ IDEA.
 * User: Chuck Finley
 * Date: 7/9/12
 * Time: 7:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class InvisiButtonRegion extends View {

    /**
     * The location of the invisible button region.
     */
    private float x, y;

    /**
     * The size of the invisible button region.
     */
    private float sx, sy;

    /**
     * The listener that the invisible button region talks to.
     */
    private InvisiButtonRegionListenerI listener;

    /**
     * Constructs the InvisibleButtonRegion.
     * @param x The X-location of the top-left corner of the region.
     * @param y The Y-location of the top-left corner of the region.
     * @param sx The width of the region.
     * @param sy The height of the region.
     * @param context The context of the reigning Activity.
     */
    public InvisiButtonRegion(float x, float y, float sx, float sy, Context context){

        //Initialize the parent View.
        super(context);

        //Copy in all the parameters.
        this.x = x;
        this.y = y;
        this.sx = sx;
        this.sy = sy;

    }

    /**
     * Assigns an InvisiButtonRegionListenerI to this invisible button region.
     * @param listener
     * @see CaveEscapeCore.GUIViews.InvisiButtonRegion
     */
    public void setListener(InvisiButtonRegionListenerI listener){
        this.listener = listener;
    }

    /**
     * Called when a MotionEvent occurs.
     * @param m The MotionEvent that occurred.
     * @return true
     */
    @Override
    public boolean onTouchEvent(MotionEvent m){

        float touchX = m.getX();
        float touchY = m.getY();

        if(touchY > y && touchY < y+sy){
            if(touchX > x && touchX < x+sx){
                if(listener != null){
                    listener.onTouched();
                    listener = null;
                }
            }
        }
        return true;
    }
}
