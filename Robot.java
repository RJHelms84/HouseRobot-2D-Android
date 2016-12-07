package hons.segd.androidgame;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.Vector;

import static android.R.attr.level;
import static java.lang.Math.sqrt;

public class Robot extends GameObject {

    State currentState;

    private int playerX;
    private int playerY;

    private NavGraph robotNavGraph;
    private int startGraphIndex;
    private Vector<Integer> pathToTarget;
    private int pathListSize;
    private int pathListCounter;
    private boolean navSwitch; //switch on to begin moving to new nav node
    private int currentSandboxLocation;
    private int currentSandboxTarget;
    private boolean atSandboxPos;
    private boolean canSeePlayer;

    private int lastPositionX;
    private int lastPositionY;

    private Rect srcRect;

    //separate bitmaps for main animation and death animation
    private Bitmap playerTextureIdleLeft;
    private Bitmap playerTextureIdleRight;
    private Bitmap playerTextureIdleDown;
    private Bitmap playerTextureIdleUp;
    private Bitmap[] playerTextureWalkingLeft;
    private Bitmap[] playerTextureWalkingRight;
    private Bitmap[] playerTextureWalkingDown;
    private Bitmap[] playerTextureWalkingUp;

    private Bitmap wetTexture;
    private Bitmap damagedTexture;

    private Bitmap playerTextureIdleCurrent;

    //animation
    private SpriteAnimation animation = new SpriteAnimation();

    private boolean isIdle;
    private int facingDirection;	//0=left,1=right,2=up,3=down
    private boolean isActivating; //is player activating an element?

    private boolean isRendered;
    private boolean animMode;
    private boolean staticAnimMode;
    private int animID;
    private boolean isLocked; //is robot's controls locked?
    private boolean isDead;
    private boolean isWet;
    private boolean isDamaged;

    private int robotSpeed;

    private boolean seekStart;
    private int seekWaitCounter;


    public Robot(int x, int y, Player p1,
                 Bitmap iLeft, Bitmap iRight, Bitmap iDown, Bitmap iUp,
                 Bitmap wLeft, Bitmap wRight, Bitmap wDown, Bitmap wUp,
                 Bitmap wet, Bitmap damaged, Level level)
    {

        //set starting state
        currentState = new SandboxState();

        playerX = p1.getX();
        playerY = p1.getY();

        this.x = x;
        this.y = y-130;
        width = 69;
        height = 130;

        pathToTarget = new Vector<Integer>();

        facingDirection = 3;
        isIdle = false;
        isRendered = true;
        animMode = false;
        staticAnimMode = false;
        animID = 0;
        isLocked = false;
        isDead = false;
        isWet = false;

        robotSpeed = 4;

        //put state init here

        //create navgraph and populate with nodes and edges
        robotNavGraph = new NavGraph();

        //add nodes
        robotNavGraph.AddNode(new GraphNode(0,64,158)); //0
        robotNavGraph.AddNode(new GraphNode(1,66,215));//1
        robotNavGraph.AddNode(new GraphNode(2,258,215));//2
        robotNavGraph.AddNode(new GraphNode(3,258,478));//3
        robotNavGraph.AddNode(new GraphNode(4,74,478));//4
        robotNavGraph.AddNode(new GraphNode(5,393,214));//5
        robotNavGraph.AddNode(new GraphNode(6,603,213));//6
        robotNavGraph.AddNode(new GraphNode(7,393,486));//7
        robotNavGraph.AddNode(new GraphNode(8,607,491));//8
        robotNavGraph.AddNode(new GraphNode(9,608,423));//9
        robotNavGraph.AddNode(new GraphNode(10,782,422));//10
        robotNavGraph.AddNode(new GraphNode(11,784,253));//11
        robotNavGraph.AddNode(new GraphNode(12,987,251));//12
        robotNavGraph.AddNode(new GraphNode(13,988,498));//13
        robotNavGraph.AddNode(new GraphNode(14,1338,498));//14
        robotNavGraph.AddNode(new GraphNode(15,1338,251));//15
        robotNavGraph.AddNode(new GraphNode(16,769,788));//16
        robotNavGraph.AddNode(new GraphNode(17,175,788));//17
        robotNavGraph.AddNode(new GraphNode(18,775,1221));//18
        robotNavGraph.AddNode(new GraphNode(19,577,1221));//19
        robotNavGraph.AddNode(new GraphNode(20,577,1109));//20
        robotNavGraph.AddNode(new GraphNode(21,63,1109));//21
        robotNavGraph.AddNode(new GraphNode(22,63,1408));//22
        robotNavGraph.AddNode(new GraphNode(23,580,1408));//23
        robotNavGraph.AddNode(new GraphNode(24,775,1345));//24
        robotNavGraph.AddNode(new GraphNode(25,993,1345));//25
        robotNavGraph.AddNode(new GraphNode(26,993,1230));//26
        robotNavGraph.AddNode(new GraphNode(27,1069,1230));//27
        robotNavGraph.AddNode(new GraphNode(28,1312,1230));//28
        robotNavGraph.AddNode(new GraphNode(29,1318,1394));//29
        robotNavGraph.AddNode(new GraphNode(30,993,1394));//30
        robotNavGraph.AddNode(new GraphNode(31,1068,916));//31
        robotNavGraph.AddNode(new GraphNode(32,1311,916));//32
        robotNavGraph.AddNode(new GraphNode(33,1311,790));//33
        robotNavGraph.AddNode(new GraphNode(34,990,790));//34
        robotNavGraph.AddNode(new GraphNode(35,990,916));//35
        //extra nodes added to break up long edges
        robotNavGraph.AddNode(new GraphNode(36,254,331));//36, between 2 and 3
        robotNavGraph.AddNode(new GraphNode(37,397,345));//37, between 5 and 7
        robotNavGraph.AddNode(new GraphNode(38,602,315));//38, between 6 and 9
        robotNavGraph.AddNode(new GraphNode(39,1150,252));//39, between 12 and 15
        robotNavGraph.AddNode(new GraphNode(40,1337,362));//40, between 15 and 14
        robotNavGraph.AddNode(new GraphNode(41,1160,500));//41, between 13 and 14
        robotNavGraph.AddNode(new GraphNode(42,983,375));//42, between 12 and 13
        robotNavGraph.AddNode(new GraphNode(43,769,597));//43, between 10 and 16
        robotNavGraph.AddNode(new GraphNode(44,573,790));//44, between 16 and 17
        robotNavGraph.AddNode(new GraphNode(45,381,790));//45, between 16 and 17
        robotNavGraph.AddNode(new GraphNode(46,772,909));//46, between 16 and 18
        robotNavGraph.AddNode(new GraphNode(47,772,1073));//47, between 16 and 18
        robotNavGraph.AddNode(new GraphNode(48,314,1107));//48, between 20 and 21
        robotNavGraph.AddNode(new GraphNode(49,67,1240));//49, between 21 and 22
        robotNavGraph.AddNode(new GraphNode(50,324,1405));//50, between 22 and 23
        robotNavGraph.AddNode(new GraphNode(51,1153,1395));//51, between 29 and 30
        robotNavGraph.AddNode(new GraphNode(52,1145,790));//52, between 33 and 34
        robotNavGraph.AddNode(new GraphNode(53,503,347));//53
        robotNavGraph.AddNode(new GraphNode(54,1164,372));//54
        robotNavGraph.AddNode(new GraphNode(55,330,1250));//55
        //add edges
        robotNavGraph.AddEdge(new GraphEdge(0,1));
        robotNavGraph.AddEdge(new GraphEdge(1,2));
        robotNavGraph.AddEdge(new GraphEdge(2,36));
        robotNavGraph.AddEdge(new GraphEdge(36,3));
        robotNavGraph.AddEdge(new GraphEdge(3,4));
        robotNavGraph.AddEdge(new GraphEdge(2,5));
        robotNavGraph.AddEdge(new GraphEdge(5,6));
        robotNavGraph.AddEdge(new GraphEdge(5,37));
        robotNavGraph.AddEdge(new GraphEdge(37,7));
        robotNavGraph.AddEdge(new GraphEdge(6,38));
        robotNavGraph.AddEdge(new GraphEdge(38,9));
        robotNavGraph.AddEdge(new GraphEdge(7,8));
        robotNavGraph.AddEdge(new GraphEdge(8,9));
        robotNavGraph.AddEdge(new GraphEdge(9,10));
        robotNavGraph.AddEdge(new GraphEdge(10,11));
        robotNavGraph.AddEdge(new GraphEdge(11,12));
        robotNavGraph.AddEdge(new GraphEdge(12,42));
        robotNavGraph.AddEdge(new GraphEdge(42,13));
        robotNavGraph.AddEdge(new GraphEdge(12,39));
        robotNavGraph.AddEdge(new GraphEdge(39,15));
        robotNavGraph.AddEdge(new GraphEdge(13,41));
        robotNavGraph.AddEdge(new GraphEdge(41,14));
        robotNavGraph.AddEdge(new GraphEdge(14,40));
        robotNavGraph.AddEdge(new GraphEdge(40,15));
        robotNavGraph.AddEdge(new GraphEdge(10,43));
        robotNavGraph.AddEdge(new GraphEdge(43,16));
        robotNavGraph.AddEdge(new GraphEdge(16,44));
        robotNavGraph.AddEdge(new GraphEdge(44,45));
        robotNavGraph.AddEdge(new GraphEdge(45,17));
        robotNavGraph.AddEdge(new GraphEdge(16,46));
        robotNavGraph.AddEdge(new GraphEdge(46,47));
        robotNavGraph.AddEdge(new GraphEdge(47,18));
        robotNavGraph.AddEdge(new GraphEdge(18,19));
        robotNavGraph.AddEdge(new GraphEdge(19,20));
        robotNavGraph.AddEdge(new GraphEdge(19,23));
        robotNavGraph.AddEdge(new GraphEdge(20,48));
        robotNavGraph.AddEdge(new GraphEdge(48,21));
        robotNavGraph.AddEdge(new GraphEdge(21,49));
        robotNavGraph.AddEdge(new GraphEdge(49,22));
        robotNavGraph.AddEdge(new GraphEdge(22,50));
        robotNavGraph.AddEdge(new GraphEdge(50,23));
        robotNavGraph.AddEdge(new GraphEdge(18,24));
        robotNavGraph.AddEdge(new GraphEdge(24,25));
        robotNavGraph.AddEdge(new GraphEdge(25,26));
        robotNavGraph.AddEdge(new GraphEdge(25,30));
        robotNavGraph.AddEdge(new GraphEdge(26,27));
        robotNavGraph.AddEdge(new GraphEdge(27,28));
        robotNavGraph.AddEdge(new GraphEdge(28,29));
        robotNavGraph.AddEdge(new GraphEdge(29,51));
        robotNavGraph.AddEdge(new GraphEdge(51,30));
        robotNavGraph.AddEdge(new GraphEdge(27,31));
        robotNavGraph.AddEdge(new GraphEdge(31,32));
        robotNavGraph.AddEdge(new GraphEdge(32,33));
        robotNavGraph.AddEdge(new GraphEdge(33,52));
        robotNavGraph.AddEdge(new GraphEdge(52,34));
        robotNavGraph.AddEdge(new GraphEdge(34,35));
        robotNavGraph.AddEdge(new GraphEdge(35,31));
        robotNavGraph.AddEdge(new GraphEdge(53,5));
        robotNavGraph.AddEdge(new GraphEdge(53,6));
        robotNavGraph.AddEdge(new GraphEdge(53,38));
        robotNavGraph.AddEdge(new GraphEdge(53,9));
        robotNavGraph.AddEdge(new GraphEdge(53,8));
        robotNavGraph.AddEdge(new GraphEdge(53,7));
        robotNavGraph.AddEdge(new GraphEdge(53,37));
        robotNavGraph.AddEdge(new GraphEdge(54,12));
        robotNavGraph.AddEdge(new GraphEdge(54,39));
        robotNavGraph.AddEdge(new GraphEdge(54,15));
        robotNavGraph.AddEdge(new GraphEdge(54,40));
        robotNavGraph.AddEdge(new GraphEdge(54,14));
        robotNavGraph.AddEdge(new GraphEdge(54,41));
        robotNavGraph.AddEdge(new GraphEdge(54,13));
        robotNavGraph.AddEdge(new GraphEdge(54,42));
        robotNavGraph.AddEdge(new GraphEdge(55,21));
        robotNavGraph.AddEdge(new GraphEdge(55,48));
        robotNavGraph.AddEdge(new GraphEdge(55,20));
        robotNavGraph.AddEdge(new GraphEdge(55,19));
        robotNavGraph.AddEdge(new GraphEdge(55,23));
        robotNavGraph.AddEdge(new GraphEdge(55,50));
        robotNavGraph.AddEdge(new GraphEdge(55,22));
        robotNavGraph.AddEdge(new GraphEdge(55,49));

        //starting location on navgraph
        startGraphIndex = 6;

        pathListSize = 0;
        pathListCounter = 0;
        navSwitch = false;
        currentSandboxLocation = 0;
        currentSandboxTarget = 0;
        atSandboxPos = false;
        canSeePlayer = false;

        seekStart=false;
        seekWaitCounter=0;

        playerTextureIdleLeft = iLeft;
        playerTextureIdleRight = iRight;
        playerTextureIdleDown = iDown;
        playerTextureIdleUp = iUp;
        wetTexture = wet;
        damagedTexture = damaged;

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
        animation.setDelay(500);

        //Enter starting State behaviour
        currentState.Enter(this);

    }

    public void SetToIdle()
    {
        //set to idle
        isIdle = true;

        if(facingDirection == 0)
            playerTextureIdleCurrent = playerTextureIdleLeft;
        else if (facingDirection == 1)
            playerTextureIdleCurrent = playerTextureIdleRight;
        else if(facingDirection == 2)
            playerTextureIdleCurrent = playerTextureIdleUp;
        else if(facingDirection == 3)
            playerTextureIdleCurrent = playerTextureIdleDown;
    }

    public void SetRobotSpeed(int newSpeed, int newDelay)
    {
        robotSpeed = newSpeed;
        animation.setDelay(newDelay);
    }

    public void ChangeState(State newState)
    {
        currentState.Exit(this);

        currentState = newState;

        currentState.Enter(this);
    }

    public int GetFacingDirection() {return facingDirection;}//0=left,1=right,2=up,3=down

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

    public boolean GetActivating() {return isActivating;}
    public void SetActivating(boolean state) {isActivating = state;}

    public boolean GetRendered()
    {
        return isRendered;
    }

    public void SetRendered(boolean choice)
    {
        isRendered = choice;
    }

    public int GetClosestNode(int x, int y)
    {
        int lastWinner = 0;
        int currentDistance = 0;
        int closestNodeIndex = 0;

        //find closest graph node to robot
        for(int i=0; i<robotNavGraph.GetNumNodes();i++)
        {
            currentDistance = Distance(robotNavGraph.GetNode(i).GetX(),
                    robotNavGraph.GetNode(i).GetY(),
                    x,
                    y);

            if(lastWinner == 0)
            {
                closestNodeIndex = i;
                lastWinner=currentDistance;
            }
            else if(currentDistance<lastWinner)
            {
                closestNodeIndex = i;
                lastWinner=currentDistance;
            }
        }

        return closestNodeIndex;
    }

    //get distance between two points
    public int Distance(int x1, int y1,
                          int x2, int y2)
    {
        //(adaptation of pythagoras)
        //get differences between points
        double deltaX = (x2-x1);
        double deltaY = (y2-y1);
        //add squares of each delta and then return square root
        return (int)sqrt(deltaX*deltaX+deltaY*deltaY);
    }

    public void NavTo(int targetIndex)
    {
        int closestNode = GetClosestNode(x, y);

        GraphSearch search = new GraphSearch(robotNavGraph,closestNode,targetIndex);
        pathToTarget = search.GetPathToTarget();
        pathListSize = pathToTarget.size();
        pathListCounter = pathListSize-1;
        navSwitch=true;
    }

    public void StopNav()
    {
        pathListCounter = 0;
        pathListSize = 0;
        navSwitch = false;

        isIdle = true;
    }

    public void NavToUpdate()
    {
        if(navSwitch)
        {
            if(pathListCounter >= 0)
            {
                int node = pathToTarget.get(pathListCounter);
                int posX = robotNavGraph.GetNode(node).GetX();
                int posY = robotNavGraph.GetNode(node).GetY();

                MoveTo(posX,(posY-height));
            }
            else
            {
                StopNav();
            }
        }
    }

    public void MoveTo(int x, int y)
    {
        //if within range of 5, no need to move to point
        if(Math.abs(this.x-x) <= 5)
        {
            if(Math.abs(this.y-y) <= 5)
            {
                pathListCounter--;
                return;
            }
        }

        //store last position for collision
        lastPositionX = this.x;
        lastPositionY = this.y;

        //move in direction of target coordinates
        if(x > this.x + (robotSpeed-1)) //need to + 1 to account for speed of 2
            this.x = this.x + robotSpeed;
        else if(x < this.x - (robotSpeed-1)) //need to - 1 to account for speed of 2
            this.x = this.x - robotSpeed;
        if(y > this.y + (robotSpeed-1))
            this.y = this.y + robotSpeed;
        else if(y < this.y - (robotSpeed-1))
            this.y = this.y - robotSpeed;

        //set current texture
        if(x > lastPositionX) //is moving right
        {
            if(y > lastPositionY) //is moving down
            {
                if((x-lastPositionX) >= (y-lastPositionY)) //is moving more right than down
                {
                    animation.setFrames(playerTextureWalkingRight);
                    SetFacingDirection(1);
                }
                else
                {
                    animation.setFrames(playerTextureWalkingDown);
                    SetFacingDirection(3);
                }
            }
            else //is moving up
            {
                if((x-lastPositionX) > (lastPositionY-y)) //is moving more right than up
                {
                    animation.setFrames(playerTextureWalkingRight);
                    SetFacingDirection(1);
                }
                else
                {
                    animation.setFrames(playerTextureWalkingUp);
                    SetFacingDirection(2);
                }
            }
        }
        else //is moving left
        {
            if(y > lastPositionY) //is moving down
            {
                if((lastPositionX-x) >= (y-lastPositionY)) //is moving more left than down
                {
                    animation.setFrames(playerTextureWalkingLeft);
                    SetFacingDirection(0);
                }
                else
                {
                    animation.setFrames(playerTextureWalkingDown);
                    SetFacingDirection(3);
                }
            }
            else //is moving up
            {
                if((lastPositionX-x) > (lastPositionY-y)) //is moving more left than up
                {
                    animation.setFrames(playerTextureWalkingLeft);
                    SetFacingDirection(0);
                }
                else
                {
                    animation.setFrames(playerTextureWalkingUp);
                    SetFacingDirection(2);
                }
            }
        }

        isIdle=false;
    }

    public boolean GetNavSwitch(){return navSwitch;}
    public int GetCurrentSandboxTarget(){return currentSandboxTarget;}
    public boolean GetAtSandboxPos(){return atSandboxPos;}
    public void SetAtSandboxPos(boolean val){atSandboxPos=val;}

    //ai behaviour functions
    public void SeekPlayer()
    {
        //only run when the SeekPlayerStart function has finished
        if(!seekStart)
        {
            int closestNode = GetClosestNode(playerX,(playerY+130));//add player height(130) to get bottom of sprite
            NavTo(closestNode);
        }
    }

    public void SeekPlayerStart(){seekStart=true;}//has a delayed start

    public void FindNextSandboxLocation()
    {
        if(currentSandboxLocation==0)
        {
            if(GetClosestNode(this.x,this.y+this.height)==29)
            {
                atSandboxPos = true;
                currentSandboxLocation++;
            }
            else currentSandboxTarget=29;
        }
        else if(currentSandboxLocation==1)
        {
            if(GetClosestNode(this.x,this.y+this.height)==33)
            {
                atSandboxPos = true;
                currentSandboxLocation++;
            }
            else currentSandboxTarget=33;
        }
        else if(currentSandboxLocation==2)
        {
            if(GetClosestNode(this.x,this.y+this.height)==17)
            {
                atSandboxPos = true;
                currentSandboxLocation++;
            }
            else currentSandboxTarget=17;
        }
        else if(currentSandboxLocation==3)
        {
            if(GetClosestNode(this.x,this.y+this.height)==14)
            {
                atSandboxPos = true;
                currentSandboxLocation++;
            }
            else currentSandboxTarget=14;
        }
        else if(currentSandboxLocation==4)
        {
            if(GetClosestNode(this.x,this.y+this.height)==10)
            {
                atSandboxPos = true;
                currentSandboxLocation++;
            }
            else currentSandboxTarget=10;
        }
        else if(currentSandboxLocation==5)
        {
            if(GetClosestNode(this.x,this.y+this.height)==10)
            {
                atSandboxPos = true;
                currentSandboxLocation++;
            }
            else currentSandboxTarget=22;
        }

        if(currentSandboxLocation>5)
            currentSandboxLocation=0;
    }

    public int GetCurrentSandboxLocation(){return currentSandboxLocation;}

    public boolean CheckDetectionBox(Player player, Level levelObject)
    {
        if(facingDirection==0)//facing left
        {
            Rect box = new Rect();
            box.right = this.x;
            box.top = this.y;
            box.bottom = this.y+this.height;
            box.left = box.right-400;//box extends 400 to the left

            int closest = 0;

            for(int i=0; i < levelObject.GetNextElementPos(); i++)
            {
                if(levelObject.GetElement(i).GetHasCollision())
                {
                    if(Collision(box, levelObject.GetElement(i)))
                    {
                        if(levelObject.GetElement(i).GetX()>closest)
                        {
                            closest=levelObject.GetElement(i).GetX();
                        }
                    }
                }
            }

            if(Collision(box,player.getRectangle()))
            {
                if(player.getX() > closest)
                {
                    //player has been detected
                    return true;
                }
                else return false;
            }
        }
        else if(facingDirection==1)//facing right
        {
            Rect box = new Rect();
            box.left = this.x+this.width;
            box.right = box.left+400;
            box.top = this.y;
            box.bottom = this.y+this.height;

            int closest = 9999;

            for(int i=0; i < levelObject.GetNextElementPos(); i++)
            {
                if(levelObject.GetElement(i).GetHasCollision())
                {
                    if(Collision(box, levelObject.GetElement(i)))
                    {
                        if(levelObject.GetElement(i).GetX()<closest)
                        {
                            closest=levelObject.GetElement(i).GetX();
                        }
                    }
                }
            }

            if(Collision(box,player.getRectangle()))
            {
                if(player.getX() < closest)
                {
                    //player has been detected
                    return true;
                }
                else return false;
            }
        }
        else if(facingDirection==2)//facing up
        {
            Rect box = new Rect();
            box.right = this.x+this.width+30;
            box.top = this.y-400;
            box.bottom = this.y;
            box.left = this.x-30;

            int closest = 0;

            for(int i=0; i < levelObject.GetNextElementPos(); i++)
            {
                if(levelObject.GetElement(i).GetHasCollision())
                {
                    if(Collision(box, levelObject.GetElement(i)))
                    {
                        if(levelObject.GetElement(i).GetY()>closest)
                        {
                            closest=levelObject.GetElement(i).GetY();
                        }
                    }
                }
            }

            if(Collision(box,player.getRectangle()))
            {
                if(player.getY() > closest)
                {
                    //player has been detected
                    return true;
                }
                else return false;
            }
        }
        else if(facingDirection==3)//facing down
        {
            Rect box = new Rect();
            box.right = this.x+this.width+30;
            box.top = this.y+this.height;
            box.bottom = box.top+400;
            box.left = this.x-30;

            int closest = 9999;

            for(int i=0; i < levelObject.GetNextElementPos(); i++)
            {
                if(levelObject.GetElement(i).GetHasCollision())
                {
                    if(Collision(box, levelObject.GetElement(i)))
                    {
                        if(levelObject.GetElement(i).GetY()<closest)
                        {
                            closest=levelObject.GetElement(i).GetY();
                        }
                    }
                }
            }

            if(Collision(box,player.getRectangle()))
            {
                if(player.getY() < closest)
                {
                    //player has been detected
                    return true;
                }
                else return false;
            }
        }

        return false;
    }

    public boolean GetCanSeePlayer(){return canSeePlayer;}

    public boolean Collision(Rect box, ElementObj sprite2)	//collision detection
    {
        Rect rect2 = new Rect();
        rect2.left = sprite2.GetX() + 1;
        rect2.top = sprite2.GetY() + 1;
        rect2.right = sprite2.GetX() + sprite2.GetWidth() - 1;
        rect2.bottom = sprite2.GetY() + sprite2.GetHeight() - 1;

        return box.intersect(rect2);
    }

    public boolean Collision(Rect box, Rect rect2)	//collision detection
    {
        return box.intersect(rect2);
    }

    public void SetHasCollided()
    {
        this.x = lastPositionX;
        this.y = lastPositionY;
    }

    public void CaughtPlayer(Player p1)
    {
        //ChangeState(new IdleState());
        //p1.LockPlayerControls();
    }

    public void CheckRobotCollisions(Player player)
    {
        if(Collision(this.getRectangle(), player.getRectangle()))
        {
            //reset player to last position
            x = lastPositionX;
            y = lastPositionY;

            ChangeState(new IdleState());
            player.LockControls(true);
        }
    }

    public void Update(int currentOffsetX, int currentOffsetY, Player p1, Level levelObject)
    {
        //update animation
        animation.update();


        //SeekPlayerStart() delay
        if(seekStart)
        {
            if(seekWaitCounter>=60)
            {
                int closestNode = GetClosestNode(playerX,playerY);
                NavTo(closestNode);
                seekWaitCounter=0;
                seekStart=false;
            }
            else seekWaitCounter++;
        }

        //sound detection
        /*if(p1.GetIsRunning())
        {
            //only run if in sandbox or search states
            if(currentState->GetStateID()==2 || currentState->GetStateID()==5)
            {
                RECT r;
                r.left=p1.GetX()-200;
                r.right=p1.GetX()+p1.GetWidth()+200;
                r.top=p1.GetY()-200;
                r.bottom=p1.GetY()+p1.GetHeight()+200;

                if(Collision(&r,&sprite))
                {
                    //if robot hears player, enter seek state
                    ChangeState(new SeekState());
                }
            }
        }*/

        //update if player can be seen by robot
        if(CheckDetectionBox(p1,levelObject))
        {
            canSeePlayer = true;
        }
        else canSeePlayer = false;


        //update player coords
        playerX = p1.getX();
        playerY = p1.getY();

        NavToUpdate();

        currentState.Execute(this);


        if((!animMode) && (!staticAnimMode))
        {
            if(!isIdle)
            {
                //move the robot
                currentState.Execute(this);
            }

        }
        else if (staticAnimMode)
        {
            //PlayStaticAnimUpdate();
        }
        else //if animMode is activated, run this update function instead
        {
            //PlayAnimUpdate();
        }
    }

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
}
