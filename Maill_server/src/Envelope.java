import java.io.*;
import java.net.*;
import java.util.*;

/* $Id: Envelope.java,v 1.8 1999/09/06 16:43:20 kangasha Exp $ */

/**
 * SMTP envelope for one mail message.
 *
 * @author Jussi Kangasharju
 */
public class Envelope {
    /* SMTP-sender of the message (in this case, contents of From-header.
    * 定義寄件人*/
    public String Sender;

    /* SMTP-recipient, or contents of To-header.
    * 定義收件人 */
    public String Recipient;

    /* Target MX-host
    * 伺服器主機*/
    public String DestHost;
    public InetAddress DestAddr;

    /* The actual message
    * 定義訊息 */
    public Message Message;

    /* Create the envelope.
    * 定義成物件 */
    public Envelope(Message message, String localServer) throws UnknownHostException {
        /* Get sender and recipient.
         呼叫gerFrom()與getTo()*/
        Sender = message.getFrom();
        Recipient = message.getTo();

	/* Get message. We must escape the message to make sure that
	   there are no single periods on a line. This would mess up
	   sending the mail. */
        Message = escapeMessage(message);

        /* Take the name of the local mailserver and map it into an InetAddress
        獲取localServer的名稱，並映射到InetAddress*/
        DestHost = localServer;
        try {
            DestAddr = InetAddress.getByName(DestHost);
        } catch (UnknownHostException e) {
            System.out.println("Unknown host: " + DestHost);
            System.out.println(e);
            throw e;
        }
        return;
    }

    /* Escape the message by doubling all periods at the beginning of
       a line.
        將一行開頭的所有句點加倍來轉義消息。*/
    private Message escapeMessage(Message message) {
        String escapedBody = "";
        String token;
        StringTokenizer parser = new StringTokenizer(message.Body, "\n", true);

        while(parser.hasMoreTokens()) {
            token = parser.nextToken();
            if(token.startsWith(".")) {
                token = "." + token;
            }
            escapedBody += token;
        }
        message.Body = escapedBody;
        return message;
    }

    /* For printing the envelope. Only for debug.
     印出信封，用於測試 */
    public String toString() {
        String res = "Sender: " + Sender + '\n';
        res += "Recipient: " + Recipient + '\n';
        res += "MX-host: " + DestHost + ", address: " + DestAddr + '\n';
        res += "Message:" + '\n';
        res += Message.toString();

        return res;
    }
}
