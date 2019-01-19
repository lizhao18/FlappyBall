package com.example.lizhao.flappy;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    private int Width;
    private int Height;
    //小球的变量
    float ball_size = 16;  //小球的大小
    private float ball_velocity_down;  //小球的下降速度
    private float ball_velocity_up;  //小球的上升速度
    float ballX;  //小球的X坐标
    float ballY;  //小球的Y坐标
    //柱子的变量
    private float pillar1_width = 80;
    float pillar1_height;
    private float pillar2_width = 80;
    float pillar2_height;
    float pillar1X;  //柱子1的X坐标
    float pillar1Y;  //柱子1的Y坐标
    float pillar2X;  //柱子2的X坐标
    float pillar2Y;  //柱子2的Y坐标
    private float pillar_velocity = 5;

    int score = 0;  //分数：小球通过的柱子数

    boolean gameOver = false;  //游戏结束标志

    MyGameView myGameView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //使程序全屏运行
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //获取游戏视图的实例
        myGameView = new MyGameView(this);
        setContentView(myGameView);
        //获取窗口的一个管理器(这是用来获取屏幕的宽和高的)
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        //获取屏幕的宽和高
        Width = metrics.widthPixels;
        Height = metrics.heightPixels;
        play();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0x123){
                myGameView.invalidate();
            }
        }
    };

    public void play(){
        gameOver = false;
        //设置两根柱子的初始位置
        pillar1X = Width - pillar1_width;
        pillar1Y = 0;
        pillar2X = Width - pillar2_width;
        pillar2Y = Height;
        pillar1_height = Height/2 - 200;
        pillar2_height = Height - pillar1_height - 200;
        score = 0;
        pillar_velocity = 5;

        ball_velocity_down = 0;
        ball_velocity_up = 90;

        ballX = 85;
        ballY = Height/2;

        //监听触摸事件，在触摸事件对应的方法里改变部分参数
        myGameView.setOnTouchListener(touch);

        handler.sendEmptyMessage(0x123);

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //设置小球和柱子的坐标变化
                ball_velocity_down = ball_velocity_down + 0.1f;
                ballY = ballY + ball_velocity_down;
                pillar1X = pillar1X - pillar_velocity;
                pillar2X = pillar2X - pillar_velocity;
                //如果柱子碰到屏幕边缘
                if(pillar1X <= 0){
                    pillar1X = Width - pillar1_width;
                    pillar2X = Width - pillar1_width;
                }
                //判断小球是否碰到屏幕边缘
                if(ballY >= Height){
                    gameOver = true;
                    timer.cancel();
                }
                //判断小球是否碰到柱子
                if(ballX >= pillar1X){
                    if (ballY < pillar1_height || ballY > pillar2_height){
                        gameOver = true;
                        timer.cancel();
                    }
                }
                //判断是否得分
                if(ballX > pillar1X + pillar1_width){
                    score = score +1;
                }
                handler.sendEmptyMessage(0x123);
            }
        },0,15);//15ms执行上面的内容（每隔15ms通知一次myGameView去画图）
    }



    View.OnTouchListener touch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    //手指点击事件
                    ballY = ballY - ball_velocity_up;
                    ball_velocity_down = 0;
                    handler.sendEmptyMessage(0x123);
                    break;
            }
            return true;
        }
    };

    class MyGameView extends View{

        Paint paint = new Paint();

        public MyGameView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            //设置画笔的一些属性
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true);  //设置抗锯齿
            if(gameOver){
                //执行游戏结束后的效果
                paint.setColor(Color.RED);
                paint.setTextSize(80);
                canvas.drawText("GAME OVER",Width/2 - 200,Height/2-120,paint);
                canvas.drawText("SCORE:"+score,Width/2 - 140,Height/2-20,paint);
                canvas.drawText("PLAY AGAIN",Width/2 - 200,Height/2+80,paint);
                this.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()){
                            case MotionEvent.ACTION_DOWN:
                                play();
                                break;
                        }
                        return true;
                    }
                });
            }else{
                //执行游戏进行中的效果
                paint.setColor(Color.RED);
                paint.setTextSize(80);
                canvas.drawText("score:"+score,Width/2 - 10,80,paint);
                canvas.drawCircle(ballX,ballY,ball_size,paint);
                //绘制上柱子
                paint.setColor(Color.BLUE);
                canvas.drawRect(pillar1X,pillar1Y,pillar1X+pillar1_width,pillar1Y+pillar1_height,paint);
                //绘制下柱子
                canvas.drawRect(pillar2X,pillar2Y-pillar2_height,pillar2X+pillar2_width,pillar2Y+pillar2_height,paint);
            }
        }
    }
}
