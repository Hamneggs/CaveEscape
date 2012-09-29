package CaveEscapeCore.GUIViews;

import android.content.Context;
import android.graphics.*;
import android.view.View;

/**
 * The status bar is a width-wise fractionally filled bar by whose ratio of
 * filled area to not-filled area represents its value in relation to its
 * maximum value. This allows for easy display of game-related values
 * that have any maximum value and a minimum value of zero.
 * This status bar should also have a label.
 *
 * It is drawn by first drawing a fill rectangle that is a fraction of the
 * full length of the status bar, then overlaying it with an outline of the
 * full bar. Finally, the text of the label is drawn over both elements.
 *
 * This is not an OpenGL element, but rather a View that can be added to an
 * activity simply through addContentView().
 *
 * Every time a change occurs to the StatusBar, it forces the redrawing
 * of the View.
 */
public class StatusBar extends View{

    /**
     * The outline's color.
     */
    private int outlineColor;

    /**
     * The text's color.
     */
    private int textColor;

    /**
     * Represents if their is a change in the statusBar, so
     * we can force the redrawing of the element.
     */
    boolean change;

    /**
     * The paint object that does our drawing.
     */
    private Paint paint;

    /**
     * The x and y location of the display. Note that this is not the location
     * of the top a corner of the bar, but of the text above the bar.
     */
    private float x, y;

    /**
     * The size of the bar, the width of its border, and the text size.
     */
    private float sx, sy, borderWidth, textSize;

    /**
     * The text, or more specifically the title/name/moniker of the bar.
     */
    private String text;

    /**
     * The current value being represented, the unit of bar space per unit value,
     * and the maximum value allowed.
     */
    private float val, maxVal;

    /**
     * The Gradient Shader used interpolate between the two fill colors.
     */
    private Shader gradient;

    /**
     * The alignment of the text atop the actual bar.
     */
    Paint.Align alignment;

    /**
     * A status bar listener that we can interact with should
     * it not be null.
     */
    StatusBarListenerI listener;

    /**
     * Constructs the oh god oh god oh god parameters..
     * @param x  The x coordinate of the top a corner of the text.
     * @param y  The y coordinate of the top a corner of the text.
     * @param sx The x size of the bar.
     * @param sy The y size of the bar.
     * @param borderWidth The stroke width of the border of the bar.
     * @param textSize The size of the text.
     * @param fillColorA The first of the fill colors.
     * @param fillColorB The second of the fill colors.
     * @param outlineColor The color of the outline.
     * @param textColor The color of the text.
     * @param context The context of the application.
     * @param alignment The alignment of the text atop the actual bar.
     */
    public StatusBar(
            float x,
            float y,
            float sx,
            float sy,
            float borderWidth,
            float textSize,
            String text,
            int fillColorA,
            int fillColorB,
            int outlineColor,
            int textColor,
            Paint.Align alignment,
            Context context
    ){
        super(context);

        this.outlineColor = outlineColor;
        this.textColor = textColor;
        this.x  = x ;
        this.y  = y ;
        this.sx = sx;
        this.sy = sy;
        this.borderWidth = borderWidth;
        this.textSize = textSize;
        this.text = text;
        this.alignment = alignment;

        //We want the statusBar to be drawn for the first time,
        //so we start out with change being true.
        change = true;

        //Initialize the Paint for use.
        paint = new Paint(Paint.ANTI_ALIAS_FLAG |Paint.SUBPIXEL_TEXT_FLAG);

        //Initialize the LinearGradient shader to lerp between the two fill colors starting
        //from the base location, and extending to the base location plus size. This is the same
        //area ultimately covered by the fill. We account for alignment to aid in appearance.
        switch (alignment) {
            case CENTER:
                gradient = new RadialGradient(x+(sx/2), y+(sy/2), (sx/2),fillColorA, fillColorB, Shader.TileMode.MIRROR);
                break;
            case LEFT:
                gradient = new LinearGradient(x, y, x+sx, y+sy, fillColorA, fillColorB, Shader.TileMode.MIRROR);
                break;
            case RIGHT:
                gradient = new LinearGradient(x+sx, y+sy, x, y, fillColorA, fillColorB, Shader.TileMode.MIRROR);
                break;
        }

    }

    /**
     * Returns the value currently represented by the health bar. This is not the
     * fraction of the health bar remaining, but the value it is representing.
     * @return The value currently represented by the health bar.
     */
    public float getVal(){
        return val;
    }

    /**
     * Set listener.
     */

    public void setListener(StatusBarListenerI listener){
        this.listener = listener;
    }

    /**
     * Allows control of the bar.
     * @param newVal The new value you want to display. Clamped to the range [0-maxVal].
     */
    public void setVal(float newVal){

        if(newVal > maxVal){
            newVal = maxVal;
        }
        else if(newVal < 0){
            newVal = 0;
        }
        val = newVal;
        postInvalidate((int)x, (int)y, (int)(x+sx), (int)(y+sy));

    }

    /**
     * Returns the maximum value representable by this bar.
     * @return the maximum value representable by this bar.
     */
    public float getMaxVal(){
        return maxVal;
    }

    /**
     * Allows the changing of the maximum value representable. If you
     * set the maximum value to below 0, the bullcrap alarm goes off,
     * and nothing is done with the new value.
     * @param newVal The new maximum value.
     */
    public void setMaxVal(float newVal){

        if(newVal < 0){
            return;
        }
        if(newVal < val){
            val = newVal;
        }
        maxVal = newVal;
        postInvalidate();

    }

    /**
     * Allows for the reshaping of the bar. Negative sizes are ignored.
     * @param sx The new x dimension of the bar.
     * @param sy The new y dimension of the bar.
     */
    public void setSize(float sx, float sy){

        if(sx <= 0 || sy <= 0){
            return;
        }
        this.sx = sx;
        this.sy = sy;
        postInvalidate();

    }

    /**
     * Draws the status bar.
     *
     * @param canvas The canvas of the (Surface)view that we are drawing
     *               the bar to.
     */
    @Override
    public void onDraw(Canvas canvas){

        //The fill is the bottom-most element, so it is drawn first.
        drawFill(canvas);

        //We draw the outline over the fill so the outline isn't
        //partially eclipsed by the fill and made super ugly.
        drawOutline(canvas);

        //Draw the text over them all for maximum compactness
        //and readability.
        drawText(canvas);


        if(listener != null && val <= 0){
            listener.onDepleted();
        }


    }

    /**
     * Draws the text of the Status Bar. First (underneath) we draw a shadow--
     * a translucent black rectangle under the text. Next, we draw the
     * text itself.
     *
     * @param canvas The Canvas to draw this text on.
     */
    private void drawText(Canvas canvas){

        //Set the color to the text color.
        paint.setColor(textColor);

        //Text is a 2d shape--using stroke would ruin the typography.
        paint.setStyle(Paint.Style.FILL);

        paint.setTextAlign(alignment);

        //This font looks pretty good, and is standard on all Android devices.
        paint.setTypeface(Typeface.SANS_SERIF);

        //Allows for nicer text details.
        paint.setSubpixelText(true);

        //Using a shadow layer with text greatly enhances contrast, and hence
        //makes the text easier to read and overall prettier.
        paint.setShadowLayer(1, .5f, .5f, 0xFF000000);

        //Set the text to the expected size.
        paint.setTextSize(textSize);

        //Draw the text at the expected location, based on
        //the specified text alignment.
        switch (alignment) {
            case CENTER:
                canvas.drawText(text, x+(sx/2), y, paint);
                break;
            case LEFT:
                canvas.drawText(text, x, y, paint);
                break;
            case RIGHT:
                canvas.drawText(text, x+sx, y, paint);
                break;
        }
    }

    /**
     * Draws the outline of the StatusBar. This is essentially a non-filled
     * rectangle of stoke width [borderWidth] and [or, og, ob] color.
     *
     * @param canvas The Canvas on which to draw this outline.
     */
    private void drawOutline(Canvas canvas){

        //Set the color to the proper values.
        paint.setColor(outlineColor);

        //We want the shadow of the outline to be very faint,
        //so we give it a very small offset, and a grey color.
        //This has the added bonus of casting a luminous effect
        //onto the fill.
        paint.setShadowLayer(2, .5f, .5f, 0xFFAAAAAA);

        //Since this is merely an outline, stroke weight is essential.
        paint.setStrokeWidth(borderWidth);

        //Once again, since this an outline, we need to use stroke.
        paint.setStyle(Paint.Style.STROKE);

        //Draw the rectangle.
        canvas.drawRect(new RectF(x, y, x + sx, y + sy), paint);

    }

    /**
     * Draws the fill of the StatusBar. This is just a rectangle whose
     * horizontal width is a fraction of the total width, and whose color
     * is [fr, fg, fb].
     * @param canvas The Canvas on which to draw the fill rectangle.
     */
    private void drawFill(Canvas canvas){

        //First we give our Paint instance a shader to use. Also, using
        //this means we don't have to set the color.
        paint.setShader(gradient);

        //With the interior of the bar, we need not to stroke either.
        paint.setStyle(Paint.Style.FILL);

        //The fill should appear denser than the rest of the features
        //of the bar, so we give it a bit more.
        paint.setShadowLayer(3f, 1, 1, 0xFF000000);

        //Draw the fill, which is simply a rectangle.
        //How this is done though relies on the alignment.
        switch (alignment) {
            case CENTER:
                canvas.drawRect(new RectF(( x+(sx/2))-( (sx*(val/maxVal))/2), y, ( x+(sx/2))+( (sx*(val/maxVal))/2), y+sy), paint);
                break;
            case LEFT:
                canvas.drawRect(new RectF(x, y, x + ( sx*(val/maxVal) ), y+sy), paint);
                break;
            case RIGHT:
                canvas.drawRect(new RectF((x+sx)-( sx*(val/maxVal) ), y, x+sx, y+sy), paint);
                break;
        }

        //Since we don't want to continue using the linear gradient shader
        //to draw the other elements, we set the Paint's shader to null.
        paint.setShader(null);

    }
}
