package edu.hitsz.application;

import edu.hitsz.dao.ScoreRecord;
import edu.hitsz.dao.ScoreRecordDao;
import edu.hitsz.dao.ScoreRecordDaoImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * 排行榜界面：使用 JTable 展示当前难度得分记录，并支持删除选中记录。
 */
public class ScoreBoard extends JPanel {

    private final ScoreRecordDao dao;
    private final DefaultTableModel model;
    private final JTable scoreTable;
    private final Runnable restartAction;

    public ScoreBoard(String difficultyName) {
        this(difficultyName, null);
    }

    public ScoreBoard(String difficultyName, Runnable restartAction) {
        this.restartAction = restartAction;
        this.setLayout(new BorderLayout(14, 14));
        this.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        this.setBackground(new Color(245, 247, 252));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("排行榜", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 28));
        titleLabel.setForeground(new Color(36, 52, 82));
        JLabel subTitleLabel = new JLabel("当前难度：" + getChineseDifficultyName(difficultyName), SwingConstants.CENTER);
        subTitleLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        subTitleLabel.setForeground(new Color(100, 112, 135));
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subTitleLabel, BorderLayout.SOUTH);
        this.add(titlePanel, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"名次", "玩家名", "得分", "时间"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        scoreTable = new JTable(model);
        scoreTable.setRowHeight(32);
        scoreTable.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        scoreTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scoreTable.setShowGrid(false);
        scoreTable.setIntercellSpacing(new Dimension(0, 0));
        scoreTable.getTableHeader().setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        scoreTable.getTableHeader().setBackground(new Color(55, 75, 110));
        scoreTable.getTableHeader().setForeground(Color.WHITE);
        scoreTable.setDefaultRenderer(Object.class, new ZebraRenderer());

        JScrollPane scrollPane = new JScrollPane(scoreTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 226, 238)));
        this.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 4));
        bottomPanel.setOpaque(false);
        JButton deleteButton = createButton("删除选中记录", new Color(225, 96, 90));
        JButton restartButton = createButton("重新选择难度", new Color(76, 135, 220));
        JButton closeButton = createButton("关闭", new Color(95, 110, 130));
        bottomPanel.add(deleteButton);
        if (restartAction != null) {
            bottomPanel.add(restartButton);
        }
        bottomPanel.add(closeButton);
        this.add(bottomPanel, BorderLayout.SOUTH);

        dao = new ScoreRecordDaoImpl(difficultyName + "_records.txt");
        refreshTable();

        deleteButton.addActionListener(e -> deleteSelectedRecord());
        if (restartAction != null) {
            restartButton.addActionListener(e -> restartGame());
        }
        closeButton.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) {
                window.dispose();
            }
        });
    }

    private JButton createButton(String text, Color background) {
        JButton button = new JButton(text);
        button.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(background);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(130, 34));
        return button;
    }

    private String getChineseDifficultyName(String difficultyName) {
        switch (difficultyName) {
            case "easy":
                return "简单模式";
            case "hard":
                return "困难模式";
            case "normal":
            default:
                return "普通模式";
        }
    }

    private void restartGame() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose();
        }
        restartAction.run();
    }

    private void refreshTable() {
        model.setRowCount(0);
        List<ScoreRecord> records = dao.getAllRecords();
        for (int i = 0; i < records.size(); i++) {
            ScoreRecord record = records.get(i);
            model.addRow(new Object[]{
                    i + 1,
                    record.getPlayerName(),
                    record.getScore(),
                    record.getTime()
            });
        }
    }

    private void deleteSelectedRecord() {
        int row = scoreTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一条记录");
            return;
        }

        int result = JOptionPane.showConfirmDialog(
                this,
                "确认删除选中的成绩记录吗？",
                "确认删除",
                JOptionPane.YES_NO_OPTION
        );
        if (result == JOptionPane.YES_OPTION) {
            dao.deleteRecord(row);
            refreshTable();
        }
    }

    private static class ZebraRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(column == 1 ? LEFT : CENTER);
            if (isSelected) {
                c.setBackground(new Color(205, 225, 255));
                c.setForeground(new Color(20, 35, 60));
            } else {
                c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(242, 246, 252));
                c.setForeground(new Color(45, 55, 75));
            }
            return c;
        }
    }
}
