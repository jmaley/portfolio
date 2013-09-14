/*
 * Title:   ContactEditingDlg.java
 * Authors: Author:  Joe Maley
 * Date:    April 19 2012
 */

package ContactEditingDlg;

import java.awt.BorderLayout;
import java.awt.Toolkit;

import javax.swing.JDialog;
import SimpleMail.Mediator;

public class ContactEditingDlg extends JDialog {
	private static final long serialVersionUID = 704287253122212714L;
	private final short[] frameSize = {400, 300};

	private Mediator mediator;
	
	private ContactEditingDlgEditorPanel editorPanel;
	private ContactEditingDlgButtonPanel buttonPanel;
	
	public ContactEditingDlg(Mediator m) {
		mediator = m;
		
		mediator.setContactEditingDlg(this);
		
		createGui();
	}
	
	private void createGui() {
        setSize(frameSize[0], frameSize[1]);
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width  - frameSize[0]) / 2,
                    (Toolkit.getDefaultToolkit().getScreenSize().height - frameSize[1]) / 2);
		setResizable(false);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setModal(true);
		setLayout(new BorderLayout());
		
		initPanels();
	}
	
	private void initPanels() {
		editorPanel = new ContactEditingDlgEditorPanel(mediator);
		buttonPanel = new ContactEditingDlgButtonPanel(mediator);
		
		mediator.setContactEditingDlgEditorPanel(editorPanel);
		mediator.setContactEditingDlgButtonPanel(buttonPanel);
		
		add(editorPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}
}
