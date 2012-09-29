package CaveEscapeCore.Player;

import android.content.Context;
import android.graphics.*;
import android.view.MotionEvent;
import android.view.View;

/**
 * This class is a View that merely handles TouchEvents. It is
 * in this sense that it is intended to be used as a "controller"
 * of sorts. This is tailored for control during gameplay.
 */
public class GameplayControllerView extends View {

    /**
     * The Player instance to manipulate.
     */
    Player player;

    /**
     * The location and size of the buttons.
     */
    float x, y, sx, sy;

    /**
     * The bitmap of the button.
     */
    Bitmap image;

    /**
     * The Paint instance that is used to draw the Bitmap.
     */
    Paint paint;

    /**
     * Represents whether or not the button is currently being pressed.
     */
    boolean pressed;

    /**
     * The location of the most recent touch.
     */
    float locX, locY;




    /**
     * Constructs the GameplayControllerView.
     * @param context The context of the reigning Activity.
     * @param player The Player instance to be manipulated.
     */
    public GameplayControllerView(Context context, Player player, float x, float y, float sx, float sy, Bitmap image){

        super(context);

        this.player = player;
        this.x = x;
        this.y = y;
        this.sx = sx;
        this.sy = sy;
        this.image = image;

        paint = new Paint(Paint.FILTER_BITMAP_FLAG);

    }

    @Override
    public void onDraw(Canvas canvas){
        canvas.drawBitmap(image, new Rect(0, 0, image.getWidth(), image.getHeight()), new RectF(x, y, x+sx, y+sy), paint);
    }

    public boolean isPressed(){
        return pressed;
    }

    /**
     * Manipulates the Player based on where the touch event occurred on screen.
     * It's based on fourths.
     * @param m The MotionEvent
     * @return true
     */
    @Override
    public boolean onTouchEvent(MotionEvent m){

        if(m.getAction() == MotionEvent.ACTION_DOWN || m.getAction() == MotionEvent.ACTION_MOVE){

            player.setState(ShipState.straight);

            //Get the physical location of the action.
            locX = m.getX();
            locY = m.getY();

            if(locX > x && locX < x+sx){
                if(locY > y && locY < y+sy){
                    pressed = true;
                    float relX = locX-x-(sx/2);
                    float relY = locY-y-(sy/2);
                    player.setYVelocity(
                            -player.getShip().getStrafeSpeed() *
                                    ((relY) / (sy / 2))
                    );

                    player.setXVelocity(
                            player.getShip().getStrafeSpeed() *
                                    (relX / (sx / 2))
                    );
                }
            }


        }

        if(m.getAction() == MotionEvent.ACTION_UP){
               pressed = false;

        }

        return true;
    }


}
