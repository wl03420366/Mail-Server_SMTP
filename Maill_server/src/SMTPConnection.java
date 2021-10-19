import java.net.*;
import java.io.*;
import java.util.*;
/**
   打開與遠端機器的SMTP連接並發送一封郵件。
 */

public class SMTPConnection {

    /* 定義Socket名稱 */
    private Socket connection;

    /* 定義讀取與寫入的Socket名稱*/
    private BufferedReader fromServer;
    private DataOutputStream toServer;

    private static final int SMTP_PORT = 587;
    private static final String CRLF = "\r\n";

    /* Are we connected? Used in close() to determine what to do.
    是否已連上伺服器 */
    private boolean isConnected = false;

    /* 建立SMTPConnection Object。
    建立socket與associated的接口。
    初始化SMTP連接*/
    public SMTPConnection(Envelope envelope) throws IOException {
        connection = new Socket("msa.".concat(envelope.DestHost), SMTP_PORT);
        fromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        toServer = new DataOutputStream(connection.getOutputStream());

/* 讀取伺服器，並返回值220
如果連接失敗則丟回IOException
If not, throw an IOException. */
        String text = fromServer.readLine();
        System.out.println(parseReply(text));
        if (parseReply(text) != 220)
            throw new IOException();
        System.out.println("Mail server ".concat("smtp.").concat(envelope.DestHost).concat("found."));

/* SMTP handshake. We need the name of the local machine.
Send the appropriate SMTP handshake command.
 */
        String localhost = (InetAddress.getLocalHost()).getCanonicalHostName();
        System.out.println("LOCALHOST: "+localhost);
        sendCommand("HELO ".concat(localhost), 250);

        isConnected = true;
    }

    /* Send the message. Write the correct SMTP-commands in the
    correct order. No checking for errors, just throw them to the
    caller.
     發送消息。
     將正確的SMTP命令寫入正確的順序。
     不檢查錯誤，只扔給caller*/
    public void send(Envelope envelope) throws IOException {

/* Send all the necessary commands to send a message. Call
sendCommand() to do the dirty work.
Do _not_ catch the
exception thrown from sendCommand().
 呼叫sendCommand。
 呼叫sendCommand處理dirty work，
 不抓住從sendCommand拋出的異常
 */

        sendCommand("MAIL FROM: ".concat(envelope.Sender),250);
        sendCommand("RCPT TO: ".concat(envelope.Recipient),250);
        sendCommand("DATA",354);
        sendCommand(envelope.Message.toString().concat(CRLF).concat("."),250);
    }

    /* Close the connection. First, terminate on SMTP level, then
    close the socket.
     終止連線，關閉接口*/
    public void close() {
        isConnected = false;
        try {
            sendCommand("QUIT", 221); // closes SMTP
            connection.close(); // closes socket
        } catch (IOException e) {
            System.out.println("Unable to close connection: " + e);
            isConnected = true;
        }
    }

    /* Send an SMTP command to the server. Check that the reply code
    is what is is supposed to be according to RFC 821.
     向伺服器發送SMTP指令，
     檢查的回覆代碼為RFC 821協定*/
    private void sendCommand(String command, int rc) throws IOException{

        /* Write command to server and read reply from server. */
        System.out.println("Command to server: " + command);
        command = command.concat(CRLF); // adds newline at end of command.
        toServer.writeBytes(command);

/* Check that the server’s reply code is the same as the
parameter rc. If not, throw an IOException.
 檢查server的回覆代碼是否為rc，如果不是，則拋出IOException。*/
        String text = fromServer.readLine();
        System.out.println("Reply from server: " + text);
        if (parseReply(text) != rc){
            System.out.println("reply codes do not match");
            throw new IOException();
        }
    }

    /* Parse the reply line from the server. Returns the reply
    code.
     讀取server回傳數值*/
    private int parseReply(String reply) {
        String tmp = reply.substring(0,3); // takes the first three digits of the string
        int i = Integer.parseInt(tmp); // converts the sting to an integer
        return i;
    }

    /* Destructor. Closes the connection if something bad happens.
    * 中斷與server的連線*/
    protected void finalize() throws Throwable {
        if(isConnected) {
            close();
        }
        super.finalize();
    }
}