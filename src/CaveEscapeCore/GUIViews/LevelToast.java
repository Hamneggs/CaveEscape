package CaveEscapeCore.GUIViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;

/**
 * This class represents a custom Toast message that ideally is
 * used to announce gameplay events, such as item collection
 * and level increase.
 * @see android.widget.Toast
 */
public class LevelToast {

    /**
     * The wrapped Toast instance that the LevelToast uses.
     */
    Toast toast;

    /**
     * Returns the message currently assigned to
     * the LevelToast.
     * @return text
     */
    public String getText() {
        return text;
    }

    /**
     * Allows the changing of the message assigned
     * to the LevelToast.
     * @param text The new message.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * The text that the LevelToast displays.

     */
    String text;

    /**
     * Our own custom View that the Toast displays.
     */
    ToastView view;

    /**
     * Constructs the LevelToast.
     * @param text The message to display.
     * @param duration The length of the Toast. Must be Toast.LENGTH_LONG or Toast.LENGTH_SHORT.
     * @param outlineColor The color of the LevelToast's outline.
     * @param fillColor The color of the LevelToast's fill.
     * @param textColor The color of the LevelToast's text.
     * @param context The context of the reigning activity.
     */
    public LevelToast(String text, int duration, int outlineColor, int fillColor, int textColor, Context context){

        this.text = text;

        //Initialize our custom View.
        view = new ToastView(outlineColor, fillColor, textColor, context);

        toast = new Toast(context);

        //Set the view of the Toast to our custom ToastView instance.
        toast.setView(view);

        //Also, let's set the duration of the Toast.
        toast.setDuration(duration);
    }

    /**
     * Displays the LevelToast.
     */
    public void show(){

        toast.show();

    }

    /**
     * Kills off the LevelToast.
     */
    public void kill(){
        toast.cancel();
    }

    /**
     * Our custom View for the Toast. It draws an outlined rectangle with centered text.
     */
    private class ToastView extends View {

        /**
         * The paint object for our View.
         */
        private Paint paint;

        /**
         * The dimensions of the screen in pixels.
         */
        private float screenWidth, screenHeight;

        /**
         * The color of the outline.
         */
        private final int outlineColor;

        /**
         * The color of the fill.
         */
        private final int fillColor;

        /**
         * The color of the text.
         */
        private final int textColor;

        /**
         * Constructs the Toast's View.
         * @param outlineColor The intended outline color.
         * @param fillColor    The intended fill color.
         * @param textColor    The intended text color.
         * @param context      The context of the reigning Activity.
         */
        public ToastView(int outlineColor, int fillColor, int textColor, Context context){

            super(context);

            this.outlineColor = outlineColor;
            this.fillColor = fillColor;
            this.textColor = textColor;

            //Initialize our Paint object.
            paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);

            //Get the dimensions of the screen.
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            screenWidth = metrics.widthPixels;
            screenHeight = metrics.heightPixels;

        }

        public void onDraw(Canvas canvas){

            //First we draw the fill.
            paint.clearShadowLayer();
            paint.setColor(fillColor);
            paint.setStyle(Paint.Style.FILL);
            //paint.setShadowLayer(screenHeight/300f, screenHeight/300f, screenHeight/300f, 0xFF000000);
            canvas.drawRect(new RectF((screenWidth / 2) - (screenWidth / 8),
                    (screenHeight * .75f),
                    (screenWidth / 2) - (screenWidth / 8) + (screenWidth / 4),
                    (screenHeight * .75f) + (screenHeight / 8)), paint);

            paint.setShadowLayer(1, .5f, .5f, 0xFF000000);

            //Next we draw the outline.
            paint.setColor(outlineColor);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth((float) screenHeight / 180.0f);
            canvas.drawRect(new RectF((screenWidth / 2) - (screenWidth / 8),
                    (screenHeight * .75f),
                    (screenWidth / 2) - (screenWidth / 8) + (screenWidth / 4),
                    (screenHeight * .75f) + (screenHeight / 8)), paint);

            //And finally the text.
            paint.setColor(textColor);
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(screenHeight / 20);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(text, (screenWidth/2), (screenHeight*.75f)+(screenHeight/16), paint);

        }
    }



}
