package com.example.demogame2;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class SpaceInvaderView extends SurfaceView implements Runnable {
    Context context;

    //khai báo và khởi tạo thread
    private Thread gameThread = null;
    //khai báo surfaceHolder
    private SurfaceHolder surfaceHolder;
    //biến xác định đang chơi hay không
    volatile boolean isPlaying;
    //biến game đã dừng hay không
    private boolean isPaused, shooting;

    //khai báo canvas và paint
    private Canvas canvas;
    private Paint paint;
    //theo dõi tốc độ khung hình
    private long fps;
    //biến tính toán fps
    private long timeThisFrame;
    //size screen
    private int screenX, screenY;
    //playerShip
    private PlayerShip playerShip;
    //player bullet
    private Bullet bullet;
    //bullet invader
    private Bullet[] invaderBullets = new Bullet[200];
    private int newBullet;
    private int maxInvaderBullets = 10;

    private Invader[] invaders = new Invader[60];
    int numInvader = 0;

    //sound
    private SoundPool soundPool;
    private int playerExplodeID = -1;
    private int invaderExplodeID = -1;
    private int shootID = -1;
    private int damageShelterID = -1;
    private int uhID = -1;
    private int ohID = -1;

    //score
    private int score;
    //lives
    private int lives = 5;
    // How menacing should the sound be?
    private long menaceInterval = 3000;
    // Which menace sound should play next
    private boolean uhOrOh;
    // When did we last play a menacing sound
    private long lastMenaceTime = System.currentTimeMillis();

    public SpaceInvaderView(Context context, int screenX, int screenY) {
        super(context);
        this.context = context;
        //khởi tạo surfaceHolder
        surfaceHolder = getHolder();
        paint = new Paint();
        this.screenX = screenX;
        this.screenY = screenY;
        //soundPool
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

        try {
            AssetManager manager = context.getAssets();
            AssetFileDescriptor descriptor;
            descriptor = manager.openFd("damageShelter.ogg");
            damageShelterID = soundPool.load(descriptor, 0);
            descriptor = manager.openFd("invaderExplode.ogg");
            invaderExplodeID = soundPool.load(descriptor, 0);
            descriptor = manager.openFd("oh.ogg");
            ohID = soundPool.load(descriptor, 0);
            descriptor = manager.openFd("playerExplode.ogg");
            playerExplodeID = soundPool.load(descriptor, 0);
            descriptor = manager.openFd("shoot.ogg");
            shootID = soundPool.load(descriptor, 0);
            descriptor = manager.openFd("uh.ogg");
            uhID = soundPool.load(descriptor, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        prepareLevel();
    }

    private void prepareLevel() {
        // Here we will initialize all the game objects
        menaceInterval = 1000;
        // Make a new player space ship
        playerShip = new PlayerShip(context, screenX, screenY);
        // Prepare the players bullet
        bullet = new Bullet(screenY);
        for (int i = 0; i < invaderBullets.length; i++) {
            invaderBullets[i] = new Bullet(screenY);
        }

        // Initialize the invadersBullets array

        // Build an army of invaders
        numInvader = 0;
        for (int column = 0; column < 6; column++) {
            for (int row = 0; row < 5; row++) {
                invaders[numInvader] = new Invader(context, row, column, screenX, screenY);
                numInvader++;
            }
        }

        // Build the shelters
    }

    @Override
    public void run() {
        while (isPlaying) {
            // Capture the current time in milliseconds in startFrameTime
            long startFrameTime = System.currentTimeMillis();

            // Update the frame
            if (!isPaused) {
                update();

            }
            // Draw the frame
            draw();
            // Calculate the fps this frame
            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame;
            }
            if (!isPaused) {
                if ((startFrameTime - lastMenaceTime) > menaceInterval) {
                    if (uhOrOh) {
                        // Play Uh
                        soundPool.play(uhID, 1, 1, 0, 0, 1);

                    } else {
                        // Play Oh
                        soundPool.play(ohID, 1, 1, 0, 0, 1);
                    }

                    // Reset the last menace time
                    lastMenaceTime = System.currentTimeMillis();
                    // Alter value of uhOrOh
                    uhOrOh = !uhOrOh;
                }
            }

        }

    }

    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.argb(255, 26, 128, 182));
            paint.setColor(Color.argb(255, 255, 255, 255));
            // Draw the player spaceship
            canvas.drawBitmap(playerShip.getPlayerBitmap(), playerShip.getX(), screenY - 200, paint);

            // Draw the invaders
            for (int i = 0; i < numInvader; i++) {
                if (invaders[i].isVisible()) {
                    if (uhOrOh) {
                        canvas.drawBitmap(invaders[i].getInvaderBitmap1(), invaders[i].getX(), invaders[i].getY(), paint);
                    } else {
                        canvas.drawBitmap(invaders[i].getInvaderBitmap2(), invaders[i].getX(), invaders[i].getY(), paint);
                    }
                }
            }

            // Draw the bricks if visible

            // Draw the players bullet if active
            if (bullet.isActive()) {
                canvas.drawRect(bullet.getRectF(), paint);
            }

            // Draw the invaders bullets if active
            for (int i = 0; i < invaderBullets.length; i++) {
                if (invaderBullets[i].isActive()) {
                    canvas.drawRect(invaderBullets[i].getRectF(), paint);
                }
            }
            // Draw the score and remaining lives
            // Change the brush color
            paint.setColor(Color.argb(255, 249, 129, 0));
            paint.setTextSize(40);
            canvas.drawText("Score: " + score + "   Lives: " + lives, 10, 50, paint);

            // Draw everything to the screen
            surfaceHolder.unlockCanvasAndPost(canvas);
        }

    }

    private void update() {
        boolean bumped = false;
        // Has the player lost
        boolean lost = false;
        // Move the player's ship
        playerShip.update(fps);

        //update player's bullet
        bullet.update(fps);
        // Update the invaders if visible
        for (int i = 0; i < invaderBullets.length; i++) {
            invaderBullets[i].update(fps);
        }
        // Update all the invaders if visible
        for (int i = 0; i < numInvader; i++) {

            if (invaders[i].isVisible()) {
                // Move the next invader
                invaders[i].update(fps);

                // Does he want to take a shot?
                if (invaders[i].takeAim(playerShip.getX(),
                        playerShip.getLength())) {

                    // If so try and spawn a bullet
                    if (invaderBullets[newBullet].shoot(invaders[i].getX()
                                    + invaders[i].getLength() / 2,
                            invaders[i].getY(), bullet.DOWN)) {

                        // Shot fired
                        // Prepare for the next shot
                        newBullet++;

                        // Loop back to the first one if we have reached the last
                        if (newBullet == maxInvaderBullets) {
                            // This stops the firing of another bullet until one completes its journey
                            // Because if bullet 0 is still active shoot returns false.
                            newBullet = 0;
                        }
                    }
                }

                // If that move caused them to bump the screen change bumped to true
                if (invaders[i].getX() > screenX - invaders[i].getLength()
                        || invaders[i].getX() < 0) {

                    bumped = true;

                }
            }

        }
        // Did an invader bump into the edge of the screen
        if (bumped) {

            // Move all the invaders down and change direction
            for (int i = 0; i < numInvader; i++) {
                invaders[i].dropDownAndReverse();
                // Have the invaders landed
                if (invaders[i].getY() > screenY - screenY / 10) {
                    lost = true;
                }
            }

            // Increase the menace level
            // By making the sounds more frequent
            menaceInterval = menaceInterval - 80;
        }
        if (lost) {
            prepareLevel();
        }

        // Has the player's bullet hit the top of the screen
        if(bullet.getImpactPointY() < 0){
            bullet.setActive();
        }

        // Has an invaders bullet hit the bottom of the screen
        for(int i = 0; i < invaderBullets.length; i++){
            if(invaderBullets[i].getImpactPointY() > screenY){
                invaderBullets[i].setActive();
            }
        }

        // Has the player's bullet hit an invader
        if(bullet.isActive()) {
            for (int i = 0; i < numInvader; i++) {
                if (invaders[i].isVisible()) {
                    if (RectF.intersects(bullet.getRectF(), invaders[i].getRectF())) {
                        invaders[i].setVisible();
                        soundPool.play(invaderExplodeID, 1, 1, 0, 0, 1);
                        bullet.setActive();
                        score = score + 10;

                        // Has the player won
                        if(score == numInvader * 10){
                            isPaused = true;
                            score = 0;
                            lives = 5;
                            prepareLevel();
                        }
                    }
                }
            }
        }

        // Has an alien bullet hit a shelter brick

        // Has a player bullet hit a shelter brick

        // Has an invader bullet hit the player ship
        for(int i = 0; i < invaderBullets.length; i++){
            if(invaderBullets[i].isActive()){
                if(RectF.intersects(playerShip.getRectF(), invaderBullets[i].getRectF())){
                    invaderBullets[i].setActive();
                    lives --;
                    soundPool.play(playerExplodeID, 1, 1, 0, 0, 1);

                    // Is it game over?
                    if(lives == 0){
                        isPaused = true;
                        lives = 3;
                        score = 0;
                        prepareLevel();

                    }
                }
            }
        }

    }

    public void paused() {
        isPlaying = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        isPlaying = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                isPaused = false;
                if (event.getY() > screenY - screenY / 8) {
                    if (event.getX() > screenX / 2) {
                        playerShip.setMovementState(playerShip.RIGHT);
                    } else {
                        playerShip.setMovementState(playerShip.LEFT);
                    }

                }
                if (event.getY() < screenY - screenY / 8) {
                    // Shots fired
                    if (bullet.shoot(playerShip.getX() +
                            playerShip.getLength() / 2, screenY, bullet.UP)) {
                        soundPool.play(shootID, 1, 1, 0, 0, 1);
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                if (event.getY() > screenY - screenY / 10) {
                    playerShip.setMovementState(playerShip.STOPPED);
                }
                break;
        }
        return true;
    }
}
