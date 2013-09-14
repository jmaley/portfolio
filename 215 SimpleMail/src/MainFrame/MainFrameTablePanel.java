/*
 * Title:   MainFrameTablePanel.java
 * Author:  Joe Maley, Jacob Harrelson
 * Date:    April 19 2012
 */

package MainFrame;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import EmailTransmissionDlg.EmailTransmissionDlg;
import SimpleMail.Contact;
import SimpleMail.Mediator;
import SimpleMail.DataStore;

public class MainFrameTablePanel extends JPanel {
	private static final long serialVersionUID = 2141543622477761647L;

	private ContactsTable contactsTable;
	private TableModel2 tableModel;
	
	private DataStore dataStore;
	private ArrayList<Contact> contacts;
	
	Mediator mediator;
	
	public MainFrameTablePanel(Mediator m) {
		mediator = m;
		
		dataStore = DataStore.getInstance();
		contacts = dataStore.getContacts();
		
		createGui();
	}
	
	/* 
	 * Initializes components and adds them to panel
	 */
	private void createGui() {
		setLayout(new BorderLayout());
		
		// initialize table
		tableModel = new TableModel2();
		contactsTable = new ContactsTable();
		
		// Add contacts table to scrollbar and add scrollbar to panel
		add(new JScrollPane(contactsTable));
	}
	
	/*
	 * Returns selected contact in contacts table
	 */
	public Contact getSelectedContact() {
		int row = contactsTable.getSelectedRow();
		return contacts.get(row);
	}
	
	/*
	 * Return selected row index
	 */
	public int getSelectedRow() {
		return contactsTable.getSelectedRow();
	}
	
	/*
	 * Add a new contact to the table
	 */
	public void addContact(Contact contact) {
		tableModel.addRow(contact);
	}
	
	/*
	 * Modify existing contact on specified table row
	 */
	public void updateContact(Contact contact, int index) {
		contacts.get(index).setFirstName(contact.getFirstName());
		contacts.get(index).setLastName(contact.getLastName());
		contacts.get(index).setAddress(contact.getAddress());
		contacts.get(index).setPhoneNumber(contact.getPhoneNumber());
		contacts.get(index).setEmail(contact.getEmail());
		
		tableModel.setValueAt(contact.getFirstName(), index, 0);
		tableModel.setValueAt(contact.getLastName(), index, 1);
		tableModel.setValueAt(contact.getAddress(), index, 2);
		tableModel.setValueAt(contact.getPhoneNumber(), index, 3);
		tableModel.setValueAt(contact.getEmail(), index, 4);
	}
	
	/*
	 * Delete contact from arraylist and delete row from table
	 */
	public void deleteContact(Contact contact) {
		tableModel.removeRow(contact);
	}

	private class ContactsTable extends JTable implements MouseListener {
		private static final long serialVersionUID = 3317927690309921861L;
		
		public ContactsTable() {
			setModel(tableModel);
			setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			addMouseListener(this);
		}

		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				if (e.getClickCount() == 2) {
				    
				    String[] contents = new String[contacts.size()];
				    
				    
				    for (int i = 0; i < contacts.size(); i++)
				        contents[i] = contacts.get(i).getEmail();
				    
					new EmailTransmissionDlg(contents, getSelectedRow());
				}
			}
		}

		public void mouseEntered(MouseEvent e) {

		}

		public void mouseExited(MouseEvent e) {

		}

		public void mousePressed(MouseEvent e) {

		}

		public void mouseReleased(MouseEvent e) {
			
		}
	};
	
	
	private class TableModel2 extends DefaultTableModel {
		private static final long serialVersionUID = -5225589900681629215L;
		
		// Column header names
		private String[] columnNames = {
				"First name", 
				"Last name", 
				"Address",
				"Phone number", 
				"Email"
		};
		
		/*
		 * Initialize table model
		 * 	- Add column headers
		 * 	- Fill table with contacts
		 */
		public TableModel2() {
			for (int i = 0; i < columnNames.length; i++) {
				addColumn(columnNames[i]);
			}
			
			for (int i = 0; i < contacts.size(); i++) {
				addRow(contacts.get(i));
			}
		}
		
		/*
		 * Add a new row to the table containing contact
		 */
		public void addRow(Contact contact) {
			Object[] data = {
					contact.getFirstName(),
					contact.getLastName(),
					contact.getAddress(),
					contact.getPhoneNumber(),
					contact.getEmail()
			};
			addRow(data);
		}
		
		public void removeRow(Contact contact) {
			int row = contacts.indexOf(contact);
			removeRow(row);
		}
		
		/*
		 * Make sure cell items are not editable
		 */
		public boolean isCellEditable(int row, int column) {
			return false;
		}
		
	};
}
