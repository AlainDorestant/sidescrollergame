//Avoids are entities the player needs to avoid colliding with.
//If a player collides with an avoid, it reduces the players Hit Points (HP).
public class Avoid extends Entity implements CollisionReactive, AutoScroller {
    
    //Location of image file to be drawn for an Avoid
    private static final String AVOID_IMAGE_FILE = "media_files/avoid.gif";
    //Dimensions of the Avoid    
    private static final int AVOID_WIDTH = 75;
    private static final int AVOID_HEIGHT = 75;
    //Number of pixels that the Avoid moves left each time the game scrolls
    private static final int AVOID_DEFAULT_SCROLL_SPEED = 5;
    //Amount of HP deducted when Player collides with an Avoid
    private static final int AVOID_DMG_VALUE = -1;    

    private int scrollSpeed = AVOID_DEFAULT_SCROLL_SPEED;
        
    
    public Avoid(){
        this(0, 0);        
    }
    
    public Avoid(int x, int y){
        this(x, y, AVOID_IMAGE_FILE);  
    }

    public Avoid(int x, int y, String imageName){
        this(x, y, AVOID_WIDTH, AVOID_HEIGHT, imageName); 
    }
    
    public Avoid(int x, int y, int width, int height, String imageName){
        super(x, y, width, height, imageName);
    }

    
    public int getScrollSpeed(){
        return this.scrollSpeed;
    }
    
    //Sets the scroll speed to the argument amount
    public void setScrollSpeed(int newSpeed){
       this.scrollSpeed = newSpeed;
    }
    
    //Move the avoid left by the scroll speed
    public void scroll(){
        setX(getX() - this.scrollSpeed);
    }
    
    //Colliding with an Avoid does not affect the player's score
    public int getScoreChange(){
       //implement me!(No need to do anything here since "Colliding with an Avoid does not affect the player's score" )
        return 0;
       //throw new IllegalStateException("Hey 102 Student! You need to implement getScoreChange() in Avoid.java!");
    }

    //Colliding with an Avoid reduces players HP by 1
    public int getHPChange(){
        return AVOID_DMG_VALUE;
    }
    
}
