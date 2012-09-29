package CaveEscapeCore.GUIViews;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

/**
 * Since one View covers the entire screen,
 * a TouchEvent will not reach any View behind the front one.
 * This class exists to live in front of two or more Views,
 * and then delegate any MotionEvents down to them.
 * @see android.view.MotionEvent
 * @see android.view.View
 * @see CaveEscapeCore.GUIViews.ArrowButton
 */
public class ViewMotionEventDistributor extends View {

    /**
     * The contained Views.
     */
    private View[] views;

    /**
     * Constructs the ArrowButton pair.
     * @param context The context of the reigning Activity.
     * @param viewsToAdd Any Views that should be part of the distribution.
     */
    public ViewMotionEventDistributor(Context context, View ... viewsToAdd){

        //Initialize the parent View.
        super(context);

        views = viewsToAdd;

    }

    /**
     * Called when a TouchEvent occurs atop this view. Technically
     * we just pass this MotionEvent down to the ArrowButtons' onTouchEvent().
     * @param m The MotionEvent that occurred.
     * @return true
     */
    @Override
    public boolean onTouchEvent(MotionEvent m){

        //If the TouchEvent occurred over the pixels covered by
        //this button, then trigger the animation and if
        //if the listener is not null, tell it that the
        //button was clicked.
        for(View view : views){
            view.onTouchEvent(m);
        }

        //Return true to fit the signature of onTouchEvent(MotionEvent m).
        return true;
    }
}
