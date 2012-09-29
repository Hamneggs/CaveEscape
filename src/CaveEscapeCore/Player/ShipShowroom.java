package CaveEscapeCore.Player;

import CaveEscapeCore.Constants.Const;
import CaveEscapeCore.SoundAndMusic.SFXMEngine;
import CaveEscapeCore.Terrain.PerlinTerrainMenu;
import android.content.Context;
import android.util.FloatMath;

import javax.microedition.khronos.opengles.GL10;
import java.util.ArrayList;

/**
 * Draws several ships in a circle on the X-Z plane.
 * This encapsulates all OpenGL components of the
 * Ship selection menu.
 * @see BasicShip
 * @see PerlinTerrainMenu
 * @see Player
 */
public class ShipShowroom {

    /**
     * The ArrayList of Players that make up the
     * selection of the showroom.
     */
    private ArrayList<Player> ships;

    /**
     * The center of the circle. of ships.
     */
    private float centerX, centerY, centerZ;

    /**
     * The radius of the circle of ships.
     */
    private float radius;

    /**
     * the current angle of the showroom, the angle of the
     * showroom that the currently highlighted ship is at,
     * and the unit angle to rotate between them each frame.
     */
    private float curRot, nextRot, unit;

    /**
     * The offset required to place the first ship in front of the
     * camera at startup.
     */
    float frontAngle;

    /**
     * The instance of the sound engine that is passed to the players.
     */
    SFXMEngine sfx;

    /**
     * The index of the current selection within the
     * Player ArrayList, as well as the index
     * of the previous selection.
     */
    private int current, previous;

    /**
     * The PerlinTerrainMenus that makes the backdrop of the showroom.
     */
    private PerlinTerrainMenu bgTop, bgBottom;

    /**
     * Constructs the ShipShowroom.
     * @param centerX The center of the ShipShowroom's circle of ships.
     * @param centerY The center of the ShipShowroom's circle of ships.
     * @param centerZ The center of the ShipShowroom's circle of ships.
     * @param radius The radius of this circle.
     * @param terrainR The color of the Showroom's terrain.
     * @param terrainG The color of the Showroom's terrain.
     * @param terrainB The color of the Showroom's terrain.
     */
    public ShipShowroom(float centerX, float centerY, float centerZ, float radius, float frontAngle, float terrainR, float terrainG, float terrainB, SFXMEngine sfx, Context context){

        this.centerX = centerX;
        this.centerY = centerY;
        this.centerZ = centerZ;
        this.radius = radius;
        this.frontAngle = frontAngle;
        this.sfx = sfx;

        //Initialize everything so we don't have wonky behaviour.
        unit = 0f;
        curRot = 0;
        nextRot = 0;
        current = 0;
        previous = 0;

        //Create the ShipArray. Yes, it is actually of the Player type, but
        //that is merely for ease of drawing, and selection.
        ships = new ArrayList<Player>();

        //Create the seed for the PerlinTerrain.
        float seed = Const.smSeedSize*(float)Math.random(), deviation = Const.smDevSize * (float)Math.random();

        bgTop = new PerlinTerrainMenu(
                0,
                Const.smVertOffset,
                0,
                Const.smTerSize,
                Const.smTerSize,
                Const.smTerRes,
                Const.smTerRes,
                0,  //The Terrain shouldn't rotate independently of the Ships.
                Const.smFDensity,
                Const.smHScale,
                Const.smSScale,
                seed+deviation,
                true, //The height values should be added to the Y-Coordinate of the point.
                true   //This is being used for the ship selection screen.
        );
        bgBottom = new PerlinTerrainMenu(
                0,
                -1.5f,
                0,
                Const.smTerSize,
                Const.smTerSize,
                Const.smTerRes,
                Const.smTerRes,
                0,  //The Terrain shouldn't rotate independently of the Ships.
                Const.smFDensity,
                Const.smHScale,
                Const.smSScale,
                seed-deviation,
                false,//The height values should be added to the Y-Coordinate of the point.
                true  //This is being used for the ship selection screen.
        );

        //Set the color of the terrain to the given color. We want some excitement.
        //The alpha however will always be solid.
        bgTop.setColor(terrainR, terrainG, terrainB, Const.smTerAlpha);
        bgBottom.setColor(terrainR, terrainG, terrainB, Const.smTerAlpha);

    }

    /**
     * Adds a ship to the Showroom floor (circle).
     * @param ship A new Ship object.
     */
    public void addShip(Ship ship){

        //Add the ship at a placeholder location, in the menuRotate state.
        Player toAdd = new Player(ship, sfx, 0, 0, 0);
        toAdd.setState(ShipState.menuRotate);
        ships.add(toAdd);

    }

    /**
     * Organizes the ships in the showroom into a circle,
     * and sets their states to menuRotation.
     */
    public void organizeSelection(){

        //Since we remove and add the ships,
        //we need a consistent size for our for-loop.
        int size = ships.size();

        for(int i = 0; i < size; i++){
            Player temp = ships.remove(i);

            //Use trig!
            temp.setX(
                    ( FloatMath.cos( ((float)(Math.PI*2) * (i / (float)(size))) + frontAngle)  * radius)
            );
            //Don't use trig!
            temp.setY(centerY);
            //Use trig!
            temp.setZ(
                    ( FloatMath.sin( ((float)(Math.PI*2) * (i / (float)(size))) + frontAngle)  * radius)
            );

            //Reset the ship's state to Menu Rotation.
            temp.setState(ShipState.menuRotate);

            //Finally add it back to the list.
            ships.add(i, temp);

        }
    }

    /**
     * Draws all the Ships in the showroom and the backdrop
     * PerlinTerrainMenu, and also handles the rotation
     * of all this geometry.
     * @param gl The GL10 instance that the game is using.
     */
    public void drawShowroom(GL10 gl){

        //Perform the rotation of curRot on the OpenGL state.
        rotateGL(gl);

        //Draw the terrain.
        bgTop.drawTerrain(gl);
        bgBottom.drawTerrain(gl);

        //Draw all the ships.
        for(Player p : ships){
            p.drawShip(gl, true);
        }

        //If the current angle of rotation is not the angle of the currently
        //selected Ship, increment the current angle by the unit angle.
        //Note that we have to account for which way the user turns
        //the screen.
        if(current >= previous){
            if( Math.abs(curRot) > Math.abs(nextRot) + (unit*Const.smRotAccuracy) || Math.abs(curRot) < Math.abs(nextRot) - (unit*Const.smRotAccuracy) ){
                curRot+=unit;
            }
        }
        else{
            if( Math.abs(curRot) < Math.abs(nextRot) + (unit*Const.smRotAccuracy) || Math.abs(curRot) > Math.abs(nextRot) - (unit*Const.smRotAccuracy) ){
                curRot+=unit;
            }
        }

    }

    /**
     * Performs the rotation of the gl state needed for the animation of the background.
     * @param gl The GL10 instance that the game is using.
     */
    public void rotateGL(GL10 gl){

        //Refresh the ModelView matrix.
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        //Notice how OpenGL uses DEGREES! JERKS!
        gl.glRotatef((float)Math.toDegrees(curRot), 0f, 1f, 0f);
    }

    /**
     * Rotates the showroom to the left by changing
     * nextRot and the unit angle.
     * It also increments the index of the
     * current selection.
     */
    public void rotateLeft(){

        //Set up the rotation.
        float diff = (float)( Const.TWO_PI / (float)ships.size() );
        nextRot-=diff;
        unit = (nextRot-curRot)/15;

        //Store the previous index.
        previous = current;
        //Change the current index.
        current --;

        //Play the menu move sound.
        sfx.playMenuMove(-.8f, .8f);

    }

    /**
     * Rotates the showroom to the right by changing
     * nextRot and the unit angle.
     * It also increments the index of the
     * current selection.
     */
    public void rotateRight(){

        //Set up the rotation.
        float diff = (float)( Const.TWO_PI / (float)ships.size() );
        nextRot+=diff;
        unit = (nextRot-curRot)/15;

        //Store the previous index.
        previous = current;

        //Change the current index.
        current ++;

        sfx.playMenuMove(5f, .8f);

    }

    /**
     * Returns the X-coordinate of the
     * center of the showroom.
     * @return centerX
     */
    public float getCenterX() {
        return centerX;
    }

    /**
     * Returns the Y-coordinate of the
     * center of the showroom.
     * @return centerY
     */
    public float getCenterY() {
        return centerY;
    }

    /**
     * Returns the Z-coordinate of the
     * center of the showroom.
     * @return centerZ
     */
    public float getCenterZ() {
        return centerZ;
    }

    /**
     * Returns the currently selected ship.
     * @return The currently selected ship.
     */
    public Player getCurrentSelection(){
        int errorCorrection = current;
        while(errorCorrection < 0)errorCorrection+=ships.size();
        while(errorCorrection >= ships.size())errorCorrection-=ships.size();

        //Play the selection sound.
        sfx.playMenuSelect(0, .8f);

        //Then we mod the current index by the size of the ArrayList
        //for redundant safety.
        return ships.get(Math.abs(errorCorrection)%ships.size());
    }
}
