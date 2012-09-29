package CaveEscapeGeneral;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

/**
 * This is a button whose appearance is merely some brief, shadowed text.
 * It is animated, however. It flies in from a starting position to
 * to a usable position when first created. Then if this button is pressed,
 * it flashes a highlighting rectangle behind it for a bit, then flies back off
 * screen. If one of its sibling buttons is pressed, then it flies off screen
 * without flashing.
 *
 * This button is intended to be used in a collection of buttons.
 *
 * Also, it is a view, meaning it needs to be refreshed by calling postInvalidate()
 * every frame.
 *
 * Any listeners must take into account that it must tell all other buttons that
 * they have not been clicked.
 */
public class HouseLogo extends View {

    /**
     * Represents the state of animation:
     * Fading in,
     * Pause for awareness,
     * Fading out.
     */
    private enum State{
        fadeIn,
        pause,
        fadeOut,
        done
    }

    /**
     * The Bitmap to store the logo.
     */
    private Bitmap b;

    /**
     * The canvas to draw everything on.
     */
    private Canvas c;

    /**
     * The paint object to draw the background, and the alpha mask.
     */
    private Paint p;

    /**
     * The SurfaceHolder to hold our SurfaceView.
     */
    private SurfaceHolder holder;

    /**
     * How much the alpha needs to change per frame.
     */
    private float alphaChange;

    /**
     * How long are the fades and the pause?
     */
    private int fadeTime, pauseTime;

    /**
     * The color of the overlay(alpha ignored),
     * the color of the background, and
     * the current alpha.
     */
    private int overlayColor, curAlpha;

    /**
     * The dimensions of the screen.
     */
    private int screenWidth, screenHeight;

    /**
     * The State of animation the logo screen is in.
     */
    private State state;

    /**
     * The HouseLogoListener.
     */
    HouseLogoListenerI listener;

    /**
     * The AlphaAnimation used to manage the alpha.
     */
    AlphaAnimation in, during, out;



    /**
     * Constructs the logo.
     * @param context The Context of the application.
     * @param fadeTime How long the fades should last, in milliseconds.
     * @param pauseTime How long the awareness pause should last, in milliseconds.
     * @param overlayColor The color of the alpha overlay. Alpha is ignored.
     */
    public HouseLogo(Context context, int fadeTime, int pauseTime, int overlayColor, int imageID){

        super(context);

        this.fadeTime = fadeTime;

        this.pauseTime = pauseTime;

        this.overlayColor = overlayColor;

        this.state = State.fadeIn;


        //Initialize the screen dimensions, so we can fit the logo image to the screen.
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        screenWidth= metrics.widthPixels;
        screenHeight = metrics.heightPixels;

        b = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(context.getResources(), imageID), screenWidth, screenHeight, true);


        //Initialize the Paint to be anti-aliased.
        p = new Paint(Paint.FILTER_BITMAP_FLAG);

        //Initialize the AlphaAnimation.
        AlphaAnimationListener listener = new AlphaAnimationListener();

        in = new AlphaAnimation(0f, 1f);
        in.setDuration(fadeTime);
        in.setAnimationListener(listener);

        during = new AlphaAnimation(1f, 1f);
        during.setDuration(pauseTime);
        during.setAnimationListener(listener);

        out = new AlphaAnimation(1f, 0f);
        out.setFillAfter(true);
        out.setDuration(fadeTime);
        out.setAnimationListener(listener);


    }

    /**
     * Called when the app comes back into focus.
     */
    public void start(){
        startAnimation(in);
    }

    /**
     * Called when the app goes out of focus.
     */
    public void onPause(){

    }

    /**
     * Is the animation done?
     * @return A boolean representing whether or not the animation has
     *          completed its lifecycle.
     */
    public boolean isDone(){
        return state == State.done;
    }

    @Override
    public void draw(Canvas canvas){
        canvas.drawColor(0xFFFFFFFF);
        canvas.drawBitmap(b, 0, 0, p);
        //canvas.drawBitmap(b, new Rect(0, 0, b.getWidth(), b.getHeight()), new Rect(0, 0, screenWidth, screenHeight), p);

    }

    /**
     * Allows the setting of this HouseLogo's HouseLogoListener.
     *
     * @param listener An object that implements HouseLogoListenerI.
     */
    public void setListener(HouseLogoListenerI listener){

        this.listener = listener;

    }

    private void changeBackgroundColor(int color){
        super.setBackgroundColor(color);
    }

    public class AlphaAnimationListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            switch (state) {

                case fadeIn:
                    state = State.pause;
                    startAnimation(during);
                    break;
                case pause:
                    state = State.fadeOut;
                    startAnimation(out);
                    changeBackgroundColor(0xFFFFFFFF);
                    break;
                case fadeOut:
                    state = State.done;
                    if(listener != null){
                        listener.onDone();
                    }
                    break;
                case done:
                    break;
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }
}
