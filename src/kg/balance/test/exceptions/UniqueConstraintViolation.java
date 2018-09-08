package kg.balance.test.exceptions;

public class UniqueConstraintViolation extends CodedException {
    public UniqueConstraintViolation (String fieldName) {
        super("duplicate_data_error", String.format("Field \"%s\" must be unique", fieldName));
    }
}
