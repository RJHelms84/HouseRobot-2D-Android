package hons.segd.androidgame;


public class GraphNode {

    //every node has an index that is >= 0
    //index is set to -1 for makeshift deletion
    private int index;
    private int parentIndex;

    private int x;
    private int y;

    public GraphNode()
    {
        index = -1;//index set to -1 (invalid) if no index is passed to constructor
    }

    public GraphNode(int idx, int newX, int newY)
    {
        index = idx;
        x = newX;
        y = newY;
    }

    public GraphNode(int idx, int parent, int newX, int newY)
    {
        index = idx;
        parentIndex = parent;
        x = newX;
        y = newY;
    }

    public int GetIndex(){return index;}
    public void SetIndex(int idx){index=idx;}

    public int GetParentIndex(){return parentIndex;}

    public int GetX(){return x;}
    public int GetY(){return y;}
    public void SetX(int newX){x=newX;}
    public void SetY(int newY){y=newY;}

}
