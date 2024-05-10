import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Color;

public class Bullet extends Collider {
    private float x;
    private float y;
    private int size;
    private double xDir;
    private double yDir;
    private double speed;
    private double power;
    private double damage;

    public Bullet(float x, float y, float xClicked, float yClicked, double power) {
        //Sets x and y
        this.x = x;
        this.y = y;

        //Sets size and damage
        this.power = power;
        if (power > 3) {
            power = 3;
        }

        size = (int)(10 * power);
        damage = 5 * power;

        speed = 8;
        tag = "Bullet";
        double deltaX = xClicked - x;
        double deltaY = yClicked - y;
        double magnitude = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
        xDir = deltaX / magnitude;
        yDir = deltaY / magnitude;
    }

    public void updatePos() {
        x += xDir * speed;
        y += yDir * speed;

        if (collision(this) != null) {
            Sound.playSound("Hit.wav");
            Canvas.bullets.remove(this);
            Canvas.obj.remove(this);
        }
    }

    public void removeCollider() {
        Canvas.obj.remove(this);
    }

    @Override
    public double getDamage() {
        return damage;
    }

    public Rectangle getRect() {
        return new Rectangle((int)x - (size/2), (int)y - (size/2), size, size);
    }

    public void draw(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillOval((int)(x - Canvas.xCam) - (size/2), (int)(y - Canvas.yCam) - (size/2), size, size);
    }

}
