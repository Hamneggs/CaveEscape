package CaveEscapeCore.GUIViews;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

/**
 * A theatrical fade in. Draws an overlay of a solid color whose alpha either
 * increased or decreased based on the type of fade every call to onDraw().
 *
 * This is not an OpenGL element, but rather a View. It uses the Animation
 * API.
 *
 * @see android.view.animation.Animation
 */
public class Fade extends View {

    /**
     * This enum signals whether the Fade fades in, or fades out.
     */
    public enum Type{
        IN,
        OUT,
    }

    /**
     * The color of the fade.
     */
    int color;

    /**
     * Animation used for animating the alpha change of the mask.
     */
    private AlphaAnimation animation;

    /**
     * This Fade's Type.
     */
    private Type type;

    /**
     * A Listener.
     */
    private FadeListenerI listener;

    /**
     *
     * @param color The color of the fade overlay.
     * @param time The length of time this fade should last in milliseconds.
     * @param type The type of fade.
     * @param context The context of the Activity drawing this element.
     */
    public Fade(int color, long time, Type type, Context context){

        super(context);

        this.color = color;

        //Store the Type of this Fade for querying purposes.
        this.type = type;

        //Create the AlphaAnimation based on the Type of the fade.
        if(type == Type.IN){
            animation = new AlphaAnimation(1, 0);
        }
        else if(type == Type.OUT){
            animation = new AlphaAnimation(0, 1);
        }

        //Set the animation's time.
        animation.setDuration(time);

        //And make it maintain changes after finishing.
        animation.setFillAfter(true);

        //But make sure it doesn't display before being triggered.
        animation.setFillBefore(false);

        //Finally give it a listener to talk to.
        animation.setAnimationListener(new MyAnimationListener());

    }

    /**
     * Triggers the fade. By calling this the fade is signaled to be drawn,
     * and, if not done, to be incremented alpha-wise.
     */
    public void trigger(){
        startAnimation(animation);
    }

    public void setFadeListener(FadeListenerI listener){

        this.listener = listener;

    }

    /**
     * Returns the AlphaAnimation currently being used.
     *
     * @return animation
     */
    public AlphaAnimation getAnimationState(){
        return animation;
    }

    /**
     * Returns the type of the Fade.
     *
     * @return the type of the Fade.
     */
    public Type getType(){
        return type;
    }

     /**
     * Returns whether or not the fade is done.
     *
     * @return Whether or not the fade is done.
     */
    public boolean isDone(){
        return animation.hasEnded();
    }

    /**
     * Returns whether or not the fade has been triggered.
     *
     * @return Whether or not the fade has been triggered.
     */
    public boolean hasBeenTriggered(){
        return animation.hasStarted();
    }

    /**
     * This is called by the GUI framework.
     * It simply draws an overlay the color of the Fade.
     *
     * Nothing is drawn if the Fade has not yet been triggered.
     *
     * @param canvas The Canvas on which to draw the fade.
     */
    @Override
    public void onDraw(Canvas canvas){

        if(animation.hasStarted())
            canvas.drawColor(color);

    }

    private class MyAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if(listener != null){
                listener.onComplete();
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

}
