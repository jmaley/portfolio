/*
 * Title:   MainDriver.java
 * Author:  Joe Maley
 * Date:    April 19 2012
 */

package SimpleMail;
import  MainFrame.MainFrame;

public class MainDriver {
	public static void main(String[] args) {
        DataStore.getInstance().loadDataStore();
        new MainFrame("Simple Mail");
	}
}