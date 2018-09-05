package kg.balance.test.exceptions;

public class SellPointNotFound extends CodedException {
    public SellPointNotFound () {
        super("sellpoint_not_found", "SellPoint not found");
    }
}
