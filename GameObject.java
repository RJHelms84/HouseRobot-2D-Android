package hons.segd.androidgame;

import android.graphics.Rect;

public abstract class GameObject {

    //common inherited vars; position and dimensions
    protected int x;
    protected int y;
    protected int width;
    protected int height;

    //get rectangle based on position / dimensions (for collision mainly)
    public Rect getRectangle() { return new Rect(x,y,x+width,y+height);}

    public int getY() {return y;}
    public int getX() {return x;}
    public void setX(int newX){x = newX;}
    public void setY(int newY){y = newY;}

}
