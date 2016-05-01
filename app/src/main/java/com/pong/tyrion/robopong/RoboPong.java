package com.pong.tyrion.robopong;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.view.ViewGroup.*;
import android.view.*;
import android.graphics.drawable.*;
import android.graphics.*;
import android.media.MediaPlayer;

public class RoboPong extends Activity
{
    MediaPlayer mPlayer, mPlayer2;
    LinearLayout mLinearLayout;
    ImageView robot, explosion, topPaddle, bottomPaddle;
    TextView myText, myText2, myText3;
    Rect robotRect, imgTransRect, robotTransRect, collideRect;
    boolean forward, down = true;
    int screenWidth, screenHeight, topScore = 0, bottomScore = 0;
    boolean exploded = false;
    boolean held = true;
    int speed = 1;
    int hitCounter = 1;
    //int explodedFrames = 0;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {

        public void run() {

            if(held)
            {
                handler.postDelayed(this, 1);
                return;
            }

            int cacheBottom = bottomScore;
            int cacheTop = topScore;

            if(forward)
            {
                robot.setX(robot.getX()+(2*speed+2));
            }
            else
            {
                robot.setX(robot.getX()-(2*speed+2));
            }

            if(down)
            {
                robot.setY(robot.getY()+(2*speed+2));
            }
            else
            {
                robot.setY(robot.getY()-(2*speed+2));
            }

            if(forward && (robotRect.right+robot.getX() >= screenWidth))
                forward = false;
            else if(! forward && (robotRect.left+robot.getX() <= 0))
                forward = true;

            if(down && (robotRect.bottom+robot.getY() >= (screenHeight)))
            {
                topScore++;
                down = false;
                robot.setX(bottomPaddle.getX()+(bottomPaddle.getWidth()/2)-18);
                robot.setY(bottomPaddle.getY()-35);
                held = true;
                speed=1;
                hitCounter=1;
                mPlayer2.start();
            }
            else if(! down && (robotRect.top+robot.getY() <= (0-36)))
            {
                bottomScore++;
                down = true;
                robot.setX(topPaddle.getX()+(topPaddle.getWidth()/2)-18);
                robot.setY(topPaddle.getY()+18);
                held=true;
                speed=1;
                hitCounter=1;
                mPlayer2.start();
            }

            if(down && checkForCollisions(bottomPaddle))
            {
                down = false;
                hitCounter++;
                mPlayer.start();

                if(hitCounter%5 == 0)
                    speed++;
            }

            if(! down && checkForCollisions(topPaddle))
            {
                down = true;
                hitCounter++;
                mPlayer.start();

                if(hitCounter%5 == 0)
                    speed++;
            }

            if((cacheTop != topScore) || (cacheBottom != bottomScore))
                myText.setText("YELLOW = "+topScore+"     RED = "+bottomScore);

            handler.postDelayed(this, 1);
        }
    };

    private boolean checkForCollisions(ImageView image)
    {
        boolean pixelCollide = false;
        //int boomX = 0, boomY = 0;

        Rect iRect = image.getDrawable().getBounds();

        Bitmap imgBit = ((BitmapDrawable)image.getDrawable()).getBitmap();
        Bitmap imgBit2 = ((BitmapDrawable)robot.getDrawable()).getBitmap();

        imgTransRect.left = (int)(iRect.left+image.getX());
        imgTransRect.top = (int)(iRect.top+image.getY());
        imgTransRect.right = (int)(iRect.right+image.getX());
        imgTransRect.bottom = (int)(iRect.bottom+image.getY());

        robotTransRect.left = (int)(robotRect.left+robot.getX());
        robotTransRect.top = (int)(robotRect.top+robot.getY());
        robotTransRect.right = (int)(robotRect.right+robot.getX());
        robotTransRect.bottom = (int)(robotRect.bottom+robot.getY());

        // Collision Detection
        //String boundsCollision = "F";
        //String pixelCollision = "F";
        if(imgTransRect.intersect(robotTransRect))
        {
            //boundsCollision = "T";

            collideRect.left = Math.max(imgTransRect.left, robotTransRect.left);
            collideRect.top = Math.max(imgTransRect.top, robotTransRect.top);
            collideRect.right = Math.min(imgTransRect.right, robotTransRect.right);
            collideRect.bottom = Math.min(imgTransRect.bottom, robotTransRect.bottom);

            for (int i = collideRect.left; i < collideRect.right; i++)
            {
                for (int j = collideRect.top; j < collideRect.bottom; j++)
                {
                    int imgPix = imgBit.getPixel(i-(int)image.getX(), j-(int)image.getY());
                    int imgPix2 = imgBit2.getPixel(i-(int)robot.getX(), j-(int)robot.getY());
                    //boomX = i-(int)image.getX();
                    //boomY = j-(int)image.getY();

                    if( (imgPix != Color.TRANSPARENT) && (imgPix2 != Color.TRANSPARENT)) {
                        //pixelCollision = "BOOM";
                        pixelCollide = true;
                        exploded = true;
                        break;
                    }
                }

                if(pixelCollide)
                    break;
            }

            if(!pixelCollide)
                exploded = false;
        }
        else
        {
            exploded = false;
        }

      /*
      if(exploded)
      {
        //explosion.setX(robot.getX()-5);
        //explosion.setY(robot.getY());
        explosion.setX(boomX);
        explosion.setY(boomY);
        explosion.setAlpha(1.0f);
      }
      else if(! exploded)
      {
        if(explodedFrames >= 20)
        {
            explosion.setAlpha(0.0f);
            explodedFrames = 0;
        }
        else
            explodedFrames++;
      }
      */

        //myText3.setText("Bounds Collision: "+boundsCollision+" Pixel Collision: "+pixelCollision);
        return exploded;
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        myText = new TextView(this);
        myText2 = new TextView(this);
        myText3 = new TextView(this);
        myText.setText("YELLOW = "+topScore+"     RED = "+bottomScore);
        myText2.setText("myText2");
        myText3.setText("myText3");

        // Create a LinearLayout in which to add the ImageView
        mLinearLayout = new LinearLayout(this);
        mLinearLayout.setOrientation(LinearLayout.VERTICAL);

        robot = new ImageView(this);
        robot.setImageResource(R.drawable.ic_launcher_small);
        robot.setAdjustViewBounds(true); // set the ImageView bounds to match the Drawable's dimensions
        robot.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        explosion = new ImageView(this);
        explosion.setImageResource(R.drawable.explosion);
        explosion.setAdjustViewBounds(true); // set the ImageView bounds to match the Drawable's dimensions
        explosion.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        topPaddle = new ImageView(this);
        topPaddle.setImageResource(R.drawable.top_paddle_new);
        topPaddle.setAdjustViewBounds(true); // set the ImageView bounds to match the Drawable's dimensions
        topPaddle.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        bottomPaddle = new ImageView(this);
        bottomPaddle.setImageResource(R.drawable.bottom_paddle_new);
        bottomPaddle.setAdjustViewBounds(true); // set the ImageView bounds to match the Drawable's dimensions
        bottomPaddle.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        mLinearLayout.addView(myText);
        //mLinearLayout.addView(myText2);
        //mLinearLayout.addView(myText3);
        mLinearLayout.addView(topPaddle);
        mLinearLayout.addView(robot);
        //mLinearLayout.addView(explosion);
        mLinearLayout.addView(bottomPaddle);
        setContentView(mLinearLayout);

        robotRect = robot.getDrawable().getBounds();
        imgTransRect = new Rect();
        robotTransRect = new Rect();
        collideRect = new Rect();

        explosion.setAlpha(0.0f);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        //myText.setText("screenWidth="+screenWidth+" screenHeight="+screenHeight);

        topPaddle.setX(0);
        topPaddle.setY(140);
        bottomPaddle.setX(0);
        bottomPaddle.setY(825);

        myText.setRotation(90.0f);
        myText.setX(300);
        myText.setY(745);

        // put it initially in the held position
        robot.setX(topPaddle.getX()+(topPaddle.getWidth()/2)+8);
        robot.setY(topPaddle.getY());

        mPlayer=MediaPlayer.create(this,R.raw.pong);
        mPlayer.setVolume(0.5f,0.5f);

        mPlayer2=MediaPlayer.create(this,R.raw.missed);
        mPlayer2.setVolume(0.2f,0.2f);

        handler.postDelayed(runnable, 1000);
    }

    public boolean onTouchEvent(MotionEvent event)
    {
        // Single player OR first touch in multi player game
        if(event.getPointerCount() > 0)
        {
            // Move the paddle and held image for the main touch point.  See if
            // the top half was touch or the bottom half
            if(event.getY(0) < screenHeight / 2)
            {
                // Top half was touched - move the top paddle
                topPaddle.setX(event.getX(0)-(topPaddle.getWidth()/2));

                if(held)
                {
                    // Only move the image on the top paddle if the image is held
                    // by the top
                    if(robot.getY() < screenHeight / 2)
                    {
                        robot.setX(event.getX(0)-(topPaddle.getWidth()/2)+8);

                        // We can immediately determine held status for primary touch
                        // point without getting the index
                        if(event.getActionMasked() == MotionEvent.ACTION_UP)
                            held=false;
                    }
                }
            }
            else  // Bottom half was touched - move the bottom paddle
            {
                bottomPaddle.setX(event.getX(0)-(bottomPaddle.getWidth()/2));

                // Only move the image on the bottom paddle if the image is held
                // by the bottom
                if(held)
                {
                    if(robot.getY() > screenHeight / 2)
                    {
                        robot.setX(event.getX(0)-(bottomPaddle.getWidth()/2)+8);

                        if(event.getActionMasked() == MotionEvent.ACTION_UP)
                            held=false;
                    }
                }
            }
        }

        // Second touch in a multi player game
        if(event.getPointerCount() > 1)
        {
            // Move the paddle and held image for the second touch point.  See if
            // the top half was touch or the bottom half
            if(event.getY(1) < screenHeight / 2)
            {
                // Top half was touched - move the top paddle
                topPaddle.setX(event.getX(1)-(topPaddle.getWidth()/2));

                if(held)
                {
                    // Only move the image on the top paddle if the image is held
                    // by the top
                    if(robot.getY() < screenHeight / 2)
                    {
                        robot.setX(event.getX(1)-(topPaddle.getWidth()/2)+8);
                    }
                }
            }
            else  // Bottom half was touched - move the bottom paddle
            {
                bottomPaddle.setX(event.getX(1)-(bottomPaddle.getWidth()/2));

                if(held)
                {
                    // Only move the image on the bottom paddle if the image is held
                    // by the bottom
                    if(robot.getY() > screenHeight / 2)
                    {
                        robot.setX(event.getX(1)-(bottomPaddle.getWidth()/2)+8);
                    }
                }
            }

            // If we are held, see if this is a secondary mouse event and we need
            // to evaluate conditions for a secondary release
            if(held)
            {
                int upIndex = -1;
                if((event.getActionMasked() == MotionEvent.ACTION_POINTER_UP))
                {
                    // It is indeed a secondary release, so find which mouse was
                    // released so we can get the location
                    upIndex = event.getActionIndex();
                }

                if(upIndex >= 0)
                {
                    // See if we released the top paddle or the bottom paddle.
                    // Only release the image if the image side and touch side match
                    if(event.getY(upIndex) < screenHeight / 2)
                    {
                        if(robot.getY() < screenHeight / 2)
                            held=false;
                    }
                    else
                    {
                        if(robot.getY() > screenHeight / 2)
                            held=false;
                    }
                }
            }
        }

        return true;
    }
}
