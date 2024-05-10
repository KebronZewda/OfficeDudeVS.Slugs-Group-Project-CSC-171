import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import javax.swing.Timer;
import java.util.ArrayList;
import java.util.Random;
import java.awt.Point;
import java.util.Collections;
import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;

public class Main {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Game");
        Canvas canvas = new Canvas();
        frame.add(canvas);
        frame.pack(); 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
        //frame for the game
    }
}

class Canvas extends JPanel {

    private boolean wPressed;
    private boolean aPressed;
    private boolean sPressed;
    private boolean dPressed;

    private Timer timer;
    private Timer wheelTimer;
    private Timer waveTimer;

    private Player p;

    public static float xCam;
    public static float yCam;

    private final int arenaWidth = 1200;
    private final int arenaHeight = 800;
    private final int wallThickness = 100;

    private final int screenWidth = 640;
    private final int screenHeight = 480;

    private File file;

    public static int numberTimesPlayed;
    public static ArrayList<Integer> numberTimesPlayedList = new ArrayList<>();

    public static ArrayList<Collider> obj = new ArrayList<>();
    public static ArrayList<Bullet> bullets = new ArrayList<>();
    public static ArrayList<Enemy> enemies = new ArrayList<>();
    public static ArrayList<Block> wall = new ArrayList<>();
    public static ArrayList<Score> highScoresAList = new ArrayList<>();

    private long startHolding;
    private double timeElapsed = 1;
    private boolean laser = false;

    private int mouseX;
    private int mouseY;

    public static int score;
    static JLabel scoreLabel;
    public int waveCount = 0;
    static JLabel waveLabel;

    public String highScoreListString;

    static JLabel highScoreLabel;

    public boolean death = false;
    public boolean mouseFlag = true;
    public boolean keyFlag = true;

    private long waveTimeStamp;
    private boolean wavewait;

    public Canvas() {
        addMouseListener(new MouseController());
        addMouseMotionListener(new MouseController());
        addKeyListener(new KeyController());
        setBackground(new Color(234, 221, 202));
        setFocusable(true);
        setPanelSize();

        timer = new Timer(20, new Physics());
        timer.start();
        wheelTimer = new Timer(20, new Wheel()); //timer to show wheel for long pressed bullets
        waveTimer = new Timer(20, new Wave(3)); //timer to draw enemies in current wave
        waveTimer.start();

        p = new Player(0, 0);

        wall.add(new Block(-arenaWidth / 2, -arenaHeight / 2, wallThickness, arenaHeight));
        wall.add(new Block(arenaWidth / 2 - wallThickness, -arenaHeight / 2, wallThickness, arenaHeight));
        wall.add(new Block(-arenaWidth / 2, -arenaHeight / 2, arenaWidth, wallThickness));
        wall.add(new Block(-arenaWidth / 2, arenaHeight / 2, arenaWidth, wallThickness)); //walls
        
        //blocks within the wall
        wall.add(new Block(-402, 140, 234, 20));
        wall.add(new Block(-402, -122, 230, 20));  
        wall.add(new Block(-402, -240, 230, 20));
        wall.add(new Block(-402, -122, 20, 150));
        wall.add(new Block(-192, -220, 20, 100));
        wall.add(new Block(-188,140, 20, 180));

        wall.add(new Block(110, 220, 20, 100));
        wall.add(new Block(110, 220, 234, 20));

        wall.add(new Block(160, -400, 100, 100));

        wall.add(new Block(324, -90, 20, 326));
        wall.add(new Block(124, -220, 140, 20));

        wall.add(new Block(-80, -80, 60, 20));
        wall.add(new Block(30, -80, 60, 20));
        wall.add(new Block(-80, -60, 20, 35));
        wall.add(new Block(70, -60, 20, 35));
        wall.add(new Block(-80, 20, 20, 60));
        wall.add(new Block(70, 20, 20, 60));
        wall.add(new Block(-80, 60, 60, 20));
        wall.add(new Block(30, 60, 60, 20));
        

        scoreLabel = new JLabel();
        scoreLabel.setText("Current Score: " + score);
        scoreLabel.setFont(new Font("DIALOG", 0, 14));
        scoreLabel.setLocation(0, 0);
        scoreLabel.setBackground(Color.WHITE);
        scoreLabel.setOpaque(true);
        add(scoreLabel); //current score within each game.

        waveLabel = new JLabel();
        waveLabel.setText("Wave " + waveCount);
        waveLabel.setFont(new Font("DIALOG", 0, 14));
        waveLabel.setLocation(200, 0);
        waveLabel.setBackground(Color.WHITE);
        waveLabel.setOpaque(true);
        add(waveLabel); //current wavecount within each game

        highScoreLabel = new JLabel();
        highScoreLabel.setFont(new Font("DIALOG", 0, 14));
        highScoreLabel.setForeground(Color.WHITE);
        highScoreLabel.setVisible(false); //only shown in game over screen
        add(highScoreLabel); 

        numberTimesPlayed++;
        numberTimesPlayedList.add(numberTimesPlayed); //to count playthroughs for HSList
        wavewait = false;

        readFile();
    }

    public class Wave implements ActionListener {
        private int credits;

        public Wave(int credits) {
            this.credits = credits;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (enemies.isEmpty() && wavewait == false) {
                wavewait = true;
                waveTimeStamp = System.nanoTime();
            } else if (enemies.isEmpty() && wavewait) {
                double timeElapsed = (System.nanoTime() - waveTimeStamp) / 1e9;
                if (timeElapsed > 2) {
                    spawnWave(credits);
                    waveCount++;
                    waveLabel.setText("Wave " + waveCount);
                    credits++;
                    wavewait = false;
                }
            }
        }
    }

    public void spawnWave(int credits) {

        Random r = new Random();
        while (credits > 0) {
            int type = r.nextInt(10);

            // If you want a value between two integer values (min and max)
            // int randInt = r.nextInt(max-min) + min;

            int x = (r.nextInt(800) - 400);
            int y = (r.nextInt(600) - 300); //randomize where the enemy respawns

            if (spawnWallCheck(x, y)) {
                if (type > 7) {
                    enemies.add(new Enemy(x, y, 1));
                    credits = credits - 3;
                } else {
                    enemies.add(new Enemy(x, y, 0));
                    credits = credits - 1;
                }
            }
        }
    }

    public boolean spawnWallCheck(int x, int y) {
        Point point = new Point(x, y);

        int distanceX = Math.abs(x - (int) (p.getX() + p.getSize() / 2));
        int distanceY = Math.abs(y - (int) (p.getY() + p.getSize() / 2));

        for (Block w : wall) {
            if (distanceX <= 250 && distanceY <= 250) {
                if (w.getRect().contains(point)) {
                    return false;
                }
            }
        }
        return true;

    }

    private void setPanelSize() {
        Dimension size = new Dimension(640, 480);
        setPreferredSize(size);
    }

    public boolean getAPressed() {
        return this.aPressed;
    }

    public boolean getDPressed() {
        return this.dPressed;
    }

    private class Physics implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // start update positions; redraw
            if (wPressed) {
                p.setYAccel(-1);
            } else if (sPressed) {
                p.setYAccel(1);
            } else {
                p.setYAccel(0);
            }

            if (aPressed) {
                p.setXAccel(-1);
            } else if (dPressed) {
                p.setXAccel(1);
            } else {
                p.setXAccel(0);
            }

            p.updatePos();

            for (Bullet b : bullets.toArray(new Bullet[bullets.size()])) {
                b.updatePos();
            }

            for (Enemy enemy : enemies.toArray(new Enemy[enemies.size()])) {

                enemy.updatePos(p.getX(), p.getY());
            }

            xCam = (int) (p.getX() - (screenWidth / 2 - 10));
            yCam = (int) (p.getY() - (screenHeight / 2 - 20));

            if (xCam <= -arenaWidth / 2) {
                xCam = -arenaWidth / 2;
            }

            if (xCam >= arenaWidth / 2 - screenWidth) {
                xCam = arenaWidth / 2 - screenWidth;
            }

            if (yCam <= -arenaHeight / 2) {
                yCam = -arenaHeight / 2;
            }

            if (yCam >= arenaHeight / 2 - screenHeight / 2 - 140) {
                yCam = arenaHeight / 2 - screenHeight / 2 - 140;
            }

            if (p.getHealth() < 0 && death == false) {
                mouseFlag = false; 
                keyFlag = false; 
                death = true; //so paint compoenent draws game over stuff.
                p.setYVel(0);
                p.setXVel(0);
                wPressed = false;
                aPressed = false;
                sPressed = false;
                dPressed = false; //disable player from moving or shooting bullets
                updateHighScore(); 
                writeFile(); 
                
                for (Enemy i : enemies) {
                    i.setDamage(0);
                }
                
            }

            repaint(); 
        }
    }

    //wheel right next to player for large bullets
    private class Wheel implements ActionListener { 
        @Override
        public void actionPerformed(ActionEvent e) {
            timeElapsed = ((System.nanoTime() - startHolding) * 2 / 1e9) + 1;
            if (timeElapsed > 3) {
                wheelTimer.stop();
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (laser) {
            g.setColor(Color.RED);
            double playerX = ((p.getX() - xCam) + (p.getSize() / 2));
            double playerY = ((p.getY() - yCam) + (p.getSize() / 2));

            double deltaX = mouseX - (playerX - (p.getSize() / 2));
            double deltaY = mouseY - (playerY - (p.getSize() / 2));
            double magnitude = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));

            double targetX = (deltaX / magnitude) * 1000 + playerX;
            double targetY = (deltaY / magnitude) * 1000 + playerY;

            g.drawLine((int) playerX - (p.getSize() / 2), (int) playerY - (p.getSize() / 2), (int) targetX,
                    (int) targetY);
        } //draws laser

        for (Block w : wall) {
            w.draw(g);
        } 

        for (Bullet b : bullets) {
            b.draw(g);
        }

        for (Enemy e : enemies) {
            e.draw(g);
        }

        p.draw(g, timeElapsed); //basic settings(wall, enemies) will always be drawn.

        if (death) {

            g.setColor(new Color(0, 0, 0, 150)); //semi transparent screen
            g.fillRect(0, 0, getWidth(), getHeight());

            String text;

            text = "Game Over";

            g.setColor(Color.WHITE);
            g.drawString(text, 290, 100);

            text = "Press ENTER to RESTART";
            g.drawString(text, 250, 200);

            text = "Press ESC to QUIT";
            g.drawString(text, 270, 300);

            HSListToString();

        } //game over screen
    }

    // score stuff
    public int getScore() {
        return score;
    }

    //JLabel for current score in game.
    public static void changeScore(int scoreChange) {
        score += scoreChange;
        scoreLabel.setText("Current Score: " + score);

    }

    //label that keeps scores in all playthrough
    public static void updateHighScore() {
        String scoreString = "Play through " + numberTimesPlayed + ": " + score + " points";
        highScoresAList.add(new Score(score, scoreString));
        Collections.sort(highScoresAList);
    }

    public void HSListToString() {
        String label;
        label = "<html><body>";

        int index = 0;
        for (Score i : highScoresAList) {
            label += i.getScoreString();
            label += "<br>";
            if (index >= 4) {
                break;
            }
            index++;
        }
        label += "</body></html>";
        highScoreLabel.setText(label);

        highScoreLabel.setLocation(50, 150);
        highScoreLabel.setVisible(true);
    }

    public void readFile() {
        try {
            file = new File("scores.txt");
            Scanner s = new Scanner(file);

            //read in all the data
            numberTimesPlayed = s.nextInt() + 1;
            s.nextLine();
            while (s.hasNextLine()) {
                String line = s.nextLine();
                Scanner q = new Scanner(line);
                int score = q.nextInt();
                String scoreString = q.nextLine();
                scoreString = scoreString.substring(1);
                highScoresAList.add(new Score(score, scoreString));
            }

            System.out.println("File exists");
        } catch (FileNotFoundException e) {
            try {
                file = new File("scores.txt");
                file.createNewFile();
                System.out.println("Creating file");
            } catch (IOException p) {
                System.out.println("An error occured.");
                p.printStackTrace();
            }
        }
    }

    public void writeFile() {
        try {
            FileWriter writer = new FileWriter (file);
            try {
                PrintWriter pw = new PrintWriter(file);
                System.out.println("Cleared file");
            } catch (FileNotFoundException f) {
                f.printStackTrace();
            }
            writer.write(numberTimesPlayed + "\n");
            for (Score s: highScoresAList) {
                writer.write(s.getScore() + " " + s.getScoreString() + "\n");
            }
            writer.close();
        }
        catch (IOException e) {
            System.out.println("An error occured.");
            e.printStackTrace();
        }
    }

    public static String listToString(ArrayList<?> list) {
        String result = "";
        for (int i = 0; i < list.size(); i++) {
            result += " " + list.get(i);
        }
        return result;

    }

    // Key and Mouse Detection

    private class MouseController implements MouseListener, MouseMotionListener {
        public void mouseClicked(MouseEvent e) {

        }

        public void mousePressed(MouseEvent e) {
            if (mouseFlag) {
                if (e.getButton() == MouseEvent.BUTTON1) { //left click for bullets
                    startHolding = System.nanoTime(); //record time pressed length to shoot large bullets
                    if (!wheelTimer.isRunning()) { 
                        timeElapsed = 1;
                        wheelTimer.start();
                    }
                }
                if (e.getButton() == MouseEvent.BUTTON3) { //right click for laser
                    laser = !laser;
                    mouseX = e.getX();
                    mouseY = e.getY(); 
                }
            }
        }

        public void mouseReleased(MouseEvent e) {
            if (mouseFlag) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    bullets.add(new Bullet(
                            p.getX(), p.getY(), (int) (e.getX() + xCam), (int) (e.getY() + yCam), timeElapsed));
                    timeElapsed = 1;
                    wheelTimer.stop();
                }
            }
        }

        public void mouseEntered(MouseEvent e) {

        }

        public void mouseExited(MouseEvent e) {

        }

        public void mouseDragged(MouseEvent e) {
            if (laser) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        }

        public void mouseMoved(MouseEvent e) {
            if (laser) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        }
    }

    // KeyController
    private class KeyController implements KeyListener {
        public void keyPressed(KeyEvent e) {
            if (keyFlag) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W:
                        wPressed = true;
                        break;
                    case KeyEvent.VK_A:
                        aPressed = true;
                        p.setSpriteMode(1); //player facing left in drawplayer method in player class
                        break;
                    case KeyEvent.VK_S:
                        sPressed = true;
                        break;
                    case KeyEvent.VK_D:
                        dPressed = true;
                        p.setSpriteMode(2); //player facing right
                        break;
                }
                repaint();
            }
            switch (e.getKeyCode()) {
                case KeyEvent.VK_ENTER:
                    if (death) {
                        resetGame();
                    }
                    break;
                case KeyEvent.VK_ESCAPE:
                    if (death) {
                        System.exit(0);
                    }
                    break;

            }
        }

        public void keyTyped(KeyEvent e) {

        }

        public void keyReleased(KeyEvent e) {
            if (keyFlag) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W:
                        wPressed = false;
                        break;
                    case KeyEvent.VK_A:
                        aPressed = false;
                        break;
                    case KeyEvent.VK_S:
                        sPressed = false;
                        break;
                    case KeyEvent.VK_D:
                        dPressed = false;
                        break;
                }
            }
        }
    }

    public void resetGame() {
        numberTimesPlayed++;
        numberTimesPlayedList.add(numberTimesPlayed); //for displaying playthrough count in highscorelist
        death = false;
        mouseFlag = true;
        keyFlag = true; 

        score = 0;
        changeScore(score);//reset everything

        waveCount = 0;
        p.setX(0);
        p.setY(0);
        p.setYVel(0);
        p.setXVel(0);
        p.setHealth(30);//reset player location and cleath
        for (Enemy e : enemies) {
            e.removeCollider();
        }
        for (Bullet b : bullets) {
            b.removeCollider();
        }
        enemies.clear();
        bullets.clear();
        waveTimer.stop(); //reset current wave
        waveTimer = new Timer(20, new Wave(3)); //restart a new wave.
        waveTimer.start();

        highScoreLabel.setVisible(false);
    }

}