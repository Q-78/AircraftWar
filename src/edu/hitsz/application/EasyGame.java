package edu.hitsz.application;

/** 简单难度：敌机少、无 Boss、难度不递进。 */
public class EasyGame extends Game {
    @Override
    protected void initDifficultyParameters() {
        enemyMaxNumber = 4;
        enemySpawnCycle = 28;
        heroShootCycle = 18;
        enemyShootCycle = 28;
        bossScoreThreshold = Integer.MAX_VALUE;
        nextBossScore = bossScoreThreshold;

        mobProbability = 0.70;
        eliteProbability = 0.20;
        elitePlusProbability = 0.10;
        eliteProProbability = 0.00;
    }

    @Override
    protected boolean canGenerateBoss() {
        return false;
    }
}
