import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;
import java.io.IOException;

/**
 * A class that uses PDFBox library to scan a PDF file and extract names and email addresses.
 */
public class TestPDFBox {
    private String pdfFilePath;
    private HashMap<String, String> emailMap; 

    /**
     * Constructs a TestPDFBox object with the specified PDF file path.
     * 
     * @param pdfFile The path of the PDF file to scan.
     */
    public TestPDFBox(String pdfFile) {
        this.pdfFilePath = pdfFile; // Assign file path
        this.emailMap = new HashMap<String, String>();
    }
    
    /**
     * Scans the PDF file and extracts names and email addresses.
     * 
     * @return A HashMap containing email addresses as keys and names as values.
     */
    public HashMap<String, String> scanPdf() {     	
    	String namePattern = "(?<=Attention:)(.*)";
    	String emailPattern = "\\b[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\\b";
        try {
            PDDocument document = PDDocument.load(new File(pdfFilePath));
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);

            Pattern nameRegex = Pattern.compile(namePattern);
            Pattern emailRegex = Pattern.compile(emailPattern);

            Matcher nameMatcher = nameRegex.matcher(text);
            Matcher emailMatcher = emailRegex.matcher(text);

            while (nameMatcher.find() && emailMatcher.find()) {
                String name = nameMatcher.group().trim();
                String email = emailMatcher.group();

                emailMap.put(email, name);
            }
            
            for(Map.Entry<String, String> entry : emailMap.entrySet()) {
            	System.out.println("Name: " + entry.getValue() + " email: " + entry.getKey());
            }
            
            document.close();
            return emailMap;
        } catch (IOException e) {
            e.printStackTrace();
        }
		return null;
    }
}

