import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class Player extends Collider {
    
    private float x;
    private float y;
    private float dx;
    private float dy;
    private float ax;
    private float ay;
    private final int size = 40;
    private final float friction = 1.4f;
    private final float cap = 5.25f;
    private final float accel = 1.4f;
    private double health = 30;
    private double maxHealth = 30;
    private double timeElapsed;
    private long timestamp;
    private Timer timer;

    Image dudeL = new ImageIcon("DudeL.png").getImage();
    Image dudeR = new ImageIcon("DudeR.png").getImage();
    int spriteMode = 1; //determines is player is facing left or right
    
    public int Screen = 1;

    public Player(float x, float y) {
        this.x = x;
        this.y = y;
        dx = 0;
        dy = 0;
        tag = "Player";
        timer = new Timer(20, new Invincibility()); 
        timer.start();
    }
    private class Invincibility implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            timeElapsed = ((System.nanoTime() - timestamp) / 1e9);
        }
    }


    public void draw(Graphics g, double power) {
        g.setColor(Color.BLACK);
        int xPos = (int)(x - Canvas.xCam) - (size/2);
        int yPos = (int)(y - Canvas.yCam) - (size/2);
        drawPlayer(g, xPos, yPos); 
        
        power = power - 1;
        if (power > 2) {
            power = 2;
        }
        int angle = -(int)((power / 2.0) * 360);
        g.fillArc(xPos - 20, yPos - 20, 20, 20, 90, angle);

        g.setColor(Color.RED);
        g.fillRect(xPos - 2, yPos - 8, size + 4, 5);
        g.setColor(Color.GREEN); 
        
        int healthSize = (int)((health / maxHealth) * (size + 4));
        g.fillRect(xPos - 2, yPos - 8, healthSize, 5); //draw healthbar
    } 

    public void drawPlayer(Graphics g, int xPos, int yPos) {
        if (spriteMode == 1) {
            g.drawImage(dudeL, xPos, yPos, null); //player facing left
        } else if (spriteMode == 2) {
            g.drawImage(dudeR, xPos, yPos, null); //player facing right
        }
    }

    public void setSpriteMode(int SM) {
        this.spriteMode = SM;
    }

    public void setXVel(float value) {
        this.dx = value;
    }
    
    public void setYVel(float value) {
        this.dy = value;
    }

    public void setXAccel(float value) {
        this.ax = value * accel;
    }
    
    public void setYAccel(float value) {
        this.ay = value * accel;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x){
        this.x = x;
    }

    public void setY(float y){
        this.y = y;
    }

    public int getSize() {
        return size;
    }

    public void damage(double damage) {
        if (timeElapsed > 1 && damage > 0) { //if statement make sure that player is invincible for a while after damage
            health -= damage;
            Sound.playSound("Hurt.wav");
            timestamp = System.nanoTime();
        }
    }

    public void updatePos() {
        //Update Acceleration (changes velocity based on accel)
        dx += ax;
        dy += ay;

        //If you aren't accelerating the player, apply friction
        if (ax == 0) {
            dx = dx / friction;
        }
        if (ay == 0) {
            dy = dy / friction;
        }

        //If velocity exceeds cap, set it to the cap
        if (dx > cap) {
            dx = cap;
        }
        if (dx < -cap) {
            dx = -cap;
        }
        if (dy > cap) {
            dy = cap;
        }
        if (dy < -cap) {
            dy = -cap;
        }

        

        //Update position
        x += dx;
        Rectangle intersection = collision(this);
        if (intersection != null) {
            if (dx > 0) {
                x -= intersection.getWidth();
            } else {
                x += intersection.getWidth();
            }
        }

        y += dy;
        intersection = collision(this);
        if (intersection != null) {
            if (dy > 0) {
                y -= intersection.getHeight();
            } else {
                y += intersection.getHeight();
            }
        }
    }

    public double getHealth (){
        return this.health;

    }

    public void setHealth(double health){
        this.health = health;
    }

    public Rectangle getRect() {
        return new Rectangle((int)x - (size/2), (int)y - (size/2), size, size);
    }
}
