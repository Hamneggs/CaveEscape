package CaveEscapeGeneral;

/**
 * An interface used to define the abilities of
 * a HouseLogoListener.
 * Currently, it only requires functionality of
 * an onDone() method.
 */
public interface HouseLogoListenerI {

    /**
     * Called when the animation of the HouseLogo finishes.
     */
    public abstract void onDone();

}
