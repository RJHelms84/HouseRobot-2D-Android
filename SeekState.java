package hons.segd.androidgame;


public class SeekState extends State
{

    private int stateID;
    private int seekCounter;

    public SeekState()
    {
        stateID = 1;
        seekCounter = 0;
    }

    //called when state is entered
    public void Enter(Robot robot)
    {
        seekCounter=0;
        stateID=1;
        robot.SetRobotSpeed(6,200);//4/500 is the default
        robot.SeekPlayerStart();
    }

    //update function
    public void Execute(Robot robot)
    {
        if(!robot.GetNavSwitch())//skip wait period, if at destination
        {
            if(seekCounter<1200)
                robot.SeekPlayer();
        }

        if(seekCounter>=1200)
        {
            if(robot.GetCanSeePlayer())
            {
                robot.SeekPlayer();
            }
            else
            {
                robot.StopNav();
                //then enter search behaviour
                robot.ChangeState(new SearchState());
            }
            seekCounter=0;
        }
        else seekCounter++;
    }

    //called when leaving state
    public void Exit(Robot robot)
    {
        seekCounter=0;
        robot.SetRobotSpeed(4,500);
        robot.StopNav();
    }

    public int GetStateID(){return stateID;}

}
