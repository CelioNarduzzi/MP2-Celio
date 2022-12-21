package ch.epfl.cs107.play.game;

import ch.epfl.cs107.play.game.icrogue.ICRogue;

import javax.sound.sampled.*;
import java.net.URL;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class Sound {
    Clip clip;
    private final BlockingQueue<URL> queue = new ArrayBlockingQueue<URL>(1);
    URL soundURL[] = new URL[30];

    public Sound() {

        soundURL[0] = getClass().getResource("/sounds/music.wav");
        soundURL[1] = getClass().getResource("/sounds/FinalBattle.wav");
        soundURL[2] = getClass().getResource("/sounds/gameover.wav");
        soundURL[3] = getClass().getResource("/sounds/unlock.wav");
        soundURL[4] = getClass().getResource("/sounds/burning.wav");
        soundURL[5] = getClass().getResource("/sounds/boots.wav");


    }

    public void setFile(int i) {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
        clip = AudioSystem.getClip();
        clip.open(ais);


                    } catch (Exception e) {

                    }
                }


                public void play() {
                    clip.setFramePosition(0);
                    clip.start();
                }

                public void loop() {
                    clip.loop(Clip.LOOP_CONTINUOUSLY);
                }

                public void stop() {
                    clip.stop();
                }

                public void close() {
                    clip.close();
                }
            }
