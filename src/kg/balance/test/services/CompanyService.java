package kg.balance.test.services;

import kg.balance.test.exceptions.CodedException;
import kg.balance.test.exceptions.CompanyNotFound;
import kg.balance.test.exceptions.UniqueConstraintViolation;
import kg.balance.test.models.Company;
import kg.balance.test.models.SellPoint;
import kg.balance.test.models.User;

import java.util.List;

public interface CompanyService {
    public Company getCompany(Long id) throws CompanyNotFound;

    public List<Company> getCompanies();

    public Company createCompany(Company company) throws UniqueConstraintViolation;

    public Company updateCompany(Long companyId, Company companyData) throws CompanyNotFound, UniqueConstraintViolation;

    public void deleteCompany(User user, Long id) throws CodedException;

    public void addSellPointToCompany (Long companyId, SellPoint sellPoint) throws CompanyNotFound;

    public void removeSellPointFromCompany (Long companyId, SellPoint sellPoint) throws CompanyNotFound;
}