package CaveEscapeCore.GUIViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;

/**
 * This is a more primitive TextView, but at least we
 * can position it easily and give it a shadow!
 * Actually, since we are using API Level 10, we don't have
 * all the more recent enhancements to Android's TextView.
 * Sad, but true.
 *
 * This is also not an OpenGL element. Pretty much everything isn't.
 *
 * @see android.widget.TextView
 */
public class EnhancedTextView extends View{

    /**
     * The text that is displayed.
     */
    private String text;

    /**
     * The location and the text size of
     * this so-called EnhancedTextView.
     */
    private float x, y, textSize;

    /**
     * The color of the text and its shadow.
     */
    private int textColor, shadowColor;

    /**
     * Whether or not the text is underlined.
     */
    private boolean underlined;

    private final boolean serif;
    private int serifColor;
    private final float serifX;
    private final float serifY;
    /**
     * Whether or not the text is centered on the location.
     */
    private Paint.Align alignment;

    /**
     * The radius and offset of the text's shadow.
     */
    private float shadowRadius, shadowX, shadowY;

    /**
     * The Paint instance used to draw the
     * text anti-aliased, and filtered for
     * sub-pixel text sizes.
     */
    Paint paint;

    /**
     * Constructs the EnhancedTextView.
     *
     * @param x The x location of this EnhancedTextView.
     * @param y The y location of this EnhancedTextView.
     * @param textSize The size of this EnhancedTextView's text.
     * @param text The message for this EnhancedTextView to display.
     * @param textColor The color of this EnhancedTextView's text.
     * @param shadowColor The color of the text's shadow.
     * @param shadowRadius The radius of the text's shadow.
     * @param shadowX The x offset of the text's shadow.
     * @param shadowY The y offset of the text's shadow.
     * @param context The Context of the reigning Activity.
     */
    public EnhancedTextView(float x, float y, float textSize, String text, int textColor, int shadowColor, float shadowRadius,
                            float shadowX, float shadowY, boolean underlined, boolean serif, int serifColor, float serifX,
                            float serifY, Paint.Align alignment, Context context){

        //Initialize the parent view.
        super(context);

        //Copy in everything.
        this.x = x;
        this.y = y;
        this.textSize = textSize;

        this.text = text;
        this.textColor = textColor;

        this.shadowColor = shadowColor;
        this.shadowRadius = shadowRadius;
        this.shadowX = shadowX;
        this.shadowY = shadowY;

        this.underlined = underlined;
        this.serif = serif;
        this.serifColor = serifColor;
        this.serifX = serifX;
        this.serifY = serifY;
        this.alignment = alignment;

        //Initialize the Paint instance to anti-alias its works and to
        //prepare for text sizes that make features of the typeface smaller
        //than a single pixel.
        paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);

        //Set up the paint.
        //Set the color of the Paint to that of the text.
        paint.setColor(textColor);
        //Set the size of the text to that specified.
        paint.setTextSize(textSize);
        //If centered is flagged, then we too center the text.
        paint.setTextAlign(alignment);
        //Set the typeface of the text.
        paint.setTypeface(Typeface.SANS_SERIF);
        //Set the underline of the text.
        paint.setUnderlineText(underlined);
        //Prepare the shadow layer.
        paint.setShadowLayer(shadowRadius, shadowX, shadowY, shadowColor);

    }

    /**
     * Called when it's time to draw this EnhancedTextView.
     * Draws the text specified with the color
     * and shadow described.
     *
     * @param canvas The Canvas onto which we should draw our features.
     */
    public void onDraw(Canvas canvas){
        //Draw the serif if need be.
        if(serif){
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(serifColor);
            switch (alignment) {
                case CENTER:
                    //You don't get a serif.
                    break;
                case LEFT:
                    canvas.drawLine(x, y, x+serifX, y, paint);
                    canvas.drawLine(x, y, x, y+serifY, paint);
                    break;
                case RIGHT:
                    canvas.drawLine(x, y, x-serifX, y, paint);
                    canvas.drawLine(x, y, x, y+serifY, paint);
                    break;
            }
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(textColor);
        }
        //Draw the text.
        canvas.drawText(text, x, y, paint);

    }

    /**
     * Returns the text that this EnhancedTextView
     * is set to display.
     *
     * @return text
     */
    public String getText() {
        return text;
    }

    /**
     * Allows the changing of the text that
     * this EnhancedTextView is to display.
     *
     * @param text The new message.
     */
    public void setText(String text) {
        this.text = text;
        postInvalidate();
    }
}
