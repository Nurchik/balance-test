package kg.balance.test.exceptions;

public class UserNotFound extends CodedException {
    public UserNotFound () {
        super("user_not_found", "User not found");
    }
}
