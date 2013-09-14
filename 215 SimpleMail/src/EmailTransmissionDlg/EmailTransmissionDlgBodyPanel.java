/*
 * Title:   EmailTransmissionDlgBodyPanel.java
 * Author:  Joe Maley
 * Date:    April 19 2012
 */

package EmailTransmissionDlg;

import  javax.swing.BoxLayout;
import  javax.swing.JPanel;
import  javax.swing.JTextArea;

public class EmailTransmissionDlgBodyPanel extends JPanel {
    private static final long serialVersionUID = 1014234352354428L;
   
    private JTextArea  bodyTextArea;
    
    public EmailTransmissionDlgBodyPanel() {
        createGUI();
    }
    
    private void createGUI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        initTextFields();
        
        setVisible(true);
    }
    
    private void initTextFields() {
        bodyTextArea = new JTextArea();
        add(bodyTextArea);
    }
    
    public String getBodyText() { return bodyTextArea.getText(); }
}
