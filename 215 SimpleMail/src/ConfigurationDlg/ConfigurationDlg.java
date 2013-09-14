/*
 * Title:   ConfigurationDlg.java
 * Author:  Joe Maley
 * Date:    April 19 2012
 */

package ConfigurationDlg;

import java.awt.BorderLayout;
import java.awt.Toolkit;

import javax.swing.JDialog;

public class ConfigurationDlg extends JDialog {
    private static final long serialVersionUID = 37942385235235L;
    private final short[] frameSize = {350, 150};
    
    private ConfigurationDlgEditorPanel editorPanel;
    private ConfigurationDlgButtonPanel buttonPanel;
    
    public ConfigurationDlg() {    
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
        editorPanel = new ConfigurationDlgEditorPanel();
        buttonPanel = new ConfigurationDlgButtonPanel(this);
        
        add(editorPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    public ConfigurationDlgEditorPanel getEditorPanel() { return editorPanel; }
}

