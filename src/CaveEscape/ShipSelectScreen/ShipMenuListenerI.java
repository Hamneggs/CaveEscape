package CaveEscape.ShipSelectScreen;

import CaveEscapeCore.Player.Player;

/**
 * Created with IntelliJ IDEA.
 * User: Chuck Finley
 * Date: 7/10/12
 * Time: 1:05 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ShipMenuListenerI {

    public abstract void onRotateLeft();

    public abstract void onRotateRight();

    public abstract void onInflate();

    public abstract void onShipSelected(Player selected);

    public abstract void onDeflate();

}
