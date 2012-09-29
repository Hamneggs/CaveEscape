package CaveEscape.Gameplay;

import CaveEscape.CaveEscape.R;
import CaveEscape.Gameplay.GLES10.GLES10Renderer;
import CaveEscape.Gameplay.GLES10.GameplaySurfaceView;
import CaveEscapeCore.Constants.Const;
import CaveEscapeCore.CoreGameplay.GameplayMode;
import CaveEscapeCore.GUIViews.*;
import CaveEscapeCore.Player.GameplayControllerView;
import CaveEscapeCore.Player.Player;
import CaveEscapeCore.Player.ShipState;
import CaveEscapeCore.SoundAndMusic.SFXMEngine;
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.opengl.GLSurfaceView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: Chuck Finley
 * Date: 7/11/12
 * Time: 8:52 AM
 * To change this template use File | Settings | File Templates.
 */
public class Gameplay {


    GameplaySurfaceView gameplayView;

    GLES10Renderer renderer;

    GameplayControllerView UDLRController;
    StatusBar multTime;
    MultDisp curMult;

    EnhancedTextView scoreLabel;
    EnhancedTextView scoreDisp;

    EnhancedTextView hPickupDisplay;
    EnhancedTextView mPickupDisplay;
    EnhancedTextView pPickupDisplay;

    StatusBar health;
    StatusBar levelProgress;

    LevelToast toast;
    int curLevel;
    Timer leveler;

    SFXMEngine sfx;

    Player player;

    GameplayMode mode;





    public void inflateGameplay(Activity activity, Context context, SFXMEngine sfx, Player player, GameplayMode mode, StatusBarListenerI listensForDeath){

        //Store the dimensions of the screen.
        DisplayMetrics m = context.getResources().getDisplayMetrics();
        float w = m.widthPixels;
        float h = m.heightPixels;

        //Store a reference to the player object that the user is playing as.
        this.player = player;

        //Store the chosen gameMode for later.
        this.mode = mode;

        UDLRController = new GameplayControllerView(context, player, (w*.75f), (h*.6f), (w*.215f), (w*.215f), BitmapFactory.decodeResource(context.getResources(), R.drawable.unimove));

        //Set up the stuff for the multiplier display.
        multTime = new StatusBar((w*.025f), (h*.9f), (w*.3f), (h*.02f), h*.0025f, h*.025f, "Multiplier", 0xFF2200FF, 0xFFFF0022, 0xFF000000, 0xFFFFFFFF, Paint.Align.LEFT, context);
        curMult = new MultDisp(player.getShip().getBaseMult(), player.getShip().getBaseMult()*10, 30, 40, w*.325f, h*.9f, 0xFFFFFFFF, 200, 1.5f, Paint.Align.RIGHT, context);
        curMult.setMult(player.getShip().getBaseMult());

        //Set up the health bar.
        health = new StatusBar(w *.025f, h*.1f, w*.3f, h*.02f, h*.0025f, h*.025f, "Health", 0xFFFF2222, 0xFF00FF00, 0xFF000000, 0xFFFFFFFF, Paint.Align.LEFT, context);

        //Set up the score display.
        String scoreText = "";
        for(int i = 0; i < w/14; i++){
            scoreText += " ";
        }
        scoreText += "Score:";
        scoreLabel = new EnhancedTextView( (w *.95f), h*.1f, h*.025f, scoreText, 0xFFFFFFFF, 0xFF000000, 1, .5f, .5f, false, true, 0xFF000000, w*.3f, h*.04f, Paint.Align.RIGHT, context);
        scoreDisp = new EnhancedTextView( (w *.95f), h*.14f, (h/22.5f), "78975455", 0xFFFFFFFF, 0xFF000000, h*.0025f, .5f, .5f, false, false, 0, 0, 0, Paint.Align.RIGHT, context);

        toast = new LevelToast("...Level 2!", Toast.LENGTH_SHORT, 0xFF000000, 0x00000000, 0xFFFFFFFF, context);
        curLevel = 2;
        leveler = new Timer("Leveler", true);
        leveler.schedule(new DifficultTask(), Const.gpLevelTime, Const.gpLevelTime);

        hPickupDisplay = new EnhancedTextView((.025f*w), (.45f*h), h*.025f, "willBeReplaced", 0xFFFFFFFF, 0xFF000000, 1, .5f, .5f, false, false, 0x00000000, 0, 0, Paint.Align.LEFT, context);
        mPickupDisplay = new EnhancedTextView((.025f*w), (.50f*h), h*.025f, "willBeReplaced", 0xFFFFFFFF, 0xFF000000, 1, .5f, .5f, false, false, 0x00000000, 0, 0, Paint.Align.LEFT, context);
        pPickupDisplay = new EnhancedTextView((.025f*w), (.55f*h), h*.025f, "willBeReplaced", 0xFFFFFFFF, 0xFF000000, 1, .5f, .5f, false, false, 0x00000000, 0, 0, Paint.Align.LEFT, context);

        //Set up the ship the way we want it.
        player.setState(ShipState.straight);
        player.setX(0);
        player.setY(0);
        player.setZ(-Const.camShipDist);
        player.setHealthBar(health);
        health.setListener(listensForDeath);
        player.setScoreView(scoreDisp);
        player.setMultDisplay(curMult);
        player.setMultTimeDisplay(multTime);
        player.setHealthPickupDisplay(hPickupDisplay);
        player.setMultPickupDisplay(mPickupDisplay);
        player.setPointPickupDisplay(pPickupDisplay);
        renderer = new GLES10Renderer(mode, player, UDLRController, sfx);
        gameplayView = new GameplaySurfaceView(context, renderer);



        //Start the music.
        switch (mode) {
            case noneSelected:
                sfx.playMenuMusic(.8f);
                break;
            case TimeAttack:
                sfx.playBgmA(.8f);
                break;
            case Classic:
                sfx.playBgmB(.8f);
                break;
            case Survival:
                sfx.playBgmC(.8f);
                break;
        }

        System.out.println("DID WE TRY TO PLAY THE SOUND?: " + sfx.playStartLevel(.1f, 1f) );

        this.sfx = sfx;

        //Add everything to the window.
        activity.addContentView(gameplayView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        activity.addContentView(scoreLabel,  new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        activity.addContentView(scoreDisp,  new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        activity.addContentView(multTime,  new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        activity.addContentView(curMult,  new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        activity.addContentView(health,  new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        activity.addContentView(UDLRController, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        activity.addContentView(hPickupDisplay, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        activity.addContentView(pPickupDisplay, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        activity.addContentView(mPickupDisplay, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    }

    public void pause(){
        leveler.cancel();
        leveler.purge();
        gameplayView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void resume(){
        curLevel++;
        leveler = new Timer("Leveler", true);
        leveler.schedule(new DifficultTask(), Const.gpLevelTime, Const.gpLevelTime);
        gameplayView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        //TODO: You must account for this being an exploit to enhance scores.
    }

    public void finalizeDeflate(){

        gameplayView.setVisibility(View.GONE);
        gameplayView.setEnabled(false);
        gameplayView.destroyDrawingCache();
        gameplayView.clearAnimation();
        gameplayView.clearFocus();
        gameplayView = null;

        UDLRController.setVisibility(View.GONE);
        UDLRController.setEnabled(false);
        UDLRController.destroyDrawingCache();
        UDLRController.clearAnimation();
        UDLRController.clearFocus();

        multTime.setVisibility(View.GONE);
        multTime.setEnabled(false);
        multTime.destroyDrawingCache();
        multTime.clearAnimation();
        multTime.clearFocus();

        curMult.kill();
        curMult.setVisibility(View.GONE);
        curMult.setEnabled(false);
        curMult.destroyDrawingCache();
        curMult.clearAnimation();
        curMult.clearFocus();

        scoreLabel.setVisibility(View.GONE);
        scoreLabel.setEnabled(false);
        scoreLabel.destroyDrawingCache();
        scoreLabel.clearAnimation();
        scoreLabel.clearFocus();

        scoreDisp.setVisibility(View.GONE);
        scoreDisp.setEnabled(false);
        scoreDisp.destroyDrawingCache();
        scoreDisp.clearAnimation();
        scoreDisp.clearFocus();

        hPickupDisplay.setVisibility(View.GONE);
        hPickupDisplay.setEnabled(false);
        hPickupDisplay.destroyDrawingCache();
        hPickupDisplay.clearAnimation();
        hPickupDisplay.clearFocus();

        mPickupDisplay.setVisibility(View.GONE);
        mPickupDisplay.setEnabled(false);
        mPickupDisplay.destroyDrawingCache();
        mPickupDisplay.clearAnimation();
        mPickupDisplay.clearFocus();

        pPickupDisplay.setVisibility(View.GONE);
        pPickupDisplay.setEnabled(false);
        pPickupDisplay.destroyDrawingCache();
        pPickupDisplay.clearAnimation();
        pPickupDisplay.clearFocus();

        health.setVisibility(View.GONE);
        health.setEnabled(false);
        health.destroyDrawingCache();
        health.clearAnimation();
        health.clearFocus();

        toast.kill();

        leveler.cancel();
        leveler.purge();
    }

    public void deflateAllButGL(){
        UDLRController.setVisibility(View.GONE);
        UDLRController.setEnabled(false);
        UDLRController.destroyDrawingCache();
        UDLRController.clearAnimation();
        UDLRController.clearFocus();

        multTime.setVisibility(View.GONE);
        multTime.setEnabled(false);
        multTime.destroyDrawingCache();
        multTime.clearAnimation();
        multTime.clearFocus();

        curMult.kill();
        curMult.setVisibility(View.GONE);
        curMult.setEnabled(false);
        curMult.destroyDrawingCache();
        curMult.clearAnimation();
        curMult.clearFocus();

        scoreLabel.setVisibility(View.GONE);
        scoreLabel.setEnabled(false);
        scoreLabel.destroyDrawingCache();
        scoreLabel.clearAnimation();
        scoreLabel.clearFocus();

        scoreDisp.setVisibility(View.GONE);
        scoreDisp.setEnabled(false);
        scoreDisp.destroyDrawingCache();
        scoreDisp.clearAnimation();
        scoreDisp.clearFocus();

        hPickupDisplay.setVisibility(View.GONE);
        hPickupDisplay.setEnabled(false);
        hPickupDisplay.destroyDrawingCache();
        hPickupDisplay.clearAnimation();
        hPickupDisplay.clearFocus();

        mPickupDisplay.setVisibility(View.GONE);
        mPickupDisplay.setEnabled(false);
        mPickupDisplay.destroyDrawingCache();
        mPickupDisplay.clearAnimation();
        mPickupDisplay.clearFocus();

        pPickupDisplay.setVisibility(View.GONE);
        pPickupDisplay.setEnabled(false);
        pPickupDisplay.destroyDrawingCache();
        pPickupDisplay.clearAnimation();
        pPickupDisplay.clearFocus();

        health.setVisibility(View.GONE);
        health.setEnabled(false);
        health.destroyDrawingCache();
        health.clearAnimation();
        health.clearFocus();

        toast.kill();

        leveler.cancel();
        leveler.purge();
    }

    public Player getPlayer(){
        return player;
    }

    public GameplayMode getMode(){
        return mode;
    }

    private class DifficultTask extends TimerTask {

        public void run(){
            renderer.updateDifficulty();
            toast.show();
            String text = "...Level "+curLevel+"!";
            toast.setText(text);
            curLevel++;
            sfx.playLevelUp(0, .8f, 1);
        }
    }
}
