/*
 * Title:   SystemInformationDlg.java
 * Author:  Joe Maley
 * Date:    April 19 2012
 */

package SystemInformationDlg;

import  java.awt.BorderLayout;
import java.awt.Toolkit;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextArea;

public class SystemInformationDlg
extends JDialog {
    private static final long serialVersionUID = 248797324235234L;
    
    private final short[] frameSize = {320, 240};
    
    public SystemInformationDlg() {
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

        JLabel title = new JLabel("System Information");
        JTextArea text = new JTextArea();
        text.setEditable(false);
        text.setText("Simple Mail v1.0\n\n" +
                     "Author:\tJoe Maley\n");
        
        add(title, BorderLayout.NORTH);
        add(text,  BorderLayout.CENTER);
        
        setVisible(true);
    }
    

}
