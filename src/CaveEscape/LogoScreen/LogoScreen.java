package CaveEscape.LogoScreen;

import CaveEscape.CaveEscape.R;
import CaveEscapeGeneral.HouseLogo;
import CaveEscapeGeneral.HouseLogoListenerI;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * Encapsulates the Logo Screen.
 */
public class LogoScreen {

    /**
     * The house Logo
     */
    HouseLogo logo;

    /**
     * Inflates the LogoScreen by creating it, and unlike the MainMenu,
     * adds it to the screen as well.
     *
     * @param context  The context of the Activity.
     * @param activity The Activity of the application, itself.
     * @param listener The HouseLogoListenerI that the HouseLogo should listen to.
     */
    public void inflate(Context context, Activity activity, HouseLogoListenerI listener){

        logo = new HouseLogo(
                context,
                1000,
                500,
                0xFFFFFFFF,
               R.drawable.logo
        );

        activity.addContentView(logo, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        logo.setListener(listener);
        logo.start();
    }

    /**
     * Finalizes the last few things we can do to remove this view
     * without using a layout.
     */
    public void finalizeDeflate(){

        //Disable it.
        logo.setEnabled(false);
        //Destroy all the data it was wanting to draw.
        logo.destroyDrawingCache();
        //Remove all completed animations.
        logo.clearAnimation();
        //And clear its focus.
        logo.clearFocus();

        logo.setVisibility(View.GONE);

        logo = null;

    }


}
