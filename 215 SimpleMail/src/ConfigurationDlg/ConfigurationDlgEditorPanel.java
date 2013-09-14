/*
 * Title:   ConfigurationDlgEditorPanel.java
 * Author:  Joe Maley
 * Date:    April 19 2012
 */

package ConfigurationDlg;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import SimpleMail.DataStore;

public class ConfigurationDlgEditorPanel extends JPanel {
    private static final long serialVersionUID = 23423536323L;
    
    private JTextField emailTextField;
    private JTextField smtpTextField;
    
    public ConfigurationDlgEditorPanel() {
        createGUI();
    }
    
    private void createGUI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        initTextFields();
        
        setVisible(true);
    }
    
    private void initTextFields() {
        emailTextField = new JTextField();
        smtpTextField  = new JTextField();
        
        emailTextField.setText(DataStore.getInstance().getConfiguration().getEmail());
        smtpTextField.setText(DataStore.getInstance().getConfiguration().getServerIp());
        
        add(new JLabel("Source E-mail: "));
        add(emailTextField);
        
        add(new JLabel("SMTP IP: "));
        add(smtpTextField);
    }
    
    public String getEmail() { return emailTextField.getText(); }
    public String getSMTP()  { return smtpTextField.getText();  }
}
