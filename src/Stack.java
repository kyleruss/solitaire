//################################
//Name: Kyle Russell
//ID: 13831056
//DSA Assignment one
//################################

public interface Stack<E> extends Structure<E>
{
    //Return the top element of the stack
    public E peek();
    
    //add the element to the top of the stack
    public void push(E element);
    
    //remove and return the top element of the stack
    public E pop();
}