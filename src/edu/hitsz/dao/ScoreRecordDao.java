package edu.hitsz.dao;

import java.util.List;

public interface ScoreRecordDao {
    List<ScoreRecord> getAllRecords();
    void addRecord(ScoreRecord record);
    void deleteRecord(int index);
}