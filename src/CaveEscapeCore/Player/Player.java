package CaveEscapeCore.Player;

import CaveEscapeCore.Constants.Const;
import CaveEscapeCore.GUIViews.EnhancedTextView;
import CaveEscapeCore.GUIViews.MultDisp;
import CaveEscapeCore.GUIViews.StatusBar;
import CaveEscapeCore.Pickups.PickupBag;
import CaveEscapeCore.SoundAndMusic.SFXMEngine;
import android.opengl.GLU;

import javax.microedition.khronos.opengles.GL10;

/**
 * The Player object. Many of the attributes are inherited from the type of
 * ship the user is using.
 */
public class  Player {

    public enum ScoreChangeType {
        NEAR_MISS,
        BONUS,
        STANDARD
    }

    /**
     * The player's score.
     */
    private float score;

    /**
     * The player's current score multiplier.
     */
    private float scoreMult;

    /**
     * The player's base score multiplier.
     */
    private float baseMult;

    /**
     * The location and previous location of the player's ship.
     */
     private float x, y, z;

    /**
     * The shift of the ship.
     */
    private float vx, vy;

    /**
     * Frames remaining until the multiplier is reset to
     * its base value.
     */
    private int multFrames;

    /**
     * The player's maximum health.
     */
    private int maxHealth;

    /**
     * The player's current health.
     */
    private int health;

    /**
     * The instance of the sound engine that we will use to play
     * the ship noises.
     */
    private SFXMEngine sfx;

    /**
     * Number of pickups collected.
     */
    int healthPickupsCollected;
    int multPickupsCollected;
    int pointPickupsCollected;

    /**
     * EnhancedTextViews that display how many of the pickups
     * have been collected.
     */
    EnhancedTextView healthPickupDisplay;
    EnhancedTextView multPickupDisplay;
    EnhancedTextView pointPickupDisplay;

    /**
     * The maximum multiplier
     * the player attained.
     */
    float maxMultiplier;

    /**
     * A reference to the Status Bar that will
     * display the Player's health.
     */
    private StatusBar healthBar;

    /**
     * A reference to the status bar that shows how much time of the multiplier
     * the player has left.
     */
    private StatusBar multTime;

    /**
     * A reference to the score display.
     */
    private EnhancedTextView scoreView;

    /**
     * A reference to a multiplier display.
     */
    private MultDisp multDisp;

    /**
     * A reference to the pickup bag.
     */
    private PickupBag bag;

    /**
     * The Player Event Listener that the player
     * will interact with.
     */
    private PlayerEventListenerI listener;


    /**
     * Is the player dead?
     */
    private boolean dead;

    /**
     * The Ship the user has selected. most of the performance attributes of the ship
     * are directly commuted to the player. This allows for the player to be rewarded
     * by using a more difficult ship.
     */
    private Ship ship;

    /**
     * The state of flight the ship is in.
     */
    ShipState state;

    /**
     * Constructs the Player.
     *
     * @param ship The Ship the user has selected.
     */
    public Player(Ship ship, SFXMEngine sfx, float x, float y, float z){

        //Initialize dead to false, since, well, the player is not yet dead.
        this.dead = false;

        this.ship = ship;

        this.sfx = sfx;

        //Set the maximum health of the player to the maximum damage of
        //their chosen ship.
        this.maxHealth = (int)ship.getMaxDamage();
        this.health = this.maxHealth;

        //Set the base multiplier of the player to the base multiplier of
        //their chosen ship.
        this.baseMult = ship.getBaseMult();

        this.x = x;
        this.y = y;
        this.z = z;

        this.state = ShipState.straight;

        this.score = 0;
        this.scoreMult = baseMult;

        //Initialize the gameplay stats.
        healthPickupsCollected = 0;
        multPickupsCollected = 0;
        pointPickupsCollected = 0;
        maxMultiplier = 0;



    }

    /**
     * Set the StatusBar that the player manipulates as
     * the health bar.
     */
    public void setHealthBar(StatusBar healthBar){
        this.healthBar = healthBar;
        this.healthBar.setMaxVal(getShip().getMaxDamage());
        this.healthBar.setVal(health);
    }

    /**
     * Set the Player Event Listener.
     */
    public void setPlayerEventListener(PlayerEventListenerI listener){
        this.listener = listener;
    }

    /**
     * Set the EnhancedTextView that the player imposes
     * its score on.
     */
    public void setScoreView(EnhancedTextView scoreView){
        this.scoreView = scoreView;
    }

    /**
     * Sets the MultDisp that the Player manipulates based on its
     * current multiplier.
     */
    public void setMultDisplay(MultDisp multDisp){
        this.multDisp = multDisp;
    }

    /**
     * Provides a reference to a StatusBar that the player uses
     * to display the remaining time on the current multiplier.
     * @param multTime The StatusBar to display the multiplier time
     *                 remaining.
     */
    public void setMultTimeDisplay(StatusBar multTime){
        this.multTime = multTime;
    }

    /**
     * Provides a reference to an EnhancedTextView that displays
     * the number of health pickups ever collected. 
     * @param healthPickupDisplay The EnhancedTextView to display
     *                            the number of health pickups collected.
     */
    public void setHealthPickupDisplay(EnhancedTextView healthPickupDisplay){
        this.healthPickupDisplay = healthPickupDisplay;
        this.healthPickupDisplay.setText("#HP: " + healthPickupsCollected);

    }

    /**
     * Provides a reference to an EnhancedTextView that displays the
     * number of multiplier pickups ever collected.
     * @param multPickupDisplay The EnhancedTextView to display
     *                          the number of multiplier pickups 
     *                          collected.
     */
    public void setMultPickupDisplay(EnhancedTextView multPickupDisplay){
        this.multPickupDisplay = multPickupDisplay;
        this.multPickupDisplay.setText("#MP: " + multPickupsCollected );
    }

    /**
     * Provides a reference to an EnhancedTextView that displays the
     * number of multiplier pickups ever collected.
     * @param pointPickupDisplay The EnhancedTextView to display
     *                           the number of point pickups
     *                           collected.
     */
    public void setPointPickupDisplay(EnhancedTextView pointPickupDisplay){
        this.pointPickupDisplay = pointPickupDisplay;
        this.pointPickupDisplay.setText("#PP: " + pointPickupsCollected);

    }

    public void setPickupBag(PickupBag bag){
        this.bag = bag;
    }

    /**
     * Returns the player's health.
     *
     * @return the player's current health.
     */
    public int getHealth(){
        return health;
    }

    /**
     * Changes the player's health. If the change makes the health value greater than the maximum health value,
     * then the current health is set to the maximum. If the change makes the player's health dip below zero, then
     *
     * @param change The amount by which to change the player's health.
     */
    public void changeHealth(int change){
        health += change;
        if(change > 0){
            healthPickupsCollected++;
            if(healthPickupDisplay != null){
                healthPickupDisplay.setText("#HP: " + healthPickupsCollected);
            }
        }
        else if(change < 0){
            sfx.playCollideTerrain(0, .8f);
        }
        if(health > maxHealth){
            health = maxHealth;
        }
        if(health < 0){
            dead = true;
        }
        if(healthBar != null){
            healthBar.setVal(health);
        }
    }

    /**
     * Returns the Player's score multiplier.
     *
     * @return The current score multiplier.
     */
    public float getMultiplier(){
        return scoreMult;
    }

    /**
     * Sets the current score multiplier. The behaviour here is up for debate.
     *
     * @param multiplier The intended multiplier
     * @param framesOfEffect How many frames should the current score multiplier last.
     */
    public void setMultiplier(int multiplier, int framesOfEffect){
        scoreMult += multiplier;
        multPickupsCollected++;
        if(multPickupDisplay != null){
            multPickupDisplay.setText("#MP: " + multPickupsCollected );
        }
        if(scoreMult > maxMultiplier){
            maxMultiplier = scoreMult;
        }
        if(multDisp != null){
            multDisp.setMult(scoreMult);
        }
        if(multFrames > Const.gpMinMultFrames){
            multFrames = framesOfEffect;
        }
        else{
            multFrames = (multFrames + framesOfEffect)/2;
        }
        if(multTime != null){
            multTime.setMaxVal(multFrames);
            multTime.setVal(multFrames);
        }
    }

    /**
     * Returns the Ship instance of the Player's ship.
     * @return ship
     */
    public Ship getShip(){
        return ship;
    }

    /**
     * Returns whether or not the player is dead.
     *
     * @return Is the player dead?
     */
    public boolean isDead(){
        return dead;
    }

    /**
     * Returns the player's score.
     *
     * @return The player's score.
     */
    public float getScore(){
        return score;
    }

    /**
     * Changes the player's score. The incoming score is multiplied by the current
     * score multiplier.
     *
     * @param change The amount of points to add.
     * @param type The type of score change, used for adding tags.
     */
    public void changeScore(float change, ScoreChangeType type){
        score += (change*scoreMult);
        String newText = "";
        switch (type) {
            case NEAR_MISS:
                newText += "NEAR MISS!  ";
                break;
            case BONUS:
                newText += "ITEM BONUS!  ";
                pointPickupsCollected++;
                if(pointPickupDisplay != null){
                    pointPickupDisplay.setText("#PP: " + pointPickupsCollected);
                }
                break;
            case STANDARD:
                break;
        }
        if(scoreView != null){
            scoreView.setText(newText + String.format("%,2d", (int) score));
        }
    }

    /**
     * Returns the location of the player's ship in game space. This is
     * used for collision with the Terrain and pickups.
     * @return a float array of length 3 containing the
     *         coordinates of the ship's cardinal center.
     */
    public float[] getShipLocation(){
        return new float[]{x, y, z};
    }

    /**
     * Returns the X location of the center of the ship.
     * @return x
     */
    public float getX() {
        return x;
    }

    /**
     * Allows the modification of the X-Location of the ship.
     * @param x The new X Location of the Ship.
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Returns the Y location of the center of the ship.
     * @return y
     */
    public float getY() {
        return y;
    }

    /**
     * Allows the modification of the Y-Location of the ship.
     * @param y The new Y-Location of the Ship.
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * Returns the Z location of the center of the ship.
     * @return z
     */
    public float getZ() {
        return z;
    }

    /**
     * Allows the modification of the Z Location of the ship.
     * @param z The new Z Location of the Ship.
     */
    public void setZ(float z) {
        this.z = z;
    }

    public void setXVelocity(float vx){
        this.vx = vx;
    }

    public void setYVelocity(float vy){
        this.vy = vy;
    }

    public float getXVelocity(){
        return vx;
    }

    public float getYVelocity(){
        return vy;
    }

    /**
     * Allows for the changing of the ship's location, based on it's nose--the collision point.
     * @param x The new x location.
     * @param y The new y location.
     * @param z The new z location.
     */
    public void setNoseLocation(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Returns the ShipState of the Player's ship.
     * @return state
     */
    public ShipState getState() {
        return state;
    }

    /**
     * Allows the setting of the Player's Ship's ShipState.
     * @param state The new ShipState.
     */
    public void setState(ShipState state) {
        this.state = state;
    }

    /**
     * Draws the player's ship. It takes into account whether or not the
     * ship is moving, and sets the strafe state accordingly.
     */
    public void drawShip(GL10 gl, boolean actuallyDraw){

        //Handle movement.
        x+=vx;
        y+=vy;

        if(bag != null){
            bag.testPickupCollisions(this);
        }

        //Set state.
        if(state == ShipState.menuRotate){
            //Do nothing.
        }

        else if(vx > ship.getStrafeSpeed()*Const.gpShipAnimSensitivity){

            if(vy > ship.getStrafeSpeed()*Const.gpShipAnimSensitivity*3){
                state = ShipState.strafeUpRight;
            }
            else if(vy < -ship.getStrafeSpeed()*Const.gpShipAnimSensitivity*3){
                state = ShipState.strafeDownRight;
            }
            else{
                state = ShipState.strafeRight;
            }

        }

        else if(vx < -ship.getStrafeSpeed()*Const.gpShipAnimSensitivity){
            if(vy > ship.getStrafeSpeed()*Const.gpShipAnimSensitivity*3){
                state = ShipState.strafeUpLeft;
            }
            else if(vy < -ship.getStrafeSpeed()*Const.gpShipAnimSensitivity*3){
                state = ShipState.strafeDownLeft;
            }
            else{
                state = ShipState.strafeLeft;
            }
        }

        else if(vy > ship.getStrafeSpeed()*Const.gpShipAnimSensitivity){
            state = ShipState.strafeUp;
        }

        else if(vy < -ship.getStrafeSpeed()*Const.gpShipAnimSensitivity){
            state = ShipState.strafeDown;
        }

        else{
            state = ShipState.straight;
        }

        //Draw the player's ship.
        if(state != ShipState.menuRotate) {
            GLU.gluLookAt(gl,
                    x + (Const.camSlideFactorBack*(-vx)/ Const.camTiltFactor),
                    y+ (Const.camSlideFactorBack*(-vy)/ Const.camTiltFactor),
                    1,
                    x + (Const.camSlideFactorFront*(-vx)/ Const.camTiltFactor),
                    y+ (Const.camSlideFactorFront*(-vy)/ Const.camTiltFactor),
                    0,
                    (vx*Const.camShipVelSoften) / Const.camTiltFactor,
                    1,
                    (vy*Const.camShipVelSoften) / Const.camTiltFactor
            );
        }
        if(actuallyDraw){
            //Draw the ship.
            ship.drawAtLocation(gl, x, y, z, vx, vy, state);

            //Play the ship noise.
            float velocity = (vx + vy)/2;
            sfx.playShipForward(-(velocity/ship.getStrafeSpeed()), Math.abs(velocity/ship.getStrafeSpeed())+(.2f));

            if(multFrames > 0){
                multFrames --;
                if(multTime != null){
                    if(multFrames % Const.gpStatusBarFrameSkip == 0 )
                    multTime.setVal(multTime.getVal()-Const.gpStatusBarFrameSkip );
                }
                if(multFrames == 120){
                    sfx.playMultiplierAlmostUp(0, .6f);
                }
            }
            if(multDisp != null && multFrames == 0){
                multDisp.setMult(baseMult);
                multTime.setVal(0);
                scoreMult = baseMult;
                sfx.playMultiplierOver(0, .8f);

                multFrames = -1;
            }
        }


    }

}
