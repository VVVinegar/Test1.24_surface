package com.example.vin.test124_surface;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

/**
 * MySurfaceView
 *
 * @author: Vin
 * @time: 2016/1/24 16:29
 */
public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback,Runnable {
    public final static String TAG = "MySurFaceView";

    private SurfaceHolder holder;

    private Canvas canvas;
    private Paint paint;

    private Thread thread;
    private Boolean flag;

    private float x, y;
    private float speedX, speedY;
    private float radius;
    private int color;

    private float rectX, rectY;
    private float rectWidth, rectHeight;

    private float rect1X, rect1Y;
    private float rect1Width, rect1Height;

    private Vector loca;    //位置
    private Vector speed;   //速度
    private Vector acc; //加速度

    public MySurfaceView(Context context) {
        super(context);
        init();
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 初始化
     */
    private void init(){
        holder = getHolder();
        holder.addCallback(this);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);

    }

    private void initGame(){
        x = 0;
        y = 0;
        radius = 50;
        speedX = 10;
        speedY = 50;
        color = Color.BLUE;

        loca = new Vector(100, 100);
        speed = new Vector(10, 20);
        acc = new Vector(0.001f, 0.005f);

        rectX = getWidth() / 2 - 100;
        rectY = getHeight() / 2 - 100;
        rectWidth = 200;
        rectHeight = 160;

        rect1X = 0;
        rect1Y = 0;
        rect1Width = 200;
        rect1Height = 200;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated");

        initGame();

        flag = true;

        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(TAG, "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed");
        flag = false;
    }

    private void myDraw(Canvas canvas){
        paint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

        paint.setColor(Color.BLACK);
        canvas.drawRect(rectX, rectY, rectX + rectWidth, rectY + rectHeight, paint);

        paint.setColor(color);
        canvas.drawCircle(x, y, radius, paint);
//        canvas.drawRect(rect1X, rect1Y, rect1X + rect1Width, rect1Y + rect1Height, paint);

        paint.setColor(Color.BLUE);
        canvas.drawCircle(loca.x, loca.y, radius, paint);

    }

    /**
     * 逻辑
     */
    private void logic(){

//        rect1X += speedX;
//        rect1Y += speedY;
//        if(rect1X + rect1Width >= getWidth() || rect1X < 0){
//            speedX = -speedX;
//        }
//        if(rect1Y + rect1Height >= getHeight() || rect1Y < 0){
//            speedY = -speedY;
//        }

        //圆移动
        x += speedX;
        y += speedY;

        if(x >= getWidth() || x < 0){
            speedX = -speedX;
        }
        if(y >= getHeight() || y < 0){
            speedY = -speedY;
        }

        //向量移动
        speed.limit(20);
        speed.add(acc);
        loca.add(speed);

        if(loca.x + radius >= getWidth() || loca.x - radius < 0){
            speed.x = -speed.x;
            acc.mult(-1);
        }
        if(loca.y + radius >= getHeight() || loca.y - radius < 0){
            speed.y = -speed.y;
            acc.mult(-1);
        }

        boolean isColl = rectAndCircle(rectX, rectY, rectWidth, rectHeight, x, y, radius);
        if(isColl){
            paint.setColor(Color.GREEN);
            canvas.drawCircle(100, 100, 100, paint);
        }
    }

    public boolean circleAndCircle(float circle1X, float circle1Y, float circle1R, float circle2X,
                                   float circle2Y, float circle2R){
        float dis = (float)Math.pow(circle2X - circle1X, 2) + (float)Math.pow(circle2Y - circle2Y, 2);

        if(dis > Math.pow(circle1R + circle2R, 2)){
            return false;
        }

        return true;
    }

    public boolean rectAndRect(float rect1X, float rect1Y, float rect1Width, float rect1Height,
                               float rectX, float rectY, float rectWidth, float rectHeight){
        if(rect1X + rect1Width < rectWidth){
            return false;
        }else if(rect1X > rectX + rectWidth){
            return false;
        }else if(rect1Y + rect1Height < rectY){
            return false;
        }else if(rect1Y > rectY + rectHeight) {
            return false;
        }
        return true;
    }

    public boolean rectAndCircle(float rectX, float rectY, float rectWidth, float rectHeight,
                                 float circleX, float circleY, float circleR){
        if(circleX + circleR < rectX){
            return false;
        }else if(circleX - circleR > rectX + rectWidth){
            return false;
        }else if(circleY + circleR < rectY){
            return false;
        }else if(circleY - circleR > rectX + rectHeight){
            return false;
        }else if(Math.pow(rectX - circleX, 2) + Math.pow(rectY - circleY, 2) > circleR){
            return false;
        }else if(Math.pow(rectX + rectWidth - circleX, 2) + Math.pow(rectY - circleY, 2) > circleR){
            return false;
        }else if(Math.pow(rectX - circleX, 2) + Math.pow(rectY + rectHeight - circleY, 2) > circleR){
            return false;
        }else if(Math.pow(rectX + rectWidth - circleX, 2) + Math.pow(rectY + rectHeight - circleY, 2) > circleR){
            return false;
        }
        return true;
    }

    public boolean onTouchEvent(MotionEvent event){
        int x = (int) event.getX();
        int y = (int) event.getY();

        int[] colors = new int[]{Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};

        color = colors[new Random().nextInt(colors.length)];

        radius = new Random().nextInt(30) + 50;

        //引力
        Vector touch = new Vector(x, y);
        acc = Vector.sub(touch, loca);
        acc.normalize();
        acc.mult(15f);

        return super.onTouchEvent(event);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event){

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void run() {
        while(flag){
            long start = System.currentTimeMillis();

            canvas = holder.lockCanvas();   //上锁
            if(null != canvas){
                myDraw(canvas);
                holder.unlockCanvasAndPost(canvas); //解锁
            }
            logic();

            long end = System.currentTimeMillis();

            if(end - start < 50){
                try {
                    thread.sleep(50 - (end - start));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}