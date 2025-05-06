import java.util.*;
import java.awt.*;
import java.awt.event.*;



public class DorestantGame extends SimpleGame {
    protected SpecialAvoid attachedAvoid = null;
    protected int clicksToRemove = 3;
    protected int attachmentTimer = 0;
    protected static final int DEATH_TIMER = 600;//  600/60 = 10sec (since the game updates almost every 60 secs)

    protected static final int GOO_TIMER = 300; //   300/60 = 5sec (since the game updates almost every 60 secs)
    public static final String INTRO_SPLASH_FILE = "media_files/newsplash.jpeg";

    protected int ammoCount = 0;  // Track ammunition count
    protected static final int AMMO_PER_COLLECT = 5;  // Ammo gained per special collect
    protected long lastProjectileTime = 0;
    protected static final long PROJECTILE_COOLDOWN = 500;
    protected boolean hasGun = false;  // track if player has got a gun

    protected int zombiesKilled = 0;  // track # of zombies killed
    protected static final int ZOMBIES_TO_WIN = 10;  //# of zombies needed to kill to win

    public DorestantGame() {
        super();
    }

    public DorestantGame(int gameWidth, int gameHeight) {
        super(gameWidth, gameHeight);
    }


    protected void pregame() {
        super.pregame();
        this.setSplashImg(INTRO_SPLASH_FILE);
        this.setBackgroundImg("media_files/hororbackground.jpg");
       // System.out.println("gamestart");
    }


    protected void gameUpdate() {
        if (isPaused) return;

        super.performScrolling();

        if (attachedAvoid != null) {
            attachmentTimer++;//increment each game update
        attachedAvoid.setX(player.getX() + player.getWidth() - attachedAvoid.getWidth()/2);// push slightly to the player R edge
        attachedAvoid.setY(player.getY() + player.getHeight()/2 - attachedAvoid.getHeight()/2);//t sticks right to the middle.


            if (attachmentTimer >= DEATH_TIMER) {
            //System.out.println("stuck too long.");
                player.setHP(0);
            }
        }

        // update all Goos
        for (int i = 0; i < toDraw.size(); i++) {
            Entity e = toDraw.get(i);
            if (e instanceof Goo) {
                ((Goo) e).updateLifespan(); // see class
            }
        }

        // handle projectiles
        for (int i = 0; i < toDraw.size(); i++) {
            Entity e = toDraw.get(i);
            if (e instanceof Projectile) {
                ((Projectile) e).setX(e.getX() + 10);

                for (int j = 0; j < toDraw.size(); j++) {
                    Entity target = toDraw.get(j);
                    if (target instanceof SpecialAvoid && !target.collided && e.isCollidingWith(target)) {
                        System.out.println("Projectile hit a SpecialAvoid!");
                        target.collided = true;
                        target.flagForGC(true);
                        e.flagForGC(true);
                        score += 25;
                        zombiesKilled++; // Increment zombies killed counter
                        //System.out.println("Zombies killed: " + zombiesKilled + "/" + ZOMBIES_TO_WIN);
                        break;
                    }
                }

                if (e.getX() > getWindowWidth()) {
                    e.flagForGC(true);
                }
            }
        }

        // Process collisions
        for (int i = 0; i < toDraw.size(); i++) {
            Entity e = toDraw.get(i);

            if (e instanceof SpecialCollect && player.isCollidingWith(e) && !e.collided && e != player) {
                score += ((SpecialCollect)e).getScoreChange();
                player.modifyHP(1);
                e.collided = true;
                e.flagForGC(true);

                // ammo
                ammoCount += AMMO_PER_COLLECT;
                // Mark that player has acquired a gun
                hasGun = true;
                //System.out.println("got gun Current ammo: " + ammoCount);
            } else if (e instanceof Collect && player.isCollidingWith(e) && !e.collided && e != player) {
                score += ((Collect)e).getScoreChange();
                // If player has a gun, collecting regular Collect items gives ammo
                if (hasGun) {
                    ammoCount += 1; // Regular collects give 1 ammo
                    //System.out.println("Ammo +1! Current ammo: " + ammoCount);
                }
                e.collided = true;
                e.flagForGC(true);
            } else if (e instanceof SpecialAvoid && player.isCollidingWith(e) && !e.collided && e != player && attachedAvoid == null) {
                score += ((SpecialAvoid)e).getScoreChange();
                this.score -= 50;
                attachedAvoid = (SpecialAvoid)e;
                e.collided = true;
                clicksToRemove = 1;
                attachmentTimer = 0;
                attachedAvoid.setX(player.getX() + player.getWidth() - attachedAvoid.getWidth()/2);
                attachedAvoid.setY(player.getY() + player.getHeight()/2 - attachedAvoid.getHeight()/2);
                //System.out.println(" attached");
            } else if (e instanceof Avoid && player.isCollidingWith(e) && !e.collided && e != player) {
                score += ((Avoid)e).getScoreChange();
                player.modifyHP(-1);
                e.collided = true;
                e.flagForGC(true);

                // Create goo effect at the collision location when player hits an Avoid
                Goo goo = new Goo();
                toDraw.add(goo);
               // System.out.println("hit  Avoid, goo splatter.");
            }
        }

        // Important: perform garbage collection AFTER processing all entities
        toDraw.performGC();

        int adjustedSpawnInterval = SPAWN_INTERVAL * 100 / scrollSpeedMultiplier;
        adjustedSpawnInterval = Math.max(10, Math.min(90, adjustedSpawnInterval));

        if (super.getTicksElapsed() % adjustedSpawnInterval == 0) {
            super.performSpawning("media_files/orb.png","media_files/monster.png","media_files/blaster.png","media_files/greenzombie.png");
            super.gcOffscreenEntities();
        }

        updateTitleText();
    }

    @Override
    protected void updateTitleText() {
        String attachedStatus = "";
        String ammoStatus = hasGun ? " [AMMO: " + ammoCount + "]" : "";
        String zombieStatus = " [ZOMBIES: " + zombiesKilled + "/" + ZOMBIES_TO_WIN + "]";

        if (attachedAvoid != null) {
            int secondsLeft = (DEATH_TIMER - attachmentTimer) / 60;
            attachedStatus = " [PARASITE: " + clicksToRemove + " clicks, " + secondsLeft + "s until death]";
        }

        setTitle("HP: " + player.getHP() + ", Score: " + score + ammoStatus + zombieStatus + attachedStatus);
    }

    protected void shootProjectile() {
        // Check if player has a gun and ammunition
        if (!hasGun) {
           // System.out.println("you dont have gun");
            return;
        }

        if (ammoCount <= 0) {
            //System.out.println("no ammo");
            return;
        }

        long now = System.currentTimeMillis();// stackoverflow + java api + help
        if (now - lastProjectileTime < PROJECTILE_COOLDOWN)
            return;

        int x = player.getX() + player.getWidth();
        int y = player.getY() + player.getHeight() / 2 - 5;// to get porj. to shoot straight from barrel

        Projectile p = new Projectile(x-50, y-50); // more addjsutments
        toDraw.add(p);

        // Reduce ammo count when shooting
        ammoCount--;// dec. ammo count
        lastProjectileTime = now;
        //System.out.println("fired  projectile ammo remaining: " + ammoCount);
    }


    protected void reactToKeyPress(int keyCode) {
        super.reactToKeyPress(keyCode);
        if (keyCode == KeyEvent.VK_SPACE) {
            shootProjectile();
        }
    }

    protected void performScrolling() {
        super.performScrolling();
    }


    protected void gcOffscreenEntities() {
        super.gcOffscreenEntities();
    }

    protected void performSpawning(String collectImage, String avoidImage, String specialCollectImage, String specialAvoidImage) {
        //IMPROTANT CHAnge
        // Override spawning to prevent SpecialCollect items from spawning after player has a gun
        if (hasGun) {
            // Custom spawning logic that doesn't spawn SpecialCollect items
            int randX = getWindowWidth() + (int)(Math.random() * 200);
            int randY = (int)(Math.random() * (getWindowHeight() - 100)) + 50;

            int entityType = (int)(Math.random() * 3); // 0 for Collect, 1 & 2 for Avoid

            Entity spawnedEntity = null;
            switch (entityType) {
                case 0:
                    spawnedEntity = new Collect(randX, randY);
                    break;
                case 1:
                    spawnedEntity = new Avoid(randX, randY);
                    break;
                case 2:
                    if (Math.random() < 0.3) { // 30% chance for SpecialAvoid
                        spawnedEntity = new SpecialAvoid(randX, randY);
                    } else {
                        spawnedEntity = new Avoid(randX, randY);
                    }
                    break;
            }

            if (spawnedEntity != null) {
                toDraw.add(spawnedEntity);
            }
        } else {
            //inhertnace!
            super.performSpawning("media_files/orb.png","media_files/monster.png","media_files/blaster.png","media_files/greenzombie.png");
        }
    }

    protected void postgame() {
        if (checkForGameOver()) {
            if (player.getHP() <= 0)
                super.setTitle("GAME OVER - You Lose");
            if (zombiesKilled >= ZOMBIES_TO_WIN)
                super.setTitle("GAME OVER - You Won!");
        }
    }

    protected boolean checkForGameOver() {
        if (player.getHP() <= 0) return true;
        if (zombiesKilled >= ZOMBIES_TO_WIN) return true;
        return false;
    }

    protected MouseEvent reactToMouseClick(MouseEvent click) {
        if (attachedAvoid != null) {
            int clickX = click.getX();
            int clickY = click.getY();

            if (clickX >= attachedAvoid.getX() && clickX <= attachedAvoid.getX() + attachedAvoid.getWidth() &&
                clickY >= attachedAvoid.getY() && clickY <= attachedAvoid.getY() + attachedAvoid.getHeight()) {

                clicksToRemove--;
                //System.out.println("Clicked ");

                if (clicksToRemove <= 0) {
                    attachedAvoid.flagForGC(true);
                    attachedAvoid = null;
                }

                return click;
            }
        }

        return super.reactToMouseClick(click);
    }
}
