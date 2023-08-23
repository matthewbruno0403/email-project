import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public class EmailSender {
    final String toEmail = "matthewbruno01@outlook.com";
    // psw: godusopp01@
    private String username;
    private String subject;
    private String emailContent;
    private Properties properties;
    private Session session;
    private HashMap<String, String> emailNameMap;
    private HashSet<String> emailBlacklist;
    
    public EmailSender() {
        this.properties = new Properties();
        properties.put("mail.smtp.host", "smtp-mail.outlook.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");      
    }
    
    /**
     * Sign in to the email account.
     * 
     * @param email    The email address.
     * @param password The password for the email account.
     * @return True if the sign-in is successful, false otherwise.
     * @throws AuthenticationFailedException If the authentication fails.
     */
    public boolean signIn(String email, String password) throws AuthenticationFailedException {
        this.session = Session.getInstance(this.properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        });

        try {
            Transport transport = session.getTransport("smtp");
            transport.connect(properties.getProperty("mail.smtp.host"), email, password);
            transport.close();
        } catch (MessagingException e) {
            throw new AuthenticationFailedException("Failed to authenticate email session");
        }

        this.username = email;
        System.out.println("Successfully signed in");
        return true;
    }
  
//    /**
//     * Read the content from a file.
//     * 
//     * @param filePath The path of the file to read.
//     * @return The content of the file as a string.
//     */
//    private String readContentFromFile(String filePath) {
//        StringBuilder content = new StringBuilder();
//        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                content.append(line).append("\n");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return content.toString();
//    }
    
    /**
     * Set the email-to-name mapping.
     * 
     * @param emailNameMap The mapping of email addresses to names.
     */
    public void setEmailNameMap(HashMap<String, String> emailNameMap) {
        this.emailNameMap = emailNameMap;
    }
    
    /**
     * Get the subject of the email.
     * 
     * @return The subject of the email.
     */
    public String getSubject() {
        return this.subject;
    }
    
    /**
     * Set the subject of the email.
     * 
     * @param subject The subject of the email.
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    /**
     * Set the body of the email.
     * 
     * @param body The body of the email.
     */
    public void setEmailBody(String body) {
        this.emailContent = body;
    }
    
    /**
     * Get the body of the email.
     * 
     * @return The body of the email.
     */
    public String getEmailBody() {
        return this.emailContent;
    }
    
    /**
     * Filter out blacklisted emails from the email-to-name mapping.
     */
    private void filterBlacklistedEmails() {
        int count = 0; // Step 1: Declare a variable to keep track of the count

        for (String blacklistedEmail : this.emailBlacklist) {
            try {
                if (this.emailNameMap.containsKey(blacklistedEmail)) { // Step 1: Check if the key exists in the map
                    if (this.emailNameMap.remove(blacklistedEmail) != null) { // Step 2: Increment the count only if the key is successfully removed
                        count++;
                    }
                }
            } catch (Exception e) {
                // Handle the exception gracefully (if needed)
                System.out.println("An error occurred while removing the email '" + blacklistedEmail + "'.");
            }
        }

        // Step 3: Output the count of blacklisted emails found
        System.out.println("Number of blacklisted emails found: " + count);
    }

    /**
     * Sets the blacklist of email addresses and filters out any blacklisted emails from the emailNameMap.
     * Prints the filtered email list.
     * 
     * @param blacklist The HashSet of blacklisted email addresses.
     */
    public void setEmailBlacklist(HashSet<String> blacklist) {
        this.emailBlacklist = blacklist;
        this.filterBlacklistedEmails();
        
        // TEST LINE
        if(this.emailNameMap == null) return;
        System.out.println("Filtered Email List: \n");
        for(Entry<String, String> entry : this.emailNameMap.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
    }

    /**
     * Prints the list of blacklisted email addresses.
     */
    public void getEmailBlacklistFromSender() {
        System.out.println("Blacklist:\n");
        
        if(this.emailBlacklist == null) return;
        
        for(String email : this.emailBlacklist) {
            System.out.println(email);
        }
    }

    /**
     * Sends an email with the specified subject and body.
     * 
     * @param emailSubject The subject of the email.
     * @param emailBody The body of the email.
     * @return true if the email is sent successfully, false otherwise.
     * @throws MessagingException if there is an error in sending the email.
     * @throws IOException if there is an error in reading the email content from a file.
     */
    public boolean sendEmail(String emailSubject, String emailBody) throws MessagingException, IOException {
        Message message = new MimeMessage(this.session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(emailSubject);
        //emailContent = readContentFromFile("email_template.txt");
        message.setText(emailBody);
        Transport.send(message);
        System.out.println("Sent message successfully....");
        return true;
    }

    /**
     * Sends emails to all recipients in the emailNameMap using the specified subject and email body template.
     * Replaces the [NAME] placeholder in the email body template with the first name of each recipient.
     * 
     * @param emailSubject The subject of the emails.
     * @param emailBodyTemplate The template for the email body, with [NAME] as the placeholder for the recipient's first name.
     * @return true if all emails are sent successfully, false otherwise.
     * @throws MessagingException if there is an error in sending the emails.
     * @throws IOException if there is an error in reading the email content from a file.
     */
    public boolean sendAllEmails(String emailSubject, String emailBodyTemplate) throws MessagingException, IOException {
        for (Map.Entry<String, String> entry : this.emailNameMap.entrySet()) {
            // Extract the first name from entry.getValue()
            String[] names = entry.getValue().split("\\s");
            String firstName = names[0];

            // Rest of the code
            String emailBody = emailBodyTemplate.replace("[NAME]", firstName);
            Message message = new MimeMessage(this.session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(entry.getKey()));
            message.setSubject(emailSubject);
            message.setText(emailBody);
            Transport.send(message);
            System.out.println("Sent message successfully to " + entry.getKey() + " / " + entry.getValue());
        }
        return true;
    }
}
