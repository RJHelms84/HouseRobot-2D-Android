package hons.segd.androidgame;


public class SearchState extends State {

    private int stateID;
    private int searchCounter;

    public SearchState()
    {
        stateID = 5;
        searchCounter = 0;
    }

    //called when state is entered
    public void Enter(Robot robot)
    {
        searchCounter=0;
        stateID=5;
        robot.SetFacingDirection(3);
    }

    //update function
    public void Execute(Robot robot)
    {
        if(robot.GetCanSeePlayer())
            robot.ChangeState(new SeekState());

        if(searchCounter>=1500)
        {
            searchCounter=0;
            robot.ChangeState(new SandboxState());
        }
        else if(searchCounter>=1200)
            robot.SetFacingDirection(3);
        else if(searchCounter>=900)
            robot.SetFacingDirection(2);
        else if(searchCounter>=600)
            robot.SetFacingDirection(1);
        else if(searchCounter>=300)
            robot.SetFacingDirection(0);

        searchCounter++;
    }

    //called when leaving state
    public void Exit(Robot robot)
    {
        searchCounter=0;
        robot.SetRobotSpeed(4,500);
        robot.StopNav();
    }

    public int GetStateID(){return stateID;}

}
