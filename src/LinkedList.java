//################################
//Name: Kyle Russell
//ID: 13831056
//DSA Assignment one
//################################

import java.util.Iterator;

public class LinkedList<T> implements AbstractList<Node<T>>, Iterable<Node<T>>
{
    protected Node<T> firstNode;
    protected int size;
    public static final int REMOVE_NODE_INDEX    = 0;
    public static final int REMOVE_NEW_INDEX     = 1;
    
    public LinkedList()
    {
        firstNode    =   null;
        size         =   0;
    }
    
    //Returns the NodeIterator and allows iterating through the entire list
    @Override
    public Iterator<Node<T>> iterator() 
    {
        return new NodeIterator<>(firstNode, size(), 0);
    }
    
    //Returns the head of the list
    public Node<T> getHead()
    {
        return firstNode;
    }
    
    //Sets the head of the list
    public void setHead(Node<T> firstNode)
    {
        this.firstNode  =   firstNode;
    }
    
    
    
    @Override
    public String toString()
    {
        String str  =   "";
        NodeIterator<T> iterator   =   (NodeIterator)iterator();
        while(iterator.hasNext())
        {
            str += iterator.getCurrent().getValue();
            iterator.next();
            if(iterator.hasNext()) str += "->";
        }
        return str;
    }
 
    @Override
    public int indexOf(Node<T> e) 
    {
        if(firstNode.equals(e)) return 0;
        else
        {
            int count   =   1;
            NodeIterator<Node> iterator   =   (NodeIterator)iterator();
            Node temp    =   iterator.next();
            
            while(iterator.hasNext())
            {
                if(temp.equals(e)) return count;
                else
                {
                    temp    =   iterator.next();
                    count++;
                }
            }
        }     
        return -1;
    }

    @Override
    public int size()
    {
        return size;
    }
    
    public void setSize(int size)
    {
        this.size = size;
    }
    
    @Override
    public void clear()
    {
        firstNode = null;
    }

    @Override
    public boolean isEmpty() 
    {
        return size == 0;
    }

    
    @Override
    public Node<T> get(int i) 
    {
        if(i >= size) return null;
        if(i == 0) return firstNode;
        else
        {
            NodeIterator<T> iterator = new NodeIterator<>(firstNode.getNext(), i, 1);
            return iterator.getEnd();
        } 
    }

    @Override
    public void set(int i, Node<T> e) 
    {
        if(i >= size) return;
        if(i == 0)
        {
            e.setNext(firstNode.getNext());
            firstNode   =   e;
        }
        
        else
        {
            NodeIterator<T> iterator =   new NodeIterator<>(firstNode, i, 1);
            Node prev = iterator.getEnd();
            Node temp = prev.getNext();
            
            e.setNext(temp.getNext());
            prev.setNext(e);
        }
    }
    
    //Allows for elements to be quickly added without specifying position
    //Adds at the start of the list if it's empty or at the end 
    public void add(Node<T> node)
    {
        if(firstNode == null) 
            add(0, node);
        else
            add(size + 1, node);
    }

    //Adds an element at position i
    @Override
    public void add(int i, Node<T> e) 
    {
        //initialize head node if null
        if(firstNode == null)
        {
            firstNode    =   e;
            size++;
            return;
        }
        
        //Adding at start
        //Need to push firstNode forward and set e as the new firstNode
        if(i == 0)
        {
            e.setNext(firstNode);
            firstNode   =   e;
            size++;
        }
        
        //Adding at somewhere in the middle or at the end
        //Iterates through until i to get prev, temp
        //adds i infront of prev and i's next will be temp
        else
        { 
            NodeIterator<T> iterator =   new NodeIterator<>(firstNode, i-1, 1);
            Node prev = iterator.getEnd();
            Node temp = prev.getNext();
            
            prev.setNext(e);
            e.setNext(temp);
            size++;
        }
    }
    
    //Removes the node element
    //Finds the index of element and removes it
    public Node<T> remove(Node element)
    {
        int index   =   indexOf(element);
        return remove(index);
    }
    
    //Removes a node at position i
    //Uses removeAlt to remove this node and returns the removal node
    public Node<T> remove(int i)
    {
        return removeAlt(i)[REMOVE_NODE_INDEX];
    }
    
    //Removes a node at position i
    //A variant from remove(int i) implementation as it removes the node but returns the removal node and the new node which took its place
    //- removeAlt()[REMOVE_NODE_INDEX] = the removed node => use this for standard remove
    //- removeAlt()[REMOVE_NEW_INDEX]  = the node that is now in place of the removed node
    public Node<T>[] removeAlt(int i)
    {
       if(isEmpty()) return null; //cannot remove from an empty list
       else
       {
            Node[] temp  =   new Node[2]; //used to store the remove node and 'replaced' node
            if(i == 0) //removing first node
             {
                 temp[REMOVE_NODE_INDEX] =  firstNode;
                 firstNode   =   firstNode.getNext();
                 temp[REMOVE_NEW_INDEX]  = firstNode;
             }

            //Removing end node or node somewhere in the middle
            //Iterates until i and removes the node after prev
             else
             {
                 NodeIterator<T> iterator =   new NodeIterator<>(firstNode, i, 1);
                 Node prev = iterator.getEnd();
                 
                 temp[REMOVE_NODE_INDEX] = prev.getNext();
                 prev.setNext(prev.getNext().getNext());
                 temp[REMOVE_NEW_INDEX]  =  prev;
             }
            
             size--; //Node has been removed, decrease size
             return temp;
       }
    }

    //Checks if the list contains the node e
    //Uses indexOf to see that it is in the list
    @Override
    public boolean contains(Node e) 
    {
        return indexOf(e) != -1;
    }
}