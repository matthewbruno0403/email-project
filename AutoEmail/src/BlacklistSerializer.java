import java.io.*;
import java.util.HashSet;

public class BlacklistSerializer {
    public static void serializeBlacklist(HashSet<String> blacklist) {
        try {
            FileOutputStream fos = new FileOutputStream("blacklist.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(blacklist); // Write the HashSet object to the stream
            oos.close();
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static HashSet<String> deserializeBlacklist() {
        HashSet<String> deserializedEmails = new HashSet<>(); // Default, in case file does not exist or error occurs
        try {
            FileInputStream fis = new FileInputStream("blacklist.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            deserializedEmails = (HashSet) ois.readObject(); // Read the object from the stream and cast it to HashSet
            ois.close();
            fis.close();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        } catch(ClassNotFoundException cnfe) {
            cnfe.printStackTrace();   
        }
        return deserializedEmails;
    }
}
