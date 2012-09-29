package CaveEscape.CaveEscape;

import CaveEscape.GameOver.GameOverListenerI;
import CaveEscape.GameOver.GameOverScreen;
import CaveEscape.Gameplay.Gameplay;
import CaveEscape.LogoScreen.LogoScreen;
import CaveEscape.MainMenu.MainMenu;
import CaveEscape.MainMenu.MainMenuListenerI;
import CaveEscape.ShipSelectScreen.ShipMenu;
import CaveEscape.ShipSelectScreen.ShipMenuListenerI;
import CaveEscapeCore.CoreGameplay.GameplayMode;
import CaveEscapeCore.CoreGameplay.GameplayModeListenerI;
import CaveEscapeCore.GUIViews.StatusBarListenerI;
import CaveEscapeCore.Player.Player;
import CaveEscapeCore.Player.ShipState;
import CaveEscapeCore.SoundAndMusic.SFXMEngine;
import CaveEscapeGeneral.HouseLogoListenerI;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

/**
 * The main Activity for Cave Escape. This handles menu creation and
 * navigation, game state, scoring, everything.
 */
public class CaveEscape extends Activity {


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*GAME STATE ENUMERATOR GAME STATE ENUMERATOR GAME STATE ENUMERATOR GAME STATE ENUMERATOR GAME STATE ENUMERATOR       */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public enum GameState{

    LOGO_SCREEN,
    MAIN_MENU,
    SHIP_SELECT,
    GAME_PLAY,
    GAME_OVER,

    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*LOGO SCREEN LOGO SCREEN LOGO SCREEN LOGO SCREEN LOGO SCREEN LOGO SCREEN LOGO SCREEN LOGO SCREEN LOGO SCREEN         */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The HouseLogo object that will make up the LogoScreen.
     */
    LogoScreen logoScreen;

    /**
    * Deflates the logo screen by trying to reduce its residual memory
    * usage as much as possible.
    */
    public void deflateLogoScreen(){
        logoScreen.finalizeDeflate();
        logoScreen = null;

        //Oh pretty, pretty please?
        System.gc();
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*MAIN MENU MAIN MENU MAIN MENU MAIN MENU MAIN MENU MAIN MENU MAIN MENU MAIN MENU MAIN MENU MAIN MENU MAIN MENU       */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The MainMenu instance that the user will see and interact with.
     */
    MainMenu mainMenu;

    public void inflateMainMenu(){
        mainMenu = new MainMenu();
        mainMenu.inflate(this, this, new GameplayModeListener(), new MainMenuListener());
    }

    public void beginMainMenuAnimation(){
        mainMenu.startMenuAnimations();
    }

    public void deflateMainMenu(){
        mainMenu.finalizeDeflate();
        mainMenu = null;
        System.gc();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/* SHIP MENU SHIP MENU SHIP MENU SHIP MENU SHIP MENU SHIP MENU SHIP MENU SHIP MENU SHIP MENU SHIP MENU SHIP MENU      */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ShipMenu shipMenu;

    public void inflateShipMenu(GameplayMode mode){
        shipMenu = new ShipMenu();
        shipMenu.inflateShipMenu(this, this, sfx, mode);
        shipMenu.setListener(new ShipMenuListener());
    }

    public void deflateShipMenu(){
        shipMenu.finalizeDeflate();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/* GAMEPLAY GAMEPLAY GAMEPLAY GAMEPLAY GAMEPLAY GAMEPLAY GAMEPLAY GAMEPLAY GAMEPLAY GAMEPLAY GAMEPLAY GAMEPLAY        */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    Gameplay gameplay;

    public void inflateGameplay(Player player, GameplayMode mode){
        gameplay = new Gameplay();
        gameplay.inflateGameplay(this, this, sfx, player, mode, new ListensForHealthDepletion());
    }

    public void deflateToGameOver(){
        gameplay.deflateAllButGL();
    }

    public void deflateGameplay(){
        gameplay.finalizeDeflate();
        gameplay = null;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/* GAME OVER GAME OVER GAME OVER GAME OVER GAME OVER GAME OVER GAME OVER GAME OVER GAME OVER GAME OVER GAME OVER      */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    GameOverScreen gameover;

    public void inflateGameOver(Player player, GameplayMode mode){
        gameover = new GameOverScreen();
        gameover.inflateGameOverScreen(this, this, mode, player, new GameOverClickListener() );
    }

    public void deflateGameOverMenu(){
        gameover.deflateGameOverScreen();
        deflateGameplay();
    }



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/* SOUND SOUND SOUND SOUND SOUND SOUND SOUND SOUND SOUND SOUND SOUND SOUND SOUND SOUND SOUND SOUND SOUND SOUND SOUND  */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    SFXMEngine sfx;

    public void loadSoundEngine(){

        sfx = new SFXMEngine(this, 1, 1, 2);

    }

    public void unloadSoundEngine(){
        if(sfx != null){
            sfx.killSoundEngine();
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/* CONTROL CONTROL CONTROL CONTROL CONTROL CONTROL CONTROL CONTROL CONTROL CONTROL CONTROL CONTROL CONTROL CONTROL    */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The current state of the game.
     */
    GameState gameState = GameState.LOGO_SCREEN;

    /**
     * The user-selected GameplayMode.
     */
    GameplayMode selectedMode;

    /**
     * The user-selected Player instance.
     */
    Player selectedShip;

    /**
     * The instance of the HouseLogoListener that waits for the House Logo to finish
     * its animation, then kills it off, never to be seen again.
     * It also sets up and starts the MainMenu.
     */
    public class HouseLogoListener implements HouseLogoListenerI {

        @Override
        public void onDone() {
            deflateLogoScreen();
            gameState = GameState.MAIN_MENU;
            inflateMainMenu();
            beginMainMenuAnimation();
        }
    }

    /**
     * The instance of GameplayModeListenerI that waits until a gameplay mode is selected,
     * and then stores it and starts the ship menu.
     */
    public class GameplayModeListener implements GameplayModeListenerI{
        @Override
        public void onGameplayModeSelected(GameplayMode mode) {
            storeSelectedGameplayMode(mode);
            sfx.playMenuSelect(0, .8f);
        }
    }

    public class MainMenuListener implements MainMenuListenerI{
        @Override
        public void onFinished() {
            gameState = GameState.SHIP_SELECT;
            deflateMainMenu();
            inflateShipMenu(selectedMode);

        }
    }

    public class ShipMenuListener implements ShipMenuListenerI{
        @Override
        public void onRotateLeft() {
            //TODO: Put ad stuff here.
        }

        @Override
        public void onRotateRight() {
            //TODO: Put ad stuff here.
        }

        @Override
        public void onInflate() {
            //TODO: Put ad stuff here.
        }

        @Override
        public void onShipSelected(Player selectedShip) {
            //selectedShip.setPlayerEventListener(new PlayerEventListener());
            selectedShip.setState(ShipState.straight);
            storeSelectedShip(selectedShip);
            deflateShipMenu();
            inflateGameplay(selectedShip, selectedMode);
            gameState = GameState.GAME_PLAY;

        }

        @Override
        public void onDeflate() {

        }
    }

    public class GameOverClickListener implements GameOverListenerI{

        @Override
        public void onGameOverPressed() {
            deflateGameplay();
            inflateMainMenu();
            beginMainMenuAnimation();
            gameState = GameState.MAIN_MENU;
        }
    }

    public class ListensForHealthDepletion implements StatusBarListenerI{

        @Override
        public void onDepleted() {
            deflateToGameOver();
            inflateGameOver(gameplay.getPlayer(), gameplay.getMode());
        }
    }
    /**
     * Stores the given GameplayMode as the selected one.
     * @param selectedMode The user-selected gameplay mode.
     */
    public void storeSelectedGameplayMode(GameplayMode selectedMode){
        this.selectedMode = selectedMode;
    }

    /**
     * Stores the given Player instance that the user selected
     * from the ship showroom.
     * @param selectedShip The user-selected Player instance.
     */
    public void storeSelectedShip(Player selectedShip){
        this.selectedShip = selectedShip;
    }



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/* BACK BUTTON BACK BUTTON BACK BUTTON BACK BUTTON BACK BUTTON BACK BUTTON BACK BUTTON BACK BUTTON BACK BUTTON        */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onBackPressed(){
        switch (gameState) {

            case LOGO_SCREEN:
                finish();
                break;
            case MAIN_MENU:
                finish();
                break;
            case SHIP_SELECT:
                gameState = GameState.MAIN_MENU;
                deflateShipMenu();
                inflateMainMenu();
                beginMainMenuAnimation();
                break;
            case GAME_PLAY:
                gameState = GameState.MAIN_MENU;
                deflateGameplay();
                //TODO: You need to make some sort of review screen, Gerard!
                inflateMainMenu();
                beginMainMenuAnimation();
                sfx.playMenuMusic(.8f);
                break;
            case GAME_OVER:
                gameState = GameState.MAIN_MENU;
                deflateGameplay();
                deflateGameOverMenu();
                //TODO: You need to make some sort of review screen, Gerard!
                inflateMainMenu();
                beginMainMenuAnimation();
                sfx.playMenuMusic(.8f);
                break;
        }
    }



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/* EXECUTION EXECUTION EXECUTION EXECUTION EXECUTION EXECUTION EXECUTION EXECUTION EXECUTION EXECUTION EXECUTION      */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //Set up the window the way we want it.
        getWindow().requestFeature(Window.FEATURE_NO_TITLE); //Remove the title bar.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,//Remove the status bar.
                WindowManager.LayoutParams.FLAG_FULLSCREEN);            //Remove the status bar.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//Force the screen to horizontal.

        //Inflate the house logo.
        logoScreen = new LogoScreen();




    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        //Load the sound engine.
        loadSoundEngine();
        sfx.playMenuMusic(.8f);
    }

    @Override
    protected void onStart() {
        super.onStart();
        gameState = GameState.LOGO_SCREEN;
        logoScreen.inflate(this, this, new HouseLogoListener());

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //switch (gameState) {
        //    case LOGO_SCREEN:
        //        break;
        //    case MAIN_MENU:
        //        break;
        //    case SHIP_SELECT:
        //        break;
        //    case GAME_PLAY:
        //        break;
        //}

    }

    @Override
    protected void onResume() {
        super.onResume();
        //switch (gameState) {
        //    case LOGO_SCREEN:
        //        break;
        //    case MAIN_MENU:
        //        inflateMainMenu();
        //        break;
        //    case SHIP_SELECT:
        //        inflateShipMenu(selectedMode);
        //        break;
        //    case GAME_PLAY:
        //        gameplay.resume();
        //        break;
        //}
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //switch (gameState) {
        //    case LOGO_SCREEN:
        //        deflateLogoScreen();
        //        gameState = GameState.MAIN_MENU;
        //        break;
        //    case MAIN_MENU:
        //        deflateMainMenu();
        //        break;
        //    case SHIP_SELECT:
        //        deflateShipMenu();
        //        break;
        //    case GAME_PLAY:
        //        gameplay.pause();
        //        break;
        //}
    }

    @Override
    protected void onStop() {
        super.onStop();
        switch (gameState) {
            case LOGO_SCREEN:
                break;
            case MAIN_MENU:
                deflateMainMenu();
                break;
            case SHIP_SELECT:
                deflateShipMenu();
                break;
            case GAME_PLAY:
                deflateGameplay();
                break;
            case GAME_OVER:
                deflateGameOverMenu();
                deflateGameplay();
        }
        sfx.killSoundEngine();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
