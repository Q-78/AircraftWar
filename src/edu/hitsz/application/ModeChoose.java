package edu.hitsz.application;

import javax.swing.*;
import java.awt.*;

/**
 * 难度选择界面。
 * 支持开始游戏，也支持在非游戏过程中查询排行榜。
 */
public class ModeChoose {
    private JPanel mainPanel;
    private JButton easyModeButton;
    private JButton normalModeButton;
    private JButton hardModeButton;
    private JButton scoreBoardButton;

    public ModeChoose(JFrame frame) {
        mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(80, 70, 80, 70));

        JLabel titleLabel = new JLabel("Aircraft War", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 0, 22));
        easyModeButton = createMenuButton("简单模式");
        normalModeButton = createMenuButton("普通模式");
        hardModeButton = createMenuButton("困难模式");
        scoreBoardButton = createMenuButton("查看排行榜");

        buttonPanel.add(easyModeButton);
        buttonPanel.add(normalModeButton);
        buttonPanel.add(hardModeButton);
        buttonPanel.add(scoreBoardButton);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        JLabel hintLabel = new JLabel("游戏中可按 P / 空格键暂停或继续", SwingConstants.CENTER);
        hintLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        mainPanel.add(hintLabel, BorderLayout.SOUTH);

        easyModeButton.addActionListener(e -> startGame(frame, Difficulty.EASY));
        normalModeButton.addActionListener(e -> startGame(frame, Difficulty.NORMAL));
        hardModeButton.addActionListener(e -> startGame(frame, Difficulty.HARD));
        scoreBoardButton.addActionListener(e -> showScoreBoardChooser(frame));
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 22));
        button.setFocusPainted(false);
        return button;
    }

    private void startGame(JFrame frame, Difficulty difficulty) {
        GameConfig.difficulty = difficulty;
        frame.dispose();
        Main.launchGame();
    }

    /**
     * 非游戏状态下查询排行榜：先选择难度，再打开该难度排行榜。
     */
    private void showScoreBoardChooser(JFrame parent) {
        String[] options = {"简单模式", "普通模式", "困难模式"};
        int result = JOptionPane.showOptionDialog(
                parent,
                "请选择要查看的排行榜难度：",
                "查看排行榜",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (result == -1) {
            return;
        }

        String difficultyName;
        switch (result) {
            case 0:
                difficultyName = "easy";
                break;
            case 2:
                difficultyName = "hard";
                break;
            case 1:
            default:
                difficultyName = "normal";
                break;
        }
        Main.showScoreBoard(difficultyName, false);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}
