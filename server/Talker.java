//<pre>
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.io.OutputStream;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

/**
 * A class that handles the connection to the server.
 *
 * @author Bryce Wong (Dimmoro)
 * @version %I%, %G%
 */
public class Talker extends Thread {

  private Socket          chatsocket;
  private Server          server;
  private BufferedReader  in;
  private OutputStream    out;
  private boolean         finished;  //Checks incomming-line for null.
  private Talker          prev;
  private Talker          next;
  private CharData        ch;
  private Gson            gson;

  Talker (Socket _cs, Server _s) {
    this.chatsocket = _cs;
    this.server     = _s;
    this.prev       = null;
    this.next       = null;
    this.gson       = new Gson();
  }

  public void run(){
    String _str;

    try {
      this.in  = new BufferedReader(new InputStreamReader(
        this.chatsocket.getInputStream()));
      this.out = this.chatsocket.getOutputStream();
      this.finished = false;

      //Start login.
      this.ch = new CharData(this.server);
      this.ch.setTalker(this);

      //Handle Commands.
      while (!this.finished) {
        _str = this.in.readLine();
        if (_str==null)
          this.finished = true;
        else {
          this.interpret(_str);
        }
      }
      this.chatsocket.close();
    }
    catch (IOException _e) {
      System.out.println("Client probably closed.");
      System.out.println("Talker: IO: " + _e);
    }

    this.server.removefromTalkerlist(this);
  }
  
  private void interpret(String _str) {
    _str = _str.trim();
    if (_str.length() < 1) return; //May not be needed.
    if (_str.equals("<policy-file-request/>")) return; //Eat the request.  Policy has already been returned in Server.java.
    
    try {
      JSONMsg msg = gson.fromJson(_str,JSONMsg.class);
      JSONMsg newMsg = new JSONMsg();
  
      if (msg.messageType.equals("SAY")) {
        newMsg.messageType = "SAY";
        newMsg.jsonStr = msg.jsonStr;
        this.server.serverEcho(newMsg);
      }

    } catch (JsonParseException _jpe) {
      System.out.println("Talker.interpret GSON Exception:"+_jpe.getMessage()+" [Str:"+_str+"]");
    } catch (Exception _e) {
      System.out.println("Talker.interpret Exception:"+_e.getMessage());
    }
  }

  public void setFinished(boolean _f) {this.finished = _f;}

  public void setCh(CharData _ch) {this.ch =_ch;}
  public CharData getCh() {return this.ch;}
  public void addCh(CharData _ch) {
    //Add Character to World list and set the connection to the ch.
    this.ch = _ch;
    this.server.addtoCharlist(_ch);
    this.ch.setTalker(this);  //This connects the Ch to the Talker.
  }
  //Removes all instances of this Character from the entire server.
  public CharData removeCh(CharData _ch) {
    //Need to also add statements to remove all pointers to _ch.
    //I.E. from rooms, battlelist/fighting.
    this.server.removefromCharlist(_ch);

    return _ch;
  }

  public Server getServer() {return this.server;}

  public void send(JSONMsg _msg) {
    try {
      String _json = this.gson.toJson(_msg) + "\0"; //\0 required for flash.
      this.out.write(_json.getBytes());
    } catch (JsonParseException _jpe) {
      System.out.println("Talker.sendJSON GSON Exception:"+_jpe.getMessage());
    } catch (IOException _ioe) {
      System.out.println("Talker.sendJSON IO Error: "+_ioe);
    }
  }

  public void send(String _str) {
    try {
      _str += "\0"; //\0 required for flash.
      this.out.write(_str.getBytes());
    } catch (IOException _ioe) {
      System.out.println("Talker.send IO Error: "+_ioe);
    }
  }

   /**
   * This is the command to quit.
   */
  public void do_quit () {
    //Quit player.
    this.setFinished(true);
  }

 //To set lists.
  public void setNext(Talker _t) {this.next = _t;}
  public Talker getNext() {return this.next;}
  public void setPrev(Talker _t) {this.prev = _t;}
  public Talker getPrev() {return this.prev;}
}
