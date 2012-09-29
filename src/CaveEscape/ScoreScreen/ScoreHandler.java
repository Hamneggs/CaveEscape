package CaveEscape.ScoreScreen;

import CaveEscapeCore.Constants.Const;
import CaveEscapeCore.CoreGameplay.GameplayMode;
import CaveEscapeCore.Player.Player;
import CaveEscapeCore.Terrain.ImprovedNoise;
import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

/**
 * Handles the IO to the score file.
 */
public class ScoreHandler {

    boolean loaded = false;
    
    private ArrayList<String> scores = new ArrayList<String>();

    public ScoreHandler(Context context){
        readFromFile(context);
    }

    public void addScore(Player player, GameplayMode mode){
        scores.add(encodeScore(player, mode));
    }


    private String encodeScore(Player player, GameplayMode mode){

        Date date = new Date();


        String returnMe = "";

        returnMe += player.getShip().getName()+"\n";
        returnMe += player.getScore()+"\n";
        switch (mode) {
            case noneSelected:
                returnMe += "noneSelected";
                break;
            case TimeAttack:
                returnMe += "TimeAttack";
                break;
            case Classic:
                returnMe += "Classic";
                break;
            case Survival:
                returnMe += "Survival";
                break;
        }
        returnMe += "\n";
        returnMe += date.getMonth()+"\n";
        returnMe += date.getDay()+"\n";
        returnMe += date.getHours()+"\n";
        returnMe += date.getMinutes()+"\n";
        returnMe += date.getSeconds()+"\n";

        return returnMe;

    }

    private String encryptString(String string){
        char[] stringChars = string.toCharArray();
        for(int i = 0; i < stringChars.length; i++){
            stringChars[i] += (byte)(64*ImprovedNoise.noise(i, i, i));
        }
        return new String(stringChars);
    }



    public String getScores(){return "";}

    private void readFromFile(Context context){
        String exStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(exStorageState)){
            try{
                File root = Environment.getExternalStorageDirectory();

                String path = root+"/backroomGames/data/"+Const.scoreFilename+".ces";
                boolean exists = (new File(path).exists());
                if(!exists){
                    Toast.makeText(context, "High scores not loaded because the file does not exist!", Toast.LENGTH_SHORT).show();
                    if(Const.verboseInfo){
                        System.out.println(Const.verboseTag+"Could not read high scores from file because the file does not exist.");
                    }
                }
                else{
                    File f = new File(path);
                    Scanner reader = new Scanner(f);
                    if(Const.verboseInfo){
                        System.out.println(Const.verboseTag+"Clearing currently loaded scores...");
                    }
                    scores = new ArrayList<String>();
                    if(Const.verboseInfo){
                        System.out.println(Const.verboseTag+"Loading new scores: ");
                    }
                    while(reader.hasNext()){
                        scores.add(reader.next());
                        if(Const.verboseInfo){
                            System.out.println(Const.verboseTag+"Score item loaded from file!");
                        }
                    }
                    loaded = true;

                }

            }
            catch(IOException i){
                if(Const.verboseInfo){
                    System.out.println(Const.verboseTag+"Something wrong with file. See following stack trace for more info:");
                    i.printStackTrace();
                }
            }
        }
    }


    private void saveToFile( Context context){
        String exStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(exStorageState)){
            try {
                File root = Environment.getExternalStorageDirectory();

                // Test if the path exists
                String path = root+"/backroomGames/data/";
                boolean exists = (new File(path).exists());

                // If not, create dirs
                if (!exists) {
                    Toast.makeText(context, "High Score directory does not exist. Creating...", Toast.LENGTH_SHORT).show();
                    if(Const.verboseInfo){
                        System.out.println(Const.verboseTag+"High score directory does not exist. Attempting to create...");
                    }
                    new File(path).mkdirs();
                    if(Const.verboseInfo){
                        System.out.println(Const.verboseTag+"High score directory created.");
                    }
                }

                if(Const.verboseInfo){
                    System.out.println(Const.verboseTag+"Attempting to create score storage file "+path+Const.scoreFilename+".ces ...");
                }
                File scoreFile = new File(path+Const.scoreFilename+".ces");
                scoreFile.createNewFile();
                if(Const.verboseInfo){
                    System.out.println(Const.verboseTag+"Score storage file "+path+Const.scoreFilename+".ces created.");
                }

                if(Const.verboseInfo){
                    System.out.println(Const.verboseTag+"Commencing write to file "+path+Const.scoreFilename+".ces ...");
                }
                FileWriter logWriter = new FileWriter(scoreFile);
                BufferedWriter outer = new BufferedWriter(logWriter);
                // Write log entries to file
                for(String s : scores){
                    outer.write(s);
                    if(Const.verboseInfo){
                        System.out.println(Const.verboseTag+"Writing to file...");
                    }
                }

                outer.close();
                if(Const.verboseInfo){
                    System.out.println(Const.verboseTag + "Finished writing scores.");
                }
            } catch (IOException e) {
                if(Const.verboseInfo){
                    System.out.println(Const.verboseTag + "IO EXCEPTION WHEN WRITING SCORES: ");
                }
                e.printStackTrace();
                Toast.makeText(context, "Couldn't save scores.", Toast.LENGTH_SHORT);
            }
        }
        else{
            //FAIL
            if(Const.verboseInfo){
                System.out.println(Const.verboseTag + "External storage not mounted or something.");
            }
            Toast.makeText(context, "Score file not accessible!", Toast.LENGTH_SHORT).show();
        }
    }
}
