package hons.segd.androidgame;


public class GraphEdge {

    //an edge is a path connecting two nodes
    private int indexFrom;
    private int indexTo;

    private double cost;//cost of traversing edge, or its distance value

    public GraphEdge(int from, int to, double costVar)
    {
        indexFrom = from;
        indexTo = to;
        cost = costVar;
    }

    public GraphEdge(int from, int to)
    {
        indexFrom = from;
        indexTo = to;
        cost = 1.0;
    }

    //invalid edge, do not use
    public GraphEdge()
    {
        cost = 1.0;
        indexFrom = -1;
        indexTo = -1;
    }

    public int GetFrom(){return indexFrom;}
    public void SetFrom(int idx){indexFrom=idx;}
    public int GetTo(){return indexTo;}
    public void SetTo(int idx){indexTo=idx;}
    public double GetCost(){return cost;}
    public void SetCost(double newCost){cost=newCost;}

}
