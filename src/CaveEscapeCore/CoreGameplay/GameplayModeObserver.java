package CaveEscapeCore.CoreGameplay;

/**
 * A middle class that allows the modifying and observing of the
 * selected gameplay mode from within the TextButtonListener.
 *
 * @see CaveEscapeCore.CoreGameplay.GameplayMode
 */
public class GameplayModeObserver {

    /**
     * The gameplay mode to use.
     */
    private GameplayMode mode = GameplayMode.noneSelected;

    /**
     * The Listener to talk to.
     */
    private GameplayModeListenerI listener;

    public GameplayModeObserver(GameplayModeListenerI listener){
        this.listener = listener;
    }

    /**
     * Sets the game mode. This method is most useful to
     * the TextButtonListener
     * @param mode The GLES10 mode of the button the
     *             user chose.
     * @see CaveEscapeCore.GUIViews.TextButtonListener
     */
    public void setMode(GameplayMode mode){
        this.mode = mode;
        listener.onGameplayModeSelected(mode);
    }

    /**
     * Returns the GameplayMode.
     * @return mode
     */
    public GameplayMode getMode(){
        return mode;
    }
}
