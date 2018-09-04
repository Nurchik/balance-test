package kg.balance.test.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sellpoints")
public class SellPoint {
    @GetMapping("/")
    public String getSellpoints () {
        return "Salamatsyzby!";
    }

    @GetMapping("/{company_id}")
    public String getSellpointsByCompanyId (@RequestParam Number company_id) {
        return "Salamatsyzby!";
    }

    @PostMapping("/")
    public String createSellpoint () {
        return "created";
    }

    @PutMapping("/{sellpoint_id}")
    public String editCompany (@PathVariable Number sellpoint_id) {
        return "edited";
    }

    @DeleteMapping("/{sellpoint_id}")
    public String deleteCompany (@PathVariable Number sellpoint_id) {
        return "deleted";
    }
}
