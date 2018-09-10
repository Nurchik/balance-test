package kg.balance.test.services;

import kg.balance.test.dao.BalanceDAOImpl;
import kg.balance.test.exceptions.*;
import kg.balance.test.models.Company;
import kg.balance.test.models.User;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService {

    private BalanceDAOImpl<Company> companyRepository;

    @Autowired
    SellPointService sellPointService;

    @Autowired
    UserService userService;

    @Autowired
    public void setCompanyRepository(BalanceDAOImpl<Company> companyRepository) {
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
    public Company createCompany(Company company) throws UniqueConstraintViolation{
        try {
            return companyRepository.add(company);
        } catch (
                PersistenceException ex) {
            if (ex.getCause().getClass() == ConstraintViolationException.class) {
                throw new UniqueConstraintViolation("name");
            }
            throw ex;
        }
    }

    @Transactional
    public Company updateCompany(Long companyId, Company companyData) throws CompanyNotFound, UniqueConstraintViolation {
        Company company = companyRepository.get(companyId).orElseThrow(CompanyNotFound::new);
        if (companyData.getName() != null) {
            company.setName(companyData.getName());
        }
        if (companyData.getWebsite() != null) {
            company.setWebsite(companyData.getWebsite());
        }
        companyData.setId(company.getId());
        try {
            companyRepository.update(company);
        } catch (PersistenceException ex) {
            // В общем, в ConstraintViolationException не только нарушения уникальности, но, для демо, будем считать, что только UniqueConstraintViolation
            if (ex.getCause().getClass() == ConstraintViolationException.class) {
                throw new UniqueConstraintViolation("name");
            }
            throw ex;
        }
        return company;
    }

    @Transactional
    public void deleteCompany(User user, Long id) throws CodedException {
        Company company = companyRepository.get(id).orElseThrow(CompanyNotFound::new);
        companyRepository.delete(company);
    }
}
