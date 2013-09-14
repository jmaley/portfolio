/*
 * Title:   Configuration.java
 * Author:  Joe Maley
 * Date:    April 19 2012
 */

package SimpleMail;
import  java.io.Serializable;

public class Configuration implements Serializable {
	private static final long serialVersionUID = -9046486892765245672L;
	
	private String primaryEmail, smtpServerIp;
	
	public Configuration(String email, String ip) {
		primaryEmail = email;
		smtpServerIp = ip;
	}
	
	public String getEmail()    { return primaryEmail; }
	public String getServerIp() { return smtpServerIp; }
	
	public void setEmail(String email) { primaryEmail = email; }
	public void setServerIp(String ip) { smtpServerIp = ip; }

	public String toString() { return "Primary email: " + primaryEmail + 
	                                  "\nSmtp server ip: " + smtpServerIp; }
}
