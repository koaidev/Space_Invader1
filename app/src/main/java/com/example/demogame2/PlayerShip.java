package com.example.demogame2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;

public class PlayerShip {
    //for collision
    private RectF rectF;
    private Bitmap playerBitmap;
    private float length, height, x, y, shipSpeed;
    public final int STOPPED = 1;
    public final int LEFT = 0;
    public final int RIGHT = 2;
    private int shipMoving = STOPPED;

    public PlayerShip(Context context, int screenX, int screenY) {
        rectF = new RectF();
        length = screenX / 10;
        height = screenY / 10;
        x = screenX / 2;
        y = screenY - 20;
        playerBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.playership);
        playerBitmap = Bitmap.createScaledBitmap(playerBitmap, (int) length, (int) height, false);
        // How fast is the spaceship in pixels per second
        shipSpeed = 350;
    }

    public RectF getRectF() {
        return rectF;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getLength() {
        return length;
    }

    public float getHeight() {
        return height;
    }

    public Bitmap getPlayerBitmap() {
        return playerBitmap;
    }

    // This method will be used to change/set if the ship is going left, right or nowhere
    public void setMovementState(int state) {
        shipMoving = state;
    }

    public void update(long fps) {
        if (shipMoving == LEFT) {
            x = x - shipSpeed / fps;
        }
        if (shipMoving == RIGHT) {
            x = x + shipSpeed / fps;
        }
        rectF.top = y;
        rectF.left = x;
        rectF.bottom = y + height;
        rectF.right = x + length;
    }
}
