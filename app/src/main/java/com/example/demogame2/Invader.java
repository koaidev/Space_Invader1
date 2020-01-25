package com.example.demogame2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;

import java.util.Random;

public class Invader {
    private RectF rectF;
    private Bitmap invaderBitmap1, invaderBitmap2;
    Random generator = new Random();
    private float length, height, x, y, shipSpeed;
    public final int LEFT = 1;
    public final int RIGHT = 2;
    private int shipMoving = RIGHT;
    boolean isVisible;

    public Invader(Context context, int row, int column, int screenX, int screenY) {
        rectF = new RectF();
        length = screenX / 20;
        height = screenY / 20;
        isVisible = true;
        int padding = screenX / 25;
        x = column * (length + padding);
        y = row * (length + padding / 4);
        invaderBitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.invader1);
        invaderBitmap2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.invader2);
        invaderBitmap1 = Bitmap.createScaledBitmap(invaderBitmap1, (int) length, (int) height, false);
        invaderBitmap2 = Bitmap.createScaledBitmap(invaderBitmap2, (int) length, (int) height, false);
        shipSpeed = 40;
    }

    public void setVisible() {
        isVisible = false;
    }

    public RectF getRectF() {
        return rectF;
    }

    public float getLength() {
        return length;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Bitmap getInvaderBitmap1() {
        return invaderBitmap1;
    }

    public Bitmap getInvaderBitmap2() {
        return invaderBitmap2;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void update(long fps) {
        if (shipMoving == LEFT) {
            x = x - shipSpeed / fps;
        }
        if (shipMoving == RIGHT) {
            x = x + shipSpeed / fps;
        }
        rectF.left = x;
        rectF.top = y;
        rectF.right = x + length;
        rectF.bottom = y + height;
    }

    public void dropDownAndReverse() {
        if (shipMoving == LEFT) {
            shipMoving = RIGHT;
        } else {
            shipMoving = LEFT;
        }

        y = y + height;

        shipSpeed = shipSpeed * 1.18f;
    }

    public boolean takeAim(float shipPlayerX, float shipPlayerLength) {
        int randomNumber = 1;
        if (shipPlayerX + shipPlayerLength > x && shipPlayerLength + shipPlayerLength < x + length
                || shipPlayerX > x && shipPlayerX < x + length) {
            randomNumber = generator.nextInt(50);
            if(randomNumber==0){
                return true;
            }
        }
        randomNumber = generator.nextInt(2000);
        if(randomNumber==0){
            return true;
        }
        return false;
    }
}
