package edu.hitsz.dao;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ScoreRecordDaoImpl implements ScoreRecordDao {

    private final List<ScoreRecord> records = new ArrayList<>();
    private final String filename;

    public ScoreRecordDaoImpl(String filename) {
        this.filename = filename;
        loadFromFile();
    }

    private void loadFromFile() {
        File file = new File(filename);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String playerName = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    String time = parts[2];
                    records.add(new ScoreRecord(playerName, score, time));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        sortRecords();
    }

    private void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (ScoreRecord record : records) {
                bw.write(record.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sortRecords() {
        records.sort(Comparator.comparingInt(ScoreRecord::getScore).reversed());
    }

    @Override
    public List<ScoreRecord> getAllRecords() {
        sortRecords();
        return records;
    }

    @Override
    public void addRecord(ScoreRecord record) {
        records.add(record);
        sortRecords();
        saveToFile();
    }

    @Override
    public void deleteRecord(int index) {
        if (index >= 0 && index < records.size()) {
            records.remove(index);
            saveToFile();
        }
    }
}