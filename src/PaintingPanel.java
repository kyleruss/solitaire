//################################
//Name: Kyle Russell
//ID: 13831056
//DSA Assignment one
//################################

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class PaintingPanel extends JPanel implements ActionListener
{
    private Solitaire game;
    private Rectangle2D deck;
    private CardShape[] stacks;
    private CardShape[] lists;
    private CardList activeMovingCards;
    private int prev_list = -1;
    private int activeMovingCard_originalX  = -1;
    private int activeMovingCard_originalY  = -1;
    private Card hoveredCard;
    private Image bgImage;
    private Image cardBack;
    public static final int CARD_WIDTH    =   100;
    public static final int CARD_HEIGHT   =   140;
    public static int WINDOW_WIDTH   =  1050;
    public static int WINDOW_HEIGHT  =  800;
    private final JMenuBar menu_bar;
    private final JMenu game_menu;
    private final JMenuItem new_game_item;
    private final JFrame parent;
    private WinPanel winPanel;
    private String error_string;
    public static final String IMAGES_FOLDER  =   "card_images/";
    
    public PaintingPanel(Solitaire game, JFrame parent)
    {
        super();
        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            JOptionPane.showMessageDialog(null, "UI Error: " + e.getMessage());
            e.printStackTrace();
        } 
        
        try
        {
            //The games background image
            bgImage =   ImageIO.read(new File(IMAGES_FOLDER + "solitairebg.png"));
            
            //Card back image, showed for deck, unopened cards etc.
            cardBack    =   ImageIO.read(new File(IMAGES_FOLDER + "cardBack.png")).getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_DEFAULT);
        }
        catch(IOException e)
        {
            JOptionPane.showMessageDialog(null, "Failed to load image");
        }
        
        this.game       =   game;
        this.parent     =   parent; //reference kept to allow easy adding to it e.g the menu bar
        
        //Menu bar components
        menu_bar        =   new JMenuBar();
        game_menu       =   new JMenu("Game");
        new_game_item   =   new JMenuItem("New game");
        new_game_item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        menu_bar.add(game_menu);
        game_menu.add(new_game_item);
        this.parent.setJMenuBar(menu_bar);
        
        new_game_item.addActionListener(this);
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    
    //Draws the current game
    //Includes drawing:
    // Deck, opened deck cards, stacks, lists,
    //active moving cards, hovered cards, error string, timer
    
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d  =   (Graphics2D) g.create();
        
        //Draw background image
        //Will be blank if image was not found
        g.drawImage(bgImage, 0, 0, null);
        
        //Draw the current games progress
        //Takes values from game and draws them
        drawDeck(g2d);
        drawOpenedDeckCard(g2d);
        drawStacks(g2d);
        drawLists(g2d);
        
        //Paint any active moving cards
        //These cards are shown when dragging card(s)
        if(activeMovingCards != null)
        {
            Node<Card> temp    =   activeMovingCards.getCards().getHead();
            while(temp != null)
            {
                temp.getValue().paintThis(g);
                temp    =   temp.getNext();
            }
        }
        
        //Draw game time
        //Inline x with deck
        String game_time    =   "Time: " + game.getGameTime();
        g.setColor(Color.WHITE);
        g.setFont(new Font("Dialog", Font.PLAIN, 20));
        g.drawString(game_time, (int)deck.getX(), 35);//(WINDOW_WIDTH/2 - game_time.length()), 35);
        
        //Draws the hovered card, typically the stroke is changed to show the card is being hovered
        //Only one card can be hovered at any given time
        if(hoveredCard != null)
            hoveredCard.paintThis(g);
        
        //Draws the error string
        //error_string is not null if a recent InvalidMoveException has been thrown
        //Draws the string inline with the stacks and inline with the game time
        if(error_string != null)
        {
            g.setColor(Color.WHITE);
            g.drawString(error_string, (int)stacks[0].getShape().getBounds2D().getX(), 35);
        }
    }
    
    //Draws the deck
    //The deck rectangle is used as a relative point for most things on the screen
    //Uses the cardBack as the default image
    //If not found will draw a simple rectangle 
    public void drawDeck(Graphics2D g)            
    {
        
        int x   =   20;
        int y   =   80;
        deck    =   new Rectangle2D.Double(x, y, CARD_WIDTH, CARD_HEIGHT);
        
        g.setColor(new Color(255, 0, 0, 0));
        g.draw(deck);
        if(cardBack != null)
            g.drawImage(cardBack, x, y, null);
        else
        {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Dialog", Font.BOLD, 14));

            g.drawString("DECK", x+30, x+50);
            g.drawString((game.getDeck().isEmpty()? "EMPTY" : "NOT EMPTY"), x+10, x+70);
        }
    }
    
    //Draws the opened deck card if there is one
    //Draws the opened card to the right of the deck
    public void drawOpenedDeckCard(Graphics2D g)
    {
       Card openedCard  =   game.getDeck().getOpenedCard();

       if(openedCard == null) return;
       else
       {
           if(openedCard.getCardShape().isNotSet())
                openedCard.getCardShape().move((int)deck.getX() + 150, (int)deck.getY());

            openedCard.paintThis(g);
       }
    }
    
    //Draws the card stacks
    //Draws empty rectangles if there is no card at the top of the stack
    //Draws a string "FULL" if the stack is full
    //stack rectangles are kept in stacks array to allow checking of positions 
    public void drawStacks(Graphics2D g)
    {
        int initial_x   =   460;
        int initial_y   =   (int)deck.getY();
        if(stacks == null) stacks   =   new CardShape[4];
        for(int i = 0; i < game.getStacks().length; i++)
        {
            //init stacks if not already, rects are stored for mouse click check
            if(stacks[i] == null)
            {
                int next_x  =   initial_x + (150 * i);
                stacks[i]   =   new CardShape(new Rectangle2D.Double(next_x, initial_y, CARD_WIDTH, CARD_HEIGHT), new Color(0,0,0,50), new BasicStroke(10))  ; // 50 px gap between 
            }
            
            //draw the stack shape with a border
            g.setStroke(stacks[i].getStroke());
            g.setColor(stacks[i].getColor());
            g.draw(stacks[i].getShape()); 

            
            //draw full stacks differently 
            if(game.getStacks()[i].isFull())
            {
                String full_message =   "FULL";
                g.setColor(Color.WHITE);
                g.drawString(full_message,
                        //takes stack default x pos + width of stack shape to center and minuses the length of the string and the stroke size to properly horizontally center 
                        ((int)stacks[i].getShape().getBounds2D().getX() + ((int)stacks[i].getShape().getBounds2D().getWidth() / 2)) - full_message.length() - 10, 
                        (int)stacks[i].getShape().getBounds2D().getY() + ((int)stacks[i].getShape().getBounds2D().getHeight() / 2));
            }
            
            //draw the top card if the stack is not empty or full
            else if(!game.getStacks()[i].isEmpty())
            {
                game.getStacks()[i].getTopCard().getCardShape().move((int)stacks[i].getShape().getBounds2D().getX(), (int)stacks[i].getShape().getBounds2D().getY());
                game.getStacks()[i].getTopCard().paintThis(g);
            }
                
        }
    }
    
    //Draws the games lists
    public void drawLists(Graphics2D g)
    {
        int initial_x   =    (int)deck.getX();
        int initial_y   =   (int)deck.getY() + CARD_HEIGHT + 50;
  
        for(int list_index = 0; list_index < game.getLists().length; list_index++)
        {
            int next_x  =   initial_x + (150 * list_index);
            if(lists == null) lists =   new CardShape[7];
            lists[list_index] =   new CardShape(new Rectangle2D.Double(next_x, initial_y, CARD_WIDTH, CARD_HEIGHT));
            //draw outline of empty list if no cards in list
            if(game.getLists()[list_index].isEmpty())
            {
                g.setStroke(new BasicStroke(1));
                g.draw(lists[list_index].getShape());
                repaint();
                continue;
            }
            
            //draw cards in game.getLists[list_index]
            for(int card_index = 0; card_index < game.getLists()[list_index].size(); card_index++)
            {
                //by default, unopened card will have show 20px in list, opened shows 50px
                int next_y      =  initial_y + (20 * card_index);
                Card current    =  game.getLists()[list_index].getCards().get(card_index).getValue();
                
                //displaying opened cards
                if(card_index >= game.getLists()[list_index].getOpenedIndex()) //&& game.getLists()[list_index].getOpenedIndex())
                {
                    //index of opened cards relative to index cards
                    //used to increase y proportional to the index of the opened card
                    int relativeOpenedIndex =   card_index - game.getLists()[list_index].getOpenedIndex();
                    
                    if(card_index > game.getLists()[list_index].getOpenedIndex())//|| ()
                        current.getCardShape().move(next_x, next_y + (relativeOpenedIndex * 50));
                    else 
                        current.getCardShape().move(next_x, next_y);
                    
                    
                    //increase list height to lists[list_ind].height + height of opened card (+100)
                    lists[list_index] = new CardShape(new Rectangle2D.Double(next_x, initial_y, CARD_WIDTH, lists[list_index].getShape().getBounds2D().getHeight() + CARD_HEIGHT));
                    current.paintThis(g);
                }
                else
                {
                    current.getCardShape().move(next_x, next_y);
                    g.drawImage(cardBack, next_x, next_y, CARD_WIDTH, CARD_HEIGHT, null);
                    
                    //increase list height to lists[list_ind].height  + height of closed card (+20)
                    lists[list_index] = new CardShape(new Rectangle2D.Double(next_x, initial_y, CARD_WIDTH, lists[list_index].getShape().getBounds2D().getHeight() + 20));
                }
            }
        }
    }
    
    //Shows the game winning panel
    //Called when all four stacks are filled
    //Removes listeners to prevent interference with listeners in WinPanel
    public void showWinScreen()
    {
        if(!game.checkGameWon()) return;
        else
        {
            game.stopTime();
            winPanel    =   new WinPanel(game.getGameTime());
            add(winPanel);
            removeMouseListener(mouseAdapter);
            removeMouseMotionListener(mouseAdapter);
            revalidate();
            
        }
    }
    
    //Closes the current WinPanel
    //and restores the MouseListener and MouseMotionListener
    public void removeWinPanel()
    {
        remove(winPanel);
        winPanel    =   null;
        revalidate();
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
        game.restartGame();
        repaint();
    }
    
    //Finds which stack was clicked/hovered
    //Used when trying to drop cards onto the stack
    public int getClickedStack(double x, double y)
    {
        for(int i = 0; i < stacks.length; i++)
            if(stacks[i].getShape().contains(x, y)) return i;
        return -1;
            
    }
    
    //Returns which list was hovered over/dropped on
    //Used by mouseReleased to derive which list to link to
    public int getDroppedList(double x, double y)
    {
        for(int list_index = 0; list_index < lists.length; list_index++)
            if(lists[list_index].getShape().contains(x, y)) return list_index;
        return -1;
    }
    
    //Returns the which (opened) card was clicked
    //loops through the lists and then the cards in the list to find which card was clicked
    public int[] getClickedListCard(double x, double y)
    {
        //card_index[0] = list index
        //card_index[1] = card index in list
        int[] card_index    =   new int[2];
        for(int i = 0; i < lists.length; i++)
            if(lists[i].getShape().contains(x, y))
            {
                //find highlighted card in list 
                for(int j = 0; j < game.getLists()[i].size(); j++)
                {
                    if(game.getLists()[i].getCards().get(j).getValue().getCardShape().getShape().contains(x, y) && j >= game.getLists()[i].getOpenedIndex())
                    {
                        card_index[0]   =   i;
                        card_index[1]   =   j;
                        return card_index;
                    }
                }
            }
        //card was not found in any list
        card_index[0]   =   -1;
        card_index[1]   =   -1;
        return card_index;
    }

    //Resets the currently moving/active card
    //Called when a InvalidMoveException is thrown or card is released no where
    //When redrawn the previously moving cards will be reset to their previous positions
    public void resetActiveCard()
    {
        if(activeMovingCards != null)
        {
           if(prev_list != -1)
            {
                if(game.getLists()[prev_list].isEmpty())
                    activeMovingCards.linkBackToEmptyList(game.getLists()[prev_list]);
                else
                    activeMovingCards.link(game.getLists()[prev_list]);
                activeMovingCards   =   null;
                repaint();
            } 
            
            // Resets the deck card
            else
            {
                activeMovingCards.getCards().getHead().getValue().getCardShape().move(activeMovingCard_originalX, activeMovingCard_originalY);
                activeMovingCards   =   null;
                activeMovingCard_originalX =   -1;
                activeMovingCard_originalY =   -1;
                repaint();
           }
        }
    }
    
    //Sets the currently hovered card
    //Only one card may be hovered at a time
    //Hovered card will have increased stroke and different fill colour
    private void setHoveredCard(Card card)
    {
        
        if(card != null)
        {
            hoveredCard    =   card;
            hoveredCard.getCardShape().setColor(Color.DARK_GRAY);
            hoveredCard.getCardShape().setStroke(new BasicStroke(6));
            repaint();
        }
        else
        {
            hoveredCard.getCardShape().setStroke(new BasicStroke(2));
            hoveredCard.getCardShape().setColor(Color.BLACK);
            repaint();
            hoveredCard =   null;
        }
    }   

    //Used by the menu
    //Currently only menu item is to create/reset the game
    @Override
    public void actionPerformed(ActionEvent e)
    {
        Object src  =   e.getSource();
        if(src == new_game_item)
        {
            game.restartGame();
            repaint();
        }
    }
    
    
    MouseAdapter mouseAdapter   =   new MouseAdapter()
    {
        
        @Override
         public void mouseClicked(MouseEvent e) 
         {
            //Mouse clicked on deck
            //Draw card
            if(deck.contains(e.getX(), e.getY()))
            {
                try
                {
                    game.getDeck().drawCard();
                    error_string    =   null;
                    repaint();
                }
                
                catch(InvalidMoveException  ex)
                {
                    error_string =  ex.getMessage();
                    repaint();
                    
                }
            }
         }
         
         @Override
         public void mousePressed(MouseEvent e)
         {
              if(game.getDeck().getOpenedCard() != null && game.getDeck().getOpenedCard().getCardShape().getShape().contains(e.getX(), e.getY()))
              {
                    activeMovingCards    =   new CardList(); 
                    activeMovingCards.add(game.getDeck().getOpenedCard());
                    activeMovingCard_originalX  =   (int)activeMovingCards.getCards().getHead().getValue().getCardShape().getShape().getBounds2D().getX();
                    activeMovingCard_originalY  =   (int)activeMovingCards.getCards().getHead().getValue().getCardShape().getShape().getBounds2D().getY();
                    return;
              }
              
              int[] list_hover_index    =   getClickedListCard(e.getX(), e.getY());
              if(list_hover_index[0] != -1 && list_hover_index[1] != -1)
              {
                  try
                  {
                        //[EXTENSION]
                        //Right clicking an opened card will try to add it to an appropriate stack
                        if(e.getButton() == MouseEvent.BUTTON3)
                        {
                            game.listCardToStack(list_hover_index[0], -1);
                            game.getLists()[list_hover_index[0]].openCard();
                            repaint();
                        }
                        
                        //Cuts the list at the clicked card
                        //sets the activeMovingCards to the cut list so you can move the list in MouseDragged
                        //prev_list is set as the list that the list was cut from so if InvalidMoveException is thrown list is returned to prev_list
                        else if(e.getButton() == MouseEvent.BUTTON1)
                        {
                          activeMovingCards  =   game.getLists()[list_hover_index[0]].cut(list_hover_index[1]); 
                          prev_list   =   list_hover_index[0];
                        }
                  }
                  catch(InvalidMoveException ex)
                  {
                      error_string  =   ex.getMessage();
                      repaint();
                  }
              }  
         }
         
        @Override
         public void mouseReleased(MouseEvent e)
         {
             if(activeMovingCards != null)
             {
                try
                {
                    //Card has been dropped onto a stack
                    //Find which stack and attempt to add to it
                    int dropped_stack_index =   getClickedStack(e.getX(), e.getY());
                    if(dropped_stack_index != -1)
                    {
                        //move opened deck card to stacks[dropped_stack_index]
                        if(game.getDeck().getOpenedCard() != null && activeMovingCards.getCards().getHead().getValue().equals(game.getDeck().getOpenedCard()))
                        {
                            game.deckCardToStack(dropped_stack_index);
                            showWinScreen();
                        }
                        
                        //move list card to stacks[dropped_stack_index]
                        //cannot use listCardToStack() as cut was used and tail of prev list == null 
                        else if(prev_list != -1)
                        {   
                            game.sendCardToThisStack(activeMovingCards.getCards().getHead().getValue(), dropped_stack_index);
                            game.getLists()[prev_list].openCard();
                            showWinScreen();
                        }
                        
                        repaint();
                        return;
                    }
                    
                    //Card was dropped onto a list
                    //Find which list and attempt to link activeMovingCards to it
                    int dropped_list_index  =   getDroppedList(e.getX(), e.getY());
                    if(dropped_list_index != -1)
                    {
                        //attempting to link cut list to another list
                        if(prev_list != -1)
                        {
                            //Linking failed, reset the moving cards and link back if necessary
                            if(!activeMovingCards.link(game.getLists()[dropped_list_index])) 
                                throw new InvalidMoveException("Invalid move - you cannot link these card(s) to this list");
                            
                            //Linking worked (use same process as in Solitaire linkTo
                            else
                            {
                                if(game.getLists()[prev_list].isEmpty())
                                    game.getLists()[prev_list].setOpenedIndex(0);
                                else
                                    game.getLists()[prev_list].openCard();
                                repaint();
                            }
                        }
                        //dropping open deck card onto a list
                        else if(activeMovingCards.getCards().getHead().getValue().equals(game.getDeck().getOpenedCard()))
                        {
                            game.deckToList(dropped_list_index);
                            repaint();
                        }
                    } 
                    
                    //Card was dropped onto nothing, reset the cards
                    else
                        resetActiveCard();
                }
                
                //If InvalidMoveException is thrown from failed linking/adding
                //print the error string (repainted in resetActiveCard)
                //Reset the moving cards
                catch(InvalidMoveException ex)
                {
                    error_string = ex.getMessage();
                    resetActiveCard();
                }
                
                //Regardless of link/add outcome, active moving cards and prev_list need to be reset
                finally
                {
                    activeMovingCards   =   null;
                    prev_list   =   -1;
                    activeMovingCard_originalX  =   -1;
                    activeMovingCard_originalY  =   -1;
                }
             }
         }
         
         @Override
         public void mouseDragged(MouseEvent e)
         {
             //activeMovingCards set when a card is pressed
             if(activeMovingCards != null)
             {
                 //Reset the error_string when dragging since new move is about to happen
                 if(error_string != null)
                     error_string = null;
                 
                 Node<Card> temp    =   activeMovingCards.getCards().getHead();
                 int count  =   0; //number of cards in activeMovingCards
                 while(temp != null)
                 {
                     //Move the card to the current x, y
                     //center card around mouse pointer
                     temp.getValue().getCardShape().move(e.getX() - (CARD_WIDTH / 2), e.getY() + ((count > 0)? 70 : 0) - (CARD_HEIGHT / 2));
                     temp   =   temp.getNext();
                     count++;
                 }
                 repaint();


             }
         }
         
         @Override
         public void mouseMoved(MouseEvent e)
         {
             if(deck.contains(e.getX(), e.getY())) {}
             else
             {
                    if(game.getDeck().getOpenedCard() != null)
                    {
                        //hover effect for opened deck card
                        if(game.getDeck().getOpenedCard().getCardShape().getShape().contains(e.getX(), e.getY()))
                        {
                            setHoveredCard(game.getDeck().getOpenedCard());
                            return;
                        }
                        
                    }
                    
                    int stack_click_index   =   getClickedStack(e.getX(), e.getY());
                    if(stack_click_index != -1) //Hover over a stack 
                    {
                        stacks[stack_click_index].setColor(new Color(0,0,0,100));
                        stacks[stack_click_index].setStroke(new BasicStroke(10));
                        repaint();
                    }
                    else //hover outside of stacks => reset to non-hover effect
                    {
                        for(CardShape stack : stacks)
                        {
                            stack.setStroke(new BasicStroke(10));
                            stack.setColor(new Color(0,0,0,50));
                        }
                        repaint();
                    }
                    
                    //Hovering over list card
                    //Give hover effect to only opened cards in a list
                    int[] list_hover_index    =   getClickedListCard(e.getX(), e.getY());
                    if(list_hover_index[0] != -1 && list_hover_index[1] != -1)
                    {
                        //bug occurs when hovering over cards in same list as they overlap
                        //below block prevents multiple cards being hovered over in same list
                        if(hoveredCard != null)
                        {
                            Node<Card> openedCard =   game.getLists()[list_hover_index[0]].getCards().get(game.getLists()[list_hover_index[0]].getOpenedIndex());
                            while(openedCard != null)
                            {
                                openedCard.getValue().getCardShape().setStroke(new BasicStroke(2));
                                openedCard  =   openedCard.getNext();
                            }
                            repaint();
                        } 
                        
                        setHoveredCard(game.getLists()[list_hover_index[0]].getCards().get(list_hover_index[1]).getValue());

                    }
                    
                    else
                    {
                        if(hoveredCard != null)
                            setHoveredCard(null);
                    }                    
             }
         } 
    };
    
    //The WinPanel is a Jpanel added when the game has been won
    public class WinPanel extends JPanel
    {
        private Rectangle2D playAgain;
        private Rectangle2D quit;
        private final String finishTime;
        private final String end_message    =   "GAME FINISHED";
        private final String play_again_message =   "Play again";
        private final String quit_game_message  =   "Quit";
        public WinPanel(String finishTime)
        {
            setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
            playAgain           =   new Rectangle2D.Double((getPreferredSize().width / 2) - 150, 180, 300, 100);
            quit                =   new Rectangle2D.Double((getPreferredSize().width / 2) - 150, 180 + 120, 300, 100);
            this.finishTime     =   "You won in: " + finishTime;
            setOpaque(false);
            
            addMouseListener(winPanelMouseAdapter);
        }

        //Draws the WinPanel graphics
        @Override
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Graphics2D g2d  =   (Graphics2D)g;
            
            //Draws the background colour
            g.setColor(new Color(0,0,0, 230));
            g.fillRect(0, 0, getPreferredSize().width, getPreferredSize().height);
            
            //Draws the end message at top center
            g.setColor(Color.WHITE);
            g.setFont(new Font("Dialog", Font.BOLD, 28));
            g.drawString(end_message, (getPreferredSize().width / 2) - end_message.length()-100, 100);
            
            //Draws the finish time at center below end message
            g.setColor(new Color(239, 250, 115));
            g.setFont(new Font("Dialog", Font.PLAIN, 20));
            g.drawString(finishTime, (getPreferredSize().width / 2) - 100, 140);
            
            //Buttons are drawn as apposed to components for easier customization
            //Draws the playAgain button
            g.setColor(new Color(85, 220, 112));
            g2d.fill(playAgain);
            
            //Draws the quit button
            g.setColor(new Color(240, 84, 92));
            g2d.fill(quit);
            
            g.setColor(Color.WHITE);
            g.setFont(new Font("Dialog", Font.PLAIN, 20));
            
            //Draws the play again message inside the playAgain button
            g.drawString(play_again_message, 
                    (int)(playAgain.getX() + ((playAgain.getWidth() /2)) - play_again_message.length() - 30),
                    (int)(playAgain.getY() + ((playAgain.getHeight() /2))));
            
            //Draws the quit game message inside the quit button
            g.drawString(quit_game_message, 
                    (int)(quit.getX() + ((quit.getWidth() /2)) - quit_game_message.length() - 20),
                    (int)(quit.getY() + ((quit.getHeight() /2))));
        }
        
        private final MouseAdapter winPanelMouseAdapter   =   new MouseAdapter()
        {
            //Listener for the playAgain and quit buttons
            @Override
            public void mouseClicked(MouseEvent e)
            {
                //Reset the game
                if(playAgain.contains(e.getX(), e.getY()))
                    PaintingPanel.this.removeWinPanel();
                
                //Quit the game
                else if(quit.contains(e.getX(), e.getY()))
                    game.quitGame();
            }
        };
                

    }
  
    
}