//The entity that the human player controls in the game window
//The player moves in reaction to player input
public class Player extends Entity {
   
   //Location of image file to be drawn for a Player
   private static final String PLAYER_IMAGE_FILE = "media_files/gunman.png";
   //Dimensions of the Player
   private static final int PLAYER_WIDTH = 100;
   private static final int PLAYER_HEIGHT = 100;
   //Default speed that the Player moves (in pixels) each time the user moves it
   private static final int DEFAULT_MOVEMENT_SPEED = 7;
   //Starting hit points
   private static final int STARTING_HP = 3;
   
   //Current movement speed.  This is the number of pixels the Player moves by anytime
   //the user moves the Player (ie presses any movement key)
   private int moveSpeed;
   //Remaining Hit Points (HP) -- indicates the number of "hits" (ie collisions
   //with Avoids) that the player can take before the game is over
   private int hp;
   
   
   public Player(){
      this(0, 0);        
   }
   
   public Player(int x, int y){
      this(x, y, PLAYER_IMAGE_FILE);  
   }

   public Player(int x, int y, String imageName){
      this(x, y, PLAYER_WIDTH, PLAYER_HEIGHT, imageName);  
   }   

   public Player(int x, int y, int width, int height, String imageName){
      super(x, y, width, height, imageName);
      this.hp = STARTING_HP;
      this.moveSpeed = DEFAULT_MOVEMENT_SPEED;
   }


   
   //Retrieve and set the Player's current movement speed 
   //(i.e. the number of pixels the Player Entity moves by when
   //moved by the user)
   public int getMoveSpeed(){
       //implement me!
       return this.moveSpeed;
       //throw new IllegalStateException("Hey 102 Student! You need to implement getMoveSpeed() in Player.java!");
   }
   

   public void setMoveSpeed(int newSpeed){
      this.moveSpeed = newSpeed;
   }  
   
   
   //Retrieve the Player's current HP
   public int getHP(){
      return hp;
   }
   
   
   //Set the player's HP to a specific value.
   //Returns an boolean indicating if Player still has HP remaining
   public boolean setHP(int newHP){
      this.hp = newHP;
      return (this.hp > 0);
   }
   
   
   //Set the player's HP to a specific value.
   //Returns an boolean indicating if Player still has HP remaining
   public boolean modifyHP(int delta){
      this.hp += delta;
      return (this.hp > 0);
   }    
   
   public void move(int x, int y){
    this.setX(this.getX()+ x);
    this.setY(this.getY()+ y);

   }


   
   
   
   
   
}
