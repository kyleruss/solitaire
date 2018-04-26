//################################
//Name: Kyle Russell
//ID: 13831056
//DSA Assignment one
//################################

public interface AbstractList <E> extends Structure<E>
{
    //Return the index of the element in the list
    public int indexOf(E e);

    //Return the element at position i
    public E get(int i);

    //Set the element at position i to be element e
    public void set(int i, E e);

    //Add the element e at position i
    public void add(int i, E e);

    //Returns true if the list contains element e
    //Combine with indexOf implementation to see that index != -1
    public boolean contains(E e);
}
