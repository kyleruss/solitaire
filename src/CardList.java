//################################
//Name: Kyle Russell
//ID: 13831056
//DSA Assignment one
//################################

public class CardList
{
    private LinkedList<Card> cards;
    private Node<Card> tailCard;
    private int openedIndex;
    
    public CardList()
    {
        this(new LinkedList<Card>()); 
    }
    
    public CardList(LinkedList<Card> cards)
    {
        this.cards  =   cards;
        tailCard    =   (cards.isEmpty())? null : cards.get(cards.size() - 1);
        openedIndex =   cards.size() - 1; //start with tail node opened
    }
    
    //Returns the lists tail card
    public Node<Card> getTail()
    {
        return this.tailCard;
    }
    
    //Returns the cards in the list
    public LinkedList<Card> getCards()
    {
        return this.cards;
    }
    
    //Returns the lists opened index
    //Used when cutting and opening cards
    public int getOpenedIndex()
    {
        return openedIndex;
    }
    
    //Sets the tail node of the list
    public void setTail(Node tailCard)
    {
        this.tailCard   =   tailCard;
    }
    
    //To link to another list the game rules follow:
    //if the list is empty, only a king can be added [EXTENSION]
    //otherwise this CardLists head must be opposite in colour and follow consecutively to the other lists tail
    public boolean canLink(Node<Card> tail, Node<Card> head) //tail of joining list and head of temp list
    {
         if(tail == null) return head.getValue().getRank() == Card.Rank.KING; //only king or stack beginning with king can link to empty column.
         return tail.getValue().compareConsecutiveTo(head.getValue()) && tail.getValue().getSuitColor() != head.getValue().getSuitColor();
    }
    
   
    //Attempts to link this card list to another
    //Returns whether the link was successful
    public boolean link(CardList other)
    {
        //The other list is empty, only king may be added
        //For adding cards use add()
        if(other.isEmpty()) 
        {
            if(canLink(null, this.getCards().getHead()))
            {
                linkBackToEmptyList(other);
                return true;
            }
            return false;
        }
        
        //Linking to a non-empty Card list
        else if(other.getOpenedIndex() > other.getCards().size() - 1 || canLink(other.getTail(), this.getCards().getHead()))
        {
            
            other.getTail().setNext(this.getCards().getHead());
            other.getCards().setSize(other.getCards().size() + this.getCards().size());
            other.setTail(this.getTail());
            return true;
        } 
        else return false;
    }
    
    //Method of linking back to a empty list is differnt to linking back to a non-empty list
    public void linkBackToEmptyList(CardList other)
    {
        other.getCards().add(this.getCards().getHead());
        other.setTail(this.getTail());
        other.getCards().setSize(this.getCards().size());
    }
    
    //Opened the last card if the openedIndex is larger than the size
    //This will occur after cutting a list 
    public void openCard()
    {
        if(openedIndex > getCards().size() - 1 || openedIndex == -1)
            openedIndex = getCards().size() - 1;
    }
    
    //Sets the opened index
    public void setOpenedIndex(int openedIndex)
    {
        this.openedIndex    =   openedIndex;
    }
    
    //Cuts the CardList in two => cards before index and after index
    //Returns the second CardList
    //Cards cut from a list and fail to link to a new list must be linked back to the previous list
    public CardList cut(int index)
    {
        if(index > this.getCards().size() - 1 || index < openedIndex) return null;
        //cutting tail
        else if(index == openedIndex && openedIndex == this.getCards().size() - 1)
        {
            CardList otherList  =   new CardList();
            otherList.add(tailCard.getValue());
            moveTail();
            return otherList;
        }
        
        //cutting whole list from start
        else if(index == 0)
        {
            CardList otherList  =   new CardList(this.getCards());
            otherList.getCards().setSize(cards.size());
            cards  =   new LinkedList<>();
            setTail(null);
            return otherList;
        }
        
        //Cutting a list at a standard point, will normally have more than one opened card
        else
        {
            CardList otherList  =   new CardList();
            Node temp   =   cards.get(index - 1);
            otherList.getCards().add(temp.getNext());
            otherList.setTail(tailCard);
            otherList.getCards().setSize(cards.size() - index);
            
            temp.setNext(null);
            tailCard = temp;
            cards.setSize(index);
            return otherList;
        }
    }
    
    //Adds a card to the list
    //When initializing a card list at start of game openedIndex = -1 so you can bypass canLink conditions
    public void add(Card c)
    {
        if(openedIndex == -1 || canLink(this.getTail(), new Node(c)))
        {
           Node c_node =   new Node(c);
           cards.add(c_node);
           tailCard    =   c_node;
        }
        else
            System.out.println("failed to add: " + getTail().toString());
    }
    
    //Returns the string representation of the full list of cards in the CardList
    @Override
    public String toString()
    {
        return cards.toString(); 
    }
    
    //Returns a game version of the CardList
    //Cards opened give their rank and suit
    //Cards unopened are represented by '?'
    //Used strictly in the CLI
    public String toStringGame()
    {
        String str  =   "";
        int count   =   0;
        NodeIterator<Card> iterator   =   (NodeIterator)cards.iterator();
        while(iterator.hasNext())
        {
            if(count >= getOpenedIndex())
                str += iterator.getCurrent().getValue();
            else
                str += "?";
            iterator.next();
            count++;
            if(iterator.hasNext()) str += "->";
        }
        return str;
        
    }
    
    //Returns if the CardList is empty
    public boolean isEmpty()
    {
        return cards.isEmpty();
    }
    
    //Returns the size of the CardList
    public int size()
    {
        return cards.size();
    }
    
    
    //Removes the tail node 
    //card opening is controlled else where
    public Node<Card> moveTail()
    {
        Node<Card>[] removeNodes  =   cards.removeAlt(cards.size() - 1);
        
        tailCard   =   removeNodes[LinkedList.REMOVE_NEW_INDEX];
        return removeNodes[LinkedList.REMOVE_NODE_INDEX];
    }
}