package CaveEscape.GameOver;

import CaveEscapeCore.CoreGameplay.GameplayMode;
import CaveEscapeCore.GUIViews.*;
import CaveEscapeCore.Player.Player;
import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import java.util.Timer;

/**
 * Created with IntelliJ IDEA.
 * User: Chuck Finley
 * Date: 8/18/12
 * Time: 7:01 AM
 * To change this template use File | Settings | File Templates.
 */
public class GameOverScreen {

    CleavingLine top, bottom;

    Fade fadeOut;

    TextButton game, over, mainMenu;

    EnhancedTextView score;

    InvisiButtonRegion buttonRegion;

    Timer clUpdater;

    public void inflateGameOverScreen(Activity activity, Context context, GameplayMode mode, Player player, final GameOverListenerI whatToDo){

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float w = metrics.widthPixels;
        float h = metrics.heightPixels;


        int cleavingColor,
        textColor = 0xFFFFFFFF,
        fadeColor = 0xFFFFFFFF;

        if(mode == GameplayMode.TimeAttack){
            cleavingColor = 0xFF000000;
        }
        else{
            cleavingColor = 0xFFFFFFFF;

        }

        top = new CleavingLine((h/2f)-(h/4f), h/10f, cleavingColor, 500, context);
        bottom = new CleavingLine((h/2f)+(h/4f), -h/10f, cleavingColor, 500, context);

        game = new TextButton(w*.5f, h*.255f, w*.2f, h*.255f, 1, 1, h/15, textColor, 1000, "Game", context);
        over = new TextButton(w*.51f, h*.255f, w*.8f, h*.255f, 1, 1, h/15, textColor, 1000, "Over", context);
        game.setAlign(Paint.Align.RIGHT);
        over.setAlign(Paint.Align.LEFT);

        mainMenu = new TextButton(w*.5f, h*.45f, w*.5f, h*.85f, w*.98f, w/16f, h/10, textColor, 1000, "Main Menu", context);
        mainMenu.setAlign(Paint.Align.CENTER);

        buttonRegion = new InvisiButtonRegion(w/5, h/5, .6f*w, .6f*h, context);

        fadeOut = new Fade(fadeColor, 1000, Fade.Type.OUT, context);
        int scoreValue = (int)player.getScore();
        score = new EnhancedTextView(w*.5f, h*.725f, h/15, "Final Score: "+scoreValue, textColor, 0xFF000000, 1f, 0, 0, false, false, 0, 0, 0, Paint.Align.CENTER, context);

        //The CleavingLines need their Updater as well.
        clUpdater = new Timer("Cleaving Line Updater", true);
        top.scheduleUpdates(clUpdater, 120);
        bottom.scheduleUpdates(clUpdater, 120);

        buttonRegion.setListener(new InvisiButtonRegionListenerI() {

            @Override
            public void onTouched() {
                fadeOut.trigger();
            }
        });


        fadeOut.setFadeListener(new FadeListenerI() {
            @Override
            public void onComplete() {
                whatToDo.onGameOverPressed();
            }
        });

        top.activate();
        bottom.activate();
        game.activate();
        over.activate();
        mainMenu.activate();

        activity.addContentView(top, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        activity.addContentView(bottom, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        activity.addContentView(game, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        activity.addContentView(over, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        activity.addContentView(score, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        activity.addContentView(mainMenu, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        activity.addContentView(fadeOut, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        activity.addContentView(buttonRegion, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));


    }

    public void deflateGameOverScreen(){
        top.setVisibility(View.GONE);
        top.setEnabled(false);
        top.destroyDrawingCache();
        top.clearAnimation();
        top.clearFocus();

        bottom.setVisibility(View.GONE);
        bottom.setEnabled(false);
        bottom.destroyDrawingCache();
        bottom.clearAnimation();
        bottom.clearFocus();

        fadeOut.setVisibility(View.GONE);
        fadeOut.setEnabled(false);
        fadeOut.destroyDrawingCache();
        fadeOut.clearAnimation();
        fadeOut.clearFocus();

        game.setVisibility(View.GONE);
        game.setEnabled(false);
        game.destroyDrawingCache();
        game.clearAnimation();
        game.clearFocus();

        over.setVisibility(View.GONE);
        over.setEnabled(false);
        over.destroyDrawingCache();
        over.clearAnimation();
        over.clearFocus();

        mainMenu.setVisibility(View.GONE);
        mainMenu.setEnabled(false);
        mainMenu.destroyDrawingCache();
        mainMenu.clearAnimation();
        mainMenu.clearFocus();

        score.setVisibility(View.GONE);
        score.setEnabled(false);
        score.destroyDrawingCache();
        score.clearAnimation();
        score.clearFocus();

        buttonRegion.setVisibility(View.GONE);
        buttonRegion.setEnabled(false);
        buttonRegion.destroyDrawingCache();
        buttonRegion.clearAnimation();
        buttonRegion.clearFocus();
    }
}
