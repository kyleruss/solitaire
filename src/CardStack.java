//################################
//Name: Kyle Russell
//ID: 13831056
//DSA Assignment one
//################################

public class CardStack
{
    private final LinkStack<Card> stack;
    private Card.Suit stackSuit;
    public final int WIN_CAP    =   13;
    
    public CardStack()
    {
        stack   =   new LinkStack();
        stackSuit    =   null;
    }
    
    //Cards can only be added if the stack is empty and the card is an ace
    //If the stack is not empty then only cards with the same suit as this stacks stacksuit can be added
    public boolean canAdd(Card c)
    {
        if(stack.isEmpty() && c.getRank() == Card.Rank.ACE) return true;
        else if(isFull()) return false;
        else return c.getSuit() == stackSuit && c.compareConsecutiveTo(stack.peek().getValue());
    }
    
    //Adds a card to the stack if the rules allow
    //Throws an exception if rules are violated
    public void add(Card c) throws InvalidMoveException
    {
        if(canAdd(c))
        {
            if(stack.isEmpty()) setSuit(c.getSuit());
            stack.push(new Node(c));
        }
        else throw new InvalidMoveException("Invalid move - card does not belong in this stack");
    }
    
    //Returns if the stack is empty
    public boolean isEmpty()
    {
        return stack.isEmpty();
    }
    
    //Returns the size of the stack
    public int getSize()
    {
        return stack.size();
    }
    
    //Returns whether the stack is full
    //Stack is recognized as full if it contains the maximum amount of cards per suit (13)
    public boolean isFull()
    {
        return getSize() == WIN_CAP;
    }
  
    //Returns the top card
    //Used by GUi and CLI to display the top card
    public Card getTopCard()
    {
        return stack.peek().getValue();
    }
    
    //Returns the stacks stack suit
    //The stack suit defines what suit this stack has and only cards of the same suit may be added
    public Card.Suit getStackSuit()
    {
        return stackSuit;
    }
    
    //Sets the stack suit, see above
    public void setSuit(Card.Suit suit)
    {
        this.stackSuit   =   suit;
    }
    
}