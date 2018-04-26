//################################
//Name: Kyle Russell
//ID: 13831056
//DSA Assignment one
//################################

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Image;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

//CardShape provides a way to position cards and their elements underneath
//Cards, stacks, lists are represented by a rectangle in the GUI
//Properties such as stroke, colour and image can be changed by the PaintingPanel to react to game events
public class CardShape
{
    private Rectangle2D shape;
    private Color color;
    private Image image;
    private Stroke border;
    
    public CardShape(Rectangle2D shape)
    {
        this(shape, Color.BLACK, null, new BasicStroke(1));
    }
    
    public CardShape(Rectangle2D shape, Image image)
    {
        this(shape, Color.BLACK, image, new BasicStroke(2));
    }
    
    public CardShape(Rectangle2D shape, Color color)
    {
        this(shape, color, null, new BasicStroke(1));
    }
    
    public CardShape(Rectangle2D shape, Color color, Stroke border)
    {
        this(shape, color, null, border);
    }
    
    public CardShape(Rectangle2D shape, Color color, Image image, Stroke border)
    {
        this.shape  =   shape;
        this.color  =   color;
        this.image  =   getScaledImage(image);
        this.border =   border;
    }
    
    //Scales the card images to the CARD_WDITH and CARD_HEIGHT values specified in PaintingPanel
    public Image getScaledImage(Image image)
    {
        if(image != null)
            return image.getScaledInstance(PaintingPanel.CARD_WIDTH, PaintingPanel.CARD_HEIGHT, Image.SCALE_DEFAULT);
        return null;
    }
    
    //Sets the CardShapes shape
    //Rectangle may be resized/changed at later point and re-added
    public void setShape(Rectangle2D shape)
    {
        this.shape  =   shape;
    }
    
    //Returns the CardShapes shape component
    public Shape getShape()
    {
        return shape;
    }
    
    //sets the CardShapes colour
    public void setColor(Color color)
    {
        this.color  =   color;
    }
    
    //Returns the CardShapes Colour
    public Color getColor()
    {
        return color;
    }
    
    //Sets the CardShapes Image
    public void setImage(Image image)
    {
        this.image  =   image;
    }
    
    //Returns the CardShapes image
    public Image getImage()
    {
        return image;
    }
    
    //Returns the CardShapes Stroke
    //The Stroke can be used to draw borders
    public Stroke getStroke()
    {
        return border;
    }
    
    //Sets the CardShapes stroke
    public void setStroke(Stroke border)
    {
        this.border =   border;
    }
    
    //returns true if the shape/card hasn't been drawn yet by having x,y = (-1, -1)
    public boolean isNotSet()
    {
        return shape.getBounds().getX() == -1 && shape.getBounds().getY() == -1;
    }
    
    //Moves the card shape's underlying rectangle to x, y
    public void move(int x, int y)
    {
        shape   =   new Rectangle2D.Double(x, y, PaintingPanel.CARD_WIDTH, PaintingPanel.CARD_HEIGHT);
    }
}