package edu.hitsz.application;

import javax.sound.sampled.*;
import java.io.File;

/**
 * 音频播放线程：支持单次播放、循环播放和中途停止。
 */
public class MusicThread extends Thread {

    private final String filename;
    private final boolean loop;
    private volatile boolean running = true;
    private Clip clip;

    public MusicThread(String filename) {
        this(filename, false);
    }

    public MusicThread(String filename, boolean loop) {
        this.filename = filename;
        this.loop = loop;
    }

    @Override
    public void run() {
        try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filename))) {
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);

            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                clip.start();
            }

            while (running && clip.isOpen()) {
                if (!loop && !clip.isRunning()) {
                    break;
                }
                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            // stopMusic() 主动 interrupt 线程时会进入这里，这是正常停止音乐，不是播放失败。
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            // 只有真正的音频文件、格式或播放设备异常才打印错误。
            System.out.println("音频播放失败：" + filename);
            e.printStackTrace();
        } finally {
            closeClip();
        }
    }

    public void stopMusic() {
        running = false;
        closeClip();
        interrupt();
    }

    private void closeClip() {
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            if (clip.isOpen()) {
                clip.close();
            }
        }
    }
}
