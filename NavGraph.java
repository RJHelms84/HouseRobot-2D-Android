package hons.segd.androidgame;


import java.util.Vector;

public class NavGraph {

    //the nodes in the navgraph
    private Vector<GraphNode> nodes;

    //the vector of adjacency edge lists
    //each node index keys into the list of edges associated with that node
    private Vector<Vector<GraphEdge>> edges;

    //the index value for the next node to be added to the navgraph
    private int nextNodeIndex;

    public NavGraph()
    {
        nextNodeIndex = 0;

        nodes = new Vector<GraphNode>();
        edges = new Vector<Vector<GraphEdge>>();
    }

    public Vector<Vector<GraphEdge>> GetEdges()
    {
        return edges;
    }

    public int GetNextFreeNodeIndex(){return nextNodeIndex;}

    public int GetNumNodes() {return nodes.size();}

    public GraphNode GetNode(int idx){return nodes.get(idx);}

    public GraphEdge GetEdge(int from, int to)
    {
        for(GraphEdge e : edges.get(from))
        {
            if(e.GetTo() == to)
                return e;
        }

        //this should never run
        return edges.get(from).get(0);
    }

    public int AddNode(GraphNode node)
    {
        if(node.GetIndex() < (int)nodes.size())
        {
            //if node index is within the current size of the vector
            //this means you are replacing a current node
            nodes.set(node.GetIndex(),node);

            return node.GetIndex();
        }
        else
        {
            //if not replacing current node, add new one
            nodes.add(node);

            //also, add new edge list at the same index position
            edges.add(new Vector<GraphEdge>());

            //return index for newly created node and increment nextNodeIndex
            return nextNodeIndex++;
        }
    }

    public void AddEdge(GraphEdge edge)
    {
        //check the connecting nodes' index are not invalid (-1)
        if((nodes.get(edge.GetTo()).GetIndex() != -1) &&
                (nodes.get(edge.GetFrom()).GetIndex() != -1))
        {
            //add the edge
            edges.get(edge.GetFrom()).add(edge);

            //add an edge coming from the opposite direction
            //so you can travel both ways
            GraphEdge newEdge = new GraphEdge();

            newEdge.SetTo(edge.GetFrom());
            newEdge.SetFrom(edge.GetTo());

            edges.get(edge.GetTo()).add(newEdge);
        }
    }

}
