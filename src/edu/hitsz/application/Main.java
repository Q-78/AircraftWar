package edu.hitsz.application;

import javax.swing.*;
import java.awt.*;

/**
 * 程序入口
 * @author hitsz
 */
public class Main {

    public static final int WINDOW_WIDTH = 512;
    public static final int WINDOW_HEIGHT = 768;

    public static void main(String[] args) {
        System.out.println("Hello Aircraft War");
        showModeChoose();
    }

    /**
     * 显示难度选择界面
     */
    public static void showModeChoose() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        JFrame frame = new JFrame("Aircraft War - 选择难度");
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setResizable(false);
        frame.setBounds(((int) screenSize.getWidth() - WINDOW_WIDTH) / 2, 0,
                WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ModeChoose modeChoose = new ModeChoose(frame);
        frame.setContentPane(modeChoose.getMainPanel());
        frame.setVisible(true);
    }

    /**
     * 根据已选择的难度启动游戏
     */
    public static void launchGame() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        JFrame frame = new JFrame("Aircraft War - " + GameConfig.difficulty);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setResizable(false);
        frame.setBounds(((int) screenSize.getWidth() - WINDOW_WIDTH) / 2, 0,
                WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Game game;
        switch (GameConfig.difficulty) {
            case EASY:
                game = new EasyGame();
                break;
            case HARD:
                game = new HardGame();
                break;
            case NORMAL:
            default:
                game = new NormalGame();
                break;
        }
        frame.add(game);
        frame.setVisible(true);
        game.action();
    }
    /**
     * 打开排行榜窗口。allowRestart 为 true 时，排行榜底部提供“重新选择难度”入口。
     */
    public static void showScoreBoard(String difficultyName, boolean allowRestart) {
        JFrame frame = new JFrame("排行榜");
        frame.setSize(500, 600);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(new ScoreBoard(difficultyName, allowRestart ? Main::showModeChoose : null));
        frame.setVisible(true);
    }

}
