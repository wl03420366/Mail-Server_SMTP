
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/* $Id: MailClient.java,v 1.7 1999/07/22 12:07:30 kangasha Exp $ */

/**
 * A simple mail client with a GUI for sending mail.
 * 架設GUI介面供使用者寄信
 *
 * @author Jussi Kangasharju
 */
public class MailClient extends Frame {
    /* The stuff for the GUI.
    定義GUI上的物件 */
    private Button btSend = new Button("Send");
    private Button btClear = new Button("Clear");
    private Button btQuit = new Button("Quit");
    private Label serverLabel = new Label("Local mailserver:");
    private TextField serverField = new TextField("", 40);
    private Label fromLabel = new Label("From:");
    private TextField fromField = new TextField("", 40);
    private Label toLabel = new Label("To:");
    private TextField toField = new TextField("", 40);
    private Label subjectLabel = new Label("Subject:");
    private TextField subjectField = new TextField("", 40);
    private Label messageLabel = new Label("Message:");
    private TextArea messageText = new TextArea(10, 40);

    /**
     * Create a new MailClient window with fields for entering all
     * the relevant information (From, To, Subject, and message).
     */
    public MailClient() {
        super("Java Mailclient");

	/* Create panels for holding the fields. To make it look nice,
	   create an extra panel for holding all the child panels.
	    創建面板放入物件*/

        Panel serverPanel = new Panel(new BorderLayout());
        Panel fromPanel = new Panel(new BorderLayout());
        Panel toPanel = new Panel(new BorderLayout());
        Panel subjectPanel = new Panel(new BorderLayout());
        Panel messagePanel = new Panel(new BorderLayout());
        serverPanel.add(serverLabel, BorderLayout.WEST);
        serverPanel.add(serverField, BorderLayout.CENTER);
        fromPanel.add(fromLabel, BorderLayout.WEST);
        fromPanel.add(fromField, BorderLayout.CENTER);
        toPanel.add(toLabel, BorderLayout.WEST);
        toPanel.add(toField, BorderLayout.CENTER);
        subjectPanel.add(subjectLabel, BorderLayout.WEST);
        subjectPanel.add(subjectField, BorderLayout.CENTER);
        messagePanel.add(messageLabel, BorderLayout.NORTH);
        messagePanel.add(messageText, BorderLayout.CENTER);
        Panel fieldPanel = new Panel(new GridLayout(0, 1));
        fieldPanel.add(serverPanel);
        fieldPanel.add(fromPanel);
        fieldPanel.add(toPanel);
        fieldPanel.add(subjectPanel);

	/* Create a panel for the buttons and add listeners to the
	   buttons.
	    創建面板放入按鈕，並為按鈕增加監聽器*/
        Panel buttonPanel = new Panel(new GridLayout(1, 0));
        btSend.addActionListener(new SendListener());
        btClear.addActionListener(new ClearListener());
        btQuit.addActionListener(new QuitListener());
        buttonPanel.add(btSend);
        buttonPanel.add(btClear);
        buttonPanel.add(btQuit);

        /* Add, pack, and show. */
        add(fieldPanel, BorderLayout.NORTH);
        add(messagePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        pack();
        show();
    }

    static public void main(String argv[]) {
        new MailClient();
    }

    /* Handler for the Send-button.
    發送按鈕的處理程序 */
    class SendListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            System.out.println("Sending mail");

            /* Check that we have the local mailserver
            檢查是否有local mailserver */
            if ((serverField.getText()).equals("")) {
                System.out.println("Need name of local mailserver!");
                return;
            }

            /* Check that we have the sender and recipient.
            檢查是否有寄件人與收件人 */
            if((fromField.getText()).equals("")) {
                System.out.println("Need sender!");
                return;
            }
            if((toField.getText()).equals("")) {
                System.out.println("Need recipient!");
                return;
            }
            /*以分號分開，利用for迴圈抓取收件人字串*/
            String[] split = toField.getText().split(";");
            for (int i = 0 ; i<split.length ; i++) {
                System.out.println(split[i]);



                /* Create the message */
                Message mailMessage = new Message(fromField.getText(),
                        split[i],
                        subjectField.getText(),
                        messageText.getText());

	    /* Check that the message is valid, i.e., sender and
	       recipient addresses look ok.
	        檢查寄件者與收件者address是否有效*/
                if (!mailMessage.isValid()) {
                    return;
                }

	    /* Create the envelope, open the connection and try to send
	       the message.
	        建立信封，連接並嘗試發送消息*/
                try {
                    Envelope envelope = new Envelope(mailMessage,
                            serverField.getText());
                    try {
                        SMTPConnection connection = new SMTPConnection(envelope);
                        connection.send(envelope);
                        connection.close();
                    } catch (IOException error) {
                        System.out.println("Sending failed: " + error);
                        return;
                    }
                } catch (UnknownHostException e) {
                    /* If there is an error, do not go further */
                    return;

            }
            System.out.println("Mail sent succesfully!");
        }
        }
    }

    /* Clear the fields on the GUI. */
    class ClearListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.out.println("Clearing fields");
            fromField.setText("");
            toField.setText("");
            subjectField.setText("");
            messageText.setText("");
        }
    }

    /* Quit. */
    class QuitListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }
}
