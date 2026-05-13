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
import java.awt.event.KeyEvent;
import java.awt.event.HierarchyEvent;
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

    /**
     * Shift 是纯修饰键，部分 Swing 输入映射不会稳定触发它；
     * 因此额外使用 KeyEventDispatcher 捕获 Shift 按下事件。
     */
    private KeyEventDispatcher shortcutDispatcher;
    private boolean shortcutDispatcherRegistered = false;

    /** 主动大招：按 B 释放全屏炸弹，复用观察者模式中的炸弹通知逻辑 */
    private static final long ULTIMATE_COOLDOWN_MS = 15000L;
    private long lastUltimateTime = -ULTIMATE_COOLDOWN_MS;

    /** 无敌冲刺：按 Shift 触发，短时间免疫伤害，并带有独立冷却 */
    private static final long DASH_COOLDOWN_MS = 10000L;
    private static final long DASH_DURATION_MS = 2000L;
    private long lastDashTime = -DASH_COOLDOWN_MS;

    /** 连击系统：连续击毁敌机可以获得额外得分，超过指定时间未击杀则清零 */
    private static final long COMBO_TIMEOUT_MS = 3000L;
    private int comboCount = 0;
    private int maxCombo = 0;
    private long lastKillTime = 0L;

    private String screenMessage = "";
    private int screenMessageCounter = 0;

    /** Boss 登场警告动画：先显示警告，再生成 Boss */
    private static final int BOSS_WARNING_DURATION = 75;
    private boolean bossWarningActive = false;
    private int bossWarningCounter = 0;

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

        // 使用 CopyOnWriteArrayList，避免游戏计时线程修改列表时，Swing 绘制线程遍历列表产生偶发卡顿或并发异常。
        enemyAircrafts = new java.util.concurrent.CopyOnWriteArrayList<>();
        heroBullets = new java.util.concurrent.CopyOnWriteArrayList<>();
        enemyBullets = new java.util.concurrent.CopyOnWriteArrayList<>();
        props = new java.util.concurrent.CopyOnWriteArrayList<>();

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
                updateAnimationCounters();
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
        pauseButton.setBounds(Main.WINDOW_WIDTH - 102, 12, 88, 34);
        styleOverlayButton(pauseButton);
        pauseButton.addActionListener(e -> togglePause());
        add(pauseButton);

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("P"), "togglePause");
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "togglePause");
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("B"), "ultimateSkill");
        // 兜底绑定：某些环境可以触发，但纯 Shift 在 Swing 中并不总是稳定。
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("shift pressed SHIFT"), "dashSkill");
        getActionMap().put("togglePause", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                togglePause();
            }
        });
        getActionMap().put("ultimateSkill", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                useUltimateSkill();
            }
        });
        getActionMap().put("dashSkill", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                useDashSkill();
            }
        });

        registerShortcutDispatcher();
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0 && !isDisplayable()) {
                removeShortcutDispatcher();
            }
        });
    }

    private void styleOverlayButton(JButton button) {
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(35, 55, 85));
        button.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void registerShortcutDispatcher() {
        if (shortcutDispatcherRegistered) {
            return;
        }
        shortcutDispatcher = e -> {
            if (!isShowing() || gameOverFlag) {
                return false;
            }
            if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_SHIFT) {
                useDashSkill();
            }
            return false;
        };
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(shortcutDispatcher);
        shortcutDispatcherRegistered = true;
    }

    private void removeShortcutDispatcher() {
        if (shortcutDispatcherRegistered && shortcutDispatcher != null) {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(shortcutDispatcher);
            shortcutDispatcherRegistered = false;
            shortcutDispatcher = null;
        }
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
            if (hasBossEnemy() || bossWarningActive) {
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

    /**
     * 主动释放大招：按 B 键触发一次全屏炸弹。
     * 该功能不新增耦合逻辑，而是复用 BombProp 的观察者通知机制。
     */
    private void useUltimateSkill() {
        if (gameOverFlag || paused) {
            return;
        }
        long now = System.currentTimeMillis();
        long remain = ULTIMATE_COOLDOWN_MS - (now - lastUltimateTime);
        if (remain > 0) {
            showScreenMessage("大招冷却中：" + ((remain + 999) / 1000) + "s");
            return;
        }

        lastUltimateTime = now;
        BombProp skillBomb = new BombProp(heroAircraft.getLocationX(), heroAircraft.getLocationY(), 0, 0);
        registerPropObservers(skillBomb);
        MusicManager.playBombExplosion();
        activateBombAndAddScore(skillBomb);
        showScreenMessage("主动大招：全屏炸弹！");
        System.out.println("Ultimate skill active: Bomb!");
    }


    /**
     * 主动释放无敌冲刺：按 Shift 触发。
     * 持续时间内英雄机受到子弹或敌机碰撞时不会扣血，适合在弹幕密集或 Boss 阶段突围。
     */
    private void useDashSkill() {
        if (gameOverFlag || paused) {
            return;
        }
        long now = System.currentTimeMillis();
        long remain = DASH_COOLDOWN_MS - (now - lastDashTime);
        if (remain > 0) {
            showScreenMessage("无敌冲刺冷却中：" + ((remain + 999) / 1000) + "s");
            return;
        }

        lastDashTime = now;
        heroAircraft.activateInvincible(DASH_DURATION_MS);

        // 给“冲刺”一个直观反馈：向上瞬移一小段，但不超出屏幕。
        int targetY = Math.max(ImageManager.HERO_IMAGE.getHeight() / 2 + 10, heroAircraft.getLocationY() - 90);
        heroAircraft.setLocation(heroAircraft.getLocationX(), targetY);

        showScreenMessage("无敌冲刺！2 秒内免疫伤害");
        System.out.println("Dash skill active: invincible for 2 seconds.");
    }

    private void showScreenMessage(String message) {
        this.screenMessage = message;
        this.screenMessageCounter = 45;
    }

    private void updateAnimationCounters() {
        if (screenMessageCounter > 0) {
            screenMessageCounter--;
        }
        if (bossWarningActive && bossWarningCounter > 0) {
            bossWarningCounter--;
        }
        updateComboStatus();
    }

    private int getUltimateCooldownSeconds() {
        long remain = ULTIMATE_COOLDOWN_MS - (System.currentTimeMillis() - lastUltimateTime);
        if (remain <= 0) {
            return 0;
        }
        return (int) ((remain + 999) / 1000);
    }

    private int getDashCooldownSeconds() {
        if (heroAircraft.isInvincible()) {
            return 0;
        }
        long remain = DASH_COOLDOWN_MS - (System.currentTimeMillis() - lastDashTime);
        if (remain <= 0) {
            return 0;
        }
        return (int) ((remain + 999) / 1000);
    }

    private long getUltimateRemainMs() {
        return Math.max(0L, ULTIMATE_COOLDOWN_MS - (System.currentTimeMillis() - lastUltimateTime));
    }

    private long getDashRemainMs() {
        if (heroAircraft.isInvincible()) {
            return heroAircraft.getInvincibleRemainMs();
        }
        return Math.max(0L, DASH_COOLDOWN_MS - (System.currentTimeMillis() - lastDashTime));
    }

    private double getUltimateReadyRatio() {
        return 1.0 - Math.min(1.0, getUltimateRemainMs() * 1.0 / ULTIMATE_COOLDOWN_MS);
    }

    private double getDashReadyRatio() {
        if (heroAircraft.isInvincible()) {
            return 1.0;
        }
        return 1.0 - Math.min(1.0, getDashRemainMs() * 1.0 / DASH_COOLDOWN_MS);
    }

    private void updateComboStatus() {
        if (comboCount > 0 && System.currentTimeMillis() - lastKillTime > COMBO_TIMEOUT_MS) {
            comboCount = 0;
        }
    }

    private void addScoreWithCombo(int baseScore) {
        comboCount++;
        lastKillTime = System.currentTimeMillis();
        maxCombo = Math.max(maxCombo, comboCount);

        double multiplier = 1.0 + Math.min(comboCount - 1, 20) * 0.05;
        int gainedScore = (int) Math.round(baseScore * multiplier);
        score += gainedScore;

        if (comboCount >= 2) {
            showScreenMessage("COMBO x" + comboCount + "  +" + gainedScore);
        }
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
                if (!heroAircraft.isInvincible()) {
                    heroAircraft.decreaseHp(bullet.getPower());
                } else {
                    showScreenMessage("无敌冲刺：免疫伤害");
                }
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
                        addScoreWithCombo(enemyAircraft.getScoreValue());
                        props.addAll(enemyAircraft.dropProps());
                    }
                }
            }
        }

        // 英雄机与敌机碰撞。
        // 原实现把这段逻辑写在“英雄子弹攻击敌机”的循环内部，
        // 当屏幕上暂时没有英雄子弹时，Shift 无敌冲刺撞机不会触发。
        for (AbstractEnemy enemyAircraft : enemyAircrafts) {
            if (enemyAircraft.notValid()) {
                continue;
            }
            if (enemyAircraft.crash(heroAircraft) || heroAircraft.crash(enemyAircraft)) {
                if (heroAircraft.isInvincible()) {
                    if (enemyAircraft instanceof boss_enemy) {
                        enemyAircraft.decreaseHp(50);
                        showScreenMessage("无敌冲刺：Boss 受到撞击伤害！");
                    } else {
                        enemyAircraft.vanish();
                        addScoreWithCombo(enemyAircraft.getScoreValue());
                        showScreenMessage("无敌冲刺：撞毁敌机！");
                    }
                } else {
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
                    showScreenMessage("炸弹道具：清屏爆炸！");
                } else {
                    prop.activate(heroAircraft);
                    showPropEffectMessage(prop);
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
                addScoreWithCombo(enemy.getScoreValue());
            }
        }
    }

    private void showPropEffectMessage(AbstractProp prop) {
        if (prop instanceof BloodProp) {
            showScreenMessage("加血道具：生命值 +30");
        } else if (prop instanceof BulletPlusProp) {
            showScreenMessage("超级火力：环形弹幕 8 秒！");
        } else if (prop instanceof BulletProp) {
            showScreenMessage("火力道具：散射弹幕 5 秒！");
        } else if (prop instanceof FreezeProp) {
            showScreenMessage("冰冻道具：冻结敌机与子弹！");
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
        removeShortcutDispatcher();
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
        paintDashEffect(g);

        paintStatusPanel(g);
        paintSkillPanel(g);
        paintBossHpBar(g);
        paintScreenMessage(g);
        paintBossWarning(g);
        paintPauseHint(g);

        // 最后绘制 Swing 控件，避免暂停按钮被游戏画面覆盖。
        super.paintChildren(g);
    }

    private void paintDashEffect(Graphics g) {
        if (!heroAircraft.isInvincible()) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        int radius = Math.max(ImageManager.HERO_IMAGE.getWidth(), ImageManager.HERO_IMAGE.getHeight()) / 2 + 14;
        boolean flash = (System.currentTimeMillis() / 120) % 2 == 0;
        g2.setColor(flash ? new Color(255, 220, 60, 180) : new Color(255, 255, 255, 130));
        g2.setStroke(new BasicStroke(4));
        g2.drawOval(heroAircraft.getLocationX() - radius, heroAircraft.getLocationY() - radius, radius * 2, radius * 2);
        g2.setFont(new Font("SansSerif", Font.BOLD, 14));
        String text = "INVINCIBLE";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, heroAircraft.getLocationX() - fm.stringWidth(text) / 2, heroAircraft.getLocationY() - radius - 6);
        g2.dispose();
    }

    private void paintScreenMessage(Graphics g) {
        if (screenMessageCounter <= 0 || screenMessage == null || screenMessage.isEmpty()) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(new Color(0, 0, 0, 130));
        int msgY = hasBossEnemy() ? 150 : 108;
        g2.fillRoundRect(70, msgY, Main.WINDOW_WIDTH - 140, 42, 18, 18);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Microsoft YaHei", Font.BOLD, 20));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(screenMessage, (Main.WINDOW_WIDTH - fm.stringWidth(screenMessage)) / 2, msgY + 28);
        g2.dispose();
    }

    private void paintBossWarning(Graphics g) {
        if (!bossWarningActive || bossWarningCounter <= 0) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        boolean flash = (bossWarningCounter / 8) % 2 == 0;
        g2.setColor(new Color(0, 0, 0, flash ? 170 : 110));
        g2.fillRect(0, 0, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);

        g2.setColor(flash ? Color.RED : Color.ORANGE);
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(42, Main.WINDOW_HEIGHT / 2 - 90, Main.WINDOW_WIDTH - 84, 150, 30, 30);

        g2.setFont(new Font("SansSerif", Font.BOLD, 44));
        String warning = "WARNING!";
        FontMetrics fm1 = g2.getFontMetrics();
        g2.drawString(warning, (Main.WINDOW_WIDTH - fm1.stringWidth(warning)) / 2, Main.WINDOW_HEIGHT / 2 - 25);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 24));
        String bossText = "Boss 敌机即将登场";
        FontMetrics fm2 = g2.getFontMetrics();
        g2.drawString(bossText, (Main.WINDOW_WIDTH - fm2.stringWidth(bossText)) / 2, Main.WINDOW_HEIGHT / 2 + 20);

        g2.setFont(new Font("Microsoft YaHei", Font.PLAIN, 18));
        String countDown = "倒计时 " + Math.max(1, (bossWarningCounter + 24) / 25);
        FontMetrics fm3 = g2.getFontMetrics();
        g2.drawString(countDown, (Main.WINDOW_WIDTH - fm3.stringWidth(countDown)) / 2, Main.WINDOW_HEIGHT / 2 + 55);
        g2.dispose();
    }

    private void paintPauseHint(Graphics g) {
        if (!paused) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Microsoft YaHei", Font.BOLD, 36));
        String title = "游戏暂停";
        FontMetrics titleMetrics = g2.getFontMetrics();
        g2.drawString(title, (Main.WINDOW_WIDTH - titleMetrics.stringWidth(title)) / 2, Main.WINDOW_HEIGHT / 2 - 20);

        g2.setFont(new Font("Microsoft YaHei", Font.PLAIN, 18));
        String hint = "按 P / 空格键，或点击右上角按钮继续";
        FontMetrics hintMetrics = g2.getFontMetrics();
        g2.drawString(hint, (Main.WINDOW_WIDTH - hintMetrics.stringWidth(hint)) / 2, Main.WINDOW_HEIGHT / 2 + 20);
        g2.dispose();
    }

    private void paintImageWithPositionRevised(Graphics g, List<? extends AbstractFlyingObject> objects) {
        if (objects.isEmpty()) {
            return;
        }
        for (AbstractFlyingObject object : new ArrayList<>(objects)) {
            if (object.notValid()) {
                continue;
            }
            BufferedImage image = object.getImage();
            assert image != null : objects.getClass().getName() + " has no image! ";
            g.drawImage(image, object.getLocationX() - image.getWidth() / 2,
                    object.getLocationY() - image.getHeight() / 2, null);
        }
    }

    private void paintStatusPanel(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int panelX = 12;
        int panelY = 12;
        int panelW = 218;
        int panelH = 86;
        g2.setColor(new Color(8, 18, 34, 150));
        g2.fillRoundRect(panelX, panelY, panelW, panelH, 20, 20);
        g2.setColor(new Color(255, 255, 255, 70));
        g2.drawRoundRect(panelX, panelY, panelW, panelH, 20, 20);

        g2.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        g2.setColor(new Color(180, 210, 255));
        g2.drawString("SCORE", panelX + 16, panelY + 25);
        g2.setFont(new Font("Microsoft YaHei", Font.BOLD, 24));
        g2.setColor(Color.WHITE);
        g2.drawString(String.valueOf(score), panelX + 88, panelY + 30);

        g2.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        g2.setColor(new Color(255, 160, 160));
        g2.drawString("LIFE", panelX + 16, panelY + 61);
        drawMiniHpBar(g2, panelX + 70, panelY + 48, 120, 14, heroAircraft.getHp(), heroAircraft.getMaxHp());

        if (heroAircraft.getHp() < heroAircraft.getMaxHp() * 0.3) {
            g2.setColor(new Color(255, 80, 80));
            g2.setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
            g2.drawString("LOW", panelX + 194, panelY + 61);
        }
        g2.dispose();
    }

    private void paintSkillPanel(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int panelX = 12;
        int panelY = Main.WINDOW_HEIGHT - 132;
        int panelW = 268;
        int panelH = 96;
        g2.setColor(new Color(8, 18, 34, 150));
        g2.fillRoundRect(panelX, panelY, panelW, panelH, 20, 20);
        g2.setColor(new Color(255, 255, 255, 65));
        g2.drawRoundRect(panelX, panelY, panelW, panelH, 20, 20);

        g2.setColor(new Color(225, 235, 255));
        g2.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
        g2.drawString("技能栏", panelX + 16, panelY + 24);

        drawSkillCooldownBar(
                g2, panelX + 16, panelY + 34, 232, 18,
                "B", "全屏炸弹", getUltimateReadyRatio(), getUltimateRemainMs(),
                new Color(255, 185, 80), getUltimateRemainMs() == 0
        );

        String dashName = heroAircraft.isInvincible() ? "无敌中" : "无敌冲刺";
        drawSkillCooldownBar(
                g2, panelX + 16, panelY + 62, 232, 18,
                "Shift", dashName, getDashReadyRatio(), getDashRemainMs(),
                new Color(90, 210, 255), getDashRemainMs() == 0 || heroAircraft.isInvincible()
        );

        g2.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        g2.setColor(comboCount > 1 ? new Color(255, 215, 90) : new Color(220, 225, 235));
        g2.drawString("COMBO x" + comboCount + "   MAX x" + maxCombo, panelX + 16, panelY + 90);
        g2.dispose();
    }

    private void drawSkillCooldownBar(Graphics2D g2, int x, int y, int w, int h,
                                      String key, String name, double ratio, long remainMs,
                                      Color fillColor, boolean ready) {
        g2.setColor(new Color(255, 255, 255, 38));
        g2.fillRoundRect(x, y, w, h, 12, 12);
        int fillW = Math.max(0, Math.min(w, (int) Math.round(w * ratio)));
        g2.setColor(fillColor);
        g2.fillRoundRect(x, y, fillW, h, 12, 12);
        g2.setColor(new Color(255, 255, 255, 80));
        g2.drawRoundRect(x, y, w, h, 12, 12);

        g2.setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
        g2.setColor(Color.WHITE);
        String state = ready ? "READY" : ((remainMs + 999) / 1000) + "s";
        g2.drawString("[" + key + "] " + name + "  " + state, x + 8, y + 14);
    }

    private void paintBossHpBar(Graphics g) {
        boss_enemy boss = getBossEnemy();
        if (boss == null) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int barW = Main.WINDOW_WIDTH - 130;
        int barH = 18;
        int x = (Main.WINDOW_WIDTH - barW) / 2;
        int y = 116;

        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRoundRect(x - 12, y - 24, barW + 24, 54, 20, 20);
        g2.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        g2.setColor(new Color(255, 210, 210));
        String title = "BOSS HP";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(title, (Main.WINDOW_WIDTH - fm.stringWidth(title)) / 2, y - 6);

        drawMiniHpBar(g2, x, y, barW, barH, boss.getHp(), boss.getMaxHp());

        g2.setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
        g2.setColor(Color.WHITE);
        String hpText = boss.getHp() + " / " + boss.getMaxHp();
        FontMetrics hpFm = g2.getFontMetrics();
        g2.drawString(hpText, (Main.WINDOW_WIDTH - hpFm.stringWidth(hpText)) / 2, y + 14);
        g2.dispose();
    }

    private void drawMiniHpBar(Graphics2D g2, int x, int y, int w, int h, int hp, int maxHp) {
        int safeMaxHp = Math.max(1, maxHp);
        double ratio = Math.max(0.0, Math.min(1.0, hp * 1.0 / safeMaxHp));
        g2.setColor(new Color(255, 255, 255, 45));
        g2.fillRoundRect(x, y, w, h, h, h);
        Color hpColor;
        if (ratio > 0.6) {
            hpColor = new Color(80, 225, 135);
        } else if (ratio > 0.3) {
            hpColor = new Color(255, 205, 80);
        } else {
            hpColor = new Color(255, 90, 90);
        }
        g2.setColor(hpColor);
        g2.fillRoundRect(x, y, (int) Math.round(w * ratio), h, h, h);
        g2.setColor(new Color(255, 255, 255, 80));
        g2.drawRoundRect(x, y, w, h, h, h);
    }

    private boss_enemy getBossEnemy() {
        for (AbstractEnemy enemy : enemyAircrafts) {
            if (enemy instanceof boss_enemy && !enemy.notValid()) {
                return (boss_enemy) enemy;
            }
        }
        return null;
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
            if (!bossWarningActive) {
                startBossWarning();
                return;
            }
            if (bossWarningCounter > 0) {
                return;
            }

            bossWarningActive = false;
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

    private void startBossWarning() {
        bossWarningActive = true;
        bossWarningCounter = BOSS_WARNING_DURATION;
        MusicManager.stopBgm();
        MusicManager.playBossBgm();
        showScreenMessage("Boss 敌机来袭！");
        System.out.println("Warning: Boss approaching!");
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
        String playerName = JOptionPane.showInputDialog(
                this,
                "游戏结束！\n你的得分：" + score + "\n最高连击：x" + maxCombo + "\n请输入玩家姓名：",
                "保存游戏成绩",
                JOptionPane.INFORMATION_MESSAGE
        );
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
