package hons.segd.androidgame;

//inherit from SurfaceView and implement the callback
//(lets us act like a standard android SurfaceView)

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {

    //application context
    private Context context;

    //dimensions
    public static final float WIDTH = 856;
    public static final float HEIGHT = 480;

    public static final float GAME_WIDTH = 1500;
    public static final float GAME_HEIGHT = 1500;

    //sprite offset (for follow cam)
    public int currentOffsetX = 0;
    public int currentOffsetY = 0;

    //level object for storage of level objects
    Level level;

    //game states
    private static final int GAME_TITLE_STATE = 1;
    private static final int GAME_PLAY_STATE = 2;
    private int gameState = 1;

    //class reference
    private MainThread thread;
    private Title title;
    //private Background background;
    private Bitmap background;
    private Player player;
    private Robot robot;

    //music / media
    private MediaPlayer titleBgm;
    private MediaPlayer levelBgm;

    //arrays / play data
    //private int level[] = {0,1,0,1,2,3};

    private static Vibrator vibrate;
    private boolean vibrateSwitch;

    //constructor; takes in device context
    public GamePanel(Context context)
    {
        //calls superclass method
        super(context);

        //create thread
        thread = new MainThread(getHolder(), this);

        //add the callback to the SurfaceHolder to intercept events
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        //Event Handler: if the surface changes, what do we do?
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        //Event Handler: when the surface is destroyed, what do we do?
        //retry flag
        boolean retry = true;
        //keep trying if flag set
        while(retry)
        {
            //try setting thread running to false
            //then join main execution thread
            try
            {
                thread.setRunning(false);
                thread.join();

                //stop all media
                titleBgm.stop();
                levelBgm.stop();

            } catch(InterruptedException e)
            {
                //if exception, print to console
                //set retry to false
                e.printStackTrace();
                retry = false;
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        //Event Handler: when the surface is created, what do we do?

        //device context needed for setting up particular methods
        context = getContext();

        //load title screen into Title class
        title = new Title(BitmapFactory.decodeResource(getResources(),
                            R.drawable.houserobot_title));

        //title screen bgm, load, create and play looping
        titleBgm = MediaPlayer.create(context, R.raw.title_bgm);
        titleBgm.start();
        titleBgm.setLooping(true);

        //level bgm, load, create, stop(looping)
        levelBgm = MediaPlayer.create(context,R.raw.level_bgm);
        levelBgm.setLooping(true);
        levelBgm.start();
        levelBgm.pause();

        vibrate = (Vibrator)context.getSystemService(context.VIBRATOR_SERVICE);
        vibrateSwitch=true;

        //create new background object
       // background =
         //       new Background(BitmapFactory.decodeResource(getResources(),R.drawable.bgtexture),
         //               (int)WIDTH,(int)HEIGHT, level);
        background = BitmapFactory.decodeResource(getResources(),R.drawable.gamebackground);


        //init level objects
        level = new Level();

        //wall collision elements
        level.CreateElement(0,0,28,1500,0,0,true);
        level.CreateElement(29,260,211,24,0,0,true);
        level.CreateElement(338,259,22,272,0,0,true);
        level.CreateElement(0,525,706,20,0,0,true);
        level.CreateElement(704,0,30,245,0,0,true);
        level.CreateElement(705,456,29,130,0,0,true);
        level.CreateElement(931,280,19,880,0,0,true);
        level.CreateElement(931,551,569,5,0,0,true);
        level.CreateElement(0,548,130,304,0,0,true);
        level.CreateElement(0,851,707,10,0,0,true);
        level.CreateElement(705,804,19,230,0,0,true);
        level.CreateElement(708,1246,19,199,0,0,true);
        level.CreateElement(933,1368,19,78,0,0,true);
        level.CreateElement(952,958,100,13,0,0,true);
        level.CreateElement(1201,955,231,25,0,0,true);
        level.CreateElement(0,0,1500,17,0,0,true);
        level.CreateElement(0,1443,1500,57,0,0,true);
        level.CreateElement(1469,0,31,552,0,0,true);
        level.CreateElement(1434,568,66,932,0,0,true);
        level.CreateElement(931,26,19,45,0,0,true);

        //pickups
        level.CreateElement(85,370,16,17,0,10,true,false,
                BitmapFactory.decodeResource(getResources(),R.drawable.waterglass),true,true, 1);
        level.CreateElement(1400,470,30,30,1,10,true,false,
                BitmapFactory.decodeResource(getResources(),R.drawable.hairdryer),true,true, 2);


        //create new player object
        player = new Player(BitmapFactory.decodeResource(getResources(),R.drawable.idle_left),
                BitmapFactory.decodeResource(getResources(),R.drawable.idle_right),
                BitmapFactory.decodeResource(getResources(),R.drawable.idle_down),
                BitmapFactory.decodeResource(getResources(),R.drawable.idle_up),
                BitmapFactory.decodeResource(getResources(),R.drawable.walking_left),
                BitmapFactory.decodeResource(getResources(),R.drawable.walking_right),
                BitmapFactory.decodeResource(getResources(),R.drawable.walking_down),
                BitmapFactory.decodeResource(getResources(),R.drawable.walking_up));

        //create new robot object
        robot = new Robot(782,422,player,
                BitmapFactory.decodeResource(getResources(),R.drawable.robot_idle_left),
                BitmapFactory.decodeResource(getResources(),R.drawable.robot_idle_right),
                BitmapFactory.decodeResource(getResources(),R.drawable.robot_idle_down),
                BitmapFactory.decodeResource(getResources(),R.drawable.robot_idle_up),
                BitmapFactory.decodeResource(getResources(),R.drawable.robot_walking_left),
                BitmapFactory.decodeResource(getResources(),R.drawable.robot_walking_right),
                BitmapFactory.decodeResource(getResources(),R.drawable.robot_walking_down),
                BitmapFactory.decodeResource(getResources(),R.drawable.robot_walking_up),
                BitmapFactory.decodeResource(getResources(),R.drawable.robot_idle_wet),
                BitmapFactory.decodeResource(getResources(),R.drawable.robot_idle_damaged),level);



        //we can safely start the game loop
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        //Event Handler: when we detect a touch on the screen, what do we do?

        //handy output
        System.out.println("X = " + event.getX());
        System.out.println("Y = " + event.getY());

        //if we are on the title screen
        if(gameState==GAME_TITLE_STATE)
        {
            //set to play state
            gameState = GAME_PLAY_STATE;
            //stop title music /  start level music
            titleBgm.stop();
            levelBgm.start();
        }

        //if we are in the player state
        if(gameState==GAME_PLAY_STATE)
        {
            //this will give us a number to scale by
            //so that the touch x,y co-ordinates
            //can be mapped to the logical x,y coordinates
            final float scaleFactorX = WIDTH/getWidth();
            final float scaleFactorY = HEIGHT/getHeight();
            //scale the x,y vars
            int newX = (int)(event.getX()*scaleFactorX);
            int newY = (int)(event.getY()*scaleFactorY);
            //handy printouts
            System.out.println("newX = " + newX);
            System.out.println("newY = " + newY);
            //set player destination
            player.setDestination(newX,newY);
            //if we are in the credits state
        }

        return super.onTouchEvent(event);
    }

    public void update()
    {
        //if in PLAY state
        if(gameState == GAME_PLAY_STATE)
        {
            //scroll background
            //background.update();
            //update player
            player.update(currentOffsetX, currentOffsetY, robot);
            player.CheckPlayerCollisions(level, robot);
            player.RestartCountdown(robot);

            //update robot
            robot.Update(currentOffsetX,currentOffsetY,player,level);
            robot.CheckRobotCollisions(player);

            //update level objects
            level.Update(player);
        }
    }

    //override draw method
    @Override
    public void draw(Canvas canvas)
    {
        //get scaling values for device screen
        final float scaleFactorX = getWidth()/WIDTH;
        final float scaleFactorY = getHeight()/HEIGHT;

        //if canvas is there
        if(canvas!=null)
        {
            //save current state before scaling
            final int savedState = canvas.save();
            canvas.scale(scaleFactorX, scaleFactorY);

            //state area
            if(gameState==GAME_TITLE_STATE)
            {
                //draw the title screen
                title.draw(canvas);
            }
            else if(gameState == GAME_PLAY_STATE)
            {
                //draw level background
                //background.draw(canvas);

                Rect sr = new Rect();
                sr.top = player.getY()-(int)(HEIGHT/2);
                sr.left = player.getX()-(int)(WIDTH/2);

                //if beyond level boundaries, move back
                if(sr.left > (GAME_WIDTH - WIDTH))
                    sr.left = ((int)GAME_WIDTH - (int)WIDTH);
                else if(sr.left < 0)
                    sr.left = 0;

                if(sr.top > (GAME_HEIGHT - HEIGHT))
                    sr.top = ((int)GAME_HEIGHT - (int)HEIGHT);
                else if(sr.top < 0)
                    sr.top = 0;

                sr.right = sr.left + (int)WIDTH;
                sr.bottom = sr.top + (int)HEIGHT;

                //offset vars for sprites
                currentOffsetX = sr.left;
                currentOffsetY = sr.top;

                Rect dr = new Rect();
                dr.left=0;
                dr.top=0;
                dr.right=(int)WIDTH;
                dr.bottom=(int)HEIGHT;


                canvas.drawBitmap(background,sr,dr,null);

                //draw player
                player.draw(canvas, currentOffsetX, currentOffsetY);

                //draw robot
                robot.draw(canvas, currentOffsetX, currentOffsetY);

                //draw level objects
                level.RenderElements(currentOffsetX,currentOffsetY);

                //create new paint object
                Paint paint = new Paint();

                //set colour and other params for hud
                paint.setColor(Color.argb(255,150,255,150));
                paint.setTextSize(30);
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setShadowLayer(5,1,1,Color.argb(100,255,255,255));

                //draw HUD
                if(player.GetMessageState()==1)
                {
                    canvas.drawText("You've been caught!", 10, 40, paint);
                    if(vibrateSwitch)
                    {
                        vibrate.vibrate(200);
                        vibrateSwitch=false;
                    }
                }
                else if(player.GetMessageState()==2)
                {
                    canvas.drawText("You've Escaped! You win!", 10, 40, paint);
                    if(vibrateSwitch)
                    {
                        vibrate.vibrate(500);
                        vibrateSwitch=false;
                    }
                }
                else
                {
                    //reset switch, ready for next use
                    if(!vibrateSwitch)
                        vibrateSwitch=true;
                }
                //canvas.drawText("Health: " + player.getHealth(),
                //        (WIDTH/4)+10,40,paint);
                //canvas.drawText("Lives: " + player.getLives(),
                //        10,HEIGHT-30,paint);

            }
            //restore to 'un-scaled' canvas state
            canvas.restoreToCount(savedState);
        }
    }
}
