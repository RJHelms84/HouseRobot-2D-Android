package hons.segd.androidgame;


public class State {

    //called when state is entered
    public void Enter(Robot robot){}

    //update function
    public void Execute(Robot robot){}

    //called when leaving state
    public void Exit(Robot robot){}

    public int GetStateID(){return 0;}
}
