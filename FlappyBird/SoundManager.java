import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

public class SoundManager {
    private static Clip bgmClip;
    private static boolean soundEnabled = true;

    private SoundManager() {}

    public static void play(String soundName) {
        if (!soundEnabled) {
            return;
        }

        try {
            AudioInputStream audioStream = getAudioStream(soundName + ".wav");
            if (audioStream == null) {
                return;
            }
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            // Audio error non-blocking
        }
    }

    public static void playBgm() {
        if (!soundEnabled) {
            return;
        }

        try {
            if (bgmClip != null && bgmClip.isRunning()) {
                return;
            }
            AudioInputStream audioStream = getAudioStream("bgm.wav");
            if (audioStream == null) {
                return;
            }
            bgmClip = AudioSystem.getClip();
            bgmClip.open(audioStream);
            bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
            bgmClip.start();
        } catch (Exception e) {
            // Non-blocking
        }
    }

    public static void stopBgm() {
        try {
            if (bgmClip != null) {
                bgmClip.stop();
                bgmClip.close();
                bgmClip = null;
            }
        } catch (Exception e) {
            // Non-blocking
        }
    }

    private static AudioInputStream getAudioStream(String fileName) {
        try {
            URL url = SoundManager.class.getResource("/assets/" + fileName);
            if (url == null) {
                url = SoundManager.class.getResource("assets/" + fileName);
            }
            if (url != null) {
                InputStream in = new BufferedInputStream(url.openStream());
                return AudioSystem.getAudioInputStream(in);
            }

            File file = new File("assets/" + fileName);
            if (!file.exists()) {
                file = new File("FlappyBird/assets/" + fileName);
            }
            if (file.exists()) {
                InputStream in = new BufferedInputStream(file.toURI().toURL().openStream());
                return AudioSystem.getAudioInputStream(in);
            }
        } catch (Exception e) {
            // Resource not found or stream unreadable
        }
        return null;
    }

    public static boolean isSoundEnabled() {
        return soundEnabled;
    }

    public static void setSoundEnabled(boolean enabled) {
        soundEnabled = enabled;
        if (!soundEnabled) {
            stopBgm();
        }
    }
}
