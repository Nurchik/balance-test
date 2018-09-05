package kg.balance.test.exceptions;

public class AccessDenied extends CodedException {
    public AccessDenied() {
        super("access_denied", "Access Denied!");
    }
}
