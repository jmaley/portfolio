/*
 * Title:   EmailTransmissionDlg.java
 * Author:  Joe Maley
 * Date:    April 19 2012
 */

package EmailTransmissionDlg;

import java.awt.BorderLayout;
import java.awt.Toolkit;

import javax.swing.JDialog;

public class EmailTransmissionDlg extends JDialog {
    private static final long serialVersionUID = 704287253122212714L;
    private final short[] frameSize = {640, 480};
    
    private String[] destEmails;
    private int      destEmail;
    
    private EmailTransmissionDlgEditorPanel editorPanel;
    private EmailTransmissionDlgBodyPanel   bodyPanel;
    private EmailTransmissionDlgButtonPanel buttonPanel;
    
    public EmailTransmissionDlg(String[] destEmails, int destEmail) {
        this.destEmails = destEmails;
        this.destEmail  = destEmail;
        
        createGUI();
    }
    
    private void createGUI() {
        setSize(frameSize[0], frameSize[1]);
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width  - frameSize[0]) / 2,
                    (Toolkit.getDefaultToolkit().getScreenSize().height - frameSize[1]) / 2);
        setResizable(false);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setModal(true);
        setLayout(new BorderLayout());
        
        initPanels();
        
        setVisible(true);
    }
    
    private void initPanels() {
        editorPanel = new EmailTransmissionDlgEditorPanel(destEmails, destEmail);
        buttonPanel = new EmailTransmissionDlgButtonPanel(this);
        bodyPanel   = new EmailTransmissionDlgBodyPanel();
        
        add(editorPanel, BorderLayout.NORTH);
        add(bodyPanel,   BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    public EmailTransmissionDlgBodyPanel getBodyPanel()     { return bodyPanel; }
    public EmailTransmissionDlgEditorPanel getEditorPanel() { return editorPanel; }
}

