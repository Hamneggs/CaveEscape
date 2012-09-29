package CaveEscape.Gameplay.GLES10;

import CaveEscapeCore.Constants.Const;
import CaveEscapeCore.CoreGameplay.GameplayMode;
import CaveEscapeCore.Pickups.PickupBag;
import CaveEscapeCore.Player.GameplayControllerView;
import CaveEscapeCore.Player.Player;
import CaveEscapeCore.SoundAndMusic.SFXMEngine;
import CaveEscapeCore.Terrain.PerlinTerrainGame;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * This is the OpenGL portion of the gameplay screen/state.
 * Here we render the terrain, player and pickups, check for collisions,
 * and create new terrain.
 */
public class GLES10Renderer implements GLSurfaceView.Renderer {

    /**
     * The Player object that the player chose. It
     * is stored here so that we can render it.
     */
    private Player player;

    /**
     * We store the gameplay mode here so we can populate the pickup
     * bag accordingly.
     */
    private GameplayMode mode;

    /**
     * You know that little circle that you use to steer the ship?
     * This is it.
     */
    private GameplayControllerView controllerView;

    /**
     * The top and bottom Perlin terrains that make up the cave.
     */
    private PerlinTerrainGame top, bottom;

    /**
     * The container for all the pickups in the scene.
     */
    public PickupBag pickupBag;

    /**
     * Since the only way we can have a context to the framecount of the
     * game is from within the OpenGL context, we store the framecount here.
     * This is used to limit on what frames the score increases.
     */
    byte frame;

    /**
     * The constructor for the OpenGL portion of the gameplay. Here we encapsulate
     * the gameplay mode, the selected player object, a reference to the controller view,
     * and the sound engine.
     * @param mode The gameplay mode that the user selected.
     * @param player The player (ship) that the user selected.
     * @param controllerView The controller view to observe.
     * @param sfx An initialized instance of the sound engine so that we can play sounds when need be.
     */
    public GLES10Renderer(GameplayMode mode, Player player, GameplayControllerView controllerView, SFXMEngine sfx){

        //Encapsulate everything.
        this.mode = mode;
        this.player = player;
        this.controllerView = controllerView;

        //Create the pickup bag, which populates the scene with pickups.
        pickupBag = new PickupBag(5, player, bottom, sfx);
        pickupBag.refreshBag(mode);

        //player.setPickupBag(pickupBag);

        //Create the top Perlin terrain.
        top = new PerlinTerrainGame(0,
                Const.gpVertOffset,
                0,
                Const.gpTWidth,
                Const.gpTDepth,
                Const.gpResX,
                Const.gpResY,
                Const.gpBaseFDensity,
                Const.gpBaseHScale,
                Const.gpSScale,
                (float)Math.random(),
                true,
                player.getShip().getForwardSpeed(),
                player,
                true,
                null,
                false);

        //Create the bottom Perlin terrain.
        bottom = new PerlinTerrainGame(0,
                -Const.gpVertOffset,
                0,
                Const.gpTWidth,
                Const.gpTDepth,
                Const.gpResX,
                Const.gpResY,
                Const.gpBaseFDensity,
                Const.gpBaseHScale,
                Const.gpSScale,
                (float)Math.random(),
                false,
                player.getShip().getForwardSpeed(),
                player,
                false,
                pickupBag,
                true);

        //Based on the mode selected, we color the terrain differently.
        switch (mode) {
            case noneSelected:
                top.setColor(Const.clrNoneSelectedR, Const.clrNoneSelectedG, Const.clrNoneSelectedB, 1);
                bottom.setColor(Const.clrNoneSelectedR, Const.clrNoneSelectedG, Const.clrNoneSelectedB, 1);
                break;
            case TimeAttack:
                top.setColor(Const.clrTimeAttackR, Const.clrTimeAttackG, Const.clrTimeAttackB, 1);
                bottom.setColor(Const.clrTimeAttackR, Const.clrTimeAttackG, Const.clrTimeAttackB, 1);
                break;
            case Classic:
                top.setColor(Const.clrClassicR, Const.clrClassicG, Const.clrClassicB, 1);
                bottom.setColor(Const.clrClassicR, Const.clrClassicG, Const.clrClassicB, 1);
                break;
            case Survival:
                top.setColor(Const.clrSurvivalR, Const.clrSurvivalG, Const.clrSurvivalB, 1);
                bottom.setColor(Const.clrSurvivalR, Const.clrSurvivalG, Const.clrSurvivalB, 1);
                break;
        }

        //Enable haptic feedback.
        controllerView.setHapticFeedbackEnabled(true);
        top.setHapticFeedbackView(controllerView);
        bottom.setHapticFeedbackView(controllerView);

        //Finally we initialize the frame count.
        frame = 0;

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig eglConfig) {
        PerlinTerrainGame.setupGLState(gl, mode);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        // Set GL_MODELVIEW transformation mode
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();   // reset the matrix to its default state

        // When using GL_MODELVIEW, you must set the view point
        GLU.gluLookAt(gl, 0, 0, -5, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f, 1000.0f);
        gl.glViewport(0, 0, width, height);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        //gl.glMatrixMode(GL10.GL_PROJECTION);
        //gl.glLoadIdentity();
        //
        //GLU.gluPerspective(gl, 45.0f, (float) 16 / (float) 9, 0.1f, 1000.0f);
        //gl.glViewport(0, 0, 16, 9);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        //gl.glLoadIdentity();   // reset the matrix to its default state


        // When using GL_MODELVIEW, you must set the view point
        GLU.gluLookAt(gl, 0, 0, 5, 0, 0, 0, 0f, 1.0f, 0.0f);
        //gl.glViewport(0, 0, w, h);
        top.drawTerrain(gl);
        bottom.drawTerrain(gl);
        pickupBag.drawBag(gl);
        pickupBag.testPickupCollisions(player);
        //player.drawShip(gl);
        GLU.gluLookAt(gl, 0, 0, -5, 0f, player.getX()*.6f, player.getY()*.6f, 0f, 1.0f, 0.0f);

        if(!controllerView.isPressed()){
            player.setXVelocity(player.getXVelocity()/Const.gpVelDecayFactor);
            player.setYVelocity(player.getYVelocity() / Const.gpVelDecayFactor);
        }

        if(frame%Const.gpFramesPerScoreIncrease == 0)
            player.changeScore(Const.gpScoreIncrease, Player.ScoreChangeType.STANDARD);

        if(Const.gpDoCollTests){
            top.testCollision(player);
            bottom.testCollision(player);
        }
        frame++;
    }

    /**
     * Updates the difficulty.
     */
    public void updateDifficulty(){

        //Increment the density scale.
        top.setDensityScale(top.getdScale()* Const.gpDensityChangeFactor);
        bottom.setDensityScale(bottom.getdScale()*Const.gpDensityChangeFactor);

        //Increment the height scale.
        top.setHeightScale(top.gethScale()*Const.gpHeightChangeFactor);
        bottom.setHeightScale(bottom.gethScale()*Const.gpHeightChangeFactor);

        //increment the speed scales, if the forward speed
        //is less than the max speed.
        if(top.getSpeed() <= Const.shipMaxForwardSpeed){
            top.setSpeed(top.getSpeed()+Const.gpFwdSpdChangeConstant);
            bottom.setSpeed(bottom.getSpeed() + Const.gpFwdSpdChangeConstant);
        }
        player.getShip().setStrafeSpeed(player.getShip().getStrafeSpeed()+Const.gpStfSpdChangeConstant);
    }
}
