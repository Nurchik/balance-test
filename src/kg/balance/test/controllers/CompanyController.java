package kg.balance.test.controllers;

import kg.balance.test.dto.BaseResponse;
import kg.balance.test.dto.Result;
import kg.balance.test.exceptions.CodedException;
import kg.balance.test.exceptions.CompanyNotFound;
import kg.balance.test.exceptions.UniqueConstraintViolation;
import kg.balance.test.models.Company;
import kg.balance.test.security.UserPrincipal;
import kg.balance.test.services.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/companies")
public class CompanyController {

    @Autowired
    CompanyService companyService;

    @GetMapping("/")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<?> getCompanies () {
        List<Company> companiesList = companyService.getCompanies();
        return ResponseEntity.ok(new BaseResponse("ok", null, new Result () {
            public List<Company> companies = companiesList;
        }));
    }

    @GetMapping("/{company_id}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<?> getCompany (@PathVariable Long company_id) throws CompanyNotFound {
        Company companyData = companyService.getCompany(company_id);
        return ResponseEntity.ok(new BaseResponse("ok", null, new Result () {
            public Company company = companyData;
        }));
    }

    @PostMapping("/")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<?> createCompany (@Valid @RequestBody Company companyData) throws UniqueConstraintViolation {
        Company newCompany = companyService.createCompany(companyData);
        return ResponseEntity.ok(new BaseResponse("ok", null, new Result () {
            public Company company = newCompany;
        }));
    }

    @PutMapping("/{company_id}")
    @Secured({"ROLE_ADMIN"})
    // Здесь, также, не нужна аннотация @Valid. Мы сами отберем нужные поля в companyService для дальнейшей обработки
    public ResponseEntity<?> updateCompany (@PathVariable Long company_id, @RequestBody Company companyData) throws CompanyNotFound, UniqueConstraintViolation {
        Company updatedCompany = companyService.updateCompany(company_id, companyData);
        return ResponseEntity.ok(new BaseResponse("ok", null, new Result () {
            public Company company = updatedCompany;
        }));

    }

    @DeleteMapping("/{company_id}")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<?> deleteCompany (@PathVariable Long company_id) throws CodedException {
        UserPrincipal up = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        companyService.deleteCompany(up.getUser(), company_id);
        return ResponseEntity.ok(new BaseResponse("ok", null));
    }
}
