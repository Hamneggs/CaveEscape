package CaveEscapeCore.GUIViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created with IntelliJ IDEA.
 * User: Chuck Finley
 * Date: 7/16/12
 * Time: 6:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class HudBG extends View {

    Paint paint;

    float w, h;

    public HudBG(Context context){
        super(context);
        paint = new Paint();
        paint.setColor(0x88FFFFFF);

        //Store the dimensions of the screen.
        DisplayMetrics m = context.getResources().getDisplayMetrics();
        w = m.widthPixels;
        h = m.heightPixels;

    }

    @Override
    public void onDraw(Canvas canvas){
        //canvas.drawRect()
    }


}
