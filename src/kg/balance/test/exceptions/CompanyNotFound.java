package kg.balance.test.exceptions;

public class CompanyNotFound extends CodedException {
    public CompanyNotFound () {
        super("company_not_found", "Company not found");
    }
}
