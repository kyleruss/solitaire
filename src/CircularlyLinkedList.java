//################################
//Name: Kyle Russell
//ID: 13831056
//DSA Assignment one
//################################

public class CircularlyLinkedList<T> extends LinkedList<T> implements Iterable<Node<T>>
{
    private Node<T> lastNode;
    
    public CircularlyLinkedList()
    {
        super();
    }
    
    //Adds a node to the list at position i
    @Override
    public void add(int i, Node<T> e)
    {
        if(i > size + 1) return; //to add node at the end of list use i = size + 1
        if(lastNode == null) //initialize lastNode
        {
            lastNode    =   e;
            updateFirstNode();
            size++;
        }
        
        //Adding node at the start
        else if(i <= 1)
        {
            if(lastNode.getNext() == null) // only 1 element in list
                e.setNext(lastNode); 
            else 
                e.setNext(lastNode.getNext());
            
            lastNode.setNext(e);
            updateFirstNode();
            size++; 
        }
        
        //Adding node at the end or somewhere in the middle
        else
        {
            if(lastNode.getNext() == null && i == size + 1)
            {
                e.setNext(lastNode);
                lastNode.setNext(e);
            }
            
            //Iterates to i position and add node
            //if i position is last node then set node as new last node
            else
            {
                NodeIterator<T> iterator =   new NodeIterator<>(firstNode, (i == size + 1)? i - 1 : i, 1);
                Node prev   =   iterator.getEnd();
                Node temp   =   prev.getNext();

                e.setNext(temp);
                prev.setNext(e);
            }
            
            //If adding node as last node
            if(i == size + 1) lastNode  =   e;
            updateFirstNode();
            size++;
        }
    }
    
    //Returns the index of the node
    //Checks lastNode is node otherwise uses LinkedList implementation of indexOf()
    @Override
    public int indexOf(Node<T> e)
    {
        if(e.equals(lastNode)) return size - 1;
        else return super.indexOf(e);
    }
    
    //Returns the node at i
    //Checks if i is the last node otherwise uses LinkedList implementation of get()
    @Override
    public Node<T> get(int i)
    {
        if(i == size - 1) return lastNode;
        else return super.get(i);
    }
    
    //keep reference to firstnode for convenience so methods in LinkedList parent are still usable
    public void updateFirstNode()
    {
        firstNode   =   (lastNode.getNext() == null) ? lastNode : lastNode.getNext();
    }
    
    //Uses LinkedList implementation of set to set the node
    //Updates last node and firstNode if necessary 
    @Override
    public void set(int i, Node<T> e)
    {
        super.set(i, e);
        if(i == 0) 
        {
            lastNode.setNext(firstNode);
            updateFirstNode();
        }
        else if(i == size - 1)  lastNode    =   e;
    }
    
    //Removes the node at position i
    //Uses LinkedList implementation of remove to remove the node
    //Then updates lastNode if necessary
    @Override
    public Node<T> remove(int i)
    {
        Node[] removeNodes =   super.removeAlt(i);
        if(i == 0)  lastNode.setNext(removeNodes[REMOVE_NEW_INDEX]);
        else if(i == size)  lastNode =   removeNodes[REMOVE_NEW_INDEX];
        return removeNodes[REMOVE_NODE_INDEX];
    }
}