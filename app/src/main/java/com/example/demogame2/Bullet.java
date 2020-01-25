package com.example.demogame2;

import android.graphics.RectF;

public class Bullet {
    private RectF rectF;
    private float x, y;
    public final int UP = 1;
    public final int DOWN = -1;
    int heading = 0;
    float speed = 350;
    private int width = 1;
    private int height;
    private boolean isActive;

    public Bullet(int screenY) {
        height = screenY / 20;
        isActive = false;
        rectF = new RectF();
    }

    public RectF getRectF() {
        return rectF;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive() {
        isActive = false;
    }

    public float getImpactPointY() {
        if (heading == UP) {
            return y;
        } else {
            return y + height;
        }
    }

    public boolean shoot(float startX, float startY, int direction) {
        if (!isActive) {
            x = startX;
            y = startY;
            heading = direction;
            isActive = true;
            return true;
        }
        return false;
    }

    public void update(long fps) {
        if (heading == UP) {
            y = y - speed / fps;
        }
        if (heading == DOWN) {
            y = y + speed / fps;
        }
        rectF.left = x;
        rectF.top = y;
        rectF.right = x + width;
        rectF.bottom = y + height;
    }
}
