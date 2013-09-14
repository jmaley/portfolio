/*
 * Title:   Contact.java
 * Author:  Joe Maley
 * Date:    April 19 2012
 */

package SimpleMail;
import  java.io.Serializable;

public class Contact implements Serializable {
	private static final long serialVersionUID = 7658245L;
	
	private String first, last, postal, phone, email;
	
	public Contact(String first, String last,
	               String postal, String phone, String email) {
		this.first  = first;
		this.last   = last;
		this.postal = postal;
		this.phone  = phone;
		this.email  = email;
	}
	
	public String getFirstName()   { return first; }
	public String getLastName()    { return last; }
	public String getAddress()     { return postal; }
	public String getPhoneNumber() { return phone; }
	public String getEmail()       { return email; }
	
	public void setFirstName(String first)   { this.first = first; }
	public void setLastName(String last)     { this.last = last; }
	public void setAddress(String postal)    { this.postal = postal; }
	public void setPhoneNumber(String phone) { this.phone = phone; }
	public void setEmail(String email)       { this.email = email; }
	
	public String toString() {
		return "Name: "             + first  + 
		       " "                  + last   + 
			   "\nPostal address: " + postal +
			   "\nPhone number: "   + phone  +
			   "\nEmail: "          + email;
	}
}