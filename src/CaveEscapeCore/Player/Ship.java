package CaveEscapeCore.Player;

import CaveEscapeCore.Constants.Const;

import javax.microedition.khronos.opengles.GL10;

/**
 * The base class for all Player ship models. It merely specifies that the
 * ship be able to be drawn at a location. Additionally, it encapsulates
 * the name of the ship, it's forward speed, its
 */
public abstract class Ship {

    /**
     *The name of the ship.
     */
    private String name;

    /**
     *The forward traversal speed of the ship.
     */
    private float fSpeed;

    /**
     *The strafing speed of the ship.
     */
    private float sSpeed;

    /**
     *The current rotation of the ship. The way OpenGL rotates is by taking an angle,
     *then the vector of the axis one wants to rotate the geometry around. Hence, we only
     *need to store the angle, in degrees.
     */
    private float rot;

    /**
     *The base score multiplier for this ship, based on their
     *skill needed to use the ship effectively.
     */
    private float baseMult;

    /**
     *The maximum health this ship has.
     */
    private int maxDamage;


    /**
     * Constructs the base Ship.
     *
     * @param name  The ship's model name.
     * @param fSpeed  The base forward speed of the ship.
     * @param sSpeed  The base strafing speed of the ship.
     * @param maxDamage The maximum damage this ship can take.
     */
    public Ship(String name, float fSpeed, float sSpeed, int maxDamage){

        this.name = name;
        this.fSpeed = fSpeed;
        this.sSpeed = sSpeed;
        this.maxDamage = maxDamage;

        //The starting rotation is where the ship is facing forward.
        this.rot = 0f;

        //The base score multiplier is:
        // 1: The ratio of the forward speed to the strafe speed. This accounts
        //    for how easy it is to react to things.
        // 2: 500 is the maximum health of the best ship. This accounts for
        //    the advantage a lot of health would give.
        // 3: The maximum speed I can even consider is 3, sadly. Thus this accounts for
        //    the advantage given by a slow ship.
        this.baseMult = ((fSpeed* Const.shipSpdDiffRatioFactor)/sSpeed) +
                ( (float)(Const.shipMaxPosHealth -maxDamage)/(float) Const.shipMaxPosHealth) +
                (fSpeed/ Const.shipMaxForwardSpeed);
    }



    /**
     * Defines functionality for drawing the ship at the coordinates x, y, and z.
     *
     * @param gl The GL10 object that the game is using.
     * @param x The intended x location for the ship to be drawn at.
     * @param y The intended y location for the ship to be drawn at.
     * @param z The intended z location for the ship to be drawn at.
     * @param vx The x velocity of the ship, used for movement rotation.
     * @param vy The y velocity of the ship. used for movement rotation.
     */
    public abstract void drawAtLocation(GL10 gl, float x, float y, float z, float vx, float vy, ShipState state);

    /**
     * Returns the Ship's model name.
     *
     * @return  The model name of the ship.
     */
    public String getName(){
        return name;
    }

    /**
     * Returns the forward speed of the ship.
     *
     * @return The speed at which the ship should move forward.
     */
    public float getForwardSpeed(){
        return fSpeed;
    }

    /**
     * Returns the strafing speed of the ship.
     *
     * @return The speed at which the ship strafes left and right.
     */
    public float getStrafeSpeed(){
        return sSpeed;
    }

    /**
     * Allows the changing of the stafe speed.
     *
     * @param sSpeed The new strafing speed.
     */
    public void setStrafeSpeed(float sSpeed){
        this.sSpeed = sSpeed;
    }

    /**
     * Returns the current number of degrees the ship is rotated in.
     * @return The number of degrees the ship is currently rotated by.
     */
    public float getRotation(){
        return rot;
    }

    /**
     * Returns the base multiplier, which is calculated based on the following
     * equation:
     *
     * 1: The ratio of the forward speed to the strafe speed. This accounts
     *    for how easy it is to react to things.
     * 2: Const.shipMaxPosHealth is the maximum health of the best ship. This accounts for
     *    the advantage a lot of health would give.
     * 3: The maximum speed I can even consider is Const.shipMaxForwardSpeed, sadly. Thus this accounts for
     *    the advantage given by a slow ship.
     *
     * EQUATION:                                                                                 TERM:
     * baseMult = ((fSpeed*Const.shipSpeedDiffRatioFactor)/sSpeed) +                            |  1  |
     *            ( (float)(Const.shipMaxPosHealth-maxDamage)/(float)Const.shipMaxPosHealth) +  |  2  |
     *            (fSpeed/Const.shipMaxForwardSpeed);                                           |  3  |
     *
     * @return The base multiplier.
     */
    public float getBaseMult(){
        return baseMult;
    }

    /**
     * Returns the maximum damage this ship can take. This should directly transfer
     * to the player's max health.
     * @return
     */
    public float getMaxDamage(){
        return maxDamage;
    }

    /**
     * Increments the current rotation of the ship by the specified amount.
     *
     * @param rotChange The intended change to the ship's x rotation.

     */
    public void rotateShip(float rotChange){
        rot += rotChange;
    }

    /**
     * Sets the current rotation of the ship, unlike rotateShip(), which merely
     * increments the rotation values.
     * @param rot The intended rotation of the ship, in degrees.
     */
    public void setShipRotation(float rot){
        this.rot = rot;
    }

}
