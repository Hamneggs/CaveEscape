package CaveEscapeCore.CoreGameplay;

/**
 * A Listener interface to be attached to a GameplayModeObserver.
 *
 * The onGameplayModeSelected() method is called when a gameplay mode
 * is set within the observer.
 * @see CaveEscapeCore.CoreGameplay.GameplayModeObserver
 */
public interface GameplayModeListenerI {

    /**
     * The method that is called whenever GameplayObserver.setMode()
     * is called.
     * @param mode The GameplayMode that was selected.
     * @see CaveEscapeCore.CoreGameplay.GameplayMode
     */
    abstract void onGameplayModeSelected(GameplayMode mode);

}
