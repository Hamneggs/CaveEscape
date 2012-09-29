package CaveEscapeCore.GUIViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * A TextButton is a button that is merely text, and should be put atop
 * another element. It uses the Animation APIs for, well, animation.
 * No forced redrawing is necessary.
 * Furthermore, it is best used in a group, as it has animation
 * that is dependent on whether or not it was chosen, or told to go away.
 *
 * Original locations should not be off-screen. Only on-screen portions of
 * the button at the start of the animation will be rendered over the
 * course of the animation.
 *
 * Animation Overview:
 * - The button flies in from an original location to a usable location.
 *   When selected, it flashes by using an AlphaAnimation, then begins its
 *   exit animation, flying back to its origin.
 * - If part of a group, and listeners have been set up accordingly,
 *   if another button was selected, the TextButton will immediately fly
 *   back to its origin.
 *
 * @see android.view.animation.Animation
 */
public class TextButton extends View {

    /**
     * This enum represents the state of the button:
     *
     * Invisible: This is before the button has crawled in, and after
     *            it has crawled out.
     *
     * Entering: The button is coming onto the screen.
     *
     * Usable: The button is in a state that the user should
     *         click it.
     *
     * exiting: If the button was clicked, it remains on screen
     *          for a while with a flashing highlight, then slides
     *          to which it came.
     *          If the button was not clicked, it
     *          immediately retreats.
     *
     */
    public enum State{
        invisible,
        entering,
        usable,
        exiting,
        selectionAnim,
    }

    /**
     * The physical location of the button on the screen when
     * in the usable state.
     */
    private float x, y;

    /**
     * The physical location of the button before entering and
     * after exiting.
     */
    private float ox, oy;

    /**
     * The physical size of the button on the screen. Note that
     * this is not the size of the text, but the size of the landing
     * pad.
     * This is the size reported in onMeasure().
     */
    private float sx, sy;

    /**
     * The current location.
     */
    private float curx, cury;

    /**
     * The size of the text.
     */
    private float textSize;

    /**
     * The color of the text.
     */
    private int textColor;

    /**
     * The animation time--or rather, how many frames to take when
     * moving the button to and from the starting location.
     * The selection flashing is exactly half as long as this value.
     */
    private int animTime;


    /**
     * The text that displays on the button.
     */
    private String text;

    /**
     * A boolean whether or not that the button is pressed.
     */
    private boolean thisButtonPressed;

    /**
     * A boolean representing whether or not the button should
     * begin its exit animation.
     */
    private boolean exit;

    /**
     * A boolean whether or not this button is done with its
     * exit animation.
     */
    private boolean done;

    /**
     * The State the button is currently in.
     */
    private State state;

    /**
     * The Paint instance used for drawing the button.
     */
    Paint paint;

    /**
     * Alignment.
     */
    Paint.Align align;

    /**
     * The Translation animation used in moving the buttons.
     * This offers smoother movement.
     */
    TranslateAnimation entering, exiting;

    /**
     * An animation for the selection as well.
     */
    //AlphaAnimation selectionAnimation;

    /**
     * The context of the app.
     */
    Context context;

    public TextButton(float x,
                      float y,
                      float ox,
                      float oy,
                      float sx,
                      float sy,
                      float textSize,
                      int textColor,
                      int animTime,
                      String text,
                      Context context){

        super(context);

        this.x              = x;
        this.y              = y;
        this.ox             = ox;
        this.oy             = oy;
        this.sx             = sx;
        this.sy             = sy;
        this.textSize       = textSize;
        this.textColor      = textColor;
        this.animTime       = animTime;
        this.text           = text;
        this.context        = context;

        //Initialize the Paint instance for drawing.
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setSubpixelText(true);
        align = Paint.Align.LEFT;
        paint.setTextAlign(align);


        //Initialize the current location to the starting location.
        curx = ox;
        cury = oy;

        //Initialize the state to invisible.
        state = State.invisible;

        //Initialize done to false.
        done = false;

        //Since nothing has been pressed at this point, we set both press booleans
        //to false.
        thisButtonPressed  = false;
        exit = false;

        //Make this button clickable.
        setClickable(true);

        //Initialize the translation.
        entering = new TranslateAnimation(Animation.ABSOLUTE, 0, Animation.ABSOLUTE, x-ox, Animation.ABSOLUTE, 0, Animation.ABSOLUTE, y-oy);

        //Give the animation a duration time.
        entering.setDuration(animTime);

        //Give it a shiny new AccelerateDecelerateInterpolator for smoothness.
        //entering.setInterpolator(new AccelerateDecelerateInterpolator());

        //Tell the animation to not retain the transformation after the animation is finished.
        entering.setFillAfter(false);

        //Do it all again with the exit animation, but since we don't want the buttons to slow down (wastes time) we
        //use merely an AccelerateInterpolator.
        //use merely an AccelerateInterpolator.
        exiting = new TranslateAnimation(Animation.ABSOLUTE, 0, Animation.ABSOLUTE, ox-x, Animation.ABSOLUTE, 0, Animation.ABSOLUTE, oy-y+textSize);
        exiting.setDuration((int)(.8*animTime));
        //exiting.setInterpolator(new AccelerateInterpolator());
        exiting.setFillAfter(false);

        //And now for the exit animation. This is a bit different, since it is not
        //a TranslationAnimation. We initialize it to last a 32nd of the length
        //of the other animations, but repeat 8 times. This allows it to last a
        //quarter longer than the others, and still give the animation a subtle
        //congruency.

        //Initialize the AlphaAnimation to go from full to none.
        /*selectionAnimation = new AlphaAnimation(1, 0);

        //Give it the appropriate length.
        selectionAnimation.setDuration(((int)(.8*animTime))/32);

        //make it reverse each repetition.
        selectionAnimation.setRepeatMode(Animation.REVERSE);

        //Make it repeat 8 times.
        selectionAnimation.setRepeatCount(8);

        //And tell it not to apply the changes done by the animation after completion.
        selectionAnimation.setFillAfter(false);  */


    }

    /**
     * Returns the x-coordinate of this textButton.
     *
     * @return x
     */
    public float getX() {
        return x;
    }

    /**
     * Sets the x-coordinate of this textButton.
     *
     * @param x The new x-location.
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Returns the y-coordinate of this textButton.
     *
     * @return y
     */
    public float getY() {
        return y;
    }

    /**
     * Sets the y-coordinate of this textButton.
     *
     * @param y The new y-location.
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * Returns the x size of this button.
     *
     * @return sx
     */
    public float getSx() {
        return sx;
    }

    /**
     * Allows the changing of the size of this button.
     *
     * @param sx The new x size.
     */
    public void setSx(float sx) {
        this.sx = sx;
    }

    /**
     * Returns the y size of this button.
     *
     * @return sy
     */
    public float getSy() {
        return sy;
    }

    /**
     * Allows the changing of the size of this button.
     *
     * @param sy The new y size.
     */
    public void setSy(float sy) {
        this.sy = sy;
    }

    /**
     * Returns the text that was assigned to this button to display.
     *
     * @return text
     */
    public String getText() {
        return text;
    }

    /**
     * Allows control of the text of the button.
     *
     * @param text The new button text.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Returns the size of the text.
     *
     * @return textSize
     */
    public float getTextSize() {
        return textSize;
    }

    /**
     * Allows the changing of the size of the text.
     *
     * @param textSize The new text size.
     */
    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    /**
     * Returns the length of the animation.
     *
     * @return animTime
     */
    public int getAnimTime(){
        return animTime;
    }

    /**
     * If a button was pressed, but it was not this one,
     * call this to tell the button to go immediately into
     * the exiting animation.
     */
    public void signalExit(){
        exit = true;
        state = State.exiting;
        startAnimation(exiting);
        postInvalidate();
    }

    /**
     * Returns whether or not the button is finished with
     * its exit animation.
     *
     * @return done
     */
    public boolean isDone(){
        return done;
    }

    /**
     * Begins the button's entrance.
     */
    public void activate(){
        state = State.entering;
        startAnimation(entering);
    }

    /**
     * Allows the setting of the alignment.
     */
    public void setAlign(Paint.Align align){
        this.align = align;
        paint.setTextAlign(align);
    }

    /**
     * Tells whether or not the button has been activated.
     *
     * @return state != State.invisible
     */
    public boolean isActive(){
        return state != State.invisible;
    }

    /**
     * Tells whether or not the button can be clicked.
     *
     * @return state == State.usable
     */
    @Override
    public boolean isClickable(){
        return state == State.usable;
    }

    /**
     * Draws the button. This is where all animation is done,
     * and where state is decided.
     *
     * @param canvas The canvas onto which the button is to be drawn.
     */
    @Override
    public void onDraw(Canvas canvas){

        switch (state) {

            case invisible:

                //Don't do shit if it's invisible.

                break;

            case entering:

                //Draw the text with shadow layer.
                drawTextWithShadow(canvas, paint);



                if( entering.hasEnded() ){
                    state = State.usable;
                    clearAnimation();
                    curx = x;
                    cury = y;
                }
                break;

            case usable:

                //Draw the text with shadow layer.
                drawTextWithShadow(canvas,paint);

                //If the button is told that it has been pressed, go
                //to the exit animation state.
                if(thisButtonPressed && !exit){
                    state = State.selectionAnim;
                    //startAnimation(selectionAnimation);
                }
                //Otherwise, just exit normally.
                else if(exit){
                     state = State.exiting;
                    setAnimation(exiting);
                    exiting.startNow();
                }
                break;

            case selectionAnim:

                drawTextWithShadow(canvas, paint);

                //if(selectionAnimation.hasEnded()){
                //    state = State.exiting;
                //    clearAnimation();
                //    startAnimation(exiting);
                //
                //}
                break;

            case exiting:

                drawTextWithShadow(canvas, paint);


                //if the button has moved back to the original spot,
                //flag the animation as done. Also make it invisible
                //again, so we don't have to see any remnants.
                if( exiting.hasEnded() ){
                    done = true;
                    curx = ox;
                    cury = oy;
                    state = State.invisible;
                }
                break;
        }
    }

    private void drawTextWithShadow(Canvas c, Paint p){

        //Give the text a shadow layer because I'm a good person.
        p.setShadowLayer(1f, .5f, .5f, 0xFF000000);

        //Set the color of the text.
        p.setColor(textColor);

        //Set the size of the text.
        p.setTextSize(textSize);

        //Draw the text at the current position. We add textSize
        //since text is drawn atop the coordinates given.
        c.drawText(text, curx, cury+textSize, paint);

    }

    /**
     * In the event that there is a touch event, this method
     * takes the original X and Y location of the touch and
     * compares it with the location of the button. If the
     * touch occured on this button, then this button's
     * listener is called.
     * This allows for this button to be notified that it
     * was pressed, and then later in the Listener, notify
     * all the other buttons that they missed out.
     *
     * @param event The MotionEvent.
     * @return If this button was clicked or not.
     * @see android.view.MotionEvent
     */
    @Override
    public boolean onTouchEvent(MotionEvent event){

        float touchX = event.getX();
        float touchY = event.getY();

        if( touchX > x && touchX < x+sx ){
            if( touchY > y && touchY < y+sy ){
                setPressed(true);
                performClick();
                thisButtonPressed = true;
                postInvalidate();
                return true;
            }
        }
        return false;
    }
}
