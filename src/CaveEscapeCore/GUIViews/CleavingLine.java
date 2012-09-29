package CaveEscapeCore.GUIViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Is a cleaving line. What this means is that it is a line that closes
 * in from either side of the screen.
 *
 * This is a non-OpenGL element, and follows my view-update framework.
 */
public class CleavingLine extends View {

    /**
     * The animation state of the CleavingLine.
     */
    private enum State{
        //The line is not drawn.
        invisible,
        //The line is cleaving, or rather,
        //closing in from either side.
        cleaving,
        //The line is whole, and is no longer
        //drawn as two lines, but a single one.
        whole
    }

    /**
     * The Y-location of the screen-wide cleaving line.
     */
    float y;

    /**
     * The thickness of this line.
     */
    float thickness;

    /**
     * The color of the line.
     */
    int color;

    /**
     * The change in the x value per update, and the current x value.
     */
    float changeX, curX;

    /**
     * The dimensions of the screen.
     */
    int screenWidth, screenHeight;

    /**
     * The length of the animation, in milliseconds.
     */
    int animSpeed;

    /**
     * The current State of the CleavingLine's animation.
     */
    State state;

    /**
     * The paint instance used to draw the Cleaving line.
     */
    Paint paint;

    /**
     * The CleaveListener that this cleaving line talks to. Can
     * be null.
     */
    CleaveListenerI listener;

    /**
     * Is the CleavingLine active? Activation begins the animation
     * and visibility of the CleavingLine.
     */
    boolean active;

    /**
     * Is the CleavingLine's animation done?
     */
    boolean done;

    /**
     * Constructs the Cleaving line.
     *
     * @param y The Y location of the new CleavingLine.
     * @param thickness The thickness of the CleavingLine.
     * @param color The color of the CleavingLine.
     * @param animSpeed The length in time of the CleavingLine's animation, in milliseconds.
     * @param context The context of the reigning Activity.
     */
    public CleavingLine(float y, float thickness, int color, int animSpeed, Context context){

        super(context);

        this.y = y;
        this.thickness = thickness;
        this.color = color;
        this.animSpeed = animSpeed;

        //Initialize the screen dimensions.
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        screenWidth= metrics.widthPixels;
        screenHeight = metrics.heightPixels;

        //Since animation has not begun, the current X value is zero.
        curX = 0;

        //The animation has not been activated yet.
        active = false;

        //And the animation is certainly not yet done.
        done = false;

        //Since we haven't activated the CleavingLine, it begins in an
        //invisible, un-drawn state.
        state = State.invisible;

        //Initialize our Paint to be nice and pretty.
        paint = new Paint();


    }

    /**
     * Activates the CleavingLine. This makes it visible, and begins
     * its animation.
     */
    public void activate(){
        active = true;
        state = State.cleaving;
    }

    /**
     * Deactivates the CleavingLine. This returns it to being invisible,
     * but does not reset the animation.
     */
    public void deactivate(){
        state = State.invisible;
        active = false;
    }

    /**
     * Resets the CleavingLine, meaning it is deactivated, and
     * resets the animation.
     */
    public void reset(){
        active = false;
        done = false;
        state = State.invisible;
        curX = 0;
    }

    /**
     * Returns whether or not the CleavingLine is active.
     *
     * @return active.
     */
    public boolean isActive(){
        return active;
    }

    /**
     * Returns whether or not the animation is done.
     *
     * @return done
     */
    public boolean isDone(){
        return done;
    }

    /**
     * Returns the current X value of the animation.
     *
     * @return curX.
     */
    public float getCurrentX(){
        return curX;
    }

    /**
     * Returns the Y location of the CleavingLine.
     *
     * @return y
     */
    public float getY(){
        return y;
    }

    /**
     * Sets the Y location of the CleavingLine.
     *
     * @param y The new Y location.
     */
    public void setY(float y){
        this.y = y;
    }

    /**
     * Returns the thickness of the CleavingLine.
     *
     * @return thickness
     */
    public float getThickness(){
        return thickness;
    }

    /**
     * Sets the thickness of the CleavingLine.
     *
     * @param thickness The new line thickness.
     */
    public void setThickness(float thickness){
        this.thickness = thickness;
    }

    public void setListener(CleaveListenerI listener){
        this.listener = listener;
    }

    /**
     * Schedules the required updates to the animation of the line.
     * This allows the use of a single Timer to update all the GUI
     * elements.
     *
     * @param updater The Timer to be used as the updater.
     * @param refreshRate The intended refresh rate.
     */
    public void scheduleUpdates(Timer updater, int refreshRate){
        changeX = ((.5f*screenWidth)/(animSpeed/5));
        updater.scheduleAtFixedRate(new CleavingLineUpdate(), 0, 1000/120);
    }

    /**
     * Draws the line.
     *
     * @param canvas The canvas onto which the line should be drawn.
     */
    public void onDraw(Canvas canvas){

        switch (state) {

            //At this state the line is invisible, and the minimum amount of work
            //is done.
            case invisible:
                break;

            //At this state the line is cleaving, meaning it is completing itself
            //coming in from both sides to meet in the middle.
            case cleaving:
                paint.setShadowLayer(3, .5f, .5f, 0xFF000000);
                paint.setColor(color);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawRect(new RectF(0, y, curX, y+thickness), paint);
                canvas.drawRect(new RectF(screenWidth, y, screenWidth-curX, y+thickness), paint);
                break;


            //At this state the line is whole. We draw a single rectangle at this state
            //so the shadow layer doesn't draw a shadow down the center of the line where
            //the two separate rectangles meet.
            case whole:
                paint.setShadowLayer(3, .5f, .5f, 0xFF000000);
                paint.setColor(color);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawRect(new RectF(0, y, screenWidth, y+thickness), paint);
                break;

        }
    }

    /**
     * Calls View.invalidate() over the area that the CleavingLine covers. This redraws
     * the pixels that the line actually covers, rather than the whole screen.
     */
    private void forceRedraw(){
        postInvalidate();
    }

    /**
     * This class provides a TimerTask that provides the
     * update functionality.
     */
    class CleavingLineUpdate extends TimerTask {

        public void run(){

            switch (state) {

                //No animation updates or redraws are required when invisible.
                case invisible:
                    break;

                //When cleaving, we must increment the current x value and force
                //the redrawing of the line.
                case cleaving:
                    curX += changeX;
                    if(curX >= .5f*screenWidth){
                        state = State.whole;
                        done = true;
                        if(listener != null){
                            listener.onComplete();
                        }
                    }
                    forceRedraw();
                    break;

                //When the line is whole, we don't want to change any of the
                //animation values, so we merely force the redrawing of
                //the element.
                case whole:
                    forceRedraw();
                    break;

            }
        }
    }


}
