package hons.segd.androidgame;


import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Background {

    //texture image received and final BG generated
    private Bitmap textureImage;//stores texture containing all tiles
    private Bitmap finalBg;//background created from tiles
    //position vars
    private int x, y;
    //scroll amount
    private int scrollX;
    //scroll count and bg width
    private int scrollCount, bgWidth;
    //screen dimensions
    private int screenWidth, screenHeight;
    //scroll finished flag
    private boolean finishedScroll;
    //tiles array and tile width
    private int[] bgTiles;
    private int tileWidth;

    //constructor; in comes texture, screen dimensions and tile array
    public Background(Bitmap res, int screenW, int screenH, int[] tiles)
    {
        //get texture image
        textureImage = res.copy(Bitmap.Config.ARGB_8888, true);
        res.recycle();
        //init vars
        scrollX = 1;
        y = 0;
        //get actual bg width (determined by tiles)
        tileWidth = 640;
        bgWidth = tileWidth*tiles.length;
        //screen dimensions
        screenWidth = screenW;
        screenHeight = screenH;
        //init vars
        finishedScroll = false;
        bgTiles = tiles;

        //create blank bitmap for final background
        Bitmap tempFinalBg = Bitmap.createBitmap(tileWidth*tiles.length,screenH,
                                                    Bitmap.Config.ARGB_8888);
        //temp bg canvas
        Canvas canvasTempBg = new Canvas(tempFinalBg);

        //loop through tiles
        for(int i = 0; i<tiles.length; i++)
        {
            //get copy of texture image
            Bitmap tempTile = textureImage.copy(Bitmap.Config.ARGB_8888, true);
            //crop tile positions from texture img
            int leftPos = tiles[i] * tileWidth;
            //handy outputs to console
            System.out.println("leftpos = " + leftPos);

            //tempTile cropped
            tempTile = Bitmap.createBitmap(tempTile,leftPos,0,tileWidth,screenH);

            //add temp tile to bg
            canvasTempBg.drawBitmap(tempTile,i*tileWidth,0,null);
        }
        textureImage.recycle();
        //set the temp canvas to final background
        finalBg = tempFinalBg.copy(Bitmap.Config.ARGB_8888,true);
    }

    //update
    public void update()
    {
        //if we haven't scrolled to end of screen, update scrollCount
        if(scrollCount+screenWidth < bgWidth)
        {
            scrollCount += scrollX;
        }
        else
        {
            //switch state here
            finishedScroll = true;
        }
    }

    //draw background based on scrollCount (negated to scroll left)
    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(finalBg,-scrollCount,y,null);
    }

    //getters and setters
    public boolean isFinishedScroll() {return finishedScroll;}
    public void setFinishedScroll(boolean b){finishedScroll = b;}

    public int getScrollCount(){return scrollCount;}
    public void setScrollCount(int i){scrollCount = i;}
}
