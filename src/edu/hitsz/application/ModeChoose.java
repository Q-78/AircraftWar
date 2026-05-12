package edu.hitsz.application;

import javax.swing.*;

/**
 * 难度选择界面，对应 Swing UI Form：ModeChoose.form
 */
public class ModeChoose {
    private JPanel mainPanel;
    private JButton easyModeButton;
    private JButton normalModeButton;
    private JButton hardModeButton;

    public ModeChoose(JFrame frame) {
        easyModeButton.addActionListener(e -> {
            GameConfig.difficulty = Difficulty.EASY;
            frame.dispose();
            Main.launchGame();
        });

        normalModeButton.addActionListener(e -> {
            GameConfig.difficulty = Difficulty.NORMAL;
            frame.dispose();
            Main.launchGame();
        });

        hardModeButton.addActionListener(e -> {
            GameConfig.difficulty = Difficulty.HARD;
            frame.dispose();
            Main.launchGame();
        });
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}
