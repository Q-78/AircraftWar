package edu.hitsz.application;

/** 困难难度：敌机更密集，且敌机/英雄射击周期、敌机速度血量、Boss 血量均会递进。 */
public class HardGame extends Game {
    private int bossHpEnhance = 0;

    @Override
    protected void initDifficultyParameters() {
        enemyMaxNumber = 7;
        enemySpawnCycle = 18;
        heroShootCycle = 20;
        enemyShootCycle = 18;
        bossScoreThreshold = 100;
        nextBossScore = bossScoreThreshold;
        bossBaseHp = 240;

        mobProbability = 0.30;
        eliteProbability = 0.30;
        elitePlusProbability = 0.25;
        eliteProProbability = 0.15;
        difficultyStepCycle = 400;
    }

    @Override
    protected void increaseDifficultyAction() {
        difficultyStepCounter++;
        if (difficultyStepCounter >= difficultyStepCycle) {
            difficultyStepCounter = 0;
            difficultyLevel++;

            enemySpawnCycle = Math.max(8, enemySpawnCycle - 2);
            enemySpeedEnhance = Math.min(10, enemySpeedEnhance + 1);
            enemyHpEnhance += 8;
            heroShootCycle = Math.max(10, heroShootCycle - 1);
            enemyShootCycle = Math.max(8, enemyShootCycle - 1.5);

            System.out.println("困难难度提升：level=" + difficultyLevel
                    + ", enemySpawnCycle=" + enemySpawnCycle
                    + ", enemySpeedEnhance=" + enemySpeedEnhance
                    + ", enemyHpEnhance=" + enemyHpEnhance
                    + ", heroShootCycle=" + heroShootCycle
                    + ", enemyShootCycle=" + enemyShootCycle);
        }
    }

    @Override
    protected int getCurrentBossHp() {
        return bossBaseHp + bossHpEnhance;
    }

    @Override
    protected void afterBossGenerated() {
        bossHpEnhance += 60;
        System.out.println("困难模式：下一次 Boss 血量提升到 " + getCurrentBossHp());
    }
}
