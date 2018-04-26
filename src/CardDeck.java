//################################
//Name: Kyle Russell
//ID: 13831056
//DSA Assignment one
//################################

import java.util.Random;

public class CardDeck
{
    private CircularlyLinkedList<Card> cards;
    private NodeIterator<Card> iterator;
    private Node<Card> currentCard;
    
    public CardDeck()
    {
        cards       =   new CircularlyLinkedList<>();
        fillDeck();
        iterator    =   (NodeIterator)cards.iterator();
        currentCard =   null;
    }
    
    //Fills the deck in order of rank
    //Once deck is in order, it is shuffled by  shuffleDeck()
    private void fillDeck()
    {
        Card[] deck_temp    =   new Card[52];
        for(int i = 0; i < 52; i++)
            deck_temp[i] = new Card(i);
        shuffleDeck(deck_temp);
        
        for(Card card : deck_temp)
            cards.add(new Node<>(card));
    }
    
    //Shuffles the deck using Fisher-Yates algorithm
    public static void shuffleDeck(Card[] card_deck)
    {
        int rand_index;
        Card temp;
        Random rGen =   new Random();
        for(int i = card_deck.length - 1; i > 0; i--)
        {
            rand_index  =   rGen.nextInt(i + 1);
            temp        =   card_deck[rand_index];
            card_deck[rand_index]   =   card_deck[i];
            card_deck[i]    =   temp; 
        }
        
    }
    
    //Draws a card from the deck
    //Throws an InvalidMoveException if the deck is empty
    public void drawCard() throws InvalidMoveException
    {
        if(iterator.hasNext())
        {
            currentCard =   (currentCard == null)? iterator.getCurrent() : iterator.next(); // init currentcard to first element if null.
            currentCard.getValue();
        }
        else throw new InvalidMoveException("Invalid move - the deck is empty");
    }
    
    //Returns the string representation of all cards in the deck
    @Override
    public String toString()
    {
        return cards.toString();
    }
    
    //Removes and returns the currently opened card in the deck
    public Card takeCard()
    { 
        Node<Card> temp  =   currentCard;
        currentCard = null;
        iterator.next();
        return cards.remove(temp).getValue();
    }
    
    //Returns if the deck is empty
    public boolean isEmpty()
    {
        return !iterator.hasNext();
    }
    
    //Returns the currently opened deck card
    public Card getOpenedCard()
    {
        return (currentCard != null)? currentCard.getValue() : null;
    }
}