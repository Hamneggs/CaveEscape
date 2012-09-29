package CaveEscapeCore.GUIViews;

import android.content.Context;
import android.graphics.*;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;

/**
 * An ArrowButton. This was created with the Ship Menu in mind.
 * This is meant to change between ships.
 *
 * This is not an OpenGL element, but rather a View. It uses the Animation
 * API.
 */
public class ArrowButton extends View {

    /**
     * The ArrowButtonListener that this button
     * talks to.
     */
    ArrowButtonListenerI listener;

    /**
     * The location of the button.
     */
    private float x, y;

    /**
     * The size of the button.
     */
    private float sx, sy;

    /**
     * Has the button been pressed?
     */
    boolean pressed;

    /**
     * The button image.
     */
    private Bitmap image;

    /**
     * The TranslateAnimation that occurs every time
     * the button is pressed.
     */
    private TranslateAnimation animation;

    /**
     * The Paint instance used to make the image filtered
     * when being drawn to the canvas.
     */
    private Paint paint;

    /**
     * Constructs the ArrowButton.
     *
     * @param x The X location of the button.
     * @param y The Y location of the button.
     * @param sx The X size of the button.
     * @param sy The Y size of the button.
     * @param dx The distance the button moves in the X direction when clicked.
     * @param dy The distance the button moves in the Y direction when clicked.
     * @param image The button image.
     * @param context The Context of the reigning Activity.
     */
    public ArrowButton(float x, float y, float sx, float sy, float dx, float dy, Bitmap image, Context context){

        super(context);

        this.x = x;
        this.y = y;
        this.sx = sx;
        this.sy = sy;
        this.image = image;

        //The button obviously hasn't been pressed yet.
        pressed = false;

        //Create the animation.
        animation = new TranslateAnimation(0, dx, 0, dy);

        //Set the animation duration to 3/20th of a second.
        animation.setDuration(150);
        //give it a CycleInterpolator so that it returns to
        //where it was after completing the animation.
        animation.setInterpolator(new CycleInterpolator(1));

        //Create the paint to anti-alias and filter the bitmap.
        paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

    }

    /**
     * Draws the Bitmap. Overridden from Android.View.
     *
     * @param canvas The Canvas on to which the Button is drawn.
     *               This Canvas is given to us by the Android runtime.
     * @see android.view.View
     * @see android.graphics.Canvas
     */
    @Override
    public void onDraw(Canvas canvas){
        canvas.drawBitmap(image, new Rect(0, 0, image.getWidth(), image.getHeight()), new RectF(x, y, x+sx, y+sy), paint);
    }

    /**
     * Sets the ArrowButtonListener that this ArrowButton talks to.
     *
     * @param listener The intended ArrowButtonListenerI.
     */
    public void setListener(ArrowButtonListenerI listener){
        this.listener = listener;
    }

    /**
     * Called when the View is touched. Also overridden from View.
     *
     * @param m The MotionEvent that happened when the View was touched.
     * @return True.
     * @see android.view.View
     * @see android.view.MotionEvent
     */
    @Override
    public boolean onTouchEvent(MotionEvent m){

        if(!pressed){

            //Set pressed to true so that
            //the button can only be pressed once
            //per screen contact.
            pressed = true;

            //Get the location of the TouchEvent.
            float touchX = m.getX();
            float touchY = m.getY();

            //If the TouchEvent occurred over the pixels covered by
            //this button, then trigger the animation and if
            //if the listener is not null, tell it that the
            //button was clicked.
            if( touchX > x && touchX < x+sx ){
                if( touchY > y && touchY < y+sy ){
                    //Clear the animation just in case we need to
                    //stop it.
                    clearAnimation();
                    startAnimation(animation);
                    if(listener != null){
                        listener.onClicked();
                    }
                    setPressed(true);
                    performClick();
                }
            }

        }

        //Once the user has lifted their touch, we reset
        //the pressed status of the button.
        if(m.getAction() == MotionEvent.ACTION_UP){
            pressed = false;
        }

        return true;

    }

}
