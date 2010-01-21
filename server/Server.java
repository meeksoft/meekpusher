//<pre>
import java.net.Socket;
import java.net.ServerSocket;

import java.util.Date;

/**
 * This is the server.
 * Here is the main class.
 * This class handles the connection to and from the mud.
 *
 * @author Bryce Wong (Dimmoro)
 * @version %I%, %G%
 */
public class Server {
  private int                    port = 3197;
  private boolean                readyToRock = true;

  /** Flash policy. */
  private static String policy = "<?xml version=\"1.0\"?><cross-domain-policy><allow-access-from domain=\"*\" to-ports=\"*\" /></cross-domain-policy>\0";

  /** List of connections, players. */
  private static Talker     talkerlist  = null;

  /** The list of all active characters, both NPC (mobs) and PC. */
  private static CharData   charlist    = null;

  /** The Datetime of when the mud was started. */
  private static Date startDate        = new Date();

  /** The running total of players that came on the mud since startup. */
  private static int  totalPlr         = 0;

  public void startup() {
    int _i, _x;
    Socket _newperson;

    //Ready to Rock!!!
    System.out.println("Let's get it ON!");

    try {
      ServerSocket _s = new ServerSocket(this.port);

      while (readyToRock) {
        _newperson = _s.accept();
        _newperson.getOutputStream().write(policy.getBytes()); //Immediately send policy before any reads.
        Talker _talker = new Talker(_newperson, this);
        this.addtoTalkerlist(_talker);
        _talker.start();
        this.totalPlr++;//Increase Total players.
      }
    }
    catch (Exception _e) {
      System.out.println(_e);
    }

  }

  public void stop() {
    this.readyToRock = false;  //Stop accepting Clients.
    System.exit(0);
  }

  //Server param.
  public int getPort() {return this.port;}
  public void setPort(int _i) {this.port = _i;}

  public Talker getTalkerlist() {return this.talkerlist;}

  public int getTotalPlr() {return this.totalPlr;}

  /**
   * This is put Into here because it looked messy when
   * referenced from somewhere else.
   * You had to make the "list" = to the method.
   */
  public Talker addtoTalkerlist(Talker _talker) {
    //Order does not matter.
    _talker.setNext(this.talkerlist);
    this.talkerlist = _talker;
    if (this.talkerlist.getNext() != null)
      this.talkerlist.getNext().setPrev(this.talkerlist);
    return this.talkerlist;
  }
  public Talker removefromTalkerlist(Talker _talker) {
    for (Talker _i = this.talkerlist; _i != null; _i = _i.getNext()) {
      if (_talker == _i) {
        if (_i.getPrev() != null) _i.getPrev().setNext(_i.getNext());
        if (_i.getNext() != null) _i.getNext().setPrev(_i.getPrev());
        //Special case where _talker is the last char in list.
        if (_i.getNext() == null
        &&  _i.getPrev() == null
        &&  _i == this.talkerlist)
          this.talkerlist = null;
        //Special case where _talker is the list.
        if (_i == this.talkerlist)
          this.talkerlist = _i.getNext();
        return this.talkerlist;
      }
    }
    return null;
  }

  public CharData getCharlist() {return this.charlist;}
  public CharData addtoCharlist(CharData _ch) {
    //Order does not matter.
    _ch.setNext(this.charlist);
    this.charlist = _ch;
    if (this.charlist.getNext() != null)
      this.charlist.getNext().setPrev(this.charlist);
    return this.charlist;
  }
  public CharData removefromCharlist(CharData _ch) {
    for (CharData _i = this.charlist; _i != null; _i = _i.getNext()) {
      if (_ch == _i) {
        if (_i.getPrev() != null) _i.getPrev().setNext(_i.getNext());
        if (_i.getNext() != null) _i.getNext().setPrev(_i.getPrev());
        //Special case where _ch is the last char in list.
        if (_i.getNext() == null
        &&  _i.getPrev() == null
        &&  _i == this.charlist)
          this.charlist = null;
        //Special case where _ch is the list.
        if (_i == this.charlist)
          this.charlist = _i.getNext();
        return this.charlist;
      }
    }
    return null;
  }
  public CharData removefromCharlist(String _name) {
    for (CharData _i = this.charlist; _i != null; _i = _i.getNext()) {
      if (_name.equals(_i.getName())) {
        return this.removefromCharlist(_i);
      }
    }
    return null;
  }

  /**
   * Sends a message to everyone on the mud.
   * @param     _str <code>String</code> is the message.
   */
  public void serverEcho (String _str) {
    if (_str == null || _str.equals("")) {
      return;
    }

    for (Talker _talker = this.getTalkerlist();
         _talker != null; _talker = _talker.getNext()) {
      _talker.send(_str);
    }
  }

  /**
   * Sends a message to everyone on the mud.
   * @param     _str <code>JSONMsg</code> is the message object.
   */
  public void serverEcho (JSONMsg _str) {
    if (_str == null) {
      return;
    }

    for (Talker _talker = this.getTalkerlist();
         _talker != null; _talker = _talker.getNext()) {
      _talker.send(_str);
    }
  }

  /**
   * Sends a message to the plr.  The server mainly uses this.
   * @param     _talker <code>Talker</code> is the plr talking.
   * @param     _str <code>String</code> is the message.
   */
  public void send(Talker _talker, String _str) {
    _talker.send(_str);
  }

  /**
   * This is where it all starts.
   */
  public static void main(String[] _args) {
    Server _chatserver = new Server();
    _chatserver.startup();
  }
}
