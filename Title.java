package hons.segd.androidgame;


import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Title {

    //image and position
    private Bitmap image;
    private int x, y;

    //constructor; sets image
    public Title(Bitmap res){image=res;}

    //update; keeps position
    public void update()
    {
        x = y = 0;
    }

    //draw to screen
    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(image, x, y, null);
    }
}
