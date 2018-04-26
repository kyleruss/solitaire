//################################
//Name: Kyle Russell
//ID: 13831056
//DSA Assignment one
//################################

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Card implements Comparable<Card>
{
    //Hold Suit and Rank enums for conveience in adding cards and comparing
    
    //Suit represents the cards 'suit'
    //The suit specific colour is held and can be fetched
    public enum Suit
    {
        CLUBS (Color.BLACK), SPADES (Color.BLACK), DIAMONDS (Color.RED), HEARTS (Color.RED);
        
        private final Color suit_colour;
        Suit(Color suit_colour)
        {
            this.suit_colour = suit_colour;
        }
        
        public Color getSuitColor()
        {
            return this.suit_colour;
        }

    }
    
    //Rank represents the cards 'rank' 
    //the value of the rank is used specifically for comparing and relative to the index
    public enum Rank
    {
        ACE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), TEN(10), JACK(11), QUEEN(12), KING(13);
        
        private final int value;
        Rank(int value)
        {
            this.value  =   value;
        }
        
        public int getValue()
        {
            return this.value;
        }
    }
    
    private final int card_num;
    private final Color suit_color;
    private final Suit suit;
    private final Rank rank;
    private final int cardIndex;
    private CardShape cardShape;
    private String card_image_path;
    
    //Cards rank and suit can be found with a given card index
    public Card(int cardIndex)
    {
        this(
                Suit.values()[cardIndex / 13], 
                Rank.values()[cardIndex % 13], 
                cardIndex
            );
    }
    
    //For convenience cards may be added by just specifying the rank and suit
    public Card(Suit suit, Rank rank, int cardIndex)
    {   
        this.suit        =   suit;
        this.rank        =   rank;
        this.cardIndex   =   (cardIndex == -1)? suit.ordinal() * 13 : cardIndex;
        card_num         =   this.rank.getValue();
        suit_color       =   this.suit.getSuitColor();
        card_image_path  =   getImagePath();
        cardShape        =   new CardShape(new Rectangle2D.Double(-1, -1, PaintingPanel.CARD_WIDTH, PaintingPanel.CARD_HEIGHT), getImage());
        
    }
    
    //Returns the cards suit colour
    public Color getSuitColor()
    {
        return this.suit_color;
    }
    
    //Returns the cards number/rank
    public int getCardNumber()
    {
        return this.card_num;
    }
    
    //Returns the cards suit
    public Suit getSuit()
    {
        return this.suit;
    }
    
    //Returns the cards rank
    public Rank getRank()
    {
        return this.rank;
    }
    
    //Returns the cards index
    //Used when sorting/scrambeling the deck
    public int getIndex()
    {
        return cardIndex;
    }
    
    //Returns the cards string representation of it's suit and rank
    @Override
    public String toString()
    {
        return getSuit() + " " + getRank();
    }
    
    //Sets the CardShape
    public void setCardShape(CardShape cardShape)
    {
        this.cardShape  =   cardShape;
    }
    
    //Returns the CardShape
    public CardShape getCardShape()
    {
        return cardShape;
    }
    
    //Image path must match the following format
    //image path name format: "card_images/rank_of_suit.png"
    //if image is not found images will not be drawn and rectangles will take their place
    public String getImagePath()
    {
        String path     =   "";
        String folder   =   PaintingPanel.IMAGES_FOLDER;
        path            +=  folder;
        if(rank.getValue() > 1 && rank.getValue() <= 10) //cards 2 - 10 have numeric representations, others don't
            path += rank.getValue();
        else
            path += rank;
        
        path += "_of_";
        path += suit;
        path += ".png";
        return path.toLowerCase();
    }
    
    // Returns the cards image
    public Image getImage()
    {
        try
        {
            return ImageIO.read(new File(card_image_path));
        }
        
        catch(IOException e)
        {
        //    JOptionPane.showMessageDialog(null, "Failed to load image: " + card_image_path);
            System.out.println("Failed to load image: " + card_image_path);
            return null;
        }
    }
    
    //Draws the card based on its card shape
    //x, y positions are provided by its shape
    //Draws the cards respective image if found
    //Otherwise draws a rectangle in the cards position
    public void paintThis(Graphics g)
    {
        Graphics2D g2d  =   (Graphics2D) g;
        
        if(cardShape.getImage() != null)
        {
            g2d.drawImage(cardShape.getImage(), (int)cardShape.getShape().getBounds2D().getX(), (int)cardShape.getShape().getBounds2D().getY(), null);
            g2d.setStroke(cardShape.getStroke());
            g2d.setColor(cardShape.getColor());
            g2d.draw(cardShape.getShape()); 
            
        }
        else
        {
            g2d.setColor(this.suit_color);
            g2d.setStroke(cardShape.getStroke());
            g2d.draw(cardShape.getShape());
            g2d.setFont(new Font("Dialog", Font.BOLD, 10));
            g2d.setColor(Color.BLACK);
            g2d.drawString(toString(), (int)cardShape.getShape().getBounds2D().getX()+10, (int)cardShape.getShape().getBounds2D().getY()+50);
            //prevent stroke from cascading onto next, put stroke back to default
            g2d.setStroke(new BasicStroke(1));
        }
    }
    
    //Checks two cards are equal
    //Two cards are considered equal if their Suit and Rank are the same
    @Override
    public boolean equals(Object other)
    {
        if(other instanceof Card)
        {
            Card other_card =   (Card)other;
            return this.suit == other_card.getSuit() && this.rank == other_card.getRank();
        }
        return false;
    }
    
    //Returns two cards follow each consecutively in rank
    //This is used specifically in linking and adding to the stacks
    public boolean compareConsecutiveTo(Card c_other)
    {
        return (this.rank.getValue() - c_other.getRank().getValue()) == 1;
    }

    //Compares two cards based on rank
    @Override
    public int compareTo(Card c_other) 
    { 
       return Integer.compare(rank.getValue(), c_other.rank.getValue());
    }
    
    
}