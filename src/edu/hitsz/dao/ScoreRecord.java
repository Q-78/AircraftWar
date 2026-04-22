package edu.hitsz.dao;

public class ScoreRecord {
    private String playerName;
    private int score;
    private String time;

    public ScoreRecord(String playerName, int score, String time) {
        this.playerName = playerName;
        this.score = score;
        this.time = time;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    public String getTime() {
        return time;
    }

    @Override
    public String toString() {
        return playerName + "," + score + "," + time;
    }
}