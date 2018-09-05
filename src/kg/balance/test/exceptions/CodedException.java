package kg.balance.test.exceptions;

public class CodedException extends Exception {
    private String code;
    public CodedException (String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
