/*
 * Title:   ContactEditingDlgButtonPanel.java
 * Authors: Author:  Joe Maley
 * Date:    April 19 2012
 */

package ContactEditingDlg;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import SimpleMail.Mediator;

public class ContactEditingDlgButtonPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 6862994229141617913L;

	Mediator mediator;
	
	private JButton saveButton;
	private JButton cancelButton;
	
	private boolean editMode = false;
	
	public ContactEditingDlgButtonPanel(Mediator m) {
		mediator = m;
		
		createGui();
	}
	
	private void createGui() {
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
	
	public void setEditMode(boolean edit) {
		editMode = edit;
	}
	
	public boolean getEditMode() {
		return editMode;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == saveButton) {
			if (editMode) {
				mediator.updateContact();
				mediator.clearContactTextFields();
				mediator.hideContactEditingDlg();
			} else {
				mediator.createContact();
				mediator.clearContactTextFields();
			}
		} else if (e.getSource() == cancelButton) {
			mediator.clearContactTextFields();
			mediator.hideContactEditingDlg();
		}
		
	}
}
