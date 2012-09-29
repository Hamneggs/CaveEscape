package CaveEscapeCore.Pickups;

import CaveEscapeCore.Constants.Const;
import CaveEscapeCore.CoreGameplay.GameplayMode;
import CaveEscapeCore.Player.Player;
import CaveEscapeCore.SoundAndMusic.SFXMEngine;

import javax.microedition.khronos.opengles.GL10;
import java.util.ArrayList;

/**
 * This class represents the collection of pickups in play. It
 * draws each pickup, tests it for collision, moves it, and
 * even replaces pickups that have been collected/missed.
 */
public class PickupBag {

    /**
     * The underlying ArrayList that stores the pickups.
     */
    private ArrayList<Pickup> bag;

    /**
     * The max capacity of the bag.
     */
    private int maxCapacity;

    /**
     * The player instance that we use for testing collision
     * against.
     */
    private Player player;

    /**
     * The instance of our sound engine with which to play the collection sounds.
     */
    private SFXMEngine sfx;

    /**
     * The gameplay mode that we are currently using. This allows us to differentiate
     * the rules of the game.
     */
    private GameplayMode mode;

    /**
     * Constructs the Pickup Bag.
     * @param maxCapacity The maximum capacity of the bag, or more relevantly, the
     *                    maximum number of pickups in play.
     * @param player The player we test for collision against.
     */
    public PickupBag(int maxCapacity, Player player, SFXMEngine sfx, GameplayMode mode){

        this.maxCapacity = maxCapacity;

        this.player = player;

        this.sfx = sfx;

        this.mode = mode;

        //Initialize the actual bag.
        bag = new ArrayList<Pickup>(maxCapacity);
        for(int i = 0; i < maxCapacity; i++){
            bag.add(new PointPickup(10000, PickupClass.MINOR, mode, .2f, 1f, .2f, -3f+((i*.1f)), 0f, -5f, .5f, .5f, .5f));
        }

    }

    /**
     * Populates the bag with all new pickups at random locations.
     * @param mode The gameplay mode that the user has chosen.
     */
    public void refreshBag(GameplayMode mode){

        bag = new ArrayList<Pickup>(maxCapacity);

        for(int i = 0; i < maxCapacity; i++){

            int seed = (int)(3*Math.random());
            float x = (float)Math.random() * ((Const.gpTWidth/2.00f)-(Const.gpTWidth))* Const.ptgHorizFraction;
            float y = 0;
            float z = -(float)Math.random() * Const.gpTDepth;
            switch(mode){

                case TimeAttack:

                    switch(seed){
                        case 0:
                            if(i > maxCapacity*.75f){
                                addHealthPickup(PickupClass.MAJOR, x, y, z);
                            }
                            else if(i > maxCapacity *.4f){
                                addHealthPickup(PickupClass.MEDIUM, x, y, z);
                            }
                            else{
                                addHealthPickup(PickupClass.MINOR, x, y, z);
                            }
                            break;

                        case 1:
                            if(i > maxCapacity*.75f){
                                addMultPickup(PickupClass.MAJOR, x, y, z);
                            }
                            else if(i > maxCapacity *.4f){
                                addMultPickup(PickupClass.MEDIUM, x, y, z);
                            }
                            else{
                                addMultPickup(PickupClass.MINOR, x, y, z);
                            }
                            break;
                        case 2:
                            if(i > maxCapacity*75f){
                                addMultPickup(PickupClass.MEDIUM, x, y, z);
                            }
                            else if(i > maxCapacity *4f){
                                addHealthPickup(PickupClass.MEDIUM, x, y, z);
                            }
                            else{
                                addHealthPickup(PickupClass.MINOR, x, y, z);
                            }
                            break;


                    }
                    break;

                case Classic:

                    switch(seed){
                        case 0:
                            if(i > maxCapacity*.5f){
                                addHealthPickup(PickupClass.MAJOR, x, y, z);
                            }
                            else if(i > maxCapacity *.25f){
                                addHealthPickup(PickupClass.MEDIUM, x, y, z);
                            }
                            else{
                                addHealthPickup(PickupClass.MINOR, x, y, z);
                            }
                            break;

                        case 1:
                            if(i > maxCapacity*.5f){
                                addMultPickup(PickupClass.MAJOR, x, y, z);
                            }
                            else if(i > maxCapacity *.25f){
                                addMultPickup(PickupClass.MEDIUM, x, y, z);
                            }
                            else{
                                addMultPickup(PickupClass.MINOR, x, y, z);
                            }
                            break;
                        case 2:
                            if(i > maxCapacity*.5f){
                                addPointPickup(PickupClass.MAJOR, x, y, z);
                            }
                            else if(i > maxCapacity *.25f){
                                addPointPickup(PickupClass.MEDIUM, x, y, z);
                            }
                            else{
                                addPointPickup(PickupClass.MINOR, x, y, z);
                            }
                            break;
                    }
                    break;

                case Survival:

                    switch(seed){
                        case 0:
                            if(i > maxCapacity*.5f){
                                addPointPickup(PickupClass.MAJOR, x, y, z);
                            }
                            else if(i > maxCapacity *.25f){
                                addPointPickup(PickupClass.MEDIUM, x, y, z);
                            }
                            else{
                                addPointPickup(PickupClass.MINOR, x, y, z);
                            }
                            break;

                        case 1:
                            if(i > maxCapacity*.5f){
                                addMultPickup(PickupClass.MAJOR, x, y, z);
                            }
                            else if(i > maxCapacity *.25f){
                                addMultPickup(PickupClass.MEDIUM, x, y, z);
                            }
                            else{
                                addMultPickup(PickupClass.MINOR, x, y, z);
                            }
                            break;
                        case 2:
                            if(i > maxCapacity*.5f){
                                addPointPickup(PickupClass.MAJOR, x, y, z);
                            }
                            else if(i > maxCapacity *.25f){
                                addPointPickup(PickupClass.MEDIUM, x, y, z);
                            }
                            else{
                                addPointPickup(PickupClass.MINOR, x, y, z);
                            }
                            break;
                    }
                    break;
            }
        }
    }

    /**
     * Adds a new health pickup to the bag, at location (x, y, z).
     * @param c The class of the pickup.
     * @param x The location of the pickup.
     * @param y The location of the pickup.
     * @param z The location of the pickup.
     */
    private void addHealthPickup(PickupClass c, float x, float y, float z){
        if(player!=null){
            switch (c) {

                case MINOR:
                    bag.add(new HealthPickup(
                            (int)(player.getShip().getMaxDamage()/8),
                            c,
                            mode,
                            Const.clrHealthMinorR, Const.clrHealthMinorG, Const.clrHealthMinorB,
                            x, y, z,
                            .5f,
                            .5f,
                            .5f)
                    );
                    break;
                case MEDIUM:
                    bag.add(new HealthPickup(
                            (int)(player.getShip().getMaxDamage()/4),
                            c,
                            mode,
                            Const.clrHealthMediumR, Const.clrHealthMediumG, Const.clrHealthMediumB,
                            x, y, z,
                            .5f,
                            .5f,
                            .5f)
                    );
                    break;
                case MAJOR:
                    bag.add(new HealthPickup(
                            (int)(player.getShip().getMaxDamage()/3),
                            c,
                            mode,
                            Const.clrHealthMajorR, Const.clrHealthMajorG, Const.clrHealthMajorB,
                            x, y, z,
                            .5f,
                            .5f,
                            .5f)
                    );
                    break;

            }
        }
        else{
            switch (c) {

                case MINOR:
                    bag.add(new HealthPickup(
                            60,
                            c,
                            mode,
                            Const.clrHealthMinorR, Const.clrHealthMinorG, Const.clrHealthMinorB,
                            x, y, z,
                            .5f,
                            .5f,
                            .5f)
                    );
                    break;
                case MEDIUM:
                    bag.add(new HealthPickup(
                           (60),
                            c,
                            mode,
                            Const.clrHealthMediumR, Const.clrHealthMediumG, Const.clrHealthMediumB,
                            x, y, z,
                            .5f,
                            .5f,
                            .5f)
                    );
                    break;
                case MAJOR:
                    bag.add(new HealthPickup(
                            (60),
                            c,
                            mode,
                            Const.clrHealthMajorR, Const.clrHealthMajorG, Const.clrHealthMajorB,
                            x, y, z,
                            .5f,
                            .5f,
                            .5f)
                    );
                    break;
            }
        }
    }

    /**
     * Adds a new multiplier pickup to the bag, at location (x, y, z).
     * @param c The class of the pickup.
     * @param x The location of the pickup.
     * @param y The location of the pickup.
     * @param z The location of the pickup.
     */
    private void addMultPickup(PickupClass c, float x, float y, float z){

        switch (c) {

            case MINOR:
                bag.add(new MultPickup(
                        2, 3600,
                        c,
                        mode,
                        Const.clrMultMinorR, Const.clrMultMinorG, Const.clrMultMinorB,
                        x, y, z,
                        .5f,
                        .5f,
                        .5f)
                );
                break;
            case MEDIUM:
                bag.add(new MultPickup(
                        3, 3000,
                        c,
                        mode,
                        Const.clrMultMediumR, Const.clrMultMediumG, Const.clrMultMediumB,
                        x, y, z,
                        .5f,
                        .5f,
                        .5f)
                );
                break;
            case MAJOR:
                bag.add(new MultPickup(
                        5, 2400,
                        c,
                        mode,
                        Const.clrMultMajorR, Const.clrMultMajorG, Const.clrMultMajorB,
                        x, y, z,
                        .5f,
                        .5f,
                        .5f)
                );
                break;

        }
    }

    /**
     * Adds a new point pickup to the bag, at location (x, y, z).
     * @param c The class of the pickup.
     * @param x The location of the pickup.
     * @param y The location of the pickup.
     * @param z The location of the pickup.
     */
    private void addPointPickup(PickupClass c, float x, float y, float z){

        switch (c) {

            case MINOR:
                bag.add(new PointPickup(
                        1000,
                        c,
                        mode,
                        Const.clrPointMinorR, Const.clrPointMinorG, Const.clrPointMinorB,
                        x, y, z,
                        .5f,
                        .5f,
                        .5f)
                );
                break;
            case MEDIUM:
                bag.add(new PointPickup(
                        5000,
                        c,
                        mode,
                        Const.clrPointMediumR, Const.clrPointMediumG, Const.clrPointMediumB,
                        x, y, z,
                        .5f,
                        .5f,
                        .5f)
                );
                break;
            case MAJOR:
                bag.add(new PointPickup(
                        10000,
                        c,
                        mode,
                        Const.clrPointMajorR, Const.clrPointMajorB, Const.clrPointMajorB,
                        x, y, z,
                        .5f,
                        .5f,
                        .5f)
                );
                break;

        }

    }


    /**
     * Draws all the pickups.
     * @param gl The GL10 instance that the game is using.
     */
    public void drawBag(GL10 gl){

        for (Pickup p : bag) {

            //Get the current pickup.
            //Set the proper matrix mode.
            gl.glMatrixMode(GL10.GL_MODELVIEW);

            //Draw the pickup.
            p.draw(gl);
        }
    }

    /**
     * Performs collision and culling duties.
     */
    public void testPickupCollisions(Player player){


        for(int i = 0; i < bag.size(); i++){
            //Get the current pickup.
            Pickup p = bag.get(i);

            //If the pickup is behind the player, and camera, (or was collected by the player)
            //we remove that pickup instance we remove that pickup instance from the bag,
            //then add a new one at the far end of the terrain.
            //if(player != null){
            if(p.collide(player)){


                //Edit the Player instance based on the pickup collected.
                p.collected(player);

                //Play the sound based on the type and class of pickup collected.
                playSound(p);

                bag.remove(i);

                //refreshBag(GameplayMode.Classic);
                //Add a new pickup.
                addNewPickup();

            }
            //}
            if(p.getZ() >= 0f){

                bag.remove(i);

                //Add a new one.
                addNewPickup();

            }
            //else bag.add(i, p);
        }
    }

    private void playSound(Pickup p){
        switch (p.getPickupType()) {
            case HEALTH:
                switch (p.getPickupClass()) {
                    case MINOR:
                        sfx.playCollectHealthPickupA(0, .8f);
                        break;
                    case MEDIUM:
                        sfx.playCollectHealthPickupB(0, .8f);
                        break;
                    case MAJOR:
                        sfx.playCollectHealthPickupC(0, .8f);
                        break;
                }
                break;
            case MULT:
                switch (p.getPickupClass()) {
                    case MINOR:
                        sfx.playCollectMultPickupA(0, .8f);
                        break;
                    case MEDIUM:
                        sfx.playCollectMultPickupB(0, .8f);
                        break;
                    case MAJOR:
                        sfx.playCollectMultPickupC(0, .8f);
                        break;
                }
                break;
            case POINT:
                switch (p.getPickupClass()) {
                    case MINOR:
                        sfx.playCollectPointPickupA(0, .8f);
                        break;
                    case MEDIUM:
                        sfx.playCollectPointPickupB(0, .8f);
                        break;
                    case MAJOR:
                        sfx.playCollectPointPickupC(0, .8f);
                        break;
                }
                break;
        }
    }

    /**
     * Increments the location of the pickups.
     */
    public void incrementPickupLocations(float x, float y, float z){
        for (Pickup p : bag) {
            p.incrementLocation(x, y, z);
        }
    }

    /**
     * Adds a new, random pickup to the bag.
     */
    private void addNewPickup(){
        //Create random samples to determine what type and class of pickup to add.
        float sampleA = (float)Math.random();
        float sampleB = (float)Math.random();

        //Generate the location of the new Pickup. We create all pickups along the
        //plane Y=0, and at a random X location, but always at the far end of the terrain.
        float x = (float)Math.random() * ((Const.gpTWidth/2f)-Const.gpTWidth) * Const.ptgHorizFraction;
        float y = 0;
        float z = -Const.gpTDepth;

        if(sampleA >= .66f  && mode != GameplayMode.Survival){
            if(sampleB >= .75f){
                addHealthPickup(PickupClass.MAJOR, x, y, z);
            }
            else if(sampleB >= .4f){
                addHealthPickup(PickupClass.MEDIUM, x, y, z);
            }
            else{
                addHealthPickup(PickupClass.MINOR, x, y, z);
            }
        }
        else if(sampleA >= .4f){
            if(sampleB >= .75f){
                addMultPickup(PickupClass.MAJOR, x, y, z);
            }
            else if(sampleB >= .4f){
                addMultPickup(PickupClass.MEDIUM, x, y, z);
            }
            else{
                addMultPickup(PickupClass.MINOR, x, y, z);
            }
        }
        else{
            if(sampleB >= .75f){
                addPointPickup(PickupClass.MAJOR, x, y, z);
            }
            else if(sampleB >= .4f){
                addPointPickup(PickupClass.MEDIUM, x, y, z);
            }
            else{
                addPointPickup(PickupClass.MINOR, x, y, z);
            }
        }
    }


}
