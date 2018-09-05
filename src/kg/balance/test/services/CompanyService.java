package kg.balance.test.services;

import kg.balance.test.dao.BalanceDAO;
import kg.balance.test.exceptions.CompanyNotFound;
import kg.balance.test.models.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

interface CompanyServiceInterface {
    public Company getCompany (Long id) throws CompanyNotFound;
    public List<Company> getCompanies ();
    public Company createCompany (Company company);
    public Company updateCompany (Long companyId, Company companyData) throws CompanyNotFound;
    public void deleteCompany (Long id) throws CompanyNotFound;

}

@Service
public class CompanyService implements CompanyServiceInterface {

    private BalanceDAO<Company> companyRepository;

    @Autowired
    public void setCompanyRepository(BalanceDAO<Company> companyRepository) {
        this.companyRepository = companyRepository;
        companyRepository.setEntityClass(Company.class);
    }

    @Transactional(readOnly = true)
    public Company getCompany(Long id) throws CompanyNotFound {
        return companyRepository.get(id).orElseThrow(CompanyNotFound::new);
    }

    @Transactional(readOnly = true)
    public List<Company> getCompanies() {
        return companyRepository.list().orElse(new ArrayList<>());
    }

    @Transactional
    public Company createCompany(Company company) {
        return companyRepository.add(company);
    }

    @Transactional
    public Company updateCompany(Long companyId, Company companyData) throws CompanyNotFound {
        Company company = companyRepository.get(companyId).orElseThrow(CompanyNotFound::new);
        company.setName(companyData.getName());
        company.setWebsite(companyData.getWebsite());
        return company;
    }

    @Transactional
    public void deleteCompany(Long id) throws CompanyNotFound {
        Company company = companyRepository.get(id).orElseThrow(CompanyNotFound::new);
        companyRepository.delete(company);
    }
}
