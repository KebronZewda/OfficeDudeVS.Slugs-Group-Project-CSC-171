import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Color;
//this class draws the wall and blocks within the wall.
public class Block extends Collider {
    int x;
    int y;
    int width;
    int height;

    public Block(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void draw(Graphics g) {
        g.setColor(Color.GRAY);
        g.fillRect((int)(x - Canvas.xCam), (int)(y - Canvas.yCam), width, height);
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, width, height);
    }
}