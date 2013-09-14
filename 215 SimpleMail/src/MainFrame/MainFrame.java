/*
 * Title:   MainFrame.java
 * Author:  Joe Maley, Jacob Harrelson
 * Date:    April 19 2012
 */

package MainFrame;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import SimpleMail.Mediator;
import SimpleMail.DataStore;
import ContactEditingDlg.ContactEditingDlg;

public class MainFrame extends JFrame implements WindowListener {
	private static final long serialVersionUID = 2269971701250845501L;
	
    private final short[] frameSize = {640, 480};
    private final String  iconPath  = "email-icon.jpg";
	
	private Mediator mediator;
	private DataStore dataStore;
	
	public MainFrame(String title) {
	    super(title);
	    
		mediator = new Mediator();
		dataStore = DataStore.getInstance();

		new ContactEditingDlg(mediator);
		
		createGui();
	}
		
	private void createGui() {
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(frameSize[0], frameSize[1]);
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width  - frameSize[0]) / 2,
                    (Toolkit.getDefaultToolkit().getScreenSize().height - frameSize[1]) / 2);
        setLayout(new BorderLayout());
        
        try {
            setIconImage(ImageIO.read(new File(iconPath)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        addWindowListener(this);
        
        initMenus();
        initPanels();
        
        setVisible(true);
	}
	
	private void initMenus() {
		setJMenuBar(new MainFrameMenu());
	}
	
	private void initPanels() {
		MainFrameTablePanel tablePanel = new MainFrameTablePanel(mediator);
		MainFrameButtonPanel buttonPanel = new MainFrameButtonPanel(mediator);
		
		mediator.setContactsTablePanel(tablePanel);
		mediator.setContactsButtonPanel(buttonPanel);
		
		add(tablePanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}

	public void windowActivated(WindowEvent arg) { }
    public void windowClosed(WindowEvent arg) { }
    public void windowClosing(WindowEvent arg) { dataStore.saveDataStore(); }
    public void windowDeactivated(WindowEvent arg) { }
    public void windowDeiconified(WindowEvent arg) { }
    public void windowIconified(WindowEvent arg) { }
    public void windowOpened(WindowEvent arg) { }
}
