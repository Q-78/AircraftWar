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
    private JButton instructionButton;
    private JButton scoreBoardButton;

    public ModeChoose(JFrame frame) {
        mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout(20, 24));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(70, 58, 62, 58));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Aircraft War", SwingConstants.CENTER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 42));
        titlePanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("选择难度，开始你的空战挑战", SwingConstants.CENTER);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setForeground(new Color(215, 225, 245));
        subtitleLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        titlePanel.add(Box.createVerticalStrut(8));
        titlePanel.add(subtitleLabel);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        JPanel cardPanel = new JPanel(new GridLayout(5, 1, 0, 16));
        cardPanel.setOpaque(false);
        cardPanel.setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));

        easyModeButton = createMenuButton("简单模式", new Color(76, 175, 120));
        normalModeButton = createMenuButton("普通模式", new Color(76, 135, 220));
        hardModeButton = createMenuButton("困难模式", new Color(225, 105, 90));
        instructionButton = createMenuButton("操作说明", new Color(110, 95, 210));
        scoreBoardButton = createMenuButton("查看排行榜", new Color(80, 180, 190));

        cardPanel.add(easyModeButton);
        cardPanel.add(normalModeButton);
        cardPanel.add(hardModeButton);
        cardPanel.add(instructionButton);
        cardPanel.add(scoreBoardButton);

        JPanel cardWrapper = new RoundedPanel(new Color(255, 255, 255, 42));
        cardWrapper.setLayout(new BorderLayout());
        cardWrapper.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        cardWrapper.add(cardPanel, BorderLayout.CENTER);
        mainPanel.add(cardWrapper, BorderLayout.CENTER);

        JLabel hintLabel = new JLabel("P/空格暂停  ·  B释放全屏大招  ·  Shift无敌冲刺", SwingConstants.CENTER);
        hintLabel.setForeground(new Color(225, 232, 245));
        hintLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        mainPanel.add(hintLabel, BorderLayout.SOUTH);

        easyModeButton.addActionListener(e -> startGame(frame, Difficulty.EASY));
        normalModeButton.addActionListener(e -> startGame(frame, Difficulty.NORMAL));
        hardModeButton.addActionListener(e -> startGame(frame, Difficulty.HARD));
        instructionButton.addActionListener(e -> showInstructionDialog(frame));
        scoreBoardButton.addActionListener(e -> showScoreBoardChooser(frame));
    }

    private JButton createMenuButton(String text, Color background) {
        JButton button = new JButton(text);
        button.setFont(new Font("Microsoft YaHei", Font.BOLD, 22));
        button.setForeground(Color.WHITE);
        button.setBackground(background);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(330, 54));
        return button;
    }

    private void startGame(JFrame frame, Difficulty difficulty) {
        GameConfig.difficulty = difficulty;
        frame.dispose();
        Main.launchGame();
    }

    /**
     * 游戏开始前的操作说明，包含基础操作、暂停、主动大招和排行榜入口。
     */
    private void showInstructionDialog(JFrame parent) {
        String message = "操作说明：\n\n"
                + "1. 鼠标移动：控制英雄机移动。\n"
                + "2. P / 空格键：暂停或继续游戏。\n"
                + "3. B 键：主动释放全屏炸弹大招，清除大部分敌机和敌机子弹。\n"
                + "   注意：Boss 不会被秒杀，只会受到炸弹伤害；大招有 15 秒冷却时间。\n"
                + "4. Shift 键：进入无敌冲刺，2 秒内免疫伤害并向前突进；技能有 10 秒冷却。\n"
                + "5. 无敌冲刺期间撞到普通敌机可直接撞毁；撞到 Boss 时只造成伤害。\n"
                + "6. 连续击毁敌机会触发 Combo，连击越高，击杀得分加成越高。\n"
                + "7. Boss 登场前会出现 WARNING 警告动画，请及时躲避弹幕。\n"
                + "8. 游戏结束后可以保存成绩，并重新选择难度开始新游戏。\n"
                + "9. 不在游戏进行时，可以在主菜单点击“查看排行榜”。";

        JOptionPane.showMessageDialog(
                parent,
                message,
                "操作说明",
                JOptionPane.INFORMATION_MESSAGE
        );
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

    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint paint = new GradientPaint(0, 0, new Color(18, 32, 55), 0, getHeight(), new Color(58, 82, 130));
            g2.setPaint(paint);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(new Color(255, 255, 255, 22));
            g2.fillOval(-90, -90, 240, 240);
            g2.fillOval(getWidth() - 130, getHeight() - 170, 260, 260);
            g2.dispose();
        }
    }

    private static class RoundedPanel extends JPanel {
        private final Color background;

        private RoundedPanel(Color background) {
            this.background = background;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(background);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 26, 26);
            g2.setColor(new Color(255, 255, 255, 45));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 26, 26);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
