/*
 * Title:   EmailTransmissionDlgButtonPanel.java
 * Author:  Joe Maley
 * Date:    April 19 2012
 */

package EmailTransmissionDlg;

import  java.awt.GridLayout;
import  java.awt.event.ActionEvent;
import  java.awt.event.ActionListener;

import  javax.swing.JButton;
import  javax.swing.JPanel;

import SimpleMail.DataStore;

import  javax.mail.*;
import  javax.mail.internet.*;
import  java.util.*;

public class EmailTransmissionDlgButtonPanel extends JPanel implements ActionListener {
    private static final long serialVersionUID = 6862994229141617913L;

    private EmailTransmissionDlg emailTransmissionDlg;
    
    private JButton sendButton;
    private JButton cancelButton;
    
    public EmailTransmissionDlgButtonPanel(EmailTransmissionDlg emailTransmissionDlg) {
        this.emailTransmissionDlg = emailTransmissionDlg;
        
        createGUI();
    }
    
    private void createGUI() {
        setLayout(new GridLayout(1, 2));
        
        initButtons();
        initButtonListeners();
        
        setVisible(true);
    }
    
    private void initButtons() {
        sendButton   = new JButton("Send");
        cancelButton = new JButton("Cancel");
        
        add(sendButton);
        add(cancelButton);
    }
    
    private void initButtonListeners() {
        sendButton.addActionListener(this);
        cancelButton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == sendButton) {
            sendEmail();
            emailTransmissionDlg.setVisible(false);
        } else if (e.getSource() == cancelButton) {
            emailTransmissionDlg.setVisible(false);
        }
    }
    
    private void sendEmail()
    {
        try
        {
            Properties props = System.getProperties();
            props.put("mail.smtp.host", DataStore.getInstance().getConfiguration().getServerIp());

            Session session = Session.getDefaultInstance(props, null);
                    
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(emailTransmissionDlg.getEditorPanel().getSourceEmail()));
                    //domainType = .com, .edu, etc...
            msg.setRecipients(Message.RecipientType.TO,
                              InternetAddress.parse(emailTransmissionDlg.getEditorPanel().getDestEmail()));
            msg.setSubject(emailTransmissionDlg.getEditorPanel().getSubject());
            msg.setText(emailTransmissionDlg.getBodyPanel().getBodyText());
                  
                    // send the message
            Transport.send(msg);
            
            System.out.println("Message sent.");
        } catch (Exception exc) {   
            System.out.println("Error: " + exc);    
        }
    }
}
