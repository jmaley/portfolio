/*
 * Title:   EmailTransmissionDlgEditorPanel.java
 * Author:  Joe Maley
 * Date:    April 19 2012
 */

package EmailTransmissionDlg;

import  javax.swing.BoxLayout;
import  javax.swing.JComboBox;
import  javax.swing.JLabel;
import  javax.swing.JPanel;
import  javax.swing.JTextField;
import SimpleMail.DataStore;

public class EmailTransmissionDlgEditorPanel extends JPanel {
    private static final long serialVersionUID = 1010592352354428L;
    
    private String[] destEmails;
    private int destEmail;
    
    private JTextField sourceEmailTextField;
    private JComboBox destEmailComboBox;
    private JTextField subjectTextField;

    public EmailTransmissionDlgEditorPanel(String[] destEmails, int destEmail) {
        this.destEmails = destEmails;
        this.destEmail = destEmail;
        
        createGUI();
    }
    
    private void createGUI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        initTextFields();
        
        setVisible(true);
    }
    
    private void initTextFields() {
        sourceEmailTextField = new JTextField(DataStore.getInstance().getConfiguration().getEmail());
        sourceEmailTextField.setEditable(false);
        
        destEmailComboBox = new JComboBox(destEmails);
        destEmailComboBox.setSelectedIndex(destEmail);
        
        subjectTextField = new  JTextField();
      
        add(new JLabel("Source E-Mail: "));
        add(sourceEmailTextField);
        
        add(new JLabel("\n\n\nDestination E-Mail: "));
        add(destEmailComboBox);
        
        add(new JLabel("Subject: "));
        add(subjectTextField);
        
        add(new JLabel("Body: "));
    }
    
    public String getSourceEmail() { return sourceEmailTextField.getText(); }
    public String getDestEmail()   { return (String)destEmailComboBox.getSelectedItem(); }
    public String getSubject()     { return subjectTextField.getText();     }
}
