/*
 * Title:   ContactEditingDlgEditorPanel.java
 * Authors: Author:  Joe Maley
 * Date:    April 19 2012
 */

package ContactEditingDlg;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import SimpleMail.Mediator;

public class ContactEditingDlgEditorPanel extends JPanel {
	private static final long serialVersionUID = 1010593289328404428L;

	@SuppressWarnings("unused")
	private Mediator mediator;
	
	private JTextField firstNameTextField;
	private JTextField lastNameTextField;
	private JTextField addressTextField;
	private JTextField phoneNumberTextField;
	private JTextField emailTextField;
	
	public ContactEditingDlgEditorPanel(Mediator m) {
		mediator = m;
		
		createGui();
	}
	
	private void createGui() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		initTextFields();
	}
	
	private void initTextFields() {
		firstNameTextField = new JTextField();
		lastNameTextField = new JTextField();
		addressTextField = new JTextField();
		phoneNumberTextField = new JTextField();
		emailTextField = new JTextField();
		
		add(new JLabel("First name: "));
		add(firstNameTextField);
		
		add(new JLabel("Last name: "));
		add(lastNameTextField);
		
		add(new JLabel("Address: "));
		add(addressTextField);
		
		add(new JLabel("Phone number: "));
		add(phoneNumberTextField);
		
		add(new JLabel("Email: "));
		add(emailTextField);
	}
	
	public void setFirstName(String name) {
		firstNameTextField.setText(name);
	}
	
	public String getFirstName() {
		return firstNameTextField.getText();
	}
	
	public void setLastName(String name) {
		lastNameTextField.setText(name);
	}
	
	public String getLastName() {
		return lastNameTextField.getText();
	}
	
	public void setAddress(String address) {
		addressTextField.setText(address);
	}
	
	public String getAddress() {
		return addressTextField.getText();
	}
	
	public void setPhoneNumber(String phone) {
		phoneNumberTextField.setText(phone);
	}
	
	public String getPhoneNumber() {
		return phoneNumberTextField.getText();
	}
	
	public void setEmail(String email) {
		emailTextField.setText(email);
	}
	
	public String getEmail() {
		return emailTextField.getText();
	}
}
