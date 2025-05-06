class Goo extends Entity { //got help for this
    private int lifespan;
    private static final int MAX_LIFESPAN = 300; //  300/60 = 5sec (since the he gove updatde almost every 60 secs)

    public Goo() {

        super(0, 0, 900, 600, "media_files/goo1.png");
        this.lifespan = MAX_LIFESPAN;
    }

    public void updateLifespan() {
        lifespan--;
        if (lifespan <= 0) {
            this.flagForGC(true);
        }
    }
}
