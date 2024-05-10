import javax.sound.sampled.*;
import java.lang.Exception;
import java.io.IOException;
import java.io.File;

public class Sound {
    public static void playSound(String s) {
        try {
            File f = new File("./" + s);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(f.toURI().toURL());
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}