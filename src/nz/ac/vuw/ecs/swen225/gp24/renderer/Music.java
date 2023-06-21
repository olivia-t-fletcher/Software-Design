package nz.ac.vuw.ecs.swen225.gp24.renderer;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

/**
 * This class is used to load music from the music folder.
 * 
 * @author Serafina Slevin
 *
 */
public final class Music {
    /**
     * fields for each music clip 
     */
    private static final Clip L1Music = loadMusic("./sound/level1.wav");
    private static final Clip L2Music = loadMusic("./sound/level2.wav");
    private static final Clip keySound = loadMusic("./sound/keys.wav");
    private static final Clip buttonClick = loadMusic("./sound/buttonClick.wav");
    private static final Clip menuMusic = loadMusic("./sound/menu.wav");
    private static final Clip treasureSound = loadMusic("./sound/treasure.wav");

    private Music() {
    } // making constuctor private so cannot be called

    /**
     * This method is used to play the music associated with the current level
     * 
     * @param level the current level
     */
    public static void startLevelMusic(int level) {
        if (level == 1) {
            L1Music.setFramePosition(0);
            L1Music.loop(Clip.LOOP_CONTINUOUSLY);
            L1Music.start();
        } else if (level == 2) {
            L2Music.setFramePosition(0);
            L2Music.loop(Clip.LOOP_CONTINUOUSLY);
            L2Music.start();
        } else
            System.out.println("Level does not have music yet, level number: " + level);
    }

    /**
     * This method is used to pause level music once it starts
     */
    public static void pauseLevelMusic(int level) {
        if (level == 1) {
            L1Music.stop();
        } else if (level == 2) {
            L2Music.stop();
        } else
            System.out.println("Level does not have music yet, level number: " + level);
    }

    /**
     * used to play the menu music
     */
    public static void playMenuMusic() {
        menuMusic.setFramePosition(0);
        menuMusic.loop(Clip.LOOP_CONTINUOUSLY);
        menuMusic.start();
    }

    /**
     * used to play the level music after it has been paused
     * allows music to continue from where it left off
     * 
     * @param level the level number
     */
    public static void playLevelMusic(int level) {
        if (level == 1) {
            L1Music.start();
        } else if (level == 2) {
            L2Music.start();
        } else
            System.out.println("Level does not have music yet, level number: " + level);
    }

    /**
     * call to stop all music files from playing
     */
    public static void stopAllMusic() {
        if (L1Music != null) L1Music.stop(); 
        if (L2Music != null) L2Music.stop();
        if (keySound != null) keySound.stop();
        if (menuMusic != null) menuMusic.stop();
        if (treasureSound != null) treasureSound.stop();
    }

    /**
     * Plays the key sound.
     */
    public static void playPickUpKeySound() {
        keySound.setFramePosition(0);
        keySound.start();
    }

    /**
     * Plays the button click sound, used when navigating the menus
     */
    public static void playButtonClick() {
        buttonClick.setFramePosition(0);
        buttonClick.start();
    }

    /**
     * Plays the treasure sound.
     */
    public static void playTreasureSound() {
        treasureSound.setFramePosition(0);
        treasureSound.start();
    }

    /**
     * Loads a music file from the given path.
     * 
     * @param path The path to the music file (wav only)
     * @return Clip field 
     */
    public static Clip loadMusic(String path) {
        File f = new File(path);
        AudioInputStream audioStream;
        try {
            audioStream = AudioSystem.getAudioInputStream(f);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
        System.out.println("Music not found, path name: " + path);
        return null;
    }
}
