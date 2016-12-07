package hons.segd.androidgame;


public class IdleState extends State {

    private int stateID;

    public IdleState()
    {
        stateID = 0;
    }

    //called when state is entered
    public void Enter(Robot robot)
    {
        robot.StopNav();
        robot.SetToIdle();
        stateID=0;
    }

    //update function
    public void Execute(Robot robot)
    {

    }

    //called when leaving state
    public void Exit(Robot robot)
    {

    }

    public int GetStateID(){return stateID;}

}
