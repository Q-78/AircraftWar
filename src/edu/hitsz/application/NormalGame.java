package edu.hitsz.application;

/** 普通难度：敌机生成周期、敌机速度和敌机血量随时间递增，Boss 血量固定。 */
public class NormalGame extends Game {
    @Override
    protected void initDifficultyParameters() {
        enemyMaxNumber = 5;
        enemySpawnCycle = 22;
        heroShootCycle = 20;
        enemyShootCycle = 22;
        bossScoreThreshold = 120;
        nextBossScore = bossScoreThreshold;
        bossBaseHp = 200;

        mobProbability = 0.45;
        eliteProbability = 0.30;
        elitePlusProbability = 0.17;
        eliteProProbability = 0.08;
        difficultyStepCycle = 500;
    }

    @Override
    protected void increaseDifficultyAction() {
        difficultyStepCounter++;
        if (difficultyStepCounter >= difficultyStepCycle) {
            difficultyStepCounter = 0;
            difficultyLevel++;

            enemySpawnCycle = Math.max(12, enemySpawnCycle - 1.5);
            enemySpeedEnhance = Math.min(6, enemySpeedEnhance + 1);
            enemyHpEnhance += 5;

            System.out.println("普通难度提升：level=" + difficultyLevel
                    + ", enemySpawnCycle=" + enemySpawnCycle
                    + ", enemySpeedEnhance=" + enemySpeedEnhance
                    + ", enemyHpEnhance=" + enemyHpEnhance);
        }
    }
}
