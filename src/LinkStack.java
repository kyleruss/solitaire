//################################
//Name: Kyle Russell
//ID: 13831056
//DSA Assignment one
//################################

//Using the singly linked list from LinkedList implementation of a stack to provide a linked stack
public class LinkStack<T> extends LinkedList<T> implements Stack<Node<T>>
{
    //Returns the node at the top of the stack
    @Override
    public Node<T> peek() 
    {
        return get(0);
    }

    //Adds element to the top of the stack
    @Override
    public void push(Node<T> element) 
    {
        add(0, element);
    }

    //Removes and returns the element at the top of the stack
    @Override
    public Node<T> pop()
    {
       return remove(0);
    }
    

}