//<pre>
/**
 * A class that holds the structure of one (player/non-player) character.
 *
 * @author Bryce Wong (Dimmoro)
 * @version %I%, %G%
 */
public class CharData implements Cloneable{
  private Talker talker = null;
  private Server server = null;

  //***  Variables for handling lists.  ***
  private CharData    prev          = null;
  private CharData    next          = null;

  //Basic attributes.
  private String name = "";

  /**
   * Constructor that initializes data and assigns mobdata to null.
   * Mobdata to null, means this is a player character.
   */
  public CharData(Server _server) {
    this.server = _server;
    this.initCharData(this);
  }

  /**
   * Method that handles
   */
  private static void initCharData(CharData _ch) {
  }

  public void send(String _s)
    {if (this.talker != null) this.talker.send(_s);}

  //***  Methods for handling lists.  ***
    //For a list of characters (charlist.)
  public void setNext(CharData _ch) {this.next = _ch;}
  public CharData getNext() {return this.next;}
  public void setPrev(CharData _ch) {this.prev = _ch;}
  public CharData getPrev() {return this.prev;}

  //Methods to grab local variables.
  public void setTalker(Talker _talker) {this.talker = _talker;}
  public Talker getTalker() {return this.talker;}
  public void setServer(Server _server) {this.server = _server;}
  public Server getServer() {return this.server;}

  public String getName() {return this.name;}
  public void setName(String _name) {this.name = _name;}

  public Object clone()
  {
    //Clone an CharData
    CharData _answer;

    try
    {
      _answer = (CharData) super.clone();
    }
    catch (CloneNotSupportedException e)
    {
      throw new RuntimeException
        ("This class does not implement Cloneable.");
    }

    return _answer;
  }
}
