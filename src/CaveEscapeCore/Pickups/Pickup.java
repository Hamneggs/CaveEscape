package CaveEscapeCore.Pickups;

import CaveEscapeCore.Player.Player;

import javax.microedition.khronos.opengles.GL10;

/**
 This is the base class of all pickups. It defines location
 and color, and requires the user to define what happens
 when it is drawn and when it is collected.
 */
public abstract class Pickup {

    //The color and location variables.
    protected float r, g, b, a, x, y, z, sx, sy, sz;

    //The type of pickup this is.
    protected PickupType pType;

    //The class of pickup this is.
    protected PickupClass pClass;

    /**
     * Constructs the base pickup. Color values should be normalized to the range [0-1].
     *
     * @param r The intended red value.
     * @param g The intended green value.
     * @param b The intended blue value.
     * @param x The intended x coordinate.
     * @param y The intended y coordinate.
     * @param z The intended z coordinate.
     * @param sx The intended x size.
     * @param sy The intended y size.
     * @param sz The intended z size.
     */
    public Pickup(
                  PickupType pType,
                  PickupClass pClass,
                  float r,
                  float g,
                  float b,
                  float x,
                  float y,
                  float z,
                  float sx,
                  float sy,
                  float sz ){

        this.pType = pType;
        this.pClass = pClass;

        this.r = r;
        this.g = g;
        this.b = b;
        this.a = 1f;

        this.x = x;
        this.y = y;
        this.z = z;

        //We take the absolute value of the given
        //dimensions, so that we don't ever have a negative size.
        this.sx = Math.abs(sx);
        this.sy = Math.abs(sy);
        this.sz = Math.abs(sz);

    }

    public void incrementLocation(float changeX, float changeY, float changeZ){
        this.x += changeX;
        this.y += changeY;
        this.z += changeZ;
    }

    /**
     * The draw method of the pickup. Draws the pickup geometry.
     * @param gl The GL10 object that the game is using.
     */
    public abstract void draw(GL10 gl);

    /**
     * Defines what happens to the Player "player" when it collects this pickup.
     * @param player The player that collects this pickup.
     */
    public abstract void collected(Player player);

    /**
     * Defines functionality for testing if the Player "player"
     * has collided with the pickup.
     * @param player The player to test collision with.
     * @return A boolean value representing if the player collided with the pickup.
     */
    public abstract boolean collide(Player player);

    /**
     * Returns the x coordinate of this pickup.
     * @return The x location of this pickup.
     */
    public float getX(){ return x; }

    /**
     * Returns the y coordinate of this pickup.
     * @return The y location of this pickup.
     */
    public float getY(){ return y; }

    /**
     * Returns the z coordinate of this pickup.
     * @return The z location of this pickup.
     */
    public float getZ(){ return z; }

    public PickupType getPickupType(){
        return pType;
    }

    public PickupClass getPickupClass(){
        return pClass;
    }

}
