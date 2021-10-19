import java.util.*;
import java.text.*;

/* $Id: Message.java,v 1.5 1999/07/22 12:10:57 kangasha Exp $ */

/**
 * Mail message.
 *檢查信件是否正常(收件人 寄件人)
 * @author Jussi Kangasharju
 */
public class Message {
    /* The headers and the body of the message.
    * 定義headers(信件)與body */
    public String Headers;
    public String Body;

    /* Sender and recipient. With these, we don't need to extract them
       from the headers.
        定義寄件人與收件人*/
    private String From;
    private String To;

    /* To make it look nicer
    * 定義換行 */
    private static final String CRLF = "\r\n";

    /* Create the message object by inserting the required headers from
       RFC 822 (From, To, Date).
       定義header為寄件人+收件人+標題*/
    public Message(String from, String to, String subject, String text) {
        /* Remove whitespace */
        From = from.trim();
        To = to.trim();
        Headers = "From: " + From + CRLF;
        Headers += "To: " + To + CRLF;
        Headers += "Subject: " + subject.trim() + CRLF;

	/* A close approximation of the required format. Unfortunately
	   only GMT. */
        SimpleDateFormat format =
                new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'");
        String dateString = format.format(new Date());
        Headers += "Date: " + dateString + CRLF;
        Body = text;
    }

    /* Two functions to access the sender and recipient.
    * 定義From與To函式 */
    public String getFrom() {
        return From;
    }

    public String getTo() {
        return To;
    }

    /* Check whether the message is valid. In other words, check that
       both sender and recipient contain only one @-sign.
       檢查寄件人與收件人信箱格式是否正確*/
    public boolean isValid() {
        int fromat = From.indexOf('@');
        int toat = To.indexOf('@');

        if(fromat < 1 || (From.length() - fromat) <= 1) {
            System.out.println("Sender address is invalid");
            return false;
        }
        if(toat < 1 || (To.length() - toat) <= 1) {
            System.out.println("Recipient address is invalid");
            return false;
        }
        if(fromat != From.lastIndexOf('@')) {
            System.out.println("Sender address is invalid");
            return false;
        }
        if(toat != To.lastIndexOf('@')) {
            System.out.println("Recipient address is invalid");
            return false;
        }
        return true;
    }

    /* For printing the message.
    * 將header與body打包定義為toString函示*/
    public String toString() {
        String res;

        res = Headers + CRLF;
        res += Body;
        return res;
    }
}
