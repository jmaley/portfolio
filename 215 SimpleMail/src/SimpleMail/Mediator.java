/*
 * Title:   Mediator.java
 * Author:  Joe Maley
 * Date:    April 19 2012
 */

package SimpleMail;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import ContactEditingDlg.ContactEditingDlg;
import ContactEditingDlg.ContactEditingDlgButtonPanel;
import ContactEditingDlg.ContactEditingDlgEditorPanel;
import MainFrame.MainFrameButtonPanel;
import MainFrame.MainFrameTablePanel;

public class Mediator {
	@SuppressWarnings("unused")
    private MainFrameButtonPanel buttonPanel;
	private MainFrameTablePanel  tablePanel;
	
	private ContactEditingDlg            contactEditingDlg;
	private ContactEditingDlgButtonPanel contactDlgButtonPanel;
	private ContactEditingDlgEditorPanel contactDlgEditorPanel;
	
	private ArrayList<Contact> contacts;
	private DataStore dataStore;
	
	public Mediator() {
		dataStore = DataStore.getInstance();
		contacts =  dataStore.getContacts();
	}
	
	public void setContactsButtonPanel(MainFrameButtonPanel panel) { buttonPanel = panel; }
	public void setContactsTablePanel(MainFrameTablePanel panel) { tablePanel = panel; }
	public void setContactEditingDlg(ContactEditingDlg dialog) { contactEditingDlg = dialog; }
	public void setContactEditingDlgButtonPanel(ContactEditingDlgButtonPanel panel) { contactDlgButtonPanel = panel; }
	public void setContactEditingDlgEditorPanel(ContactEditingDlgEditorPanel panel) { contactDlgEditorPanel = panel; }
	
	public void setContactTextFields(Contact contact) {
		contactDlgEditorPanel.setFirstName(contact.getFirstName());
		contactDlgEditorPanel.setLastName(contact.getLastName());
		contactDlgEditorPanel.setAddress(contact.getAddress());
		contactDlgEditorPanel.setPhoneNumber(contact.getPhoneNumber());
		contactDlgEditorPanel.setEmail(contact.getEmail());
	}
	
	public void clearContactTextFields() {
		contactDlgEditorPanel.setFirstName("");
		contactDlgEditorPanel.setLastName("");
		contactDlgEditorPanel.setAddress("");
		contactDlgEditorPanel.setPhoneNumber("");
		contactDlgEditorPanel.setEmail("");
	}
	
	public boolean validateContactTextFields() {
		if (contactDlgEditorPanel.getFirstName().equals("")) {
			JOptionPane.showMessageDialog(null, "First name is empty!");
			return false;
		}
		if (contactDlgEditorPanel.getLastName().equals("")) {
			JOptionPane.showMessageDialog(null, "Last name is empty!");
			return false;
		}
		if (contactDlgEditorPanel.getAddress().equals("")) {
			JOptionPane.showMessageDialog(null, "Postal address is empty!");
			return false;
		}
		if (contactDlgEditorPanel.getPhoneNumber().equals("")) {
			JOptionPane.showMessageDialog(null, "Phone number is empty!");
			return false;
		}
		if (contactDlgEditorPanel.getEmail().equals("")) {
			JOptionPane.showMessageDialog(null, "Email address is empty!");
			return false;
		}
		
		return true;
	}
	
	public void createContact() {
		Contact contact = new Contact(contactDlgEditorPanel.getFirstName(),
			                              contactDlgEditorPanel.getLastName(),
										  contactDlgEditorPanel.getAddress(),
										  contactDlgEditorPanel.getPhoneNumber(),
										  contactDlgEditorPanel.getEmail());
		
		if (!contacts.contains(contact)) {
			tablePanel.addContact(contact);
			contacts.add(contact);
		} else {
			JOptionPane.showMessageDialog(null, "Contact already exists!");
		}
	}
	
	public void updateContact() {
		Contact contact = new Contact(contactDlgEditorPanel.getFirstName(), 
										contactDlgEditorPanel.getLastName(),
										contactDlgEditorPanel.getAddress(), 
										contactDlgEditorPanel.getPhoneNumber(),
										contactDlgEditorPanel.getEmail());
		
		int row = tablePanel.getSelectedRow();
		tablePanel.updateContact(contact, row);
	}
	
	public void deleteContact() {
		Contact contact = tablePanel.getSelectedContact();
		tablePanel.deleteContact(contact);
		contacts.remove(contact);
	}
	
	public void showContactEditingDlg(boolean editMode) {
		if (editMode) {
			contactEditingDlg.setTitle("Edit existing contact");
		} else {
			contactEditingDlg.setTitle("Add new contact");
		}
		
		contactDlgButtonPanel.setEditMode(editMode);
		contactEditingDlg.setVisible(true);
	}
	
	public void hideContactEditingDlg() {
		contactEditingDlg.setVisible(false);
	}
	
	public Contact getSelectedContact() {
		if (tablePanel.getSelectedRow() >= 0) {
			return tablePanel.getSelectedContact();
		} else {
			return null;
		}
	}
}