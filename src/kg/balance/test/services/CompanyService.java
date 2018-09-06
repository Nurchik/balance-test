package kg.balance.test.services;

import kg.balance.test.exceptions.CompanyNotFound;
import kg.balance.test.models.Company;
import java.util.List;

public interface CompanyService {
    public Company getCompany(Long id) throws CompanyNotFound;

    public List<Company> getCompanies();

    public Company createCompany(Company company);

    public Company updateCompany(Long companyId, Company companyData) throws CompanyNotFound;

    public void deleteCompany(Long id) throws CompanyNotFound;
}