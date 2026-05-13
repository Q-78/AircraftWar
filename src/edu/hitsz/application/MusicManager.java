package edu.hitsz.application;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * 音频管理类：统一封装背景音乐、Boss 音乐和事件音效。
 */
public class MusicManager {

    private static final String BGM = "src/videos/bgm.wav";
    private static final String BOSS_BGM = "src/videos/bgm_boss.wav";
    private static final String BULLET_HIT = "src/videos/bullet_hit.wav";
    private static final String BOMB_EXPLOSION = "src/videos/bomb_explosion.wav";
    private static final String GAME_OVER = "src/videos/game_over.wav";
    private static final String GET_SUPPLY = "src/videos/get_supply.wav";

    private static MusicThread bgmThread;
    private static MusicThread bossBgmThread;

    /**
     * 音效线程池：避免每次子弹命中都 new Thread，减少游戏过程中的瞬时卡顿。
     */
    private static final ExecutorService SOUND_EFFECT_EXECUTOR = Executors.newFixedThreadPool(3, new ThreadFactory() {
        private int index = 1;

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "sound-effect-" + index++);
            thread.setDaemon(true);
            return thread;
        }
    });

    private static long lastBulletHitTime = 0L;
    private static final long BULLET_HIT_INTERVAL_MS = 80L;

    public static void playBgm() {
        if (bgmThread != null) {
            return;
        }
        bgmThread = new MusicThread(BGM, true);
        bgmThread.start();
    }

    public static void stopBgm() {
        if (bgmThread != null) {
            bgmThread.stopMusic();
            bgmThread = null;
        }
    }

    public static void playBossBgm() {
        if (bossBgmThread != null) {
            return;
        }
        bossBgmThread = new MusicThread(BOSS_BGM, true);
        bossBgmThread.start();
    }

    public static void stopBossBgm() {
        if (bossBgmThread != null) {
            bossBgmThread.stopMusic();
            bossBgmThread = null;
        }
    }

    public static void playBulletHit() {
        long now = System.currentTimeMillis();
        if (now - lastBulletHitTime < BULLET_HIT_INTERVAL_MS) {
            return;
        }
        lastBulletHitTime = now;
        playEffect(BULLET_HIT);
    }

    public static void playBombExplosion() {
        playEffect(BOMB_EXPLOSION);
    }

    public static void playGetSupply() {
        playEffect(GET_SUPPLY);
    }

    public static void playGameOver() {
        playEffect(GAME_OVER);
    }

    private static void playEffect(String filename) {
        SOUND_EFFECT_EXECUTOR.execute(() -> new MusicThread(filename).run());
    }

    public static void stopAll() {
        stopBgm();
        stopBossBgm();
    }
}
