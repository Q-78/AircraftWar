package edu.hitsz.application;

import edu.hitsz.dao.ScoreRecord;
import edu.hitsz.dao.ScoreRecordDao;
import edu.hitsz.dao.ScoreRecordDaoImpl;

import javax.swing.*;
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

    public ScoreBoard(String difficultyName) {
        this.setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("排行榜 - " + difficultyName.toUpperCase(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        this.add(titleLabel, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"名次", "玩家名", "得分", "时间"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        scoreTable = new JTable(model);
        scoreTable.setRowHeight(28);
        this.add(new JScrollPane(scoreTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        JButton deleteButton = new JButton("删除选中记录");
        JButton closeButton = new JButton("关闭");
        bottomPanel.add(deleteButton);
        bottomPanel.add(closeButton);
        this.add(bottomPanel, BorderLayout.SOUTH);

        dao = new ScoreRecordDaoImpl(difficultyName + "_records.txt");
        refreshTable();

        deleteButton.addActionListener(e -> deleteSelectedRecord());
        closeButton.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) {
                window.dispose();
            }
        });
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
}
