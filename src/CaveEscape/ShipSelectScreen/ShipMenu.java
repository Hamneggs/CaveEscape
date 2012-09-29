package CaveEscape.ShipSelectScreen;

import CaveEscape.CaveEscape.R;
import CaveEscape.ShipSelectScreen.GLES10.GLES10Renderer;
import CaveEscape.ShipSelectScreen.GLES10.MyGLSurfaceView;
import CaveEscapeCore.Constants.Const;
import CaveEscapeCore.CoreGameplay.GameplayMode;
import CaveEscapeCore.GUIViews.*;
import CaveEscapeCore.Player.BasicShip;
import CaveEscapeCore.Player.Player;
import CaveEscapeCore.Player.Ship;
import CaveEscapeCore.Player.ShipShowroom;
import CaveEscapeCore.SoundAndMusic.SFXMEngine;
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

/**
 * A self-contained class that populates an Activity with the elements
 * required to make a ship menu.
 */
public class ShipMenu {

    /**
     * The StatusBars that display the individual stats
     * of the ships.
     */
    StatusBar forwardSpeed, strafeSpeed, maxDamage;

    /**
     * The ArrowButtons that rotate the ShipShowroom.
     */
    ArrowButton left, right;

    /**
     * The InvisiButtonRegion that floats over the ship on screen.
     */
    InvisiButtonRegion shipSelectButton;

    /**
     * A container for the ArrowButtons.
     */
    ViewMotionEventDistributor arrowButtons;

    /**
     * The Fades that occur on entry to the menu,
     * and when a ship is selected.
     */
    Fade onEntry, onSelected;

    /**
     * "Enhanced" TextViews to make the Ship Name Label,
     * Base Multiplier Label, and the Ship Name Display.
     */
    EnhancedTextView shipNameLabel, baseMultLabel, shipNameDisplay;

    /**
     * The MultDisp that displays each Ship's base multiplier.
     */
    MultDisp baseMultDisplay;

    /**
     * The OpenGL element of this menu:
     * The actual Ships and the Cave that they are displayed in.
     */
    ShipShowroom showroom;

    /**
     * The GLSurfaceView that we render the ShipShowroom into.
     */
    MyGLSurfaceView glSurfaceView;

    /**
     * The Player object that the user selected.
     */
    Player selected;

    /**
     * The ShipMenuListenerI that the ShipMenu talks to.
     */
    ShipMenuListenerI listener;

    /**
     * Inflates the ShipMenu; or rather fills the screen with its elements.
     * @param context The Context of the reigning Activity.
     * @param activity The reigning Activity itself.
     */
    public void inflateShipMenu(Context context, Activity activity, SFXMEngine sfx, GameplayMode mode){

        //Store the dimensions of the screen.
        DisplayMetrics m = context.getResources().getDisplayMetrics();
        float w = m.widthPixels;
        float h = m.heightPixels;

        //Create the StatusBar that displays the forward speed of each Ship.
        forwardSpeed = new StatusBar((w/50), (h*.8f), (w*.75f), (h*.02f), (h*.002f), (h*.035f), "Forward Velocity", 0xFF00FF00, 0xFFFF0000, 0xFF000000, 0xFFFFFFFF, Paint.Align.LEFT, context);
        //Set its maximum value to .2--the highest safe forward speed.
        forwardSpeed.setMaxVal(Const.shipMaxForwardSpeed/5);

        //Create the StatusBar that displays the strafing speed of each Ship.
        strafeSpeed = new StatusBar((w/50), (h*.875f), (w*.75f), (h*.02f), (h*.002f), (h*.035f), "Strafe Speed", 0xFF00FF00, 0xFFFF0000, 0xFF000000, 0xFFFFFFFF, Paint.Align.LEFT, context);
        //Set its max value to the maximum strafe speed.
        strafeSpeed.setMaxVal(Const.shipMaxStrafeSpeed/8f);

        //Create the StatusBar that displays the maximum damage each ship can take,
        maxDamage = new StatusBar((w/50), (h*.95f), (w*.75f), (h*.02f), (h*.002f), (h*.035f), "Durability", 0xFF00FF00, 0xFFFF0000, 0xFF000000, 0xFFFFFFFF, Paint.Align.LEFT, context);
        //And set it to the maxDamage of the most durable ship.
        maxDamage.setMaxVal(Const.shipMaxPosHealth);

        //Initialize the LEFT ArrowButton.
        left = new ArrowButton((w/50), (h/2)-(h/10), (h/10), (h/5), -(w*.025f), 0, BitmapFactory.decodeResource(context.getResources(), R.drawable.arrow_left), context);
        //And give it a LeftListener() to talk to.
        left.setListener(new LeftListener());

        //Do the same with the RIGHT ArrowButton.
        right = new ArrowButton(w-(w/50)-(h/10), (h/2)-(h/10), (h/10), (h/5), -(w*.025f), 0, BitmapFactory.decodeResource(context.getResources(), R.drawable.arrow_right), context);
        //Including giving it a Listener to talk to as well.
        right.setListener(new RightListener());

        //Initialize the invisible ship select button, and give it a listener.
        shipSelectButton = new InvisiButtonRegion((3*w/8), (3*h/8), (w/4), (h/4), context);
        shipSelectButton.setListener(new ShipSelectListener());

        //Create the transition fades--again, based on game mode.
        switch (mode) {
            case noneSelected:
                onSelected = new Fade(0xFF000000, 1000, Fade.Type.OUT, context);
                onEntry = new Fade(0xFF000000, 1000, Fade.Type.IN, context);
                break;
            case TimeAttack:
                onSelected = new Fade(0xFFFFFFFF, 1000, Fade.Type.OUT, context);
                onEntry = new Fade(0xFFFFFFFF, 1000, Fade.Type.IN, context);
                break;
            case Classic:
                onSelected = new Fade(0xFF000000, 1000, Fade.Type.OUT, context);
                onEntry = new Fade(0xFF000000, 1000, Fade.Type.IN, context);
                break;
            case Survival:
                onSelected = new Fade(0xFF000000, 1000, Fade.Type.OUT, context);
                onEntry = new Fade(0xFF000000, 1000, Fade.Type.IN, context);
                break;
        }

        //Pack the ArrowButtons into an ViewMotionEventDistributor, and also add the ship select button.
        arrowButtons = new ViewMotionEventDistributor(context, left, right, shipSelectButton);

        //Create the EnhancedTextView that actually displays the name of the ship.
        shipNameDisplay = new EnhancedTextView((w*.2f), (h*.125f), (h*.08f), "PlaceHolder", 0xFFFFFFFF, 0xFF000000, 1.5f, .5f, .5f, false, false, 0, 0, 0, Paint.Align.LEFT, context);
        //Create the shipNameDisplay's label.
        shipNameLabel = new EnhancedTextView((w*.02f), (h*.120f), (h*.05f), "Ship Name:", 0xFFFFFFFF, 0xFF000000, 1.5f, .5f, .5f, true, false, 0, 0, 0, Paint.Align.LEFT, context);

        //Create the label for the base multiplier.
        baseMultLabel = new EnhancedTextView((w/50), (h*.730f), (h*.0366f), "Base Multiplier:", 0xFFFFFFFF, 0xFF000000, 1.5f, .5f, .5f, true, false, 0, 0, 0, Paint.Align.LEFT, context);
        //Create the base multiplier display itself.
        baseMultDisplay = new MultDisp(0, 2, (h/20), (h/10), (w*.2f), (h*.735f), 0xFFFFFFFF, 100, 1.75f, Paint.Align.LEFT, context);

        //Construct the showroom, making sure that the first ship is the first seen.
        switch (mode) {
            case noneSelected:
                //This should never happen, so we provide an alarming color.
                showroom = new ShipShowroom(0, 0, 0, 2, (float)-Math.PI*.5f, Const.clrNoneSelectedR, Const.clrNoneSelectedG, Const.clrNoneSelectedB, sfx, context);
                break;
            case TimeAttack:
                //Time attack will have a light background, so we need a darker colored
                //terrain.
                showroom = new ShipShowroom(0, 0, 0, 2, (float)-Math.PI*.5f, Const.clrTimeAttackR, Const.clrTimeAttackG, Const.clrTimeAttackB, sfx, context);
                break;
            case Classic:
                //Classic: black, with a light colored terrain.
                showroom = new ShipShowroom(0, 0, 0, 2, (float)-Math.PI*.5f, Const.clrClassicR, Const.clrClassicG, Const.clrClassicB, sfx, context);
                break;
            case Survival:
                //Survival: dark, dank, and scary.
                showroom = new ShipShowroom(0, 0, 0, 2, (float)-Math.PI*.5f, Const.clrSurvivalR, Const.clrSurvivalG, Const.clrSurvivalB, sfx, context);
                break;
        }
        //ADD A BUNCH OF SHIPS TO THE SHOWROOM.                  fwd     strf  dmg  x     y       z       top         bottom      left        right       front       back
        showroom.addShip(new BasicShip("Halken A2C",             .0725f, .025f, 200, .35f, .25f,   .5f,    0xFF1111FF, 0xFF054405, 0xFF603311, 0xFF603311, 0xFFFFFFFF, 0xFFFFAA00));
        showroom.addShip(new BasicShip("Labyrinth H200",         .067f,  .027f, 115, .2f,   .3f,   .66f,   0x11000000, 0x11000000, 0x11000000, 0x11000000, 0xFF999999, 0xFFAA2222));
        showroom.addShip(new BasicShip("Intercept CXS",          .085f,  .027f, 130, .4f,  .15f,   .425f,  0xFF2222FF, 0xFFFFFF00, 0xFFFFFF00, 0xFFFFFF00, 0xFF2222FF, 0xFFFF6600));
        showroom.addShip(new BasicShip("Escape 360",             .065f,  .034f, 50,  .5f,  .25f,   .5f,    0x66FFFFFF, 0x66FFFFFF, 0xFF000000, 0xFF000000, 0xFFFFFFFF, 0xFF00FF00));
        showroom.addShip(new BasicShip("Halken Fleetwood",       .065f,  .01f,  500, .5f,  .25f,   .35f,   0xFFFF00FF, 0xFFFF00FF, 0xFF6600FF, 0xFF6600FF, 0xFF000000, 0xFF090999));
        showroom.addShip(new BasicShip("Labyrinth TunnelRunner", .115f,  .03f,  200, .5f,  .25f,   .5f,    0x33000000, 0x33000000, 0x33000000, 0x33000000, 0xFF552222, 0xFFAA22BF));
        showroom.addShip(new BasicShip("Intercept 777x",         .085f,  .035f, 75,  .375f,.125f,  .35f,   0xFFFFFF00, 0xFF333333, 0xFF333333, 0xFF333333, 0xFF333333, 0xFF1515FF));
        showroom.addShip(new BasicShip("Escape 8888r",           .12f,   .03f,  100, .5f,  .25f,   .5f,    0xAA000000, 0xAA000000, 0xAA000000, 0xAA000000, 0xFF0000AA, 0xFF8888FF));
        showroom.organizeSelection();

        //Update the stat display for the first time.
        updateStatDisplay();

        //Create the GLSurfaceView that holds all the OpenGL content we need to give it.
        //We base the clear-color of the showroom on the mode selected.
        switch (mode) {
            case noneSelected:
                //Again, this should never happen, so we give it an alarming color.
                glSurfaceView = new MyGLSurfaceView(context, new GLES10Renderer(showroom, 1, 0, 1));
                break;
            case TimeAttack:
                //Time attack is clean and white.
                glSurfaceView = new MyGLSurfaceView(context, new GLES10Renderer(showroom, 1, 1, 1));
                break;
            case Classic:
                //Classic is dark, but not dramatic.
                glSurfaceView = new MyGLSurfaceView(context, new GLES10Renderer(showroom, 0, 0, 0));
                break;
            case Survival:
                //Survival. Dark, AND dramatic.
                glSurfaceView = new MyGLSurfaceView(context, new GLES10Renderer(showroom, 0, 0, 0));
                break;
        }

        //glSurfaceView = new MyGLSurfaceView(context, new GLES10Renderer(showroom, 0, 0, 0));

        //ADD EVERYTHING TO THE ACTIVITY from BACK TO FRONT!
        activity.addContentView(glSurfaceView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        activity.addContentView(forwardSpeed, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        activity.addContentView(strafeSpeed, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        activity.addContentView(maxDamage, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        activity.addContentView(shipNameDisplay, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        activity.addContentView(shipNameLabel, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        activity.addContentView(baseMultLabel, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        activity.addContentView(baseMultDisplay, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        activity.addContentView(right, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        activity.addContentView(left, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        activity.addContentView(shipSelectButton, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        activity.addContentView(onEntry, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        activity.addContentView(onSelected, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        activity.addContentView(arrowButtons, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));

        //Trigger the entry fade.
        onEntry.trigger();

        if(listener!=null){
            listener.onInflate();
        }


    }

    public void finalizeDeflate(){

        glSurfaceView.setVisibility(View.GONE);
        glSurfaceView.setEnabled(false);
        glSurfaceView.destroyDrawingCache();
        glSurfaceView.clearAnimation();
        glSurfaceView.clearFocus();

        forwardSpeed.setVisibility(View.GONE);
        forwardSpeed.setEnabled(false);
        forwardSpeed.destroyDrawingCache();
        forwardSpeed.clearAnimation();
        forwardSpeed.clearFocus();

        strafeSpeed.setVisibility(View.GONE);
        strafeSpeed.setEnabled(false);
        strafeSpeed.destroyDrawingCache();
        strafeSpeed.clearAnimation();
        strafeSpeed.clearFocus();

        maxDamage.setVisibility(View.GONE);
        maxDamage.setEnabled(false);
        maxDamage.destroyDrawingCache();
        maxDamage.clearAnimation();
        maxDamage.clearFocus();

        shipNameDisplay.setVisibility(View.GONE);
        shipNameDisplay.setEnabled(false);
        shipNameDisplay.destroyDrawingCache();
        shipNameDisplay.clearAnimation();
        shipNameDisplay.clearFocus();

        shipNameLabel.setVisibility(View.GONE);
        shipNameLabel.setEnabled(false);
        shipNameLabel.destroyDrawingCache();
        shipNameLabel.clearAnimation();
        shipNameLabel.clearFocus();

        baseMultLabel.setVisibility(View.GONE);
        baseMultLabel.setEnabled(false);
        baseMultLabel.destroyDrawingCache();
        baseMultLabel.clearAnimation();
        baseMultLabel.clearFocus();

        baseMultDisplay.setVisibility(View.GONE);
        baseMultDisplay.setEnabled(false);
        baseMultDisplay.destroyDrawingCache();
        baseMultDisplay.clearAnimation();
        baseMultDisplay.clearFocus();
        baseMultDisplay.kill();

        right.setVisibility(View.GONE);
        right.setEnabled(false);
        right.destroyDrawingCache();
        right.clearAnimation();
        right.clearFocus();

        left.setVisibility(View.GONE);
        left.setEnabled(false);
        left.destroyDrawingCache();
        left.clearAnimation();
        left.clearFocus();

        shipSelectButton.setVisibility(View.GONE);
        shipSelectButton.setEnabled(false);
        shipSelectButton.destroyDrawingCache();
        shipSelectButton.clearAnimation();
        shipSelectButton.clearFocus();

        onSelected.setVisibility(View.GONE);
        onSelected.setEnabled(false);
        onSelected.destroyDrawingCache();
        onSelected.clearAnimation();
        onSelected.clearFocus();

        onEntry.setVisibility(View.GONE);
        onEntry.setEnabled(false);
        onEntry.destroyDrawingCache();
        onEntry.clearAnimation();
        onEntry.clearFocus();

        arrowButtons.setVisibility(View.GONE);
        arrowButtons.setEnabled(false);
        arrowButtons.destroyDrawingCache();
        arrowButtons.clearAnimation();
        arrowButtons.clearFocus();

        if(listener != null){
            listener.onDeflate();
        }
    }

    /**
     * Updates the Ship Stat display elements.
     */
    private void updateStatDisplay(){
        //Get the currently selected ship...
        Ship current = showroom.getCurrentSelection().getShip();

        //...And display all its info!
        shipNameDisplay.setText(current.getName());
        forwardSpeed.setVal(current.getForwardSpeed());
        strafeSpeed.setVal(current.getStrafeSpeed());
        maxDamage.setVal(current.getMaxDamage());
        baseMultDisplay.setMult(current.getBaseMult());
    }

    public void setListener(ShipMenuListenerI listener){
        this.listener = listener;
    }


    /**
     * A small class that just defines what the LEFT ArrowButton
     * should do when pressed.
     */
    private class LeftListener implements ArrowButtonListenerI{
        @Override
        public void onClicked() {
            showroom.rotateLeft();
            updateStatDisplay();
            if(listener!= null){
                listener.onRotateLeft();
            }
        }
    }

    /**
     * A small class that just defines what the RIGHT ArrowButton
     * should do when pressed.
     */
    private class RightListener implements ArrowButtonListenerI{
        @Override
        public void onClicked() {
            showroom.rotateRight();
            updateStatDisplay();
            if(listener!=null){
                listener.onRotateRight();
            }
        }
    }

    /**
     * A small class that just defines what the
     * ShipSelectButton does.
     */
    private class ShipSelectListener implements InvisiButtonRegionListenerI{
        @Override
        public void onTouched() {
            selected = showroom.getCurrentSelection();
            onSelected.trigger();
            if(listener!= null){
                listener.onShipSelected(selected);
            }
        }
    }



}
