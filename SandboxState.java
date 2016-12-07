package hons.segd.androidgame;


public class SandboxState extends State {

    private int stateID;
    private int sandboxCounter;
    private int waitCounter;

    public SandboxState()
    {
        stateID = 2;
        sandboxCounter = 0;
    }

    //called when state is entered
    public void Enter(Robot robot)
    {
        sandboxCounter=601;
        waitCounter=0;
        stateID=2;
    }

    //update function
    public void Execute(Robot robot)
    {
        if(robot.GetCanSeePlayer())
            robot.ChangeState(new SeekState());

        robot.FindNextSandboxLocation();

        if(!robot.GetAtSandboxPos())
        {
            if(sandboxCounter>600)
            {
                robot.NavTo(robot.GetCurrentSandboxTarget());
                sandboxCounter=0;
            }
            else sandboxCounter++;
        }
        else
        {
            if(waitCounter>300)//wait at each sandbox location for 5 seconds(60fps*300=5seconds)
            {
                robot.SetAtSandboxPos(false);
                robot.NavTo(robot.GetCurrentSandboxTarget());
                waitCounter=0;
                sandboxCounter=0;
            }
            else waitCounter++;
        }
    }

    //called when leaving state
    public void Exit(Robot robot)
    {
        robot.SetAtSandboxPos(false);
        robot.StopNav();
    }

    public int GetStateID(){return stateID;}

}
