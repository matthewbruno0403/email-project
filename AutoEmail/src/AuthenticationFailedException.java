// Custom exception for authentication failures
public class AuthenticationFailedException extends Exception {
  
  // Constructor that takes an error message as input
  public AuthenticationFailedException(String errorMessage) {
    // Call the constructor of the base class Exception and pass the error message
    super(errorMessage);
  }
}

