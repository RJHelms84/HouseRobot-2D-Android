package hons.segd.androidgame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

import static android.R.attr.level;

public class Player extends GameObject {

    //separate bitmaps for main animation and death animation
    private Bitmap playerTextureIdleLeft;
    private Bitmap playerTextureIdleRight;
    private Bitmap playerTextureIdleDown;
    private Bitmap playerTextureIdleUp;
    private Bitmap[] playerTextureWalkingLeft;
    private Bitmap[] playerTextureWalkingRight;
    private Bitmap[] playerTextureWalkingDown;
    private Bitmap[] playerTextureWalkingUp;

    private Bitmap playerTextureIdleCurrent;

    //offset calculation
    private int tempX;
    private int tempY;
    private boolean offsetSwitch;

    //coords for collision backtracking
    private int lastX;
    private int lastY;

    //animation
    private SpriteAnimation animation = new SpriteAnimation();
    private boolean isIdle;
    //score / lives / health
    private int score;
    private int lives;
    private int health;
    //destination position (on touch)
    private int destX;
    private int destY;
    //in position flag
    private boolean inPosition;
    //states
    private int sonicState;
    public static final int SONIC_PLAY = 0; //standard in-play state
    public static final int SONIC_INV = 1;  //temp invincibility after spawn
    public static final int SONIC_DIE = 2;  //death animation and drop
    public static final int SONIC_OVER = 3; //no more lives
    //timers and flags for temp invincibility
    private long invTimer;
    private long flashTimer;
    private boolean flashFlag;

    private int facingDirection;	//0=left,1=right,2=up,3=down
    private boolean isActivating; //is player activating an element?

    private int playerSpeed;

    private boolean isLocked; //lock player controls
    private int restartCounter;

    private int messageState;

    //constructor; takes in main spritesheet, dimensions, frames and death sprite
    public Player(Bitmap iLeft, Bitmap iRight, Bitmap iDown, Bitmap iUp,
                  Bitmap wLeft, Bitmap wRight, Bitmap wDown, Bitmap wUp)
    {
        isIdle = true;
        isLocked = false;
        restartCounter = 0;
        //starting position
        x = 393;
        y = 214;
        lastX = 0;
        lastY = 0;
        //init vars
        score = 0;
        lives = 3;
        health = 100;
        //init sprite dimensions
        height = 130;
        width = 69;

        facingDirection = 1; //0=left,1=right,2=up,3=down
        isActivating = false;

        //offset calculation
        tempX = 100;
        tempY = (int)GamePanel.HEIGHT/2;
        offsetSwitch = false;

        playerTextureIdleLeft = iLeft;
        playerTextureIdleRight = iRight;
        playerTextureIdleDown = iDown;
        playerTextureIdleUp = iUp;

        //current idle texture
        playerTextureIdleCurrent = playerTextureIdleDown;

        //create new Bitmap array for sprite animation
        playerTextureWalkingLeft = new Bitmap[4];

        //loop through frames to add to image array
        for(int i = 0; i < playerTextureWalkingLeft.length; i++)
        {
            playerTextureWalkingLeft[i] = Bitmap.createBitmap(wLeft, i*width, 0, width, height);
        }

        //create new Bitmap array for sprite animation
        playerTextureWalkingRight = new Bitmap[4];

        //loop through frames to add to image array
        for(int i = 0; i < playerTextureWalkingRight.length; i++)
        {
            playerTextureWalkingRight[i] = Bitmap.createBitmap(wRight, i*width, 0, width, height);
        }

        //create new Bitmap array for sprite animation
        playerTextureWalkingDown = new Bitmap[4];

        //loop through frames to add to image array
        for(int i = 0; i < playerTextureWalkingDown.length; i++)
        {
            playerTextureWalkingDown[i] = Bitmap.createBitmap(wDown, i*width, 0, width, height);
        }

        //create new Bitmap array for sprite animation
        playerTextureWalkingUp = new Bitmap[4];

        //loop through frames to add to image array
        for(int i = 0; i < playerTextureWalkingUp.length; i++)
        {
            playerTextureWalkingUp[i] = Bitmap.createBitmap(wUp, i*width, 0, width, height);
        }



        //set animation properties sending frames
        animation.setFrames(playerTextureWalkingLeft);
        animation.setDelay(200);

        //set flag for in position
        inPosition = true;

        //init state vars
        sonicState = SONIC_PLAY;
        flashFlag = false;
        flashTimer = System.nanoTime();

        playerSpeed = 6;

        messageState = 0;
    }

    //update method
    public void update(int offsetX, int offsetY, Robot robot)
    {
        if(x>730 && x<930 && y>(1420-height))
        {
            //you've won the game
            isLocked = true;
            isIdle = true;
            robot.ChangeState(new IdleState());

            messageState = 2;//"You've Escaped! You win!"

        }

        if(offsetSwitch)
        {
            tempX = destX + offsetX;
            tempY = destY + offsetY;
            offsetSwitch = false;
        }

        //if controls not locked
        if(!isLocked)
        {
            //check if in position (AABB point collision)
            //(can be a little buggy; could this be enhanced?)
            if(x > tempX+(playerSpeed*1.25) ||
                    (x+width) < tempX-(playerSpeed*1.25) ||
                    y > tempY+(playerSpeed*1.25) ||
                    (y+height) < tempY-(playerSpeed*1.25))
            {
                //do nothing
            }
            else
            {
                inPosition = true;
                isIdle = true;
            }

            //move logic here
            if(!inPosition)
            {
                isIdle = false;

                //get vector to destination point
                int xVel = tempX - x;
                int yVel = tempY - y;

                if(tempX > x && Math.abs(xVel) > Math.abs(yVel))
                {
                    animation.setFrames(playerTextureWalkingRight);
                    SetFacingDirection(1);
                }
                else if(tempX < x && Math.abs(xVel) > Math.abs(yVel))
                {
                    animation.setFrames(playerTextureWalkingLeft);
                    SetFacingDirection(0);
                }
                else if(tempY > y)
                {
                    animation.setFrames(playerTextureWalkingDown);
                    SetFacingDirection(3);
                }
                else
                {
                    animation.setFrames(playerTextureWalkingUp);
                    SetFacingDirection(2);
                }

                //normalize vector
                double length = Math.sqrt(xVel * xVel + yVel * yVel);
                if(length != 0)
                    length = 1/length;
                double nx = xVel * length;
                double ny = yVel * length;
                //update velocities by scaling vectors
                nx *= playerSpeed;
                ny *= playerSpeed;

                //store previous coords for collision backtracking
                lastX = x;
                lastY = y;

                //update sprite position
                x += (int) nx;
                y += (int) ny;
            }

            //update animation
            animation.update();

        }
    }//end update method

    //draw method
    public void draw(Canvas canvas, int offsetX, int offsetY)
    {
        int tempX = 0;
        int tempY = 0;

        tempX = x - offsetX;
        tempY = y - offsetY;

        if(isIdle)
            canvas.drawBitmap(playerTextureIdleCurrent,tempX,tempY,null);
        else
            canvas.drawBitmap(animation.getImage(),tempX,tempY,null);
    }

    public void LockControls(boolean val)
    {
        isLocked = val;
        isIdle = val;

        messageState = 1;//"You've been caught!"
    }

    public void RestartCountdown(Robot robot)
    {
        if(isLocked)
            restartCounter++;

        if(restartCounter>(30*5))//wait 5 seconds
        {
            restartCounter=0;
            RestartGame(robot);
        }

    }

    public void RestartGame(Robot robot)
    {
        messageState = 0;// turn off message

        isLocked=false;

        isIdle = true;
        isLocked = false;
        restartCounter = 0;
        //starting position
        x = 393;
        y = 214;
        lastX = 0;
        lastY = 0;
        //init vars
        score = 0;
        lives = lives--;
        health = 100;
        //set flag for in position
        inPosition = true;

        robot.setX(782);
        robot.setY(422);
        robot.ChangeState(new SandboxState());

    }

    public int GetMessageState(){return messageState;}

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
        if(health==0)
            sonicState=SONIC_DIE;
    }

    public int getSonicState() {
        return sonicState;
    }

    public void setSonicState(int sonicState) {
        this.sonicState = sonicState;
    }

    //set destination (where to go on touch)
    //takes in x,y of destination
    public void setDestination(int destX, int destY)
    {
        //if our touch is offscreen (How?)
        //set within boundaries
        if(destX>GamePanel.WIDTH)
            destX = (int)GamePanel.WIDTH;
        if(destY>GamePanel.HEIGHT)
            destY = (int)GamePanel.HEIGHT;

        //set to class destination
        this.destX = destX;
        this.destY = destY;
        //unset in position flag
        inPosition = false;

        offsetSwitch = true;
    }

    public int GetFacingDirection() {return facingDirection;}
    public boolean GetActivating() {return isActivating;}
    public void SetActivating(boolean state) {isActivating = state;}

    public void SetFacingDirection(int direction)//0=left,1=right,2=up,3=down
    {
        facingDirection=direction;

        if(direction==0)
            playerTextureIdleCurrent=playerTextureIdleLeft;
        else if(direction==1)
            playerTextureIdleCurrent=playerTextureIdleRight;
        else if(direction==2)
            playerTextureIdleCurrent=playerTextureIdleUp;
        else
            playerTextureIdleCurrent=playerTextureIdleDown;
    }

    public void CheckPlayerCollisions(Level level, Robot robot)
    {
        for(int i=0; i < level.GetNextElementPos(); i++)
        {
            if(level.GetElement(i).GetHasCollision())
            {
                if(Collision(this.getRectangle(), level.GetElement(i)))
                {
                    //reset player to last position
                    x = lastX;
                    y = lastY;

                    //set target to current x/y, to stop player movement
                    tempX = x;
                    tempY = y;
                    break;
                }
            }
        }

        if(Collision(this.getRectangle(), robot.getRectangle()))
        {
            //reset player to last position
            x = lastX;
            y = lastY;

            LockControls(true);
            robot.ChangeState(new IdleState());
        }
    }

    public boolean Collision(Rect sprite1, ElementObj sprite2)	//collision detection
    {
        Rect rect2 = new Rect();
        rect2.left = sprite2.GetX();
        rect2.top = sprite2.GetY();
        rect2.right = sprite2.GetX() + sprite2.GetWidth();
        rect2.bottom = sprite2.GetY() + sprite2.GetHeight();

        return sprite1.intersect(rect2);
    }

    public boolean Collision(Rect box, Rect rect2)	//collision detection
    {
        return box.intersect(rect2);
    }
}
