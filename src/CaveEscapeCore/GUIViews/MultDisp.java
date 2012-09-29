package CaveEscapeCore.GUIViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Draws the current multiplier. As the multiplier grows higher, the text (should)
 * grow larger. Also, at each increase, the number and denomination (e.g. 'x' in
 * 3x) pop to the size given by popScale, and retract over the course of animSpeed
 * time. The animation will cover 20 frames.
 *
 * The animation is done with a Timer, and hence threaded, and daemon at that.
 *
 * This is not an OpenGL item, and is drawn on a canvas given to us through the
 * onDraw() method. This allows the use of a single canvas.
 *
 * Furthermore, since this is a view, it is merely a gui element that can be
 * added to the current display by merely adding it to an activity through
 * addContentView().
 *
 * <b>ANIMATION AND UPDATING REQUIRE SCHEDULED REFRESHES TO BE MADE VISIBLE. THIS
 * IS DONE BY CALLING postInvalidate().</b>
 */
public class MultDisp extends View{

    /**
     * The minimum, maximum, and currently being displayed
     * multiplier.
     */
    private float minMult, maxMult, currentMult;

    /**
     * The minimum, maximum, and current size of the text.
     */
    private float minSize, maxSize, currentSize;

    /**
     * The x and y coordinates of the text.
     */
    private float x, y;

    /**
     * The color of the text.
     */
    private int color;

    /**
     * PopScale: The scale the text rises to each pop.
     * currentPopScale: the current popScale, changed through animation.
     * popScaleUnit: how much the scale has to change to traverse the difference
     *               in scale over 20 frames.
     */
    private float popScale, currentPopScale, popScaleUnit;

    /**
     * The alignment of the text in relation to it's location.
     */
    private Paint.Align alignment;

    /**
     * How long each frame should be for the entire animation to last the
     * length of AnimTime.
     */
    private long psTimeStep;

    /**
     * The Timer used to schedule updates to currentPopScale.
     */
    private Timer scalar;

    /**
     * The Paint instance used to draw on the canvas that we are
     * given in draw().
     */
    private Paint paint;

    /**
     * Is the timer running?
     */
    private boolean timerRunning;

    /**
     * Constructs the Multiplier display.
     *
     * @param startMult The base multiplier. This is
     *                  also used as the minimum multiplier.
     * @param maxMult   The maximum multiplier.
     * @param minSize   The minimum and also base size of the text.
     * @param maxSize   The maximum size of the text.
     * @param x         The x-location of the text on the canvas.
     * @param y         The y-location of the text on the canvas.
     * @param color     The color of the text.
     * @param animSpeed The length of time (in milliseconds) that
     *                  the animation should last.
     * @param popScale  The scale by which the current size of the text is
     *                  scaled each pop.
     */
    public MultDisp(float startMult,
                    float maxMult,
                    float minSize,
                    float maxSize,
                    float x,
                    float y,
                    int color,
                    long animSpeed,
                    float popScale,
                    Paint.Align alignment,
                    Context context
    ){

        super(context);
        this.minMult = startMult;
        this.maxMult = maxMult;
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.x = x;
        this.y = y;
        this.color = color;
        this.popScale = popScale;
        this.alignment = alignment;

        //The initial popScale should be 1 to avoid any issues drawing text before
        //the first pop.
        currentPopScale = 1;

        //The current multiplier at this point is very likely the base multiplier.
        currentMult = startMult;

        //If we are starting at the base multiplier, shouldn't we also start at
        //the base size?
        currentSize = minSize;

        //Derive the change in the current popScale required to
        //traverse the entire difference between popScale and currentScale
        //over 20 frames.
        popScaleUnit = (popScale)/20;

        //Derive the time per frame to traverse 20 frames over the course
        //of animTime.
        psTimeStep = (animSpeed/20);

        //Initialize our Timer.
        scalar = new Timer("Pop handler", true);
        timerRunning = true;

        //Initialize our Paint object.
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    }

    /**
     * Allows the setting of the multiplier. Input is clamped to
     * the range [minMult-maxMult].
     *
     * @param newMult The new multiplier.
     */
    public void setMult(float newMult){

        //Set the current multiplier.
        currentMult = newMult;

        //Check the incoming multiplier.
        if(newMult > maxMult) newMult = maxMult;
        else if(newMult < minMult) newMult = minMult;

        //Calculate the current size.
        currentSize = minSize+( (maxSize-minSize)*( (currentMult)/(maxMult) ) );

        //Initialize the current pop scale.
        currentPopScale = popScale;

        //Schedule the pop.
        if(timerRunning == true)
        scalar.scheduleAtFixedRate(new PopUpdate(), 0, psTimeStep);

    }

    /**
     * Returns the current multiplier.
     *
     * @return currentMult.
     */
    public float getCurrentMult(){
        return currentMult;
    }

    /**
     * Returns the minimum multiplier.
     *
     * @return minMult
     */
    public float getMinMult(){
        return minMult;
    }

    /**
     * Returns the maximum multiplier.
     *
     * @return maxMult.
     */
    public float getMaxMult(){
        return maxMult;
    }

    /**
     * Returns the scale that the text "pops" to when
     * a new multiplier is set.
     *
     * @return popScale.
     */
    public float getPopScale(){
        return popScale;
    }

    /**
     * Draws the display on the given canvas. This allows the use of only one canvas for
     * several elements.
     *
     * @param canvas The canvas to draw the multiplier display on.
     */
    @Override
    public void onDraw(Canvas canvas){

        //Set the color of the paint.
        paint.setColor(color);

        //Text should be filled.
        paint.setStyle(Paint.Style.FILL);

        //Set the font to the good font.
        paint.setTypeface(Typeface.SANS_SERIF);

        //Give the text a shadow layer to increase contrast over
        //any color background, also enhancing edges and making
        //the text more profound.
        paint.setShadowLayer((3*(currentMult/maxMult)+.75f), .5f, .5f, 0xFF000000);

        //Calculate the actual current size by multiplying the
        //current size by the current popScale.
        paint.setTextSize(currentSize*currentPopScale);

        //Set the alignment of the text.
        paint.setTextAlign(alignment);

        //Here we have a formatted string, so the multiple always has 2 decimals following it.
        if(alignment == Paint.Align.CENTER)
            canvas.drawText(String.format("%2.2fx", currentMult), x, y+(currentSize*currentPopScale), paint);
        else
            canvas.drawText(String.format("%2.2fx", currentMult), x, y, paint);


    }

    /**
     * Forces the redrawing of the Multiplier Display.
     */
    private void forceRedraw(){
        postInvalidate();
    }

    /**
     * Schedules the required updates of the Multiplier Display.
     * @param updater The Timer with which the updates are to be scheduled.
     * @param refreshRate The refresh rate.
     */
    public void scheduleUpdates(Timer updater, int refreshRate){
        updater.scheduleAtFixedRate(new MultDispUpdate(), 0, 1000/refreshRate);
    }

    /**
     * Provides the frame refresh functionality. The View is only redrawn
     * when the popScale is greater than 1.
     */
    private class MultDispUpdate extends TimerTask{

        public void run(){
            if(popScale > 1f){
                forceRedraw();
            }
        }
    }

    /**
     * Kills off the Timer thread that the MultDisp is using.
     */
    public void kill(){
        scalar.cancel();
        scalar.purge();
        timerRunning = false;
    }

    /**
     * A single update to the popScale. This iterates popScale back down to 1,
     * a non-transforming scale. Once there, it cancels. Doing things this way means
     * if two pops happen in close proximity, they are both handled accordingly--
     * the first is merely extended.
     */
    public class PopUpdate extends TimerTask{

        /**
         * Increments the current popScale down by the amount of
         * popScaleUnit once each call. If the currentPopScale is 1,
         * we cancel the TimerTask, and purge the active PopUpdate from
         * the timer.
         */
        @Override
        public void run(){
            if(currentPopScale <= 1){
                //Cancel the current scaling...
                super.cancel();
                //And purge the instance of PopUpdate the timer is using.
                scalar.purge();
            }
            else{
                //Increment the current popScale.
                currentPopScale-=popScaleUnit;
            }
        }

    }
}
