import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Line2D;

public class Enemy extends Collider {
    
    private float x;
    private float y;
    private int size = 30;
    private double health = 40;
    private double maxHealth = 40;
    private double speed = 2;
    private double damage = 3;
    private Color color = Color.RED;
    private int type;
    Image enemyLR = new ImageIcon("Enemy2 R.png").getImage();
    Image enemyLL = new ImageIcon("Enemy2 L.png").getImage();
    Image enemySR = new ImageIcon("Enemy1 R.png").getImage();
    Image enemySL = new ImageIcon("Enemy1 L.png").getImage();
    private boolean facingLeft;

    public Enemy(float x, float y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
        tag = "Enemy";
        if (type == 1) { //the larger slug enemy
            size = 60;
            health = 70;
            maxHealth = 70;
            damage = 7;
        }
        //If you are on same x or y value as enemy, it shoots at you
    }

    public void draw(Graphics g) {
        int xPos = (int)(x - Canvas.xCam) - (size/2);
        int yPos = (int)(y - Canvas.yCam) - (size/2);

        if (type == 1){ //draws large enemy
            if (facingLeft) {
                g.drawImage(enemyLL, xPos, yPos, null);
            } else {
                g.drawImage(enemyLR, xPos, yPos, null);
            }
        } else{ //draws small enemy
            if (facingLeft) {
                g.drawImage(enemySL, xPos, yPos, null);
            } else {
                g.drawImage(enemySR, xPos, yPos, null);
            }
        }

        g.setColor(Color.RED);
        g.fillRect(xPos - 2, yPos - 8, size + 4, 5);
        g.setColor(Color.GREEN);
        
        int healthSize = (int)((health / maxHealth) * (size + 4));
        g.fillRect(xPos - 2, yPos - 8, healthSize, 5); //draws health bar of enemy.
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    @Override
    public void damage(double damage) {
        health -= damage;
    }
   
    public void setDamage(double damage) {
        this.damage = damage;
    }

    @Override
    public double getDamage() {
        return damage;
    }

    public void updatePos(float playerX, float playerY) {
        double dx = 0;
        double dy = 0;
        if (lineOfSight(playerX, playerY)) {
            //Move towards player
            double deltaX = playerX - x;
            double deltaY = playerY - y;
            double magnitude = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));

            double xDirPlayer = deltaX / magnitude;
            double yDirPlayer = deltaY / magnitude;

            dx = xDirPlayer * speed;
            dy = yDirPlayer * speed;

            for (Enemy e : Canvas.enemies) {
                if (e != this) {
                    deltaX = x - e.getX();
                    deltaY = y - e.getY();
                    magnitude = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));

                    double newSpeed = ((100 / magnitude) - 1) * 0.5;
                    
                    if (magnitude < 100) {
                        dx += (deltaX / magnitude) * newSpeed;
                        dy += (deltaY / magnitude) * newSpeed;
                    }
                    //If magnitude is 1, move away fast
                    //If magnitude is 10, move close
                }
            }
        }

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

        if (health < 0) {
            Canvas.enemies.remove(this);
            Canvas.obj.remove(this);

            switch (type) {
                case 0:
                    Canvas.changeScore(1);
                    break;
                case 1:
                    Canvas.changeScore(3);
                    break;
            }//scores differently based on type of enemy killed
        }

        if (Math.abs(x) > 590 || y > 490 || y < -390) {
            Canvas.enemies.remove(this);
            Canvas.obj.remove(this);
        }

        if (dx > 0) { //enemy moving to the right
            facingLeft = false;
        } else { //moving to the left
            facingLeft = true;
        }
    }

    public void removeCollider() {
        Canvas.obj.remove(this);
    }

    public boolean lineOfSight(float playerX, float playerY) {
        Line2D.Float line = new Line2D.Float(x, y, playerX, playerY);
        for (Block w : Canvas.wall) {
            if (w.getRect().intersectsLine(line)) {
                return false;
            }
        } 
        return true;
    }//enemy moves toward player if player and enemy has the same x/y && there is no wall between them

    public Rectangle getRect() {
        return new Rectangle((int)x - (size/2), (int)y - (size/2), size, size);
    }
}