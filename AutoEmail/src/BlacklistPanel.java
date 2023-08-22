import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class BlacklistPanel extends JPanel {
    private JList<String> emailList;
    private HashSet<String> emails;
    
    public BlacklistPanel() {
        this.emails = this.deserializeBlacklist();
        setupUI();
    }
    
    public HashSet<String> getBlacklist(){
    	return this.emails;
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        
        // Display blacklisted emails
        emailList = new JList<>(emails.toArray(new String[0]));
        add(new JScrollPane(emailList), BorderLayout.CENTER);
        
        // Input and button for adding email to blacklist
        JTextField inputField = new JTextField(20); // Making the textfield wider by specifying column width
        JButton addButton = new JButton("Add to blacklist");
        
        addButton.addActionListener(e -> {
            String inputEmail = inputField.getText();
            if (isValidEmail(inputEmail)) { // Validate the input email
                this.emails.add(inputEmail);
                this.serializeBlacklist();
                this.refreshList();
                inputField.setText("");
            } else {
                JOptionPane.showMessageDialog(null, "Invalid email entered", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Button for removing selected email from blacklist
        JButton removeButton = new JButton("Remove from blacklist");
        
        removeButton.addActionListener(e -> {
            this.emails.remove(emailList.getSelectedValue());
            this.serializeBlacklist();
            this.refreshList();
        });
        
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(inputPanel);
        buttonPanel.add(removeButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void serializeBlacklist() {
        try {
            FileOutputStream fos = new FileOutputStream("blacklist.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this.emails);
            oos.close();
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    private HashSet<String> deserializeBlacklist() {
        HashSet<String> deserializedEmails = new HashSet<>(); //default, in case file does not exist, or error occurs
        try {
            FileInputStream fis = new FileInputStream("blacklist.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            deserializedEmails = (HashSet) ois.readObject();
            ois.close();
            fis.close();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        } catch(ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
            
        }
        return deserializedEmails;
    }

    private void refreshList() {
        emailList.setListData(this.emails.toArray(new String[0]));
    }

    private boolean isValidEmail(String email) {
        // Modified the validation logic to use regex
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@"
           + "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
         
         return email.matches(emailRegex);
     }
}
