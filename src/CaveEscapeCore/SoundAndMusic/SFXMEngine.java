package CaveEscapeCore.SoundAndMusic;

//import CaveEscape.CaveEscape.R;

import CaveEscape.CaveEscape.R;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

/**
 * The sound engine for Cave Escape. Basically, we load a bunch of sounds, and
 * are able to play them whenever. The music on the other hand is loaded when
 * you play it, but all other music is immediately stopped and loaded out of memory.
 *
 * @see Sound
 */
public class SFXMEngine {

    /**
     * The various Sounds used by the sound effects and music engine.
     * @see Sound
     */
    Sound CollideTerrain,
            CollectHealthPickupA,
            CollectHealthPickupB,
            CollectHealthPickupC,
            CollectMultPickupA,
            CollectMultPickupB,
            CollectMultPickupC,
            CollectPointPickupA,
            CollectPointPickupB,
            CollectPointPickupC,
            LevelUp,
            MultiplierAlmostUp,
            MultiplierOver,
            MenuSelect,
            MenuMove,
            Pause,
            StartLevel,
            ShipForward,
            ShipStrafe;

    /**
     * The SoundPool instance used to drive all the sounds.
     */
    SoundPool sfx;

    /**
     * The MediaPlayer instance used to drive the music.
     */
    MediaPlayer mus;

    /**
     * When the player is colliding with terrain, this boolean represents whether or not the
     * collideTerrain sound is playing.
     */
    boolean collideTerrainPlaying;

    /**
     * Since we need to eliminate the possibility of the ShipForward and ShipStrafe sounds from playing together,
     * we need a couple of booleans to guide things.
     */
    boolean shipForwardPlaying, shipStrafePlaying;

    /**
     * This boolean represents whether or not the MediaPlayer is prepared yet.
     */
    boolean musIsPrepared;

    /**
     * The currently playing song.
     */
    MUS_KEYS currentBGM;

    /**
     * The general music volume.
     */
    float musVol;

    /**
     * The general SFX volume.
     */
    float sfxVol;

    /**
     * The context within which the game is running.
     */
    Context context;

    /**
     * The scaleFactor used for enhancing volume/distance representation.
     */
    float sfxScaleFactor;

    /**
     * Constructs the Sound Effects and Music Engine.
     * @param context Required for loading resources.
     * @param musVol The base music volume. Balance and percentage must bow to this and sfxScaleFactor
     *               in terms of representation.
     * @param sfxVol The base sfx volume. Balance and percentage must bow to this and sfxScaleFactor
     *               in terms of representation.
     * @param sfxScaleFactor The sfxScaleFactor is used in enhancing the difference between quiet and loud
     *                       sounds.
     */
    public SFXMEngine(Context context, float musVol, float sfxVol, float sfxScaleFactor){

        this.musVol = musVol;

        this.context = context;

        this.sfxVol = sfxVol;

        collideTerrainPlaying = false;

        shipForwardPlaying = false;

        shipStrafePlaying = false;

        musIsPrepared = false;

        //No music has yet been selected, so the current background music is set to NONE.
        currentBGM = MUS_KEYS.NONE;

        this.sfxScaleFactor = sfxScaleFactor;

        //Initialize the SoundPool to handle 32 streams.
        sfx = new SoundPool(128, AudioManager.STREAM_MUSIC, 0);

        //Loads all of the sounds. There's only 19, so it's no big deal to hold them all.
        loadSounds(context);

    }

    /**
     * Loads all of the sounds and stores their SoundIDs within their own Sound instances.
     *
     * @param context The application context to use when loading the sounds.
     */
    private void loadSounds(Context context){
        int placeHolder = 0;
        CollideTerrain       = new Sound(sfx.load(context, R.raw.ship_collide, 0));//
        CollectHealthPickupA = new Sound(sfx.load(context, R.raw.minor_health_pickup, 0));//
        CollectHealthPickupB = new Sound(sfx.load(context, R.raw.medium_health_pickup, 0));//
        CollectHealthPickupC = new Sound(sfx.load(context, R.raw.major_health_pickup, 0));//
        CollectMultPickupA   = new Sound(sfx.load(context, R.raw.minor_mult_pickup, 0));//
        CollectMultPickupB   = new Sound(sfx.load(context, R.raw.medium_mult_pickup, 0));//
        CollectMultPickupC   = new Sound(sfx.load(context, R.raw.major_mult_pickup, 0));//
        CollectPointPickupA  = new Sound(sfx.load(context, R.raw.minor_point_pickup, 0));//
        CollectPointPickupB  = new Sound(sfx.load(context, R.raw.medium_point_pickup, 0));//
        CollectPointPickupC  = new Sound(sfx.load(context, R.raw.major_point_pickup, 0));//
        LevelUp              = new Sound(sfx.load(context, R.raw.levelup, 0));//
        MultiplierAlmostUp   = new Sound(sfx.load(context, R.raw.mult_almost_over, 0));//
        MultiplierOver       = new Sound(sfx.load(context, R.raw.multiplier_up, 0));//
        MenuSelect           = new Sound(sfx.load(context, R.raw.menu_select, 0));//
        MenuMove             = new Sound(sfx.load(context, R.raw.menu_move, 0));//
        Pause                = new Sound(sfx.load(context, R.raw.pause_sound, 0));//
        StartLevel           = new Sound(sfx.load(context, R.raw.begin, 0));//
        ShipForward          = new Sound(sfx.load(context, R.raw.ship_noise, 0));//
        ShipStrafe           = new Sound(sfx.load(context, R.raw.ship_noise, 0));//
    }


    /*
      SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX
      SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX
      SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX
      SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX
      SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX
      SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX
      SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX SFX
    */
    /**
     * When the player is colliding with the terrain, they may glide along it for an indeterminate
     * length of time. Therefore we must play the sound looped, and then stop it when the player
     * is no longer striking the terrain.
     *
     * @param balance       The left/right stereo balance with which you want to play the sound.
     * @param percentage    The percentage of the current sfx volume the sound should be played at.
     * @return A boolean representing if the call began the playing of the CollideTerrain loop.
     */
    public boolean playCollideTerrain(float balance, float percentage){

        float curVolume = (float)Math.pow(sfxVol*percentage, sfxScaleFactor);

        if(!collideTerrainPlaying){

            //collideTerrainPlaying = true;
            return CollideTerrain.setStreamID( sfx.play(CollideTerrain.getSoundID(), curVolume-(curVolume*balance),
                    curVolume+(curVolume*balance), 0,  0, 0) );

        }

        else{

            sfx.setVolume( CollideTerrain.getStreamID(), curVolume-(curVolume*balance), curVolume+(curVolume*balance) );
            return false;

        }
    }

    /**
     * When the player ceases to make contact with the terrain, we need to stop playing the collide sound.
     * This is how such is accomplished, and must be called to ensure that the engine knows that the collide
     * sound is not playing, since there is no such functionality to the SoundPool.
     *
     * @return A boolean of whether or not the CollideTerrain loop was closed deliberately, as opposed to
     *          encountering an already finished stream.
     */
    public boolean stopCollideTerrain(){

        if(collideTerrainPlaying){

            sfx.stop(CollideTerrain.getStreamID());
            collideTerrainPlaying = false;
            return true;

        }

        else return false;
    }

    /**
     * When a health pickup is collected, we play a sound based on the fact that it was
     * in fact collected, and of it's effectiveness.
     *
     * @param balance       The left/right stereo balance with which you want to play the sound.
     * @param percentage    The percentage of the current sfx volume the sound should be played at.
     * @return A boolean of whether or not the sound was played successfully.
     */
    public boolean playCollectHealthPickupA(float balance, float percentage){

        float curVolume = (float)Math.pow( sfxVol*percentage, sfxScaleFactor );
        return CollectHealthPickupA.setStreamID( sfx.play(CollectHealthPickupA.getSoundID(),
                curVolume-(curVolume*balance), curVolume+(curVolume*balance), 0,  0, 0) );

    }

    /**
     * When a health pickup is collected, we play a sound based on the fact that it was
     * in fact collected, and of it's effectiveness.
     *
     * @param balance       The left/right stereo balance with which you want to play the sound.
     * @param percentage    The percentage of the current sfx volume the sound should be played at.
     * @return A boolean of whether or not the sound was played successfully.
     */
    public boolean playCollectHealthPickupB(float balance, float percentage){

        float curVolume = (float)Math.pow( sfxVol*percentage, sfxScaleFactor );
        return CollectHealthPickupB.setStreamID( sfx.play(CollectHealthPickupB.getSoundID(),
                curVolume-(curVolume*balance), curVolume+(curVolume*balance), 0,  0, 0) );

    }

    /**
     * When a health pickup is collected, we play a sound based on the fact that it was
     * in fact collected, and of it's effectiveness.
     *
     * @param balance       The left/right stereo balance with which you want to play the sound.
     * @param percentage    The percentage of the current sfx volume the sound should be played at.
     * @return A boolean of whether or not the sound was played successfully.
     */
    public boolean playCollectHealthPickupC(float balance, float percentage){

        float curVolume = (float)Math.pow( sfxVol*percentage, sfxScaleFactor );
        return CollectHealthPickupC.setStreamID( sfx.play(CollectHealthPickupC.getSoundID(),
                curVolume-(curVolume*balance), curVolume+(curVolume*balance), 0,  0, 0) );

    }

    /**
     * When a score multiplier pickup is collected, we play a sound based on the fact that it was
     * in fact collected, and of it's effectiveness.
     *
     * @param balance       The left/right stereo balance with which you want to play the sound.
     * @param percentage    The percentage of the current sfx volume the sound should be played at.
     * @return A boolean of whether or not the sound was played successfully.
     */
    public boolean playCollectMultPickupA(float balance, float percentage){

        float curVolume = (float)Math.pow( sfxVol*percentage, sfxScaleFactor );
        return CollectMultPickupA.setStreamID( sfx.play(CollectMultPickupA.getSoundID(),
                curVolume-(curVolume*balance), curVolume+(curVolume*balance), 0,  0, 0) );

    }

    /**
     * When a score multiplier pickup is collected, we play a sound based on the fact that it was
     * in fact collected, and of it's effectiveness.
     *
     * @param balance       The left/right stereo balance with which you want to play the sound.
     * @param percentage    The percentage of the current sfx volume the sound should be played at.
     * @return A boolean of whether or not the sound was played successfully.
     */
    public boolean playCollectMultPickupB(float balance, float percentage){

        float curVolume = (float)Math.pow( sfxVol*percentage, sfxScaleFactor );
        return CollectMultPickupB.setStreamID( sfx.play(CollectMultPickupB.getSoundID(),
                curVolume-(curVolume*balance), curVolume+(curVolume*balance), 0,  0, 0) );

    }

    /**
     * When a score multiplier pickup is collected, we play a sound based on the fact that it was
     * in fact collected, and of it's effectiveness.
     *
     * @param balance       The left/right stereo balance with which you want to play the sound.
     * @param percentage    The percentage of the current sfx volume the sound should be played at.
     * @return A boolean of whether or not the sound was played successfully.
     */
    public boolean playCollectMultPickupC(float balance, float percentage){

        float curVolume = (float)Math.pow( sfxVol*percentage, sfxScaleFactor );
        return CollectMultPickupC.setStreamID( sfx.play(CollectMultPickupC.getSoundID(),
                curVolume-(curVolume*balance), curVolume+(curVolume*balance), 0,  0, 0) );

    }

    /**
     * When a point pickup is collected, we play a sound based on the fact that it was
     * in fact collected, and of it's effectiveness.
     *
     * @param balance       The left/right stereo balance with which you want to play the sound.
     * @param percentage    The percentage of the current sfx volume the sound should be played at.
     * @return A boolean of whether or not the sound was played successfully.
     */
    public boolean playCollectPointPickupA(float balance, float percentage){

        float curVolume = (float)Math.pow( sfxVol*percentage, sfxScaleFactor );
        return CollectPointPickupA.setStreamID( sfx.play(CollectPointPickupA.getSoundID(),
                curVolume-(curVolume*balance), curVolume+(curVolume*balance), 0,  0, 0) );

    }

    /**
     * When a point pickup is collected, we play a sound based on the fact that it was
     * in fact collected, and of it's effectiveness.
     *
     * @param balance       The left/right stereo balance with which you want to play the sound.
     * @param percentage    The percentage of the current sfx volume the sound should be played at.
     * @return A boolean of whether or not the sound was played successfully.
     */
    public boolean playCollectPointPickupB(float balance, float percentage){

        float curVolume = (float)Math.pow( sfxVol*percentage, sfxScaleFactor );
        return CollectPointPickupB.setStreamID( sfx.play(CollectPointPickupB.getSoundID(),
                curVolume-(curVolume*balance), curVolume+(curVolume*balance), 0,  0, 0) );

    }

    /**
     * When a point pickup is collected, we play a sound based on the fact that it was
     * in fact collected, and of it's effectiveness.
     *
     * @param balance       The left/right stereo balance with which you want to play the sound.
     * @param percentage    The percentage of the current sfx volume the sound should be played at.
     * @return A boolean of whether or not the sound was played successfully.
     */
    public boolean playCollectPointPickupC(float balance, float percentage){

        float curVolume = (float)Math.pow( sfxVol*percentage, sfxScaleFactor );
        return CollectPointPickupC.setStreamID( sfx.play(CollectPointPickupC.getSoundID(),
                curVolume-(curVolume*balance), curVolume+(curVolume*balance), 0,  0, 0) );

    }

    /**
     * When the player reaches a level-up cue, this sound should be played. It has ten stages of pitch.
     * The pitch should be increased with every level up.
     *
     * @param balance       The left/right stereo balance with which you want to play the sound.
     * @param percentage    The percentage of the current sfx volume the sound should be played at.
     * @param pitchLevel    An integer whose value is reduced to the range [0-9]. 0 is the lowest pitch
     *                      (standard), and 9 is the maximum pitch.
     * @return A boolean of whether or not the sound was played successfully.
     */
    public boolean playLevelUp(float balance, float percentage, int pitchLevel){

        float curVolume = (float)Math.pow( sfxVol*percentage, sfxScaleFactor );

        boolean returnMe = LevelUp.setStreamID( sfx.play(LevelUp.getSoundID(),
                curVolume-(curVolume*balance), curVolume+(curVolume*balance), 0,  0, 0) );

        sfx.setRate(LevelUp.getStreamID(), 1+( ( (float)pitchLevel ) / 10.0f) );

        return returnMe;
    }

    /**
     * When the player's score multiplier is about to run out of time, this jingle should be played.
     * Once.
     *
     * @param balance       The left/right stereo balance with which you want to play the sound.
     * @param percentage    The percentage of the current sfx volume the sound should be played at.
     * @return A boolean of whether or not the sound was played successfully.
     */
    public boolean playMultiplierAlmostUp(float balance, float percentage){

        float curVolume = (float)Math.pow( sfxVol*percentage, sfxScaleFactor );
        return MultiplierAlmostUp.setStreamID( sfx.play(MultiplierAlmostUp.getSoundID(),
                curVolume-(curVolume*balance), curVolume+(curVolume*balance), 0,  0, 0) );

    }

    /**
     * When the multiplier is up, this depressing little sound should be played.
     *
     * @param balance       The left/right stereo balance with which you want to play the sound.
     * @param percentage    The percentage of the current sfx volume the sound should be played at.
     * @return A boolean of whether or not the sound was played successfully.
     */
    public boolean playMultiplierOver(float balance, float percentage){

        float curVolume = (float)Math.pow( sfxVol*percentage, sfxScaleFactor );
        return MultiplierOver.setStreamID( sfx.play(MultiplierOver.getSoundID(),
                curVolume-(curVolume*balance), curVolume+(curVolume*balance), 0,  0, 0) );

    }

    /**
     * When the player selects ANYTHING this sound should be played.
     *
     * @param balance       The left/right stereo balance with which you want to play the sound.
     * @param percentage    The percentage of the current sfx volume the sound should be played at.
     * @return A boolean of whether or not the sound was played successfully.
     */
    public boolean playMenuSelect(float balance, float percentage){

        float curVolume = (float)Math.pow( sfxVol*percentage, sfxScaleFactor );
        return MenuSelect.setStreamID( sfx.play(MenuSelect.getSoundID(),
                curVolume-(curVolume*balance), curVolume+(curVolume*balance), 0,  0, 0) );

    }

    /**
     * When the player moves his cursor while in any of the menus or while selecting ships, this
     * sound should be played.
     *
     * @param balance       The left/right stereo balance with which you want to play the sound.
     * @param percentage    The percentage of the current sfx volume the sound should be played at.
     * @return A boolean of whether or not the sound was played successfully.
     */
    public boolean playMenuMove(float balance, float percentage){

        float curVolume = (float)Math.pow( sfxVol*percentage, sfxScaleFactor );
        return MenuMove.setStreamID( sfx.play(MenuMove.getSoundID(),
                curVolume-(curVolume*balance), curVolume+(curVolume*balance), 0,  0, 0) );

    }

    /**
     * When the game is paused, this jingle should play.
     *
     * @param balance       The left/right stereo balance with which you want to play the sound.
     * @param percentage    The percentage of the current sfx volume the sound should be played at.
     * @return A boolean of whether or not the sound was played successfully.
     */
    public boolean playPause(float balance, float percentage){

        float curVolume = (float)Math.pow( sfxVol*percentage, sfxScaleFactor );
        return CollectHealthPickupA.setStreamID( sfx.play(CollectHealthPickupA.getSoundID(),
                curVolume-(curVolume*balance), curVolume+(curVolume*balance), 0,  0, 0) );

    }

    /**
     * When the gameplay starts, this nice little inviting jingle should be played.
     *
     * @param balance       The left/right stereo balance with which you want to play the sound.
     * @param percentage    The percentage of the current sfx volume the sound should be played at.
     * @return A boolean of whether or not the sound was played successfully.
     */
    public boolean playStartLevel(float balance, float percentage){

        float curVolume = (float)Math.pow( sfxVol*percentage, sfxScaleFactor );
        return StartLevel.setStreamID( sfx.play(StartLevel.getSoundID(),
                curVolume-(curVolume*balance), curVolume+(curVolume*balance), 0,  0, 0) );

    }

    /**
     * When the ship is moving forward, we need to have it play an engine sound. However, it is more complicated
     * than that. When told to play the ship-forward sound, the engine automatically stops (if it is playing)
     * the ship-strafe sound.
     *
     * @param balance       The left/right stereo balance with which you want to play the sound.
     * @param percentage    The percentage of the current sfx volume the sound should be played at.
     * @return A boolean of whether or not the sound was played successfully.
     */
    public boolean playShipForward(float balance, float percentage){

        float curVolume = (float)Math.pow( sfxVol*percentage, sfxScaleFactor );

        if(shipStrafePlaying){
            stopShipStrafe();
        }

        if(!shipForwardPlaying){

            shipForwardPlaying = true;
            return ShipForward.setStreamID( sfx.play(ShipForward.getSoundID(),
                    curVolume-(curVolume*balance), curVolume+(curVolume*balance), 0,  -1, 0) );

        }

        else{

            sfx.setVolume( ShipForward.getStreamID(), Math.abs(curVolume-(curVolume*balance) ),  Math.abs(curVolume+(curVolume*balance) ) );
            return false;

        }

    }

    /**
     * Avoid the misconception: This method does not stop the ship from moving forward, but rather
     * it stops the sound of it moving forward.
     *
     * @return A boolean representing whether or not the sound was stopped deliberately, as opposed
     *         to encountering an already finished stream.
     */
    public boolean stopShipForward(){

        if(shipForwardPlaying){

            sfx.stop(ShipForward.getStreamID());
            shipForwardPlaying = false;
            return true;

        }

        else return false;
    }

    /**
     * When the ship strafes it does not need to make the ship-forward sound--merely the ship-strafe sound
     * is required. Hence when called this method automatically stops the ship-forward sound if it was playing.
     *
     * @param balance       The left/right stereo balance with which you want to play the sound.
     * @param percentage    The percentage of the current sfx volume the sound should be played at.
     * @return A boolean of whether or not the sound was played successfully.
     */
    public boolean playShipStrafe(float balance, float percentage){

        float curVolume = (float)Math.pow( sfxVol*percentage, sfxScaleFactor );

        if(shipForwardPlaying){
            stopShipForward();
        }

        if(!shipStrafePlaying){

            return ShipStrafe.setStreamID( sfx.play(ShipStrafe.getSoundID(),
                    curVolume-(curVolume*balance), curVolume+(curVolume*balance), 0,  -1, 0) );

        }

        else{

            sfx.setVolume( ShipStrafe.getStreamID(), curVolume-(curVolume*balance), curVolume+(curVolume*balance) );
            return false;

        }

    }

    /**
     * Avoid the misconception: This method does not stop the ship from strafing, but rather
     * it stops the sound of it strafing.
     *
     * @return A boolean representing whether or not the sound was stopped deliberately, as opposed
     *         to encountering an already finished stream.
     */
    public boolean stopShipStrafe(){

        if(shipStrafePlaying){

            sfx.stop(ShipStrafe.getStreamID());
            shipStrafePlaying = false;
            return true;

        }

        else return false;
    }

    /*
    MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC
    MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC
    MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC
    MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC
    MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC
    MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC
    MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC MUSIC
     */

    /**
     * This method starts the playing of the Menu's music. If other music
     * is already playing, it stops that music and resets the media player.
     * If the menu song is already playing, it updates the volume.
     *
     * @param percentage The percentage of the current musVol to play the music at.
     * @return A boolean if the song is playing at the end of the method call.
     */
    public boolean playMenuMusic(float percentage){

        //If music is already playing,
        if(mus != null){
            if(mus.isPlaying()){

                //And it is the menu music,
                if(currentBGM == MUS_KEYS.MENU){
                    //update the volume and return whether or not the music is playing..
                    //mus.setVolume(musVol*percentage, musVol*percentage);
                    return mus.isPlaying();
                }
                //If it isn't the menu music,
                else{
                    //pause and stop the music,
                    mus.pause();
                    mus.stop();
                    //and reset the MediaPlayer
                    mus.reset();
                }
             }
        }

        //Update the currentBGM
        currentBGM = MUS_KEYS.MENU;

        //Recreate the MediaPlayer
        mus = MediaPlayer.create(context, R.raw.menu_theme);

        //Set the volume of the MediaPlayer to that specified.
        mus.setVolume(musVol*percentage, musVol*percentage);

        //Since game music needs to loop, we set the MediaPlayer to loop.
        mus.setLooping(true);

        /*//Prepare the MediaPlayer
        mus.setAudioStreamType(AudioManager.USE_DEFAULT_STREAM_TYPE);
        try {
            mus.prepare();
        }
        catch (IOException e) {
            e.printStackTrace();
        } */

        //Finally, we start the MediaPlayer.
        mus.seekTo(0);
        mus.start();

        //We return whether or not the music is playing properly or not.
        return true;// mus.isPlaying();

    }

    /**
     * This method starts playing BGM A. The same rules apply as the Menu Music.
     *
     * @param percentage The percentage of the current musVol to play the music at.
     * @return A boolean if the song is playing at the end of the method call.
     */
    public boolean playBgmA(float percentage){

        //If music is playing...
        if(mus.isPlaying()){

            //If that music is already BGM A...
            if(currentBGM == MUS_KEYS.BGM_A){
                //Then update the volume.
                mus.setVolume(musVol*percentage, musVol*percentage);
                return mus.isPlaying();
            }
            else{
                //pause and stop the music,
                mus.pause();
                mus.stop();
                //and reset the MediaPlayer.
                mus.reset();
            }

        }

        //Update currentBGM accordingly.
        currentBGM = MUS_KEYS.BGM_A;

        //Recreate the MediaPlayer with the proper song.
        int placeHolder =0;
        mus = MediaPlayer.create(context, R.raw.time_attack_bgm);

        //Set the volume of the MediaPlayer to the value expected.
        mus.setVolume(musVol*percentage, musVol*percentage);

        //Make the music loop as it needs to.
        mus.setLooping(true);

        //Finally start the MediaPlayer.
        mus.start();

        //Return whether or not the music was started successfully.
        return mus.isPlaying();
    }

    /**
     * This method starts playing BGM B. The same rules apply as the Menu Music.
     *
     * @param percentage The percentage of the current musVol to play the music at.
     * @return A boolean if the song is playing at the end of the method call.
     */
    public boolean playBgmB(float percentage){

        //Yet again, if music is playing,
        if(mus.isPlaying()){

            //And it's the song we want,
            if(currentBGM == MUS_KEYS.BGM_B){
                //merely update the volume, and return whether or not the
                //the music is continuing to play.
                mus.setVolume(musVol*percentage, musVol*percentage);
                return mus.isPlaying();
            }
            //If it's not the music that we want,
            else{
                //Pause, stop, and reset the MediaPlayer.
                mus.pause();
                mus.stop();
                mus.reset();

            }
        }

        //Update the current bgm.
        currentBGM = MUS_KEYS.BGM_B;

        //Recreate the MediaPlayer with the proper song.
        int placeHolder =0;
        mus = MediaPlayer.create(context, R.raw.classic_bgm);

        //Set the volume to the expected value.
        mus.setVolume(musVol*percentage, musVol*percentage);

        //Make the song loop, and start it.
        mus.setLooping(true);
        mus.start();

        //Return whether or not the song was started properly.
        return mus.isPlaying();
    }

    /**
     * This method starts playing BGM B. The same rules apply as the Menu Music.
     *
     * @param percentage The percentage of the current musVol to play the music at.
     * @return A boolean if the song is playing at the end of the method call.
     */
    public boolean playBgmC(float percentage){

        //If there is music playing.
        if(mus.isPlaying()){
            //If it's what we wanted,
            if(currentBGM == MUS_KEYS.BGM_C){
                //just update the volume and return whether the music
                //is still playing.
                mus.setVolume(musVol*percentage, musVol*percentage);
                return mus.isPlaying();
            }
            //If it's not the music that we want,
            else{
                //Pause, stop, and reset the MediaPlayer.
                mus.pause();
                mus.stop();
                mus.reset();
            }

        }

        //Update currentBGM.
        currentBGM = MUS_KEYS.BGM_C;

        //Recreate the MediaPlayer with the proper song.
        int placeHolder =0;
        mus = MediaPlayer.create(context, R.raw.survival_bgm);

        //Set the volume to the expected value.
        mus.setVolume(musVol*percentage, musVol*percentage);

        //Make loop, and start it.
        mus.setLooping(true);
        mus.start();

        //Return whether or not the music is playing.
        return mus.isPlaying();
    }

    public boolean killSoundEngine(){
        try{
            if(mus!= null){
                if(mus.isPlaying()){
                    mus.stop();
                    mus.release();
                }
            }
            if(sfx != null){
                sfx.release();
            }
            return true;
        }
        catch(Exception e){
            return false;
        }
    }

}
