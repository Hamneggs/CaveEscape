package CaveEscapeCore.GUIViews;

import CaveEscapeCore.CoreGameplay.GameplayMode;
import CaveEscapeCore.CoreGameplay.GameplayModeObserver;
import android.view.View;

import java.util.ArrayList;

/**
 * Listens for clicks on the governing TextButton.
 * Normally calls to this will be instantiated directly by the TextButton.
 *
 * On the event that this Listener hears something, it sets the mode
 * of the game's GameplayModeObserver to the mode of this
 * TextButtonListener.
 */
public class TextButtonListener implements View.OnClickListener{

    /**
     * The ArrayList of all the buttons in the group.
     */
    private ArrayList<TextButton> buttons;

    /**
     * The index of the governing TextButton in <i>buttons</i>.
     */
    private int index;

    /**
     * The GameplayMode of this TextButton. In the event that this
     * TextButton is chosen the GameplayModeObserver is set to this mode.
     */
    private GameplayMode mode;

    /**
     * The GameplayModeObserver used by the game.
     */
    private GameplayModeObserver observer;

    /**
     * If wanted, when this button is clicked, a Fade can be initiated.
     */
    private Fade effect;

    /**
     * Constructs the TextButtonListener.
     * @param buttons A list of all the buttons in the group for notifying
     *                when they've not been chosen.
     * @param index The index of this TextButton in the TextButton ArrayList.
     * @param mode The mode of this button, to set the GameplayModeObserver to.
     * @param observer The GameplayModeObserver to edit.
     * @param effect An optional (may be null) fade to trigger when the button is pressed.
     */
    public TextButtonListener(ArrayList<TextButton> buttons, int index, GameplayMode mode, GameplayModeObserver observer, Fade effect){

        this.buttons = buttons;
        this.index = index;
        this.mode = mode;
        this.observer = observer;
        this.effect = effect;

    }

    /**
     * Called when this button was clicked. Calls to this normally are
     * initiated directly by the governing TextButton.
     * @param view Unused.
     */
    @Override
    public void onClick(View view){

        //Set the mode of the GameplayModeObserver to that of this
        //button listener.
        observer.setMode(mode);

        //If the fade isn't null, trigger it.
        if(effect != null){
            effect.trigger();
        }

        //Cycle through all the buttons,
        for(int i = 0; i < buttons.size(); i++){

            //If the current button is this button, skip it.
            if(i == index){
                //Don't trigger the exit if the current button
                //is this button.
            }
            //Otherwise signal the current button that it was not
            //chosen.
            else{
                TextButton tb = buttons.get(i);
                //Tell the button to go directly into retreat.
                tb.signalExit();
            }
        }
    }

}