package hons.segd.androidgame;


public class DamagedState extends State {

    private int stateID;
    private int damagedCounter;

    public DamagedState()
    {
        stateID = 4;
        damagedCounter = 0;
    }

    //called when state is entered
    public void Enter(Robot robot)
    {
        damagedCounter=0;
        stateID=3;
        robot.SetToIdle();
        //robot.SetToDamagedTexture();
        //robot.SetIsDamaged(true);
    }

    //update function
    public void Execute(Robot robot)
    {
        if(damagedCounter>600)
        {
            robot.ChangeState(new SeekState());
            damagedCounter=0;
        }
        else damagedCounter++;
    }

    //called when leaving state
    public void Exit(Robot robot)
    {
        robot.StopNav();
        //robot.SetIsDamaged(false);
    }

    public int GetStateID(){return stateID;}

}
