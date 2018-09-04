package kg.balance.test.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/companies")
public class Company {
    @GetMapping("/")
    public String getCompanies () {
        return "Salamatsyzby!";
    }

    @PostMapping("/")
    public String createCompany () {
        return "created";
    }

    @PutMapping("/{company_id}")
    public String editCompany (@PathVariable Number company_id) {
        return "edited";
    }

    @DeleteMapping("/{company_id}")
    public String deleteCompany (@PathVariable Number company_id) {
        return "deleted";
    }
}
