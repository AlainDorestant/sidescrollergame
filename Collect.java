//Collects are entities that the player wants to collide with, as they increase
//their score.
public class Collect extends Entity implements CollisionReactive, AutoScroller {
    
    //Location of image file to be drawn for a Collect
    private static final String COLLECT_IMAGE_FILE = "media_files/orb.png";
    //Dimensions of the Collect  
    private static final int COLLECT_WIDTH = 75;
    private static final int COLLECT_HEIGHT = 75;
    //Number of pixels that the Collect moves left each time the game scrolls
    private static final int COLLECT_DEFAULT_SCROLL_SPEED = 5;
    //Amount of points received when player collides with a Collect
    private static final int COLLECT_POINT_VALUE = 20;
    
    private int scrollSpeed = COLLECT_DEFAULT_SCROLL_SPEED;
    

    public Collect(){
        this(0, 0);        
    }
    
    public Collect(int x, int y){
        this(x, y, COLLECT_IMAGE_FILE);  
    }
    
    public Collect(int x, int y, String imageFileName){
        this(x, y, COLLECT_WIDTH, COLLECT_HEIGHT, imageFileName);
    }

    public Collect(int x, int y, int width, int height, String imageName){
        super(x, y, width, height, imageName);
    }    
    
    public int getScrollSpeed(){
        return this.scrollSpeed;
    }
    
    //Sets the scroll speed to the argument amount
    public void setScrollSpeed(int newSpeed){
       this.scrollSpeed = newSpeed;
    }
    
    //Move the Collect left by its scroll speed
    public void scroll(){
       //implement me!
       this.setX(this.getX() - this.scrollSpeed);
       //throw new IllegalStateException("Hey 102 Student! You need to implement scroll() in Collect.java!");
    }
    
    //Colliding with a Collect increases the player's score by the specified amount
    public int getScoreChange(){
        return COLLECT_POINT_VALUE;
    }

    //Colliding with a Collect does not affect the player's HP
    public int getHPChange(){
        return 0;
    }
    
}
