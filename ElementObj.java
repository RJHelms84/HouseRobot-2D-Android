package hons.segd.androidgame;


import android.graphics.Bitmap;
import android.graphics.Rect;

import static hons.segd.androidgame.MainThread.canvas;

public class ElementObj extends GameObject {

    private int facingDirection; //0=left,1=right,2=up,3=down
    private int activationDistance; //distance for this element to be activated
    boolean activationState; //set to true when player has activated it
    private int vectorPos;

    private Rect srcRect;
    private Bitmap[] currentTexture;
    private Bitmap idleTexture;
    private boolean isRendered;
    private boolean hasCollision;
    //private boolean shortAnimMode;
    private boolean animMode;
    private int animID;
    private boolean removeOnActivate;
    private int uniqueID; //0 means no unique id, so id's start at 1

    private SpriteAnimation animation = new SpriteAnimation();

    public ElementObj()
    {
        facingDirection = 0; //0=left,1=right,2=up,3=down
        activationDistance = 0;
        activationState = false;
        vectorPos = 0;

        isRendered = false;
        hasCollision = false;
        //shortAnimMode = false;
        animMode = false;
        animID = 0;
        removeOnActivate = false;
        uniqueID=0;

        currentTexture = null;

        x=0;
        y=0;
        width=0;
        height=0;
    }

    public void SetX(int s) {this.x = s;}
    public void SetY(int s) {this.y = s;}
    public void SetWidth(int w) {this.width = w;}
    public void SetHeight(int h) {this.height = h;}

    public int GetVectorPos() {return vectorPos;}
    public void SetVectorPos(int p) {vectorPos = p;}

    public int GetFacingDirection() {return facingDirection;}
    public void SetFacingDirection(int d) {facingDirection = d;}

    public int GetActivationDistance() {return activationDistance;}
    public void SetActivationDistance(int d) {activationDistance = d;}

    public boolean GetActivationState() {return activationState;}
    public void SetActivationState(boolean s){activationState = s;}

    //check if player is in activation distance
    public boolean IsPlayerTouching(int playerX, int playerY, int playerWidth, int playerHeight)
    {
        if((playerX > (x+width + activationDistance))
                || (playerX+playerWidth < (x-activationDistance))
                || (playerY > (y+height + activationDistance))
                || (playerY+playerHeight < (y-activationDistance)))
            return false;
        else return true;
    }

    public boolean GetRendered() {return isRendered;}
    public void SetRendered(boolean isR) {isRendered = isR;}

    public boolean GetHasCollision() {return hasCollision;}
    public void SetHasCollision(boolean value) {hasCollision=value;}

    public boolean GetRemoveOnActivate(){return removeOnActivate;}
    public void SetRemoveOnActivate(boolean newVal){removeOnActivate=newVal;}

    public void StartAnim(Bitmap tex) //runs looped anim
    {
        animMode = true;

        //create new Bitmap array for sprite animation
        currentTexture = new Bitmap[4];

        //loop through frames to add to image array
        for(int i = 0; i < currentTexture.length; i++)
        {
            currentTexture[i] = Bitmap.createBitmap(tex, i*width, 0, width, height);
        }

        //set animation properties sending frames
        animation.setFrames(currentTexture);
        animation.setDelay(500);
    }
    //public void PlayAnim(); //runs non-looped anim

    public void StopAnim()
    {
        animMode = false;
    }

    public void Update()
    {
        if(animMode)
            //update animation
            animation.update();
    }

    public void Draw(int currentOffsetX,int currentOffsetY)
    {
        if(isRendered)
        {
            int tempX = 0;
            int tempY = 0;

            tempX = x - currentOffsetX;
            tempY = y - currentOffsetY;

            if(!animMode)
                canvas.drawBitmap(idleTexture,tempX,tempY,null);
            else
                canvas.drawBitmap(animation.getImage(),tempX,tempY,null);
        }
    }

    public int GetX() {return this.x;}
    public int GetY() {return this.y;}
    public int GetWidth() {return this.width;}
    public int GetHeight() {return this.height;}
    public boolean GetIsAnimMode() {return animMode;}

    public void SetSpriteX(int newX) {this.x = newX;}
    public void SetSpriteY(int newY) {this.y = newY;}

    public void SetTexture(Bitmap tex)
    {
        idleTexture = tex;
    }

    public void SetEnabled(boolean val)
    {
        hasCollision = val;
        isRendered = val;
    }

    public int GetUniqueID(){return uniqueID;}
    public void SetUniqueID(int id){uniqueID=id;}

}
