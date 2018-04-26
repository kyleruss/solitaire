//################################
//Name: Kyle Russell
//ID: 13831056
//DSA Assignment one
//################################

public class Node<T>
{
    private Node next;
    private T value;    //value of the Node in the context of Solitaire will be a Card
    

    public Node(T value)
    {
        this.value   =   value;
    }

    public Node(Node<T> next)
    {
        this.next   =   next;
    }

    //Returns the nodes next node
    public Node<T> getNext()
    {
        return next;
    }

    //Sets the nodes next node
    public void setNext(Node<T> next)
    {
        this.next   =   next;
    }

    //Returns values toString
    @Override
    public String toString()
    {
        return value.toString();
    }
    
    //Return the nodes value (most likely Card object)
    public T getValue()
    {
        return value;
    }

    //Checks if two nodes are equal by comparing each others values
    //Ensures other is a Node before casting to Node
    //Returns the values equals() implementation
    @Override
    public boolean equals(Object other) 
    {
        if(other instanceof Node)   
        {
            Node otherNode  =   (Node)other;
            return this.getValue().equals(otherNode.getValue());
        }
        return false;
    }
}