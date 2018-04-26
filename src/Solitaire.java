//################################
//Name: Kyle Russell
//ID: 13831056
//DSA Assignment one
//################################

//----------------------------
//     ASSIGNMENT INFO
//----------------------------
//  - Kyle Russell (13831056)  I worked alone.
//  - Extensions: Please see 'Extensions.txt'
//  - ReadME: Please see Readme regarding images and executing



import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;

public class Solitaire
{
    private CardDeck deck;
    private CardStack[] stacks;
    private  CardList[] lists;
    private final Scanner inputScan;
    private int current_time;
    private final Timer gameTimer;
    private PaintingPanel GUIPanel;
    
    public Solitaire()
    {
        deck            =   new CardDeck();
        stacks          =   new CardStack[4];
        lists           =   new CardList[7];
        inputScan       =   new Scanner(System.in);
        current_time    =   0;
        gameTimer       =   new Timer();
        
    }
    
    //Initializes 4 empty stacks
    private void initStacks()
    {
        for(int i = 0; i < stacks.length; i++)
            stacks[i] = new CardStack();
    }
    
    
    //Initializes the games lists
    //Cards taken direct from deck as the original solitaire rules allow
    private void initLists()
    {
        try
        {
            for(int i = 1; i <= lists.length; i++)
            {
                lists[i - 1]    =   new CardList(); 
                for(int j = 0; j < i; j++)
                {
                    deck.drawCard();
                    lists[i - 1].add(deck.takeCard());
                    if(j == i - 1) lists[i - 1].openCard();
                }
            }
        }
        catch(InvalidMoveException e)
        {
            System.out.println("Error initiating lists");
        }
    }
    
    //Called command: Quit
    //Stops timer, prints message and quits application
    public void quitGame()
    {
        String message  =   "Thanks for playing!";
        gameTimer.cancel();
        System.out.println(message);
        System.exit(0);
    }
    
    //Called command: Restart
    //Re-inits deck, stacks, lists and resets time
    public void restartGame() 
    {
        System.out.println("Restarting game...");
        deck        =   new CardDeck();
        stacks      =   new CardStack[4];
        lists       =   new CardList[7];
        initStacks();
        initLists();
        resetTime();
    }
    
    
    //-------------------------------------------------------------------------
    //Calls methods based on game commands 
    //-------------------------------------------------------------------------
    // - DrawCard => deck.DrawCard              -- open card in deck
    // - DeckTo => deckToList                   -- move opened deck card to list
    // - Link => linkTo                         -- link card(s) to other lists
    // - SendList => linkCardToStack            -- send list tail card to a stack
    // - SendDeck => deckCardToStack()          -- send opened deck card to stack
    // - Restart => restartGame()               -- restart the current game
    // - Quit => quitGame()                     -- quits the current game
    public void executeCommand(String command)
    {
        StringTokenizer strTokenizer    =   new StringTokenizer(command, " ");
        try
        {
            switch(strTokenizer.nextToken())
            {
                case "DrawCard":
                    deck.drawCard();
                    System.out.println("You drew: " + deck.getOpenedCard().toString());
                    break;
                case "DeckTo":
                    deckToList(Integer.parseInt(strTokenizer.nextToken()));
                    break;
                case "Link":
                    linkTo
                    (
                         Card.Suit.valueOf(strTokenizer.nextToken()), 
                         Card.Rank.valueOf(strTokenizer.nextToken()),
                         Integer.parseInt(strTokenizer.nextToken()),
                         Integer.parseInt(strTokenizer.nextToken())
                    );
                    break;
                case "SendList":
                    listCardToStack(Integer.parseInt(strTokenizer.nextToken()), -1);
                    break;
                case "SendDeck":
                    deckCardToStack(-1);
                    break;
                case "Restart":
                    restartGame();
                   break;
                case "Quit":
                    quitGame();
                    break;
                default:
                    System.out.println("Invalid command");
                    break;
            }
            
            if(GUIPanel != null)
                GUIPanel.repaint();
        }
        
        catch(InvalidMoveException e)
        {
            System.out.println(e.getMessage());
        }
    }
    
    //The game timer
    //started by startGameTime()
    //each iteration increases the current_time by 1
    //called every 1 second
    private final TimerTask game_time_task  =   new TimerTask()
    {
        @Override
        public void run()
        {
            current_time++;
            if(GUIPanel != null)
                GUIPanel.repaint();
        }
    };
    
    //Returns the games deck
    public CardDeck getDeck()
    {
        return deck;
    }
    
    //Returns the games stacks
    public CardStack[] getStacks()
    {
        return stacks;
    }
    
    //Returns the games lists
    public CardList[] getLists()
    {
        return lists;
    }

    //Resets time
    //stopTime() can be called if a full reset is wanted
    public void resetTime()
    {
        current_time = 0;
    }
    
    //Stops the timer
    //Needs to be called when pausing e.g end game/GUI win screen
    public void stopTime()
    {
        gameTimer.cancel();
        game_time_task.cancel();
        gameTimer.purge();
    }
    
    //Starts the current game timer
    //iterates every 1 second
    //Called at start of game
    public void startGameTime()
    {
        gameTimer.scheduleAtFixedRate(game_time_task, 0, 1000);
    }
    
    //Filteres the game time from purely seconds
    //Into: %min, %seconds
    public String getGameTime()
    {
        String time_str =   "";
        if(((current_time % 3600)/ 60) != 0)
            time_str += (((current_time % 3600))/ 60) + " minute, ";
        time_str += ((current_time % 3600) %60) + " seconds";
        return time_str;
    }
    
    //Checks if the game has won by looking at stacks
    //Game can only be won once all 4 stacks are full with their specific suit
    public boolean checkGameWon()
    {
        for(CardStack stack : stacks)
            if(!stack.isFull()) return false;
        return true;
    }
    
    //Prints a message and exits
    //Called when the game has been won
    public void endGameWithWin()
    {
        System.out.println("You have won in - " + getGameTime());
        quitGame();
    }
    
    //Attempts to link card(s) to another list
    //method process is as close to Solitaire game as possible and may differ from instructions
    //- gets card index in list of card, throws InvalidMoveException if not found
    //- cuts lists[from] at index
    //- attempts to link to lists[to], throws InvalidMoveException if not allowed and links back to lists[from]
    public void linkTo(Card.Suit suit, Card.Rank rank, int from, int to) throws InvalidMoveException
    {
        int card_index  =   lists[from].getCards().indexOf(new Node(new Card(suit, rank, -1)));
        if(card_index == -1 || lists[from].getOpenedIndex() > card_index) throw new InvalidMoveException("Invalid move - Card does not exist or is not opened");
        else
        {
            CardList temp   =   lists[from].cut(card_index);
            if(temp != null)
            {
                //link to lists[to] success
                //lists[from] already cut, open lists[from] tail card if allowed
                if(temp.link(lists[to]))
                    lists[from].openCard();
                
                //linking failed due to rules
                //link back to original list
                //throws InvalidMoveException
                else
                {
                    //Whole list was cut, link back to empty list lists[from]
                    if(lists[from].isEmpty())
                          temp.linkBackToEmptyList(lists[from]);
                    
                    //List was partially cut openedIndex was retained, just link back
                    else
                        temp.link(lists[from]);
                    
                    throw new InvalidMoveException("Invalid move - you cannot link these card(s) to this list");
                }
            }
            
        }
    }
 
    //Searches stacks for matching suit
    //Adds to closest empty stack if no matching suit stack is found
    private int findSuitStack(Card.Suit suit)
    {
        int index   =   -1;
        for(int i =0; i < stacks.length; i++)
        {
            if(stacks[i].getStackSuit() == null && index == -1) index = i; //set index to closest empty stack
            else if(stacks[i].getStackSuit() == suit)
            {
                index = i;
                break;
            }
        }
        return index;
    }
    
    
    //Sends a card (from deck or list) to one of stacks
    //finds appropriate stack based on suit and attempts to add to the stack
    //will throw InvalidMoveException if card violates add to stack rules
    public void sendCardToStack(Card c) throws InvalidMoveException
    {
        int index   =   findSuitStack(c.getSuit());
        stacks[index].add(c);
        System.out.println("Card " + c.toString() + " added to " + stacks[index].getStackSuit() + " stack");
        if(checkGameWon()) 
            endGameWithWin();
    }
    
    //Same as sendCardToStack but instead of searching for the appropriate stack, user specifies which one
    //Used mostly in GUI
    public void sendCardToThisStack(Card c, int stack_ind) throws InvalidMoveException
    {
        stacks[stack_ind].add(c);
        System.out.println("Card " + c.toString() + " added to " + stacks[stack_ind].getStackSuit() + " stack");
        if(checkGameWon()) 
            endGameWithWin();
    }
    
    
    
    //sends the tail of list[list_ind] to an appropriate stack
    //sends to stacks[stack_ind] if stack_ind != -1
    //otherwise find appropriate stack
    //use for directly sending tail card to stack
    //Command: SendList {list_ind}
    public void listCardToStack(int list_ind, int stack_ind) throws InvalidMoveException
    {
        if(lists[list_ind].getTail() == null) throw new InvalidMoveException("No tail card");
        else
        {
            if(stack_ind == -1)
                sendCardToStack(lists[list_ind].getTail().getValue());
            else
                sendCardToThisStack(lists[list_ind].getTail().getValue(), stack_ind);
            lists[list_ind].moveTail();
        }
    }
    
    //sends the opened deck card to an appropriate stack
    //sends to stacks[stack_ind] if stack_ind != -1
    //otherwise find appropriate stack
    //Command: SendDeck
    public void deckCardToStack(int stack_ind) throws InvalidMoveException
    {
        if(deck.getOpenedCard() == null) throw new InvalidMoveException("Invalid move - deck is empty, draw a card first");
        else
        {
            if(stack_ind == -1)
                sendCardToStack(deck.getOpenedCard());
            else
                sendCardToThisStack(deck.getOpenedCard(), stack_ind);
            deck.takeCard();
        }
    }
    
    //Sends the opened deck card to a specific list
    //throws exception if link/add rules don't allow moving deck card or there is no opened deck card
    //conditions to link are checked first before taking a card
    //Command: DeckTo 
    public void deckToList(int list_ind) throws InvalidMoveException
    {
        if(deck.getOpenedCard() == null) throw new InvalidMoveException("Invalid move - deck is empty, draw a card first");
        else if(lists[list_ind].canLink(lists[list_ind].isEmpty()? null : lists[list_ind].getTail(), new Node(deck.getOpenedCard())))
        {
            Card temp   =   deck.takeCard();
            lists[list_ind].add(temp);
        }
        else throw new InvalidMoveException("Cannot move deck card to list " + (list_ind + 1));
    }
    
    //Prints the current step/progress of the game
    //Called each time after a user executes a command
    //Prints the game time, deck status, stack status and cards in the list
    public void printStep()
    {
        System.out.println("\n------- CURRENT STEP -------");
        
        System.out.println("Current game time: " + getGameTime());
        
        //echo deck info
        System.out.print("Card Deck: ");
        System.out.print(deck.isEmpty()? "Is empty" : "Not empty");
        System.out.print("\tOpened card: " + (deck.getOpenedCard() != null? deck.getOpenedCard().toString() : "None") + "\n");
        
        //echo card stacks info
        System.out.print("Card stacks:\t");
        for(int i = 1; i <= stacks.length; i++)
            System.out.print("[" + i + "]" + (stacks[i - 1].isEmpty()? "Empty" : stacks[i - 1].getTopCard().toString()) + "\t");
        System.out.println();
        
        //echo card list info
        System.out.println("Card lists:");
        for(int i = 1; i <= lists.length; i++)
            System.out.println(i + ": " + lists[i - 1].toStringGame() + " [Opened index: " + lists[i - 1].getOpenedIndex() + "]");
    }
    
    //Starts the game, inits deck, lists, stacks and starts the timer
    //run in seperate thread to avoid hanging GUI thread
    //game will go on as long as the user doesn't want to quit or wins
    public void startGame()
    {  
        System.out.println("######### NEW GAME #########");
        
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    initStacks();
                    initLists();
                    startGameTime();
                    while(true)
                    {
                        printStep();
                        System.out.println("Enter your next command: " );
                        executeCommand(inputScan.nextLine());
                        Thread.sleep(1000); //pause to display the message before continuing
                    } 
                }
                catch(InterruptedException e)
                {
                    System.out.println(e.getMessage());
                }
            }
        }).start();
    }
    
    //creates a the container and attaches PaintingPanel
    //PantingPanel holds all of the GUI components and painting
    public void showGUI()
    {
        JFrame frame    =   new JFrame("Solitaire");
        GUIPanel    =   new PaintingPanel(this, frame);
        frame.getContentPane().add(GUIPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
  
    
    
    public static void main(String[] args)
    {
        Solitaire game  =   new Solitaire();
        game.startGame();
        game.showGUI();
    }


}