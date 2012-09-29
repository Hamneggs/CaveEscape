package CaveEscapeCore.SoundAndMusic;

/**
 * Encapsulates the soundID and streamID of a single sound within a SoundPool.
 * This allows for easy access of the fields.
 */
public class Sound {

    /**
     * The soundID of the sound. This is used when selecting a sound to play.
     * It should be only populated by a call to SoundPool.load().
     */
    private int soundID;

    /**
     * The streamID of the sound. It is used for stopping or changing the properties
     * of a currently playing sound. It should only be populated by a call to
     * SoundPool.play().
     */
    private int streamID;

    /**
     * Constructs the Sound. Since we shouldn't have a streamID at the time we make
     * the sound, the only parameter is the soundID of the sound in the SoundPool.
     * The streamID is initialized to zero, which is the standard for non-playing or
     * play-failed sounds.
     * @param soundID The soundID of the sound. This is used when selecting a sound to
     *                play within the SoundPool, and as such should only be populated by a
     *                call to SoundPool.load().
     */
    public Sound(int soundID){
        this.soundID = soundID;
        this.streamID = 0;
    }

    /**
     * Sets the StreamID of the sound.
     *
     * @param streamID The StreamID returned by calling SoundPool.play()
     *                 on the SoundID of this sound. THIS IS THE ONLY WAY
     *                 THIS FIELD SHOULD BE POPULATED.
     * @return A boolean representing whether or not the sound was successfully
     *         loaded and begun.
     */
    public boolean setStreamID(int streamID){

        this.streamID = streamID;

        //If the SoundPool gives a streamID of 0, it means that
        //it was not successfully brought into playing.
        if(streamID == 0){
            return false;
        }
        else{
            return true;
        }
    }

    /**
     * Returns the streamID of this sound.
     *
     * @return The streamID of this sound. If zero is returned,
     * it means that either no StreamID has been set yet, or
     * the sound was not started successfully at the last attempt
     * to play it.
     */
    public int getStreamID(){
        return streamID;
    }

    /**
     * Returns the soundID of this Sound.
     * @return The soundID of this Sound, which should have been
     * created by calling SoundPool.loadResource() or equivalent.
     */
    public int getSoundID(){
        return soundID;
    }
}
