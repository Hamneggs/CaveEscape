package CaveEscapeCore.GUIViews;

import android.content.Context;
import android.graphics.*;
import android.view.View;

/**
 * Displays a translated and scaled Bitmap.
 *
 * This is another non-OpenGL element.
 */
public class ImageDisp extends View {

    /**
     * The location of the image.
     */
    float x, y;

    /**
     * The size of the image.
     */
    float sx, sy;

    /**
     * The bitmap that is the image.
     */
    Bitmap image;

    /**
     * The Paint instance used to draw the Bitmap.
     */
    Paint paint;


    /**
     * Is the image visible?
     */
    boolean visible;

    /**
     * Constructs the ImageDisplay
     * @param x The intended X-Coordinate of the image.
     * @param y The intended Y-Coordinate of the image.
     * @param sx The X-size of the image.
     * @param sy The Y-size of the image.
     * @param imageID The resource ID of the image to display.
     * @param context The context of the governing Activity.
     */
    public ImageDisp(float x, float y, float sx, float sy, int imageID, Context context){

        super(context);
        this.x = x;
        this.y = y;
        this.sx = sx;
        this.sy = sy;

        Bitmap unscaledImage = BitmapFactory.decodeResource(context.getResources(), imageID);

        image = Bitmap.createScaledBitmap(unscaledImage, (int)sx, (int)sy, true);
        //image = unscaled
        unscaledImage.recycle();


        //Since we may not want to display the image right away, we set
        //visible to false at construction.
        visible = false;

        //Initialize the Paint instance in a manner that filters bitmaps.
        paint = new Paint(Paint.FILTER_BITMAP_FLAG);
    }

    /**
     * Allows the change of the x location of
     * the image display.
     * @param x The new x-location.
     */
    public void setX(float x){
        this.x = x;
        postInvalidate();
    }

    /**
     * Returns the X location of the ImageDisp.
     * @return x
     */
    public float getX(){
        return x;
    }

    /**
     * Allows the change of the y location of
     * the image display.
     * @param y The new y-location.
     */
    public void setY(float y){
        this.y = y;
        postInvalidate();
    }

    /**
     * Returns the Y location of the ImageDisp.
     * @return y
     */
    public float getY(){
        return y;
    }

    /**
     * Allows the changing of the x size
     * of the image display.
     * @param sx The new x-size.
     */
    public void setSizeX(float sx){
        this.sx = sx;
        postInvalidate();
    }

    /**
     * Returns the x size of the image-display.
     * @return sx
     */
    public float getSizeX(){
        return sx;
    }

    /**
     * Allows the changing of the y size
     * of the image size.
     * @param sy The new y size.
     */
    public void setSizeY(float sy){
        this.sy = sy;
        postInvalidate();
    }

    /**
     * Returns the y size of the image-display.
     * @return sy
     */
    public float getSizeY(){
        return sy;
    }

    /**
     * Allows the changing of the Bitmap to be displayed.
     * @param image The new Bitmap.
     */
    public void setImage(Bitmap image){
        this.image = image;
    }

    /**
     * Allows the changing of the visible status.
     * @param visible The new visible status.
     */
    public void setVisible(boolean visible){
        this.visible = visible;
        postInvalidate();
    }

    /**
     * Is the image visible?
     * @return visible
     */
    public boolean isVisible(){
        return visible;
    }

    /**
     * Returns the current Bitmap.
     * @return image
     */
    public Bitmap getImage(){
        return image;
    }

    /**
     * Draws the ImageDisp. This is called by the Android
     * GUI framework.
     * @param canvas The canvas onto which the ImageDisp is
     *               to be drawn.
     */
    public void onDraw(Canvas canvas){
        if(visible){
            canvas.drawBitmap(image, x, y, paint);
        }
    }
}
