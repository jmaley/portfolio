/*
 * Title:   ConfigurationDlgButtonPanel.java
 * Author:  Joe Maley
 * Date:    April 19 2012
 */

package ConfigurationDlg;

import  java.awt.GridLayout;
import  java.awt.event.ActionEvent;
import  java.awt.event.ActionListener;

import  javax.swing.JButton;
import  javax.swing.JPanel;

import  SimpleMail.DataStore;

public class ConfigurationDlgButtonPanel extends JPanel implements ActionListener {
    private static final long serialVersionUID = 6862994229141617913L;
    
    private ConfigurationDlg configurationDlg;
    
    private JButton saveButton;
    private JButton cancelButton;
    
    public ConfigurationDlgButtonPanel(ConfigurationDlg configurationDlg) {
        this.configurationDlg = configurationDlg;
        
        createGUI();
    }
    
    private void createGUI() {
        setLayout(new GridLayout(1, 2));
        
        initButtons();
        initButtonListeners();
    }
    
    private void initButtons() {
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
        
        add(saveButton);
        add(cancelButton);
    }
    
    private void initButtonListeners() {
        saveButton.addActionListener(this);
        cancelButton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == saveButton) {
            saveConfiguration();
            configurationDlg.setVisible(false);
        } else if (e.getSource() == cancelButton) {
            configurationDlg.setVisible(false);
        }
    }
    
    private void saveConfiguration() {
        String email  = configurationDlg.getEditorPanel().getEmail();
        String smtpIP = configurationDlg.getEditorPanel().getSMTP();
        
        DataStore.getInstance().getConfiguration().setEmail(email);
        DataStore.getInstance().getConfiguration().setServerIp(smtpIP);
    }
}

