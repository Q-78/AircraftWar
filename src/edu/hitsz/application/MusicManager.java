package edu.hitsz.application;

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
        new MusicThread(BULLET_HIT).start();
    }

    public static void playBombExplosion() {
        new MusicThread(BOMB_EXPLOSION).start();
    }

    public static void playGetSupply() {
        new MusicThread(GET_SUPPLY).start();
    }

    public static void playGameOver() {
        new MusicThread(GAME_OVER).start();
    }

    public static void stopAll() {
        stopBgm();
        stopBossBgm();
    }
}
