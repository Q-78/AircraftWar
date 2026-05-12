package edu.hitsz.application;

import edu.hitsz.aircraft.*;
import edu.hitsz.prop.*;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.factory.*;
import edu.hitsz.dao.ScoreRecord;
import edu.hitsz.dao.ScoreRecordDao;
import edu.hitsz.dao.ScoreRecordDaoImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * 游戏主面板。
 *
 * 实验六模板方法模式：action() 被重构为 final 模板方法，统一定义游戏主循环骨架；
 * 简单、普通、困难三个子类只重写难度参数、Boss 控制和难度递进等可变步骤。
 */
public abstract class Game extends JPanel {

    private int backGroundTop = 0;

    protected final java.util.Timer timer;
    protected final int timeInterval = 40;

    protected final HeroAircraft heroAircraft;
    protected final List<AbstractEnemy> enemyAircrafts;
    protected final List<BaseBullet> heroBullets;
    protected final List<BaseBullet> enemyBullets;
    protected final List<AbstractProp> props;

    /** 屏幕中出现的敌机最大数量 */
    protected int enemyMaxNumber = 5;

    /** 敌机生成周期 */
    protected double enemySpawnCycle = 20;
    private int enemySpawnCounter = 0;

    /** 英雄机与敌机射击周期，实验六中不同难度可分别调整 */
    protected double heroShootCycle = 20;
    protected double enemyShootCycle = 20;
    private int heroShootCounter = 0;
    private int enemyShootCounter = 0;

    /** 敌机概率分界线 */
    protected double mobProbability = 0.4;
    protected double eliteProbability = 0.3;
    protected double elitePlusProbability = 0.2;
    protected double eliteProProbability = 0.1;

    /** 随难度递进累加到新生成敌机上的速度和血量 */
    protected int enemySpeedEnhance = 0;
    protected int enemyHpEnhance = 0;

    protected int score = 0;
    private boolean gameOverFlag = false;

    /** 暂停状态控制 */
    private volatile boolean paused = false;
    private JButton pauseButton;

    /** Boss 生成控制 */
    protected int bossScoreThreshold = 100;
    protected int nextBossScore = bossScoreThreshold;
    protected int bossBaseHp = 200;

    /** 难度递进计时 */
    protected int difficultyStepCounter = 0;
    protected int difficultyStepCycle = 500;
    protected int difficultyLevel = 0;

    public Game() {
        initDifficultyParameters();

        HeroAircraft.init(
                Main.WINDOW_WIDTH / 2,
                Main.WINDOW_HEIGHT - ImageManager.HERO_IMAGE.getHeight(),
                0, 0, 100
        );
        heroAircraft = HeroAircraft.getInstance();

        enemyAircrafts = new LinkedList<>();
        heroBullets = new LinkedList<>();
        enemyBullets = new LinkedList<>();
        props = new LinkedList<>();

        new HeroController(this, heroAircraft);
        this.timer = new java.util.Timer("game-action-timer", true);

        initPauseControls();
    }

    /**
     * 模板方法：定义游戏每一帧的固定算法骨架。
     * 子类通过 initDifficultyParameters、increaseDifficultyAction、canGenerateBoss、
     * getCurrentBossHp 等钩子/抽象步骤实现差异化难度。
     */
    public final void action() {
        MusicManager.playBgm();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (paused) {
                    repaint();
                    return;
                }

                increaseDifficultyAction();
                enemyGenerateAction();
                shootAction();
                bulletsMoveAction();
                aircraftsMoveAction();
                crashCheckAction();
                postProcessAction();
                repaint();
                checkResultAction();
            }
        };
        timer.schedule(task, 0, timeInterval);
    }

    /** 初始化暂停按钮和快捷键。 */
    private void initPauseControls() {
        setLayout(null);
        setFocusable(true);

        pauseButton = new JButton("暂停");
        pauseButton.setBounds(Main.WINDOW_WIDTH - 95, 12, 80, 32);
        pauseButton.setFocusPainted(false);
        pauseButton.addActionListener(e -> togglePause());
        add(pauseButton);

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("P"), "togglePause");
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "togglePause");
        getActionMap().put("togglePause", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                togglePause();
            }
        });
    }

    /** 暂停/继续游戏：暂停期间主循环不再推进，鼠标拖动也不会移动英雄机。 */
    public void togglePause() {
        if (gameOverFlag) {
            return;
        }
        paused = !paused;
        pauseButton.setText(paused ? "继续" : "暂停");
        if (paused) {
            MusicManager.stopAll();
            System.out.println("Game paused. Press P/Space or click 继续 to resume.");
        } else {
            if (hasBossEnemy()) {
                MusicManager.playBossBgm();
            } else {
                MusicManager.playBgm();
            }
            System.out.println("Game resumed.");
        }
        repaint();
        requestFocusInWindow();
    }

    public boolean isPaused() {
        return paused;
    }

    /** 初始化该难度的参数，具体难度子类实现。 */
    protected abstract void initDifficultyParameters();

    /** 难度递进钩子：简单模式默认不递进，普通/困难重写。 */
    protected void increaseDifficultyAction() {
        // default: no difficulty increase
    }

    /** Boss 生成钩子：简单模式返回 false。 */
    protected boolean canGenerateBoss() {
        return true;
    }

    /** Boss 血量钩子：普通固定，困难递增。 */
    protected int getCurrentBossHp() {
        return bossBaseHp;
    }

    /** Boss 生成后钩子：困难模式可提升下一次 Boss 血量。 */
    protected void afterBossGenerated() {
        // default: no extra operation
    }

    private void shootAction() {
        heroShootCounter++;
        enemyShootCounter++;

        if (heroShootCounter >= heroShootCycle) {
            heroShootCounter = 0;
            heroBullets.addAll(heroAircraft.shoot());
        }

        if (enemyShootCounter >= enemyShootCycle) {
            enemyShootCounter = 0;
            for (AbstractEnemy enemyAircraft : enemyAircrafts) {
                enemyBullets.addAll(enemyAircraft.shoot());
            }
        }
    }

    private void bulletsMoveAction() {
        for (BaseBullet bullet : heroBullets) {
            bullet.forward();
        }
        for (BaseBullet bullet : enemyBullets) {
            bullet.forward();
        }
    }

    private void aircraftsMoveAction() {
        for (AbstractEnemy enemyAircraft : enemyAircrafts) {
            enemyAircraft.forward();
        }
        for (AbstractProp prop : props) {
            prop.forward();
        }
    }

    private void crashCheckAction() {
        // 敌机子弹攻击英雄机
        for (BaseBullet bullet : enemyBullets) {
            if (bullet.notValid()) {
                continue;
            }
            if (heroAircraft.crash(bullet) || bullet.crash(heroAircraft)) {
                heroAircraft.decreaseHp(bullet.getPower());
                bullet.vanish();
            }
        }

        // 英雄子弹攻击敌机
        for (BaseBullet bullet : heroBullets) {
            if (bullet.notValid()) {
                continue;
            }
            for (AbstractEnemy enemyAircraft : enemyAircrafts) {
                if (enemyAircraft.notValid()) {
                    continue;
                }
                if (enemyAircraft.crash(bullet)) {
                    enemyAircraft.decreaseHp(bullet.getPower());
                    bullet.vanish();
                    MusicManager.playBulletHit();
                    if (enemyAircraft.notValid()) {
                        score += enemyAircraft.getScoreValue();
                        props.addAll(enemyAircraft.dropProps());
                    }
                }
                if (!enemyAircraft.notValid()
                        && (enemyAircraft.crash(heroAircraft) || heroAircraft.crash(enemyAircraft))) {
                    enemyAircraft.vanish();
                    heroAircraft.decreaseHp(Integer.MAX_VALUE);
                }
            }
        }

        // 英雄机获得道具，道具生效
        for (AbstractProp prop : props) {
            if (prop.notValid()) {
                continue;
            }
            if (heroAircraft.crash(prop) || prop.crash(heroAircraft)) {
                MusicManager.playGetSupply();

                if (prop instanceof BombProp || prop instanceof FreezeProp) {
                    registerPropObservers(prop);
                }

                if (prop instanceof BombProp) {
                    MusicManager.playBombExplosion();
                    activateBombAndAddScore((BombProp) prop);
                } else {
                    prop.activate(heroAircraft);
                }
                prop.vanish();
            }
        }
    }

    private void registerPropObservers(AbstractProp prop) {
        for (AbstractEnemy enemy : enemyAircrafts) {
            if (!enemy.notValid()) {
                prop.addObserver(enemy);
            }
        }
        for (BaseBullet bullet : enemyBullets) {
            if (!bullet.notValid()) {
                prop.addObserver(bullet);
            }
        }
    }

    private void activateBombAndAddScore(BombProp bombProp) {
        Map<AbstractEnemy, Boolean> aliveBefore = new HashMap<>();
        for (AbstractEnemy enemy : enemyAircrafts) {
            aliveBefore.put(enemy, !enemy.notValid());
        }

        bombProp.activate(heroAircraft);

        for (AbstractEnemy enemy : enemyAircrafts) {
            if (aliveBefore.getOrDefault(enemy, false) && enemy.notValid()) {
                score += enemy.getScoreValue();
            }
        }
    }

    private void postProcessAction() {
        boolean hadBoss = hasBossEnemy();

        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
        props.removeIf(AbstractFlyingObject::notValid);

        if (hadBoss && !hasBossEnemy()) {
            MusicManager.stopBossBgm();
            MusicManager.playBgm();
        }
    }

    private void checkResultAction() {
        if (heroAircraft.getHp() <= 0 && !gameOverFlag) {
            timer.cancel();
            gameOverFlag = true;

            System.out.println("Game Over!");
            MusicManager.stopAll();
            MusicManager.playGameOver();

            showGameOverUiSmoothly();
        }
    }

    /**
     * 游戏结束后的保存成绩与排行榜显示统一交给 Swing 事件线程处理。
     * 使用短延时的 Swing Timer，而不是 invokeAndWait，避免游戏计时线程和 EDT 相互等待造成卡顿。
     */
    private void showGameOverUiSmoothly() {
        SwingUtilities.invokeLater(() -> {
            javax.swing.Timer dialogTimer = new javax.swing.Timer(120, e -> {
                ((javax.swing.Timer) e.getSource()).stop();
                saveScoreRecord();
                closeGameWindow();
                showScoreBoard();
            });
            dialogTimer.setRepeats(false);
            dialogTimer.start();
        });
    }

    /** 关闭当前游戏窗口，避免重新开始后残留旧窗口。 */
    private void closeGameWindow() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose();
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paintComponent(g);

        BufferedImage bg = getBackgroundImage();
        g.drawImage(bg, 0, this.backGroundTop - Main.WINDOW_HEIGHT, null);
        g.drawImage(bg, 0, this.backGroundTop, null);
        this.backGroundTop += 1;
        if (this.backGroundTop == Main.WINDOW_HEIGHT) {
            this.backGroundTop = 0;
        }

        paintImageWithPositionRevised(g, enemyBullets);
        paintImageWithPositionRevised(g, heroBullets);
        paintImageWithPositionRevised(g, enemyAircrafts);
        paintImageWithPositionRevised(g, props);

        g.drawImage(ImageManager.HERO_IMAGE,
                heroAircraft.getLocationX() - ImageManager.HERO_IMAGE.getWidth() / 2,
                heroAircraft.getLocationY() - ImageManager.HERO_IMAGE.getHeight() / 2, null);

        paintScoreAndLife(g);
        paintPauseHint(g);

        // 最后绘制 Swing 控件，避免暂停按钮被游戏画面覆盖。
        super.paintChildren(g);
    }

    private void paintPauseHint(Graphics g) {
        if (!paused) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 36));
        String title = "游戏暂停";
        FontMetrics titleMetrics = g2.getFontMetrics();
        g2.drawString(title, (Main.WINDOW_WIDTH - titleMetrics.stringWidth(title)) / 2, Main.WINDOW_HEIGHT / 2 - 20);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 18));
        String hint = "按 P / 空格键，或点击右上角按钮继续";
        FontMetrics hintMetrics = g2.getFontMetrics();
        g2.drawString(hint, (Main.WINDOW_WIDTH - hintMetrics.stringWidth(hint)) / 2, Main.WINDOW_HEIGHT / 2 + 20);
        g2.dispose();
    }

    private void paintImageWithPositionRevised(Graphics g, List<? extends AbstractFlyingObject> objects) {
        if (objects.isEmpty()) {
            return;
        }
        for (AbstractFlyingObject object : objects) {
            BufferedImage image = object.getImage();
            assert image != null : objects.getClass().getName() + " has no image! ";
            g.drawImage(image, object.getLocationX() - image.getWidth() / 2,
                    object.getLocationY() - image.getHeight() / 2, null);
        }
    }

    private void paintScoreAndLife(Graphics g) {
        int x = 10;
        int y = 25;
        g.setColor(Color.RED);
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        g.drawString("SCORE: " + this.score, x, y);
        y = y + 20;
        g.drawString("LIFE: " + this.heroAircraft.getHp(), x, y);
    }

    protected boolean hasBossEnemy() {
        for (AbstractEnemy enemy : enemyAircrafts) {
            if (enemy instanceof boss_enemy && !enemy.notValid()) {
                return true;
            }
        }
        return false;
    }

    private void enemyGenerateAction() {
        enemySpawnCounter++;
        if (enemySpawnCounter < enemySpawnCycle) {
            return;
        }
        enemySpawnCounter = 0;

        if (canGenerateBoss() && score >= nextBossScore && !hasBossEnemy()) {
            enemyAircrafts.add(createBossEnemy());
            MusicManager.stopBgm();
            MusicManager.playBossBgm();
            nextBossScore += bossScoreThreshold;
            afterBossGenerated();
            return;
        }

        if (enemyAircrafts.size() < enemyMaxNumber) {
            int locationX = (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth()));
            int locationY = (int) (Math.random() * Main.WINDOW_HEIGHT * 0.05);
            enemyAircrafts.add(createRandomEnemy(locationX, locationY));
        }
    }

    private AbstractEnemy createBossEnemy() {
        AbstractEnemy boss = new boss_enemy(
                Main.WINDOW_WIDTH / 2,
                Main.WINDOW_HEIGHT / 10,
                1 + enemySpeedEnhance,
                0,
                getCurrentBossHp()
        );
        System.out.println("Boss generated! hp=" + getCurrentBossHp());
        return boss;
    }

    private AbstractEnemy createRandomEnemy(int locationX, int locationY) {
        double rand = Math.random();
        EnemyFactory factory;
        double eliteLine = mobProbability + eliteProbability;
        double elitePlusLine = eliteLine + elitePlusProbability;

        if (rand < mobProbability) {
            factory = new MobEnemyFactory();
        } else if (rand < eliteLine) {
            factory = new EliteEnemyFactory();
        } else if (rand < elitePlusLine) {
            factory = new ElitePlusEnemyFactory();
        } else {
            factory = new EliteProEnemyFactory();
        }

        AbstractEnemy enemy = factory.createEnemy(locationX, locationY);
        enemy.setSpeedY(enemy.getSpeedY() + enemySpeedEnhance);
        enemy.setMaxHpAndHp(enemy.getMaxHp() + enemyHpEnhance);
        return enemy;
    }

    protected String getDifficultyName() {
        switch (GameConfig.difficulty) {
            case EASY:
                return "easy";
            case NORMAL:
                return "normal";
            case HARD:
                return "hard";
            default:
                return "normal";
        }
    }

    private String getRecordFileName() {
        return getDifficultyName() + "_records.txt";
    }

    private void saveScoreRecord() {
        String playerName = JOptionPane.showInputDialog(this, "游戏结束！请输入玩家姓名：");
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "匿名玩家";
        }
        String time = new SimpleDateFormat("MM-dd HH:mm").format(new Date());
        ScoreRecord record = new ScoreRecord(playerName, score, time);
        ScoreRecordDao dao = new ScoreRecordDaoImpl(getRecordFileName());
        dao.addRecord(record);
    }

    private void showScoreBoard() {
        Main.showScoreBoard(getDifficultyName(), true);
    }

    private BufferedImage getBackgroundImage() {
        switch (GameConfig.difficulty) {
            case EASY:
                return ImageManager.EASY_BACKGROUND_IMAGE;
            case NORMAL:
                return ImageManager.NORMAL_BACKGROUND_IMAGE;
            case HARD:
                return ImageManager.HARD_BACKGROUND_IMAGE;
            default:
                return ImageManager.NORMAL_BACKGROUND_IMAGE;
        }
    }
}
