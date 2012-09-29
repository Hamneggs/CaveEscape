package CaveEscapeCore.CoreGameplay;

/**
 * Used to tell the game what gameplay mode the user selected.
 */
public enum GameplayMode {

    /**
     * No mode has been selected yet.
     */
    noneSelected,

    /**
     * The multipliers freeze the clock for their value.
     * Run out of time and the game is over. The score
     * is your final distance.
     */
    TimeAttack,

    /**
     * All elements are used. Your score is your distance, but
     * is affected by multiplier pickups and point pickups.
     */
    Classic,

    /**
     * Only point pickups and multiplier pickups. Multiplier pickups
     * increase speed. Score is based on distance and multipliers gathered.
     */
    Survival

}