/*
 * Title:   MainFrameMenu.java
 * Author:  Joe Maley, Jacob Harrelson
 * Date:    April 19 2012
 */

package MainFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import ConfigurationDlg.*;
import SystemInformationDlg.*;

public class MainFrameMenu extends JMenuBar implements ActionListener {
	private static final long serialVersionUID = -4955738649620480797L;

	private JMenu fileMenu;
	private JMenuItem exitMenuItem;
	
	private JMenu configurationMenu;
	private JMenuItem configureMenuItem;
	
	private JMenu helpMenu;
	private JMenuItem aboutMenuItem;
	
	public MainFrameMenu() {
		createMenus();
	}
	
	private void createMenus() {	
		fileMenu = new JMenu("File");
		configurationMenu = new JMenu("Configuration");
		helpMenu = new JMenu("Help");
		
		exitMenuItem = new JMenuItem("Exit");
		configureMenuItem = new JMenuItem("Configure");
		aboutMenuItem = new JMenuItem("About");
		
		fileMenu.add(exitMenuItem);
		configurationMenu.add(configureMenuItem);
		helpMenu.add(aboutMenuItem);
		
		add(fileMenu);
		add(configurationMenu);
		add(helpMenu);
		
		initActionListeners();
	}
	
	private void initActionListeners() {
		exitMenuItem.addActionListener(this);
		configureMenuItem.addActionListener(this);
		aboutMenuItem.addActionListener(this);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == exitMenuItem) {
			System.exit(0);
		} else if (e.getSource() == configureMenuItem) {
			new ConfigurationDlg();
		} else if (e.getSource() == aboutMenuItem) {
			new SystemInformationDlg();
		}
	}

}
