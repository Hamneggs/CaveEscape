package CaveEscapeCore.Constants;

/**
 * Various constants used throughout Cave Escape,
 * stored here for easy tweaking.
 */
public class Const {

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                                                                                                    //
// GENERAL  GENERAL  GENERAL  GENERAL  GENERAL  GENERAL  GENERAL GENERAL  GENERAL  GENERAL  GENERAL  GENERAL  GENERAL //
//                                                                                                                    //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Two PI.
     */
    public static float TWO_PI = (float)Math.PI *2;

    /**
     * Whether or not to perform haptic feedback.
     */
    public static boolean performHaptics = true;

    /**
     * Whether or not to print verbose logcat info.
     */
    public static boolean verboseInfo = true;

    /**
     * The message tag when printing verbose info.
     */
    public static String verboseTag = "THE LIL' DUDE INSIDE CaveEscape SAYS: ";

    /**
     * The filename of the score file.
     */
    public static String scoreFilename = "scrs";


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                                                                                                    //
//  COLORS COLORS COLORS COLORS COLORS COLORS COLORS COLORS COLORS COLORS COLORS COLORS COLORS COLORS COLORS COLORS   //
//                                                                                                                    //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The terrain color for the NoneSelected game-type.
     */
    public static float clrNoneSelectedR = 0f;
    public static float clrNoneSelectedG = 1f;
    public static float clrNoneSelectedB = 1f;

    /**
     * The terrain color for the Time Attack game-type.
     */
    public static float clrTimeAttackR = .05f;
    public static float clrTimeAttackG = .25f;
    public static float clrTimeAttackB = .25f;

    /**
     * The terrain color for the Classic game-type.
     */
    public static float clrClassicR = .75f;
    public static float clrClassicG = .85f;
    public static float clrClassicB = 1f;

    /**
     * The terrain color for the Survival game-type.
     */
    public static float clrSurvivalR = 1f;
    public static float clrSurvivalG = .55f;
    public static float clrSurvivalB = .55f;

    /**
     * The color of a major multiplier pickup.
     */
    public static float clrMultMajorR = 9f;
    public static float clrMultMajorG = 9f;
    public static float clrMultMajorB = 0f;

    /**
    * The color of a medium multiplier pickup.
    */
    public static float clrMultMediumR = .75f;
    public static float clrMultMediumG = .75f;
    public static float clrMultMediumB = .75f;

    /**
    * The color of a minor multiplier pickup.
    */
    public static float clrMultMinorR = .75f;
    public static float clrMultMinorG = .55f;
    public static float clrMultMinorB = .0f;

    /**
    * The color of a major point pickup.
    */
    public static float clrPointMajorR = 1f;
    public static float clrPointMajorG = .6f;
    public static float clrPointMajorB = .7f;

    /**
    * The color of a medium point pickup.
    */
    public static float clrPointMediumR = 1f;
    public static float clrPointMediumG = .4f;
    public static float clrPointMediumB = .4f;

    /**
    * The color of a minor point pickup.
    */
    public static float clrPointMinorR = 1f;
    public static float clrPointMinorG = .7f;
    public static float clrPointMinorB = .4f;

    /**
     * The color of a major health pickup.
     */
    public static float clrHealthMajorR  = .15f;
    public static float clrHealthMajorG  = .95f;
    public static float clrHealthMajorB  = .15f;

    /**
     * The color of a medium health pickup.
     */
    public static float clrHealthMediumR = .15f;
    public static float clrHealthMediumG = .65f;
    public static float clrHealthMediumB = .15f;

    /**
     * The color of a minor health pickup.
     */
    public static float clrHealthMinorR = .15f;
    public static float clrHealthMinorG = .45f;
    public static float clrHealthMinorB = .15f;


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                                                                                                    //
//  CAMERA CAMERA CAMERA CAMERA CAMERA CAMERA CAMERA CAMERA CAMERA CAMERA CAMERA CAMERA CAMERA CAMERA CAMERA CAMERA   //
//                                                                                                                    //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * How much the camera slides at the eye point.
     */
    public static float camSlideFactorBack = .25f;

    /**
     * How much the camera's point of focus slides.
     */
    public static float camSlideFactorFront = .125f;

    /**
     * Inversely how far sensitive the camera slides and tilts.
     */
    public static float camTiltFactor = .02f;

    /**
     * How much of an effect the ship's up and down movement
     * has on the movement of the camera.
     */
    public static float camShipVelSoften = .1f;

    /**
     * How far the camera is away from the ship.
     */
    public static float camShipDist = 3.25f;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                                                                                                    //
//  SHIP  SHIP  SHIP  SHIP  SHIP  SHIP  SHIP  SHIP  SHIP  SHIP  SHIP  SHIP  SHIP  SHIP  SHIP  SHIP  SHIP  SHIP  SHIP  //
//                                                                                                                    //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * The max possible health for a ship to have.
     */
    public static float shipMaxPosHealth = 500;

    /**
     * The max possible strafe speed of a ship.
     */
    public static float shipMaxStrafeSpeed = .4f;

    /**
     * The scale factor used to compare a ship's forward speed
     * to its strafe speed when computing the ship's
     * base multiplier.
     */
    public static float shipSpdDiffRatioFactor = .066f;

    /**
     * The maximum forward speed of a ship.
     */
    public static float shipMaxForwardSpeed = .7f;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                                                                                                    //
//    MAIN MENU  MAIN MENU  MAIN MENU  MAIN MENU  MAIN MENU  MAIN MENU  MAIN MENU  MAIN MENU  MAIN MENU  MAIN MENU    //
//                                                                                                                    //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * How many degrees the main menu terrain turns each frame.
     */
    public static float mmRotSpeed = .15f;

    /**
     * The heightmap resolution of the main menu terrain.
     */
    public static int mmTerRes = 100;

    /**
     * The size of the main menu terrain.
     */
    public static float mmTerSize = 50;

    /**
     * How far from Y = 0 the top and bottom of the
     * main menu terrain is.
     */
    public static float mmVertOffset = 3;

    /**
     * The main menu's feature density.
     */
    public static float mmFDensity = 16;

    /**
     * The main menu's terrain height scale.
     */
    public static float mmHScale = 6;

    /**
     * The main menu's slope (exponential height) factor.
     */
    public static float mmSScale = 2;

    /**
     * The main menu top-to-bottom terrain deviation factor.
     */
    public static float mmDevSize = .15f;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                                                                                                    //
//    SHIP MENU  SHIP MENU  SHIP MENU  SHIP MENU  SHIP MENU  SHIP MENU  SHIP MENU  SHIP MENU  SHIP MENU  SHIP MENU    //
//                                                                                                                    //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * The density of the fog in the ship menu.
     */
    public static float smFogDensity = .125f;

    /**
     * The distance from Y = 0 of the top and bottom portions
     * of the ship menu's terrain.
     */
    public static float smVertOffset = 1.5f;

    /**
     * The physical size of the ship menu's terrain.
     */
    public static float smTerSize = 30f;

    /**
     * The resolution of the ship menu's
     * terrain's heightmap.
     */
    public static int smTerRes = 100;

    /**
     * The feature density of the terrain in the
     * ship menu.
     */
    public static float smFDensity = 16;

    /**
     * The height scale factor of the terrain in the
     * ship menu.
     */
    public static float smHScale = 3f;

    /**
     * The exponential height scale of the terrain in
     * the ship menu.
     */
    public static float smSScale = 2f;

    /**
     * The factor by which to multiply the seed, which
     * natively is in the set [0 - 1).
     */
    public static float smSeedSize = .1f;

    /**
     * The maximum potential value of the top-to-bottom
     * deviation of the terrain in the ship menu.
     */
    public static float smDevSize = .025f;

    /**
     * The alpha value of the terrain in the ship menu.
     */
    public static float smTerAlpha = .1f;

    /**
     * The rotational accuracy of the rotation animation
     * that occurs when the user changes between ships.
     * 1.0f represents perfect accuracy. Anything below
     * 1.0f can cause the animation to no longer stop.
     */
    public static float smRotAccuracy = 1.01f;


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                                                                                                    //
//    GAMEPLAY GAMEPLAY GAMEPLAY GAMEPLAY GAMEPLAY GAMEPLAY GAMEPLAY GAMEPLAY GAMEPLAY GAMEPLAY GAMEPLAY GAMEPLAY     //
//                                                                                                                    //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The factor by which the terrain's current feature
     * density is multiplied every time the player levels up.
     */
    public static float gpDensityChangeFactor = 1.000205f;

    /**
     * The factor by which the terrain's current height
     * scale factor is multiplied every time the player levels up.
     */
    public static float gpHeightChangeFactor = 1.0066f;

    /**
     * The amount by which the speed is increased every
     * time the player levels up.
     */
    public static float gpFwdSpdChangeConstant = .008f;

    /**
     * The amount by which the ship's strafing speed increases
     * every time the player levels up.
     */
    public static float gpStfSpdChangeConstant = .00066f;

    /**
     * When the controller is released, this is the value by which
     * the ship's strafing velocity is divided by every frame.
     * This is so that the camera fluidly returns back to
     * it's natural state.
     */
    public static float gpVelDecayFactor = 1.085f;

    /**
     * The distance from Y = 0 the top and bottom portions
     * of the gameplay terrain are.
     */
    public static float gpVertOffset = 3.5f;

    /**
     * The physical width of the gameplay terrain.
     */
    public static float gpTWidth = 80f;

    /**
     * The physical VISIBLE depth of the
     * gameplay terrain.
     */
    public static float gpTDepth = 30f;

    /**
     * The difference between the densities of the top
     * and bottom of the gameplay terrain.
     */
    public static float gpTDVar = 1.004f;

    /**
     * How many frames to skip between updates to status bars
     * in the gameplay gui.
     */
    public static int gpStatusBarFrameSkip = 5;

    /**
     * The X Resolution of the gameplay terrain's
     * height map.
     */
    public static int gpResX = 150;

    /**
     * The Y (depth) resolution of the gameplay terrain's
     * height map.
     */
    public static int gpResY = 30;

    /**
     * The starting feature density of the gameplay terrain.
     */
    public static float gpBaseFDensity = 16;

    /**
     * The starting height scale factor of the gameplay
     * terrain.
     */
    public static float gpBaseHScale = 10;

    /**
     * The slope scale of the gameplay terrain.
     */
    public static float gpSScale = 2;

    /**
     * The length of time between starting the game and
     * the Player "Leveling up", as well as the time
     * between sequential "level ups". This value is in
     * milliseconds.
     */
    public static long gpLevelTime = 10000;

    /**
     * Whether or not to perform collision checking.
     */
    public static boolean gpDoCollTests = true;

    /**
     * The number of frames between score increases.
     */
    public static int gpFramesPerScoreIncrease = 10;

    /**
     * The amount the score increases by every time the score updates.
     */
    public static float gpScoreIncrease = 2;

    /**
     * The minimum number of frames left on a multiplier to fully reset the
     * multiplier time upon collection of a multiplier pickup.
     */
    public static float gpMinMultFrames = 30;

    /**
     * The threshold at which the ship begins to rotate based on velocity.
     * Represented as a fraction of the ship's maximum strafe speed.
     */
    public static float gpShipAnimSensitivity = .05f;

    /**
     * In survival mode, we change the terrain's speed based on the player's
     * current multiplier, but cut it with this factor so that we don't
     * enter warp speed.
     */
     public static float gpSurvivalMultSpeedFactor = .04f;


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                                                                                                    //
//     GAMEPLAY TERRAIN  GAMEPLAY TERRAIN  GAMEPLAY TERRAIN  GAMEPLAY TERRAIN  GAMEPLAY TERRAIN  GAMEPLAY TERRAIN     //
//                                                                                                                    //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * The percentage of the terrain the Player can traverse
     * horizontally before hitting the invisible wall. This is
     * so that the player cannot see the edge of the terrain.
     */
    public static float ptgHorizFraction = .45f;

    /**
     * The scale difference factor between top and bottom terrain.
     * This makes things a bit less mundane.
     */
    public static float ptgScaleFactorDiff = 1.005f;

    /**
     * The percentage of teh terrain the Player can traverse
     * vertically before hitting the blanket. This is to prevent
     * the camera from warping out of the cave.
     */
    public static float ptgVertFraction = .9f;

    /**
     * Whether or not to do such a horizontal blanketing collision
     * test at all.
     */
    public static boolean ptgDoHorizBlanketColl = true;

    /**
     * Whether or not to do a vertical blanketign collision test.
     */
    public static boolean ptgDoVertBlanketColl = true;

    /**
     * Whether or not to do the fine collision testing.
     */
    public static boolean ptgDoFineColl = true;

    /**
     * How much health to detract for every hit.
     */
    public static int ptgHealthPerColl = 4 ;

    /**
     * Whether or not to damage the terrain when the player
     * hits it.
     */
    public static boolean ptgDoTerrainDmg = true;

    /**
     * When the player hits one of the blankets, we subtract from
     * it's current position its velocity in that blanket's
     * plane, multiplied by this factor.
     */
    public static float ptgBlanketJitterFactor = 3f;

    /**
     * When the player hits the terrain, we must change the
     * Player's Y-velocity so that they don't go through
     * the terrain. To do this, we multiply a factor by the ABSOLUTE
     * VALUE of the current Y velocity of the ship.
     * This is that factor.
     */
    public static float ptgCollJitterFactor = 1.1f;

    /**
     * Whether or not to soften the very first height values of
     * the terrain for easy entrance into gameplay.
     */
    public static boolean ptgScaleInitialTerrain = true;

    /**
     * The factor by which we multiply the sampled height to
     * make collisions a bit more difficult.
     */
    public static float ptgFineCollGraceFactor = .985f;

    /**
     * Makes all terrain updates form the terrain into an
     * off-camber slope.
     */
    public static boolean ptgDebugCollision = false;

    /**
     * Whether or not to do the near miss score boost.
     */
    public static boolean ptgDoNearMissTest = true;

    /**
     * The near miss point bonus for every near miss.
     *
     */
    public static float ptgNearMissPtBonus = 1.25f;

    /**
     * The raw distance to be considered a near miss.
     */
    public static float ptgNearMissDist = 1f;


}
