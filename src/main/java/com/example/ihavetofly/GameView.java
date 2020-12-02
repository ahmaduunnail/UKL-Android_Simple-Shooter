package com.example.ihavetofly;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

public class GameView extends SurfaceView implements Runnable {

    private Thread thread;
    private boolean playing;
    private Background background1, background2;
    public static float screenRatioX, screenRatioY;
    private int ScreenX, ScreenY;
    private Paint paint;
    private List<com.example.ihavetofly.Bullet> bullets;
    private com.example.ihavetofly.Flight flight;

    public GameView(Context context, int screenX, int screenY) {
        super(context);

        this.ScreenX = screenX;
        this.ScreenY = screenY;
        screenRatioX = 1920f / screenX;
        screenRatioY = 1080f / screenY;

        background1 = new Background(screenX, screenY, getResources());
        background2 = new Background(screenX, screenY, getResources());
        
        flight = new Flight(this, screenY, getResources());
        
        bullets = new ArrayList<>();

        background2.x = screenX;

        paint = new Paint();
    }

    @Override
    public void run() {
        while (playing) {
            update();
            draw();
            sleep();
        }
    }

    private void update() {

        background1.x -= 10 * screenRatioX;
        background2.x -= 10 * screenRatioX;

        if (background1.x + background1.background.getWidth() < 0) {
            background1.x = ScreenX;
        }

        if (background2.x + background2.background.getWidth() < 0) {
            background2.x = ScreenX;
        }
         if (flight.isGoingUp)
            flight.y -= 30 * screenRatioY;
        else
            flight.y += 30 * screenRatioY;

        if (flight.y < 0)
            flight.y = 0;

        if (flight.y >= this.ScreenY - flight.height)
            flight.y = this.ScreenY - flight.height;
        
        List<com.example.ihavetofly.Bullet> trash = new ArrayList<>();

        for (com.example.ihavetofly.Bullet bullet : bullets) {

            if (bullet.x > this.ScreenX)
                trash.add(bullet);

            bullet.x += 50 * screenRatioX;
        }
        for (Bullet bullet : trash)
            bullets.remove(bullet);
    }

    private void draw() {

        if (getHolder().getSurface().isValid()) {
            Canvas canvas = getHolder().lockCanvas();
            canvas.drawBitmap(background1.background, background1.x, background1.y, paint);
            canvas.drawBitmap(background2.background, background2.x, background2.y, paint);
            
            canvas.drawBitmap(flight.getFlight(), flight.x, flight.y, paint);
            
            for (Bullet bullet : bullets)
                canvas.drawBitmap(bullet.bullet, bullet.x, bullet.y, paint);

            getHolder().unlockCanvasAndPost(canvas);
        }

    }

    private void sleep() {
        try {
            thread.sleep(17);
        } catch (InterruptedException e) {


        }
    }

    public void resume() {
        playing = true;
        thread = new Thread(this);
        thread.start();

    }

    public void pause() {
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    
     @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (event.getX() < this.ScreenX / 2) {
                    flight.isGoingUp = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                flight.isGoingUp = false;
                if (event.getX() > this.ScreenX / 2)
                    flight.toShoot++;
                break;
        }

        return true;
        
    }
        
         public void newBullet() {


        Bullet bullet = new Bullet(getResources());
        bullet.x = flight.x + flight.width;
        bullet.y = flight.y + (flight.height / 2);
        bullets.add(bullet);

    }
        
}
