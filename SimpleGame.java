import java.awt.*;
import java.awt.event.*;
import java.util.*;

//A Simple version of the scrolling game, featuring Avoids, Collects, SpecialAvoids, and SpecialCollects
//Players must reach a score threshold to win.
//If player runs out of HP (via too many Avoid/SpecialAvoid collisions) they lose.
public class SimpleGame extends SSGEngine {


    //Starting Player coordinates
    protected static final int STARTING_PLAYER_X = 0;
    protected static final int STARTING_PLAYER_Y = 100;

    //Score needed to win the game
    protected static final int SCORE_TO_WIN = 300;

    //Maximum that the game speed can be increased to
    //(a percentage, ex: a value of 300 = 300% speed, or 3x regular speed)
    protected static final int MAX_GAME_SPEED = 300;
    //Interval that the speed changes when pressing speed up/down keys
    protected static final int SPEED_CHANGE_INTERVAL = 20;

    public static final String INTRO_SPLASH_FILE = "media_files/splash.gif";
    //Key pressed to advance past the splash screen
    public static final int ADVANCE_SPLASH_KEY = KeyEvent.VK_ENTER;

    //Interval that Entities get spawned in the game window
    //ie: once every how many ticks does the game attempt to spawn new Entities
    protected static final int SPAWN_INTERVAL = 45;


    //A Random object for all your random number generation needs!
    public static final Random rand = new Random();

    //player's current score
    protected int score;

    protected int scrollSpeedMultiplier = 100;


    //Stores a reference to game's Player object for quick reference (Though this Player presumably
    //is also in the DisplayList, but it will need to be referenced often)
    protected Player player;


    public SimpleGame(){
        super();
    }

    public SimpleGame(int gameWidth, int gameHeight){
        super(gameWidth, gameHeight);
    }


    //Performs all of the initialization operations that need to be done before the game starts
    protected void pregame(){
        this.setBackgroundColor(Color.BLACK);
        this.setSplashImg(INTRO_SPLASH_FILE);
        this.player = new Player(STARTING_PLAYER_X, STARTING_PLAYER_Y);
        this.toDraw.add(player);
        this.score = 0;
    }

    //Called on each game tick
    protected void gameUpdate(){
        //scroll all AutoScroller Entities on the game board
    if (isPaused) {
        return;
    }
        performScrolling();

      for (int i = 0; i < toDraw.size(); i++) {
        Entity e = toDraw.get(i);

       if (e instanceof SpecialCollect && player.isCollidingWith(e) && !e.collided && e != player) {
            score += ((SpecialCollect)e).getScoreChange();
            player.modifyHP(1);
            e.collided = true;
            e.flagForGC(true);
            //System.out.println(" u hit a Special collect" + player.getHP());
         }

       else if (e instanceof Collect && player.isCollidingWith(e) && !e.collided && e != player) {
            score += ((Collect)e).getScoreChange();
            e.collided = true;
            e.flagForGC(true);
        }


        if (e instanceof SpecialAvoid && player.isCollidingWith(e) && e.collided == false && e != player) {
            score += ((SpecialAvoid)e).getScoreChange();
            player.modifyHP(-1); // Or whatever penalty for SpecialAvoid
            this.score-= 50;
            e.collided = true;
            e.flagForGC(true);
        }



       else if (e instanceof Avoid && player.isCollidingWith(e) && e.collided == false && e != player) {
            score += ((Avoid)e).getScoreChange();
            player.modifyHP(-1);
            e.collided = true;
            e.flagForGC(true);
        }




    }

int adjustedSpawnInterval = SPAWN_INTERVAL * 100 / scrollSpeedMultiplier;

    // Make sure it doesn't get too extreme in either direction
    adjustedSpawnInterval = Math.max(10, Math.min(90, adjustedSpawnInterval));

    // Spawn new entities only at the adjusted interval
    if (super.getTicksElapsed() % adjustedSpawnInterval == 0) {
        performSpawning("media_files/collect.gif","media_files/avoid.gif","media_files/special_collect.gif","media_files/special_avoid.gif");
        gcOffscreenEntities();
    }

    updateTitleText();
}






    //Update the text at the top of the game window
    protected void updateTitleText(){


        setTitle("HP: " + player.getHP() + ", Score:" + score);

        }


        //Scroll all AutoScroller entities per their respective scroll speeds
    protected void performScrolling(){
        //****   Implement me!   ****
        for(int i  = 0; i< toDraw.size(); i++){
            Entity e = toDraw.get(i);
            if (e instanceof AutoScroller)
                ((AutoScroller)e).scroll();
        }

    }


    //Handles "garbage collection" of the entities
    //Flags entities in the displaylist that are no longer relevant
    //(i.e. will no longer need to be drawn in the game window).
    protected void gcOffscreenEntities(){
        ArrayList<Entity>toRemove = new ArrayList<>();
        for(int i  = 0; i< toDraw.size(); i++){
          Entity e = toDraw.get(i);
          if(e.getX()<0){
            toRemove.add(e);
            e.flagForGC(true);
        }
    }
  for(Entity e : toRemove){
    toDraw.performGC();
    //System.out.print("I have been GC'd");

  }
    }
private void onlyValidPos(Entity temp, ArrayList<Entity> newEntities) {
    int maxAttempts = 10;

    for(int i = 0; i < maxAttempts; i++) {
        int randomY = rand.nextInt(getWindowHeight() - temp.getHeight());
        temp.setY(randomY);

        boolean validPos = true;


        for(int j = 0; j < toDraw.size(); j++) {
            Entity existing = toDraw.get(j);
            if (existing != player && existing != temp && temp.isCollidingWith(existing)) {
                validPos = false;
                break;
            }
        }

        for(Entity e : newEntities) {
            if (e != temp && temp.isCollidingWith(e)) {
                validPos = false;
                break;
            }
        }

        if(validPos) {
            newEntities.add(temp);
            return;
        }
    }

}




    //Spawn new Entities on the right edge of the game board
    protected void performSpawning(String collectImage, String avoidImage, String specialCollectImage, String specialAvoidImage) {
    int spawnX = getWindowWidth();
    ArrayList<Entity> newEntities = new ArrayList<>();

    // Create entities as before
    if (rand.nextBoolean()) {
        Entity newCollect = new Collect(spawnX, 0,collectImage);
        onlyValidPos(newCollect, newEntities);
    }

    if (rand.nextBoolean()) {
        Entity newAvoid = new Avoid(spawnX, 0,avoidImage);
        onlyValidPos(newAvoid, newEntities);
    }

    if (rand.nextInt(4) == 0) { // 25 chance
        Entity newSpecialCollect = new SpecialCollect(spawnX, 0,specialCollectImage);
        onlyValidPos(newSpecialCollect, newEntities);
    }

    if (rand.nextInt(5) == 0) { // 20 chance
        Entity newSpecialAvoid = new SpecialAvoid(spawnX, 0,specialAvoidImage);
        onlyValidPos(newSpecialAvoid, newEntities);
    }


    for (Entity e : newEntities) {

        if (e instanceof AutoScroller) {
            int baseSpeed = 5;
            int adjustedSpeed = baseSpeed * scrollSpeedMultiplier / 100;
            ((AutoScroller) e).setScrollSpeed(adjustedSpeed);
        }

        toDraw.add(e);
    }


    updateScrollSpeeds();
}










        //****   Implement me!   ****




    //Called once the game is over, performs any end-of-game operations
    protected void postgame(){

        if (checkForGameOver())
            if(player.getHP()<= 0)
                super.setTitle("GAME OVER - You Lose");
            if(this.score>= 300)
                super.setTitle("GAME OVER - You Won!");


    }


    protected boolean checkForGameOver(){
    if(player.getHP() <=0)
        return true;

        if (score>=300)
            return true;


        return false;   //****   placeholder... Implement me!   ****

    }

    // this updates all entities whenever speed is increased/decreasedd
    private void updateScrollSpeeds() {
    //update entity
    for (int i = 0; i < toDraw.size(); i++) {
        Entity e = toDraw.get(i);
        if (e instanceof AutoScroller) {

            AutoScroller scroller = (AutoScroller) e;
            int currentSpeed = scroller.getScrollSpeed();
            int baseSpeed = currentSpeed * 100 / scrollSpeedMultiplier;
            int newSpeed = baseSpeed * scrollSpeedMultiplier / 100;
            scroller.setScrollSpeed(newSpeed);
        }
    }

   //update player
    int currentPlayerSpeed = player.getMoveSpeed();
    int basePlayerSpeed = currentPlayerSpeed * 100 / scrollSpeedMultiplier;
    int newPlayerSpeed = basePlayerSpeed * scrollSpeedMultiplier / 100;
    player.setMoveSpeed(newPlayerSpeed);
}

    protected void reactToKeyPress(int key){

        if (key == KEY_PAUSE_GAME) {
            isPaused = !isPaused;
            return;
    }


     if (isPaused) {
        return;

    }




    int moveSpeed = player.getMoveSpeed();
    int newX = player.getX();
    int newY = player.getY();

        if (getSplashImg() != null){
            if (key == ADVANCE_SPLASH_KEY)
                super.setSplashImg(null);
            return;
        }

else if (key == KeyEvent.VK_EQUALS) {

    scrollSpeedMultiplier += SPEED_CHANGE_INTERVAL;


    for (int i = 0; i < toDraw.size(); i++) {
        Entity e = toDraw.get(i);
        if (e instanceof AutoScroller) {
            int defaultSpeed = 5; // Base speed
            int newSpeed = defaultSpeed * scrollSpeedMultiplier / 100;
            ((AutoScroller) e).setScrollSpeed(newSpeed);
        }
    }


    int defaultPlayerSpeed = 10;
    player.setMoveSpeed(defaultPlayerSpeed * scrollSpeedMultiplier / 100);

    // Update title

}
else if (key == KeyEvent.VK_MINUS) {

    scrollSpeedMultiplier = Math.max(20, scrollSpeedMultiplier - SPEED_CHANGE_INTERVAL);

    for (int i = 0; i < toDraw.size(); i++) {
        Entity e = toDraw.get(i);
        if (e instanceof AutoScroller) {
            int defaultSpeed = 5;
            int newSpeed = defaultSpeed * scrollSpeedMultiplier / 100;
            ((AutoScroller) e).setScrollSpeed(newSpeed);
        }
    }


    int defaultPlayerSpeed = 10;
    player.setMoveSpeed(defaultPlayerSpeed * scrollSpeedMultiplier / 100);



}
       if (key == UP_KEY) {
        newY = Math.max(0, player.getY() - moveSpeed);
    }

   else if (key == DOWN_KEY) {
        newY = Math.min(getWindowHeight() - player.getHeight(),
                       player.getY() + player.getMoveSpeed());



    }

    else if (key == LEFT_KEY) {
        newX = Math.max(0, player.getX() - moveSpeed);
    }
    // Right movement
    else if (key == RIGHT_KEY) {
        newX = Math.min(getWindowWidth() - player.getWidth(),
                       player.getX() + moveSpeed);
    }

    else if(key == KEY_QUIT_GAME){

    }

    player.setX(newX);
    player.setY(newY);

    }





    //Handles reacting to a single mouse click in the game window
    protected MouseEvent reactToMouseClick(MouseEvent click){

        //Mouse functionality is not used at all in the Starter game...
        //you may want to override this function for a CreativeGame feature though!

        return click;//returns the mouse event for any child classes overriding this method
    }



}
