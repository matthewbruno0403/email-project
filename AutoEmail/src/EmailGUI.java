import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import jakarta.mail.MessagingException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.prefs.Preferences;

/**
 * A GUI class for the email application.
 */
public class EmailGUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JPanel loginPanel;
    private JPanel emailScreenPanel;
    private JPanel previewEmailPanel;
    private EmailSender sender;
    private String subject = "";
    private String body = "";
    
    private HashMap<String, String> pdfMap;

    /**
     * Constructs an EmailGUI object.
     */
    public EmailGUI() {
        this.sender = new EmailSender();
    	
    	setTitle("Email GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");

        ActionListener loginActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
					performLogin();
				} catch (AuthenticationFailedException e1) {
					// Handle the exception specific to authentication failure
					e1.printStackTrace();
				}
            }
        };

        KeyListener enterKeyListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    // Perform login action
                    try {
                        performLogin();
                    } catch (AuthenticationFailedException e1) {
                        // Handle the exception specific to authentication failure
                        e1.printStackTrace();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        };

        usernameField.addActionListener(loginActionListener);
        usernameField.addKeyListener(enterKeyListener);
        passwordField.addActionListener(loginActionListener);
        passwordField.addKeyListener(enterKeyListener);
        loginButton.addActionListener(loginActionListener);

        loginPanel = new JPanel();
        loginPanel.setLayout(new FlowLayout());
        loginPanel.add(new JLabel("Outlook Email:"));
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("Password:"));
        loginPanel.add(passwordField);
        loginPanel.add(loginButton);

        add(loginPanel);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
       
    }

    /**
     * Performs the login action.
     * @throws AuthenticationFailedException if the login fails.
     */
    private void performLogin() throws AuthenticationFailedException {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        boolean loggedIn = false;
        
        loggedIn = EmailGUI.this.sender.signIn(username, password);
        
        // Perform login logic here
        if (loggedIn == true) {
            // Successful login, route to new screen or perform other operations
            JOptionPane.showMessageDialog(null, "Login successful!");
            showNewScreen(); // Code to show new screen
        } else {
            // Invalid login, print error and clear fields
            JOptionPane.showMessageDialog(null, "Invalid username or password!", "Error", JOptionPane.ERROR_MESSAGE);
            usernameField.setText("");
            passwordField.setText("");
            //remove later!! 
            //showNewScreen();
        }
    }

 // Method to show the new screen after successful login
    private void showNewScreen() {
        // Hide the login panel
        loginPanel.setVisible(false);

        // Create a new panel for the email screen
        emailScreenPanel = new JPanel();
        emailScreenPanel.setLayout(new BoxLayout(emailScreenPanel, BoxLayout.Y_AXIS));

        // Create text fields for subject and body
        JTextField subjectField = new JTextField(20);
        JTextArea bodyArea = new JTextArea(10, 20);
        subjectField.setText(EmailGUI.this.subject);
        bodyArea.setText(EmailGUI.this.body);

        // Create buttons for uploading PDF and proceeding to the next step
        JButton uploadButton = new JButton("Upload PDF");
        JButton nextButton = new JButton("Next");

        // ActionListener for the "Next" button
        ActionListener nextActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Update the subject and body with the text from the text fields
                EmailGUI.this.subject = subjectField.getText();
                EmailGUI.this.body = bodyArea.getText();
                EmailGUI.this.sender.setSubject(EmailGUI.this.subject);
                EmailGUI.this.sender.setEmailBody(EmailGUI.this.body);

                // Call the method to preview the email screen
                previewEmailScreen();

                // Create a frame and panel for the blacklist
                BlacklistPanel panel;
                JFrame blacklistFrame = new JFrame();
                blacklistFrame.add(new BlacklistPanel());
                blacklistFrame.pack();
                blacklistFrame.setVisible(true);
                panel = (BlacklistPanel) blacklistFrame.getContentPane().getComponent(0);

                // Set the email blacklist in the EmailSender
                EmailGUI.this.sender.setEmailBlacklist(panel.getBlacklist());
            }
        };

        // KeyListener for the "Next" button
        KeyListener nextKeyListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    // Code to handle "Next" button action
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        };

        // ActionListener for the "Upload PDF" button
        uploadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Create a file chooser dialog
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                int result = fileChooser.showOpenDialog(EmailGUI.this);

                // If a file is selected, process the PDF
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    TestPDFBox test = new TestPDFBox(selectedFile.getAbsolutePath());
                    EmailGUI.this.pdfMap = test.scanPdf();
                }

                // Check if the PDF was successfully processed
                if (EmailGUI.this.pdfMap != null) {
                    // Display a success message and set the email name map in the EmailSender
                    JOptionPane.showMessageDialog(null, "PDF upload successful!");
                    EmailGUI.this.sender.setEmailNameMap(pdfMap);
                } else {
                    // Display an error message if the PDF was not uploaded
                    JOptionPane.showMessageDialog(null, "PDF was not uploaded", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Add action listeners to the "Next" button
        nextButton.addActionListener(nextActionListener);
        nextButton.addKeyListener(nextKeyListener);

        // Create a panel for the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(uploadButton);
        buttonPanel.add(nextButton);

        // Add components to the email screen panel
        emailScreenPanel.add(new JLabel("Subject:"));
        emailScreenPanel.add(subjectField);
        emailScreenPanel.add(new JLabel("Body:"));
        emailScreenPanel.add(new JScrollPane(bodyArea));
        emailScreenPanel.add(buttonPanel);

        // Add the email screen panel to the frame
        add(emailScreenPanel);

        // Set the size and position of the frame
        setSize(300, 300);
        setLocationRelativeTo(null);

        // Revalidate and repaint the frame to update the layout
        revalidate();
        repaint();
    }

    
    private void previewEmailScreen() {
        // Hide this panel
        emailScreenPanel.setVisible(false);
        
        // Create the preview email panel
        previewEmailPanel = new JPanel();
        previewEmailPanel.setLayout(new BorderLayout());

        // Create the input components
        JTextField subjectField = new JTextField(20);
        JTextArea bodyArea = new JTextArea(10, 20);
        
        // Load the saved subject and body values from preferences
        Preferences prefs = Preferences.userNodeForPackage(EmailGUI.class);
        String savedSubject = prefs.get("subject", "");
        String savedBody = prefs.get("body", "");
        subjectField.setText(savedSubject);
        bodyArea.setText(savedBody);
        
        // Set Subject and Body 
        subjectField.setText(EmailGUI.this.subject);
        bodyArea.setText(EmailGUI.this.body);
        
        // Make the text fields unmodifiable
        subjectField.setEditable(false);
        bodyArea.setEditable(false);

        // Create the buttons
        JButton backButton = new JButton("Back");
        JButton nextButton = new JButton("Next");
        JButton sendPreviewEmailButton = new JButton("Send Preview Email");
        JButton blacklistButton = new JButton("Email Blacklist");

        // Create the panel for the blacklist button
        // JPanel blacklistButtonPanel = new JPanel();

        // Button panel for positioning the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.add(backButton, BorderLayout.WEST);
        buttonPanel.add(nextButton, BorderLayout.EAST);
        buttonPanel.add(sendPreviewEmailButton, BorderLayout.CENTER);
        buttonPanel.add(blacklistButton, BorderLayout.SOUTH);

        // Add the components to the preview email panel
        previewEmailPanel.add(new JLabel("Subject:"), BorderLayout.NORTH);
        previewEmailPanel.add(subjectField, BorderLayout.NORTH);
        previewEmailPanel.add(new JLabel("Body:"), BorderLayout.CENTER);
        previewEmailPanel.add(new JScrollPane(bodyArea), BorderLayout.CENTER);
        previewEmailPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Register action listeners for the buttons
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Code to handle "Back" button action
                EmailGUI.this.showNewScreen();
                previewEmailPanel.setVisible(false);
            }
        });

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Code to handle "Next" button action
                // EmailGUI.this.sender.getEmailBlacklistFromSender();
                try {
                    EmailGUI.this.sender.sendAllEmails(EmailGUI.this.subject, EmailGUI.this.body);
                } catch (MessagingException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        sendPreviewEmailButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Code to handle "Send Preview Email" button action
                try {
                    EmailGUI.this.sender.sendEmail(EmailGUI.this.subject, EmailGUI.this.body);
                } catch (MessagingException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        
        // Add action listener to the blacklist button
        blacklistButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // When blacklist button is pressed -> add custom JFrame & setVisible
                BlacklistPanel panel;
                JFrame blacklistFrame = new JFrame();
                blacklistFrame.add(new BlacklistPanel());
                blacklistFrame.pack();
                blacklistFrame.setVisible(true);
                panel = (BlacklistPanel) blacklistFrame.getContentPane().getComponent(0);
                EmailGUI.this.sender.setEmailBlacklist(panel.getBlacklist());
            }
        });

        // Add the preview email panel to the main container
        add(previewEmailPanel);

        // Set the size and positioning of the panel
        setSize(300, 300);
        setLocationRelativeTo(null);
        revalidate();
        repaint();
    }
}

