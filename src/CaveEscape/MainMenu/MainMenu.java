package CaveEscape.MainMenu;

import CaveEscape.CaveEscape.R;
import CaveEscape.MainMenu.GLES10.MenuGLSurfaceView;
import CaveEscapeCore.CoreGameplay.GameplayMode;
import CaveEscapeCore.CoreGameplay.GameplayModeListenerI;
import CaveEscapeCore.CoreGameplay.GameplayModeObserver;
import CaveEscapeCore.GUIViews.*;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Timer;

public class MainMenu{

    /**
     * The actual buttons used in the menu.
     * timeAttack: This starts the game in time trial mode.
     * classic:    This starts the game in classic mode.
     * survival:   This starts the game in survival mode.
     *
     * @see CaveEscapeCore.CoreGameplay.GameplayMode
     */
    TextButton timeAttack, classic, survival;

    /**
     * The button listeners for each button.
     */
    TextButtonListener taListener, cListener, sListener;

    /**
     * The GamplayModeObserver, so we can set the GameplayMode
     * from within the listener.
     * @see GameplayModeObserver
     * @see TextButtonListener
     */
    GameplayModeObserver observer;

    /**
     * The CleavingLines used as serifs to the title text.
     */
    CleavingLine topCL, bottomCL;

    /**
     * The Timer used for animating the CleavingLines.
     */
    Timer clUpdater;

    /**
     * The ImageDisp that displays the title text.
     */
    ImageDisp titleText;

    /**
     * The sound engine instance that we use to play the menu sounds.
     */

    /**
     * The OpenGL SurfaceView used to display the
     * background terrain.
     */
    MenuGLSurfaceView terrainView;

    /**
     * The several fades used during menu use.
     * fadeIn:    Used when the menu is beginning.
     * fadeOutTT: Used when the menu is fading out to
     *            Time Trial mode.
     * fadeOutC:  Used when the menu is fading out to
     *            Classic mode.
     * fadeOutS:  Used when the menu is fading out to
     *            Survival mode.
     *
     * @see CaveEscapeCore.CoreGameplay.GameplayMode
     */
    Fade fadeIn, fadeOutTA, fadeOutC, fadeOutS;

    public MainMenu(){

    }

    public void inflate(Context context, Activity activity, GameplayModeListenerI listener, MainMenuListenerI listenerI){

        //Get the dimensions of the screen.
        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        int w = metrics.widthPixels;
        int h = metrics.heightPixels;

        //Set up the OpenGL terrain view.
        terrainView = new MenuGLSurfaceView(context);

        //Initialize the buttons.
        timeAttack = new TextButton(
                (w/12)*.35f,      //Since the time attack button is the left-most button, it is
                // at the left.
                ((h/10)*7)+(h/256),//It will set at the bottom cleaving line.
                (w/12)*.35f,            //This button has an original location that flies it in from the left.
                h-(h/13),     //No height change is needed for the animation,
                // so it starts at the same Y location.
                (w/12) * 3,   //The button needs to have a large landing pad, so it is as wide as the
                //space between buttons,
                (h/8),        //and as tall as the cleaving line.
                h/13,           //The button will have a text height of 25 px.
                0xFFFFFFFF,   //The text will be a grey-ish maroon.
                1000,         //The animation will last 1.2 seconds.
                "Time Attack",//The text of the button.
                context);        //The context of the reigning Activity.

        classic = new TextButton(
                (w/12) * 5.25f,   //This button will be near the center of the screen vertically.
                ((h/10)*7)+(h/256),//This button will also be on the lower CleavingLine.
                (w/12) * 5.25f,   //This button flies from the bottom, so it has the same X location.
                h-(h/13),        //The starting Y location, towards the bottom of the screen.
                (w/12) * 3,     //The width of the button's landing pad.
                (h/8),          //The height of the button's landing pad/
                h/13,           //Since the button's at the center, we make it bigger.
                0xFFFFFFFF,   //The same maroon color.
                1000,         //1.2 seconds again.
                "Classic",    //"Classic" will be the text of this text button.
                context);        //The context of the reigning Activity.

        survival = new TextButton(
                (w/12) * 9.75f,   //This is the right-most button.
                ((h/10)*7)+(h/256),    //Same Y-location as the others.
                (w/12) * 9.75f,//Starts at the right side,
                h-(h/13),    //and at the same height.
                (w/12) * 3,   //Landing pad X size.
                (h/8),        //Landing pad Y size.
                h/13,           //Text size.
                0xFFFFFFFF,   //Maroon.
                1000,         //1.2 seconds.
                "Survival",   //Duh.
                context);        //The context of the reigning Activity.

        //Now we need to initialize the fades.
        fadeIn = new Fade(
                0xFFFFFFFF,    // White.
                1250,          // 1.5 second animation time.
                Fade.Type.IN,  // Fades from full to zero alpha.
                context);         // App context.

        //Add a listener to the Fade-In that kills it off after it completes.
        fadeIn.setFadeListener(new FadeInListener());

        fadeOutTA = new Fade(
                0xFFFFFFFF,    // White.
                1000,          // 1 second animation time.
                Fade.Type.OUT, // Fades from zero to full alpha.
                context);         // App context.

        fadeOutC = new Fade(
                0xFF000000,    // Black.
                1000,          // 1 second animation time.
                Fade.Type.OUT, // Fades from zero to full alpha.
                context);         // App context.

        fadeOutS = new Fade(
                0xFF000000,    // Black.
                1000,          // 1 second animation time.
                Fade.Type.OUT, // Fades from zero to full alpha.
                context);         // App context.

        fadeOutTA.setFadeListener(new FadeOutListener(listenerI));
        fadeOutC.setFadeListener(new FadeOutListener(listenerI));
        fadeOutS.setFadeListener(new FadeOutListener(listenerI));

        //Now for each listener to be aware of every button, we must
        //package all the buttons in an ArrayList.
        ArrayList<TextButton> buttons = new ArrayList<TextButton>();
        buttons.add(timeAttack);
        buttons.add(classic);
        buttons.add(survival);

        //We also need to create a GameplayModeObserver for the listeners,
        //so we can have a central reference to the user's game mode choice.
        observer = new GameplayModeObserver(listener);

        //Initialize the listeners.
        taListener = new TextButtonListener( buttons, 0, GameplayMode.TimeAttack, observer, fadeOutTA);
        cListener = new TextButtonListener(  buttons, 1, GameplayMode.Classic,    observer, fadeOutC);
        sListener = new TextButtonListener(  buttons, 2, GameplayMode.Survival,   observer, fadeOutS);

        //Now we must add the listeners to the buttons.
        timeAttack.setOnClickListener(taListener);
        classic.setOnClickListener(cListener);
        survival.setOnClickListener(sListener);

        //Now must initialize the CleavingLines.
        topCL = new CleavingLine(
                (h/10)*3,   // Y location.
                -(h/10),    // Thickness. Negative so the line inflates upwards.
                0xFFFFFFFF,// A red color.
                660,      // The animation time will be 1 second.
                context);     // The context of the reigning Activity.

        bottomCL = new CleavingLine(
                (h/10)*7,   // Y location.
                (h/10),     // Thickness. This one inflates downwards.
                0xFFFFFFFF,// A red color.
                660,      // The animation time will be 1 second.
                context);     // The context of the reigning Activity.

        //The CleavingLines need their Updater as well.
        clUpdater = new Timer("Cleaving Line Updater", true);
        topCL.scheduleUpdates(clUpdater, 120);
        bottomCL.scheduleUpdates(clUpdater, 120);

        //Now for the titleText display.
        titleText = new ImageDisp(
                0,     //The x location of the image sprite.
                ( (h/10)*3 ), //The y location of the  sprite. This is just below the top CleavingLine.
                w,            //The width of the sprite. Full screen.
                (h/10)*4,  //The height of the sprite. Distance between CleavingLine.
                R.drawable.titletext,//Image load
                context); //The context of the Activity.



        //We need to add content from back to front.

        //Furthermost back is the MenuTerrain.
        activity.addContentView(terrainView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));

        //Next is the cleaving lines.
        activity.addContentView(topCL, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) );
        activity.addContentView(bottomCL, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) );

        //Now the buttons.
        activity.addContentView(timeAttack, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) );
        activity.addContentView(classic, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) );

        activity.addContentView(survival, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) );

        //And finally the title text.
        activity.addContentView(titleText, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) );

        //Oh wait, what about the fades?
        activity.addContentView(fadeIn, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) );
        activity.addContentView(fadeOutTA, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) );
        activity.addContentView(fadeOutC, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) );
        activity.addContentView(fadeOutS, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) );

    }

    /**
     * Starts the animations of all the elements in the MainMenu.
     */
    public void startMenuAnimations(){

        //Now we need to start the animations of everything.
        fadeIn.trigger();
        topCL.activate();
        bottomCL.activate();
        timeAttack.activate();
        classic.activate();
        survival.activate();
        titleText.setVisible(true);

    }

    /**
     * Kill off all the things.
     */
    public void finalizeDeflate(){

        fadeIn.setVisibility(View.GONE);
        fadeIn.setEnabled(false);
        fadeIn.destroyDrawingCache();
        fadeIn.clearAnimation();
        fadeIn.clearFocus();
        //fadeIn = null;

        fadeOutC.setVisibility(View.GONE);
        fadeOutC.setEnabled(false);
        fadeOutC.destroyDrawingCache();
        fadeOutC.clearAnimation();
        fadeOutC.clearFocus();

        fadeOutS.setVisibility(View.GONE);
        fadeOutS.setEnabled(false);
        fadeOutS.destroyDrawingCache();
        fadeOutS.clearAnimation();
        fadeOutS.clearFocus();

        fadeOutTA.setVisibility(View.GONE);
        fadeOutTA.setEnabled(false);
        fadeOutTA.destroyDrawingCache();
        fadeOutTA.clearAnimation();
        fadeOutTA.clearFocus();

        topCL.setVisibility(View.GONE);
        topCL.setEnabled(false);
        topCL.destroyDrawingCache();
        topCL.clearAnimation();
        topCL.clearFocus();
       // topCL = null;

        bottomCL.setVisibility(View.GONE);
        bottomCL.setEnabled(false);
        bottomCL.destroyDrawingCache();
        bottomCL.clearAnimation();
        bottomCL.clearFocus();
       // bottomCL = null;

        timeAttack.setVisibility(View.GONE);
        timeAttack.setEnabled(false);
        timeAttack.destroyDrawingCache();
        timeAttack.clearAnimation();
        timeAttack.clearFocus();
        //timeAttack = null;

        classic.setVisibility(View.GONE);
        classic.setEnabled(false);
        classic.destroyDrawingCache();
        classic.clearAnimation();
        classic.clearFocus();
        //classic = null;

        survival.setVisibility(View.GONE);
        survival.setEnabled(false);
        survival.destroyDrawingCache();
        survival.clearAnimation();
        survival.clearFocus();
        //survival = null;

        titleText.setVisibility(View.GONE);
        titleText.setEnabled(false);
        titleText.destroyDrawingCache();
        titleText.clearAnimation();
        titleText.clearFocus();
        //titleText = null;

        terrainView.setVisibility(View.GONE);
        terrainView.setEnabled(false);
        terrainView.destroyDrawingCache();
        terrainView.clearAnimation();
        terrainView.clearFocus();
        //terrainView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        //terrainView = null;
    }

    /**
     * This FadeInListener is attached to a fade and when that fade completes,
     * it destroys it from the inside.
     */
    class FadeInListener implements FadeListenerI{
        @Override
        public void onComplete() {
            fadeIn.setVisibility(View.GONE);
            fadeIn.setEnabled(false);
            fadeIn.destroyDrawingCache();
            fadeIn.clearAnimation();
            fadeIn.clearFocus();
            //fadeIn = null;
        }
    }

    /**
     * This fade listener encapsulates a MainMenuListener, so when the fade out
     * completes, it tells the main menu listener that it is okay to close the
     * main menu.
     */
    class FadeOutListener implements FadeListenerI{

        MainMenuListenerI listener;

        public FadeOutListener(MainMenuListenerI listener){
            this.listener = listener;
        }
        @Override
        public void onComplete() {
            listener.onFinished();
        }
    }
    
}
