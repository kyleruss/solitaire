//################################
//Name: Kyle Russell
//ID: 13831056
//DSA Assignment one
//################################

//A simple exception that is thrown to identify a user has made a invalid move during the game
public class InvalidMoveException extends Exception
{
    public InvalidMoveException() {}
    
    public InvalidMoveException(String message)
    {
        super(message);
    }
}