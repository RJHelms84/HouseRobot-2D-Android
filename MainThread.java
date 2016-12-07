package hons.segd.androidgame;

//this class inherits from Thread
//this thread runs as the application does

import android.graphics.Canvas;
import android.provider.Settings;
import android.view.SurfaceHolder;

public class MainThread extends Thread {

    //Frames per second vars
    private int FPS = 30;
    private double averageFPS;
    //references to other classes
    private SurfaceHolder surfaceHolder;
    private GamePanel gamePanel;
    public static Canvas canvas;

    //flag for if thread running
    private boolean running;

    //constructor; gets surface holder and game panel
    public MainThread(SurfaceHolder surfaceHolder, GamePanel gamePanel)
    {
        super();
        //set references to class
        this.surfaceHolder = surfaceHolder;
        this.gamePanel = gamePanel;
    }

    //running method
    @Override
    public void run()
    {
        //vars for timing
        long startTime;
        long timeMillis;
        long waitTime;
        //initialise counter vars
        long totalTime = 0;
        int frameCount = 0;
        //our target time is relative to our FPS value
        long targetTime = 1000/FPS;

        //while we are running
        while(running)
        {
            //set start time to current system time (nanoseconds)
            startTime = System.nanoTime();
            //null the canvas object
            canvas = null;

            //try locking the canvas for pixel editing
            try
            {
                canvas = this.surfaceHolder.lockCanvas();
                //can we synchronise with the canvas?
                synchronized (surfaceHolder)
                {
                    //if so, call update and draw
                    this.gamePanel.update();
                    this.gamePanel.draw(canvas);
                }
            } catch (Exception e)
            {
                //otherwise do nothing; loop until unlocked
            }
            finally
            {
                //when complete and assuming we still have a canvas
                if(canvas!=null)
                {
                    //try to unlock it
                    try
                    {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch(Exception e)
                    {
                        //otherwise output error
                        e.printStackTrace();
                    }
                }
            }
            //work out time in milliseconds since startTime
            timeMillis = (System.nanoTime() - startTime) / 1000000;
            //our waitTime is how long to wait before next update
            waitTime = targetTime - timeMillis;

            try
            {
                //we'll wait until the next frame update
                this.sleep(waitTime);
            } catch(Exception e) {}

            //update our total time and framecount vars
            totalTime += System.nanoTime()-startTime;
            frameCount++;

            //if we have reached 30 frames
            if(frameCount == FPS)
            {
                //calculate the average fps
                averageFPS = 1000/((totalTime/frameCount)/1000000);
                //reset vars
                frameCount = 0;
                totalTime = 0;
                //print to console the average fps
                System.out.println(averageFPS);
            }
        }
    }
    //set running flags
    public void setRunning(boolean b)
    {
        running = b;
    }
}
