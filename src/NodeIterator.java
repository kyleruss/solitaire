//################################
//Name: Kyle Russell
//ID: 13831056
//DSA Assignment one
//################################

import java.util.Iterator;

public class NodeIterator<T> implements Iterator<Node<T>>
{
    private Node<T> currentNode; //initial node/starting point provided, i.e first node
    private int current; //pointer for iterating
    private final int min; //ceiling/floor to end iteration
    
    //Typically to iterate an entire list of nodes pass NodeIterator(firstNode, size(), 0)
    public NodeIterator(Node<T> currentNode, int current, int min)
    {
        this.currentNode    =   currentNode;
        this.min          =   min;
        this.current        =   current;
    }
    
    
    //Returns true if the iterator has more elements to iterate through; false otherwise
    @Override
    public boolean hasNext() 
    {
        return current > min;
    }

    //Returns and sets the next element
    //Moves pointer
    @Override
    public Node<T> next() 
    {
        currentNode =   currentNode.getNext();
        current--;
        return currentNode;
    }
    
    //Iterates through all nodes until there are no more
    public Node<T> getEnd()
    {
        while(hasNext())
            next();

        return currentNode;
    }
    
    //Returns the current node
    public Node<T> getCurrent()
    {
        return currentNode;
    }
    
    //Returns the pointer index
    public int getCurrentIndex()
    {
        return current;
    }

    //Remove implementation not used
    @Override
    public void remove() {}
    
}