package CaveEscapeCore.Player;

/**
 * This enum represents the various display states a ship can be in.
 */
public enum ShipState {

    //At the main menu, when selecting a ship, the ships will be rotating slowly.
    menuRotate,

    //When you select a ship, the ship stops spinning at whatever rotation
    //the player selected it at.
    menuSelected,

    //When the player moving left, the ship dips its left side, rotating
    //around the z axis 15 degrees.
    strafeLeft,

    //Like when strafing left, while strafing right the ship dips its
    //right side down 15 degrees.
    strafeRight,

    //When going up, the nose of the ship tilts up 15 degrees. THIS DOES NOT
    //AFFECT THE PHYSICAL LOCATION OF THE NOSE IN TERMS OF COLLISION.
    strafeUp,

    //When going up, the nose of the ship tilts up 15 degrees. THIS DOES NOT
    //AFFECT THE PHYSICAL LOCATION OF THE NOSE IN TERMS OF COLLISION.
    strafeDown,

    //At this state the ship is strafing up AND to the right.
    strafeUpRight,

    //At this state the ship is strafing up AND to the left.
    strafeUpLeft,

    //At this state the ship is strafing down AND to the right.
    strafeDownRight,

    //At this state the ship is strafing down AND to the left.
    strafeDownLeft,

    //This state defines the geometry state when the ship is traveling straight
    //ahead.
    straight
}
