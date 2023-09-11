import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import jakarta.mail.MessagingException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
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
    private JPanel resultsPanel;
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
            	performLogin();
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
                    performLogin();
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
    private void performLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        boolean loggedIn = false;
        
        try {
        	loggedIn = EmailGUI.this.sender.signIn(username, password);
        } catch(AuthenticationFailedException e) {
        	loggedIn = false;
        }
        
        // Perform login logic here
        if (loggedIn == true) {
            // Successful login, route to new screen or perform other operations
            JOptionPane.showMessageDialog(null, "Login successful!");
            writeEmailScreen(); // Code to show new screen
        } else {
            // Invalid login, print error and clear fields
            JOptionPane.showMessageDialog(null, "Invalid username or password!", "Error", JOptionPane.ERROR_MESSAGE);
            usernameField.setText("");
            passwordField.setText("");
        }
    }

    // Method to show the new screen after successful login
    private void writeEmailScreen() {
        // Hide the login panel
        loginPanel.setVisible(false);

        // Create a new panel for the email screen
        this.emailScreenPanel = new JPanel();
        this.emailScreenPanel.setLayout(new BoxLayout(this.emailScreenPanel, BoxLayout.Y_AXIS));

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
                EmailGUI.this.previewEmailScreen();
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
        
        // Remove the existing panel
        getContentPane().removeAll();
        // Add the email screen panel to the frame
        getContentPane().add(emailScreenPanel);

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
        JButton nextButton = new JButton("Send All Emails");
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
                EmailGUI.this.writeEmailScreen();
                previewEmailPanel.setVisible(false);
            }
        });

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Code to handle "Send All Emails" button action
                // EmailGUI.this.sender.getEmailBlacklistFromSender();
            	
            	int option = JOptionPane.showConfirmDialog(EmailGUI.this, "Are you sure you want to send all emails?", "Confirmation", JOptionPane.YES_NO_OPTION);

            	if (option == JOptionPane.YES_OPTION) {
            		if(!EmailGUI.this.sender.emailNameMapIsNull()){
            			try {
                            EmailGUI.this.sender.sendAllEmails(EmailGUI.this.subject, EmailGUI.this.body);
                        } catch (MessagingException e1) {
                            e1.printStackTrace();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
            			
            			//go to final output panel
                		sentEmailScreen();
            		} 
            		else {
            			// Display an error message if the PDF was not uploaded
                        JOptionPane.showMessageDialog(null, "PDF was not uploaded. Please upload a PDF before sending", "Error", JOptionPane.ERROR_MESSAGE);
            		}	
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
                blacklistFrame.setTitle("Email Blacklist");
                blacklistFrame.add(new BlacklistPanel());
                blacklistFrame.pack();
                blacklistFrame.setVisible(true);
                panel = (BlacklistPanel) blacklistFrame.getContentPane().getComponent(0);
                
                // Add a WindowListener to the JFrame
                blacklistFrame.addWindowListener(new WindowListener() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        // Call your method here
                    	EmailGUI.this.sender.setEmailBlacklist(BlacklistSerializer.deserializeBlacklist());
                    }

					@Override
					public void windowOpened(WindowEvent e) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void windowClosed(WindowEvent e) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void windowIconified(WindowEvent e) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void windowDeiconified(WindowEvent e) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void windowActivated(WindowEvent e) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void windowDeactivated(WindowEvent e) {
						// TODO Auto-generated method stub
						
					}

                    // Implement the remaining methods of the WindowListener interface
                    // ...
                });
                
                EmailGUI.this.sender.setEmailBlacklist(BlacklistSerializer.deserializeBlacklist());
            }
        });

        // Add the preview email panel to the main container
        add(previewEmailPanel);
        
        // Set the size and positioning of the panel
        setSize(300, 300);
        setLocationRelativeTo(null);
        pack();
        revalidate();
        repaint();
    }
    
    private void sentEmailScreen() {
        // Hide this panel
        previewEmailPanel.setVisible(false);
        
        // After sending emails, display results.
        HashMap<String, String> sentEmails = EmailGUI.this.sender.getFilteredEmails();
        HashSet<String> notSentEmails = EmailGUI.this.sender.getBlockedEmails();

        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BorderLayout());

        JTextArea sentEmailsArea = new JTextArea(10,20);
        sentEmailsArea.setText("Sent Emails:\n");
        for(Map.Entry<String, String> entry : sentEmails.entrySet()) {
            sentEmailsArea.append(entry.getKey() + ": " + entry.getValue() + "\n");
        }
        sentEmailsArea.setEditable(false);

        JTextArea notSentEmailsArea = new JTextArea(10,20);
        notSentEmailsArea.setText("Blocked Emails:\n");
        if(notSentEmails != null) {
        	for(String email : notSentEmails) {
                notSentEmailsArea.append(email + "\n");
            }
        }
        
        notSentEmailsArea.setEditable(false);

        resultsPanel.add(new JScrollPane(sentEmailsArea), BorderLayout.NORTH);
        resultsPanel.add(new JScrollPane(notSentEmailsArea), BorderLayout.CENTER);


        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close the current frame (or any other desired action)
                EmailGUI.this.dispose();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(closeButton);

        resultsPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Remove the existing panel
        getContentPane().removeAll();
        // Add the email screen panel to the frame
        getContentPane().add(resultsPanel);
        
        // Set the size and position of the frame
        setSize(300, 425);
        setLocationRelativeTo(null);

        revalidate();
        repaint();
    }
}

