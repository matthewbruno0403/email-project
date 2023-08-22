import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;
import java.io.IOException;

public class TestPDFBox {
    private String pdfFilePath;
    private HashMap<String, String> emailMap; 

    public TestPDFBox(String pdfFile) {
        this.pdfFilePath = pdfFile; // Assign file path
        this.emailMap = new HashMap<String, String>();
    }
    
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
