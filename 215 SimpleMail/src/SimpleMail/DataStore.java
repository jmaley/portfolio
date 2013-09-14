/*
 * Title:   DataStore.java
 * Author:  Joe Maley
 * Date:    April 19 2012
 */

package SimpleMail;
import  java.io.*;
import  java.util.ArrayList;

public class DataStore {
	private static DataStore instance;
	
	private static final String DATASTORE_PATH = "data.db";
	
	private ArrayList<Contact> contacts;
	private Configuration configuration;
	
	private DataStore() {
	    contacts = new ArrayList<Contact>();
	    configuration = new Configuration(null, null);
	}
	
	public Configuration getConfiguration() { return configuration; }
	public ArrayList<Contact> getContacts() { return contacts; }
	
	public static DataStore getInstance() {
	    if (instance == null)
	        instance = new DataStore();
		return instance;
	}
	
	public void loadDataStore() {
		try {
			ObjectInputStream reader = new ObjectInputStream(new FileInputStream(DATASTORE_PATH));
			
			loadConfiguration(reader);
			loadContacts(reader);
		} catch (FileNotFoundException e) {
			// OK
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveDataStore() {
		try {
			ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(DATASTORE_PATH));
			
			saveConfiguration(writer);
			saveContacts(writer);
		} catch (FileNotFoundException e) {
			// file not found -- create it
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void loadConfiguration(ObjectInputStream reader) {
		try {
			configuration = (Configuration) reader.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (EOFException e) {
			// finished reading file -- OK!
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void saveConfiguration(ObjectOutputStream writer) {
		try {
			writer.writeObject(configuration);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void loadContacts(ObjectInputStream reader) {
		try {
			Contact contact = (Contact) reader.readObject();
			
			while (contact != null) {
				contacts.add(contact);
				contact = (Contact) reader.readObject();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (EOFException e) {
			// finished reading file -- OK!
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void saveContacts(ObjectOutputStream writer) {
		try {
			for (int i = 0; i < contacts.size(); i++) {
				writer.writeObject(contacts.get(i));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
