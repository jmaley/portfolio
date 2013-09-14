/*
 * Title:   MainFrameButtonPane.java
 * Author:  Joe Maley, Jacob Harrelson
 * Date:    April 19 2012
 */

package MainFrame;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.JButton;

import SimpleMail.Contact;
import SimpleMail.Mediator;

public class MainFrameButtonPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = -7121928514541876153L;

	private Mediator mediator;
	
	private JButton addButton;
	private JButton editButton;
	private JButton deleteButton;
	
	public MainFrameButtonPanel(Mediator m) {
		mediator = m;
		
		createGui();
	}
	
	private void createGui() {
		setLayout(new GridLayout(1, 3));
		
		initButtons();
		initButtonListeners();
	}
	
	private void initButtons() {
		addButton = new JButton("Add");
		editButton = new JButton("Edit");
		deleteButton = new JButton("Delete");
		
		add(addButton);
		add(editButton);
		add(deleteButton);
	}
	
	private void initButtonListeners() {
		addButton.addActionListener(this);
		editButton.addActionListener(this);
		deleteButton.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == addButton) {
			mediator.showContactEditingDlg(false);
		} else if (e.getSource() == editButton) {
		    Contact contact = mediator.getSelectedContact();
		    if (contact != null) {
		        mediator.setContactTextFields(contact);
		        mediator.showContactEditingDlg(true);
		    }
		} else if (e.getSource() == deleteButton) {
			if (mediator.getSelectedContact() != null) {
				mediator.deleteContact();
			}
		}
		
	}
}
