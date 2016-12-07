package hons.segd.androidgame;


import java.util.Stack;
import java.util.Vector;

public class GraphSearch {

    public int VISITED = 0;
    public int UNVISITED = 1;
    public int NO_PARENT_ASSIGNED = 2;

    //reference to the graph to be searched
    private NavGraph m_Graph;

    //record of the visited node indices
    private Vector<Integer> m_Visited;

    //route taken to target node
    private Vector<Integer> m_Route;

    //source and target node indices
    private int m_Source;
    private int m_Target;

    //has path to target been found?
    private boolean m_Found;


    public GraphSearch(NavGraph graph, int source, int target)
    {
        m_Visited = new Vector<Integer>();
        m_Route = new Vector<Integer>();

        m_Graph = graph;
        m_Source = source;
        m_Target = target;
        m_Found = false;

        for(int i = 0; i < m_Graph.GetNumNodes(); i++)
        {
            m_Visited.add(UNVISITED);
            m_Route.add(NO_PARENT_ASSIGNED);
        }

        m_Found = Search();

    }

    //the search method
    private boolean Search()
    {
        //create stack of pointers to edges
        Stack<GraphEdge> stack = new Stack<GraphEdge>();

        //create a dummy edge and put on stack
        GraphEdge Dummy = new GraphEdge(m_Source,m_Source,0);

        stack.push(Dummy);

        //while edges on the stack, keep searching
        while(!stack.empty())
        {
            //grab next edge
            GraphEdge Next = stack.peek();

            //remove edge from stack
            stack.pop();

            //record parent of the node this edge points to
            m_Route.set(Next.GetTo(), Next.GetFrom());

            //mark if visited
            m_Visited.set(Next.GetTo(), VISITED);

            //if target found, return
            if(Next.GetTo() == m_Target)
            {
                return true;
            }

            //push edges leading from node this edge points to
            //onto the stack, if edge does not point to a visited node
            for(GraphEdge e : m_Graph.GetEdges().get(Next.GetTo()))
            {
                if(m_Visited.get(e.GetTo()) == UNVISITED)
                {
                    stack.push(e);
                }
            }

        }//while


        //no path to target
        return false;
    }

    public boolean GetFound(){return m_Found;}

    //returns vector of node indices for the shortest path
    //from source to target node
    public Vector<Integer> GetPathToTarget()
    {
        Vector<Integer> path = new Vector<Integer>();

        //just return an empty path if no path to target found
        //or if no target has been specified
        if(!m_Found || m_Target<0)
        {
            return path;
        }

        int nd = m_Target;

        path.add(nd);

        while(nd != m_Source)
        {
            nd = m_Route.get(nd);

            path.add(nd);
        }

        return path;
    }

}
