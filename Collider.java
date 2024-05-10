import java.awt.Rectangle;
//abstract class for player, enemy, bullets, block to extend and use method.
public abstract class Collider {
    public Collider() {
        Canvas.obj.add(this);
    }

    protected String tag;

    abstract Rectangle getRect();
    public void damage(double damage) {}
    public double getDamage() {
        return 0;
    }

    public static Rectangle collision(Collider c) {
        for (int i = 0; i < Canvas.obj.size(); i++) {
            Collider target = Canvas.obj.get(i);
            boolean ignoreBullet = ((c.tag == "Player" && target.tag == "Bullet") || (c.tag == "Bullet" && target.tag == "Player") || (c.tag == "Bullet" && target.tag == "Bullet"));
            boolean hitEnemy = (c.tag == "Bullet" && target.tag == "Enemy");
            boolean playerHit = (c.tag == "Enemy" && target.tag == "Player");

            if (c != target && !ignoreBullet && c.getRect().intersects(target.getRect())) {
                if (hitEnemy || playerHit) {
                    target.damage(c.getDamage());
                }
                return c.getRect().intersection(target.getRect());
            }
        }
        return null;
    }
}