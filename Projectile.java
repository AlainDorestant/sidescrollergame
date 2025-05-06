// Projectile moves right and destroys SpecialAvoids on contact
public class Projectile extends Entity implements AutoScroller {

    private static final String PROJECTILE_IMAGE_FILE = "media_files/projectile.png"; // swap to real image
    private static final int PROJECTILE_WIDTH = 100;
    private static final int PROJECTILE_HEIGHT = 100;
    private static final int PROJECTILE_SPEED = 10;

    private int scrollSpeed = PROJECTILE_SPEED;

    public Projectile(int x, int y) {
        super(x, y, PROJECTILE_WIDTH, PROJECTILE_HEIGHT, PROJECTILE_IMAGE_FILE);
    }


    public void scroll() {
        setX(getX() + scrollSpeed); // move to the right
    }


    public int getScrollSpeed() {
        return scrollSpeed;
    }


    public void setScrollSpeed(int newSpeed) {
        this.scrollSpeed = newSpeed;
    }
}
