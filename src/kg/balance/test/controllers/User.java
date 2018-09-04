package kg.balance.test.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class User {
    @GetMapping("/")
    public String getUsers () {
        return "Salamatsyzby!";
    }

    @PostMapping("/")
    public String createUser () {
        return "created";
    }

    @PutMapping("/{user_id}")
    public String editUser (@PathVariable Number user_id) {
        return "edited";
    }

    @DeleteMapping("/{user_id}")
    public String deleteUser (@PathVariable Number user_id) {
        return "deleted";
    }
}
