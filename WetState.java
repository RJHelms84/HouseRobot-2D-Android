package hons.segd.androidgame;

/**
 * Created by rober on 22/10/2016.
 */

public class WetState extends State {

    private int stateID;
    private int wetCounter;

    public WetState()
    {
        stateID = 3;
        wetCounter = 0;
    }

    //called when state is entered
    public void Enter(Robot robot)
    {
        wetCounter=0;
        stateID=2;
        robot.SetToIdle();
        //robot.SetToWetTexture();
        //robot.SetIsWet(true);
    }

    //update function
    public void Execute(Robot robot)
    {
        if(wetCounter>180)
        {
            robot.ChangeState(new SeekState());
            wetCounter=0;
        }
        else wetCounter++;
    }

    //called when leaving state
    public void Exit(Robot robot)
    {
        robot.StopNav();
        //robot.SetIsWet(false);
    }

    public int GetStateID(){return stateID;}

}
