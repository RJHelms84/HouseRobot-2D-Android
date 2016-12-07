package hons.segd.androidgame;

import android.graphics.Bitmap;

public class SpriteAnimation {

    //bitmap array for each frame of animation
    private Bitmap[] frames;
    private int currentFrame;
    private long startTime;
    private long delay;
    //has animation completed once already?
    private boolean playedOnce;

    public SpriteAnimation()
    {
        startTime = System.nanoTime();
        currentFrame = 0;;
    }

    //sets the frames for the animation, taking in bitmap array
    public void setFrames(Bitmap[] frames)
    {
        //set to class frames
        this.frames = frames;
        //init vars
        //currentFrame = 0;
        //startTime = System.nanoTime();
    }

    public void update()
    {
        //checks time elapsed since last update
        long elapsed = (System.nanoTime()-startTime)/1000000;

        //if the time elapsed is greater than the delay we've set
        //(thus determining how fast the animation plays)
        if(elapsed>delay)
        {
            //update current frame and reset time
            currentFrame++;
            startTime = System.nanoTime();
        }

        //if the current frame is the same as the amount of frames
        if(currentFrame == frames.length)
        {
            //reset to first frame (0)
            //set playedOnce flag
            currentFrame = 0;
            playedOnce = true;
        }
    }

    //getters / setters
    public Bitmap getImage()
    {
        return frames[currentFrame];
    }
    public void setDelay(long d) { delay = d; }
    public void setFrame(int i) { currentFrame = i; }
    public int getFrame() { return currentFrame; }
    public boolean playedOnce() { return playedOnce; }

    public long GetDelay(){return delay;}

}
