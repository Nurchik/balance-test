package kg.balance.test.controllers;

import kg.balance.test.dto.BaseResponse;
import kg.balance.test.dto.Result;
import kg.balance.test.exceptions.UniqueConstraintViolation;
import kg.balance.test.exceptions.UserNotFound;
import kg.balance.test.models.User;
import kg.balance.test.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<?> getUsers () {
        List<User> usersList = userService.getUsers();
        return ResponseEntity.ok(new BaseResponse("ok", null, new Result() {
            public List<User> users = usersList;
        }));
    }

    @GetMapping("/{user_id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<?> getUser(@PathVariable Long user_id) throws UserNotFound {
        User userData = userService.getUser(user_id);
        return ResponseEntity.ok(new BaseResponse("ok", null, new Result() {
            public User user = userData;
        }));
    }

    @PostMapping("/")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<?> createUser (@Valid @RequestBody User userData) throws UniqueConstraintViolation {
        User newUser = userService.createUser(userData);
        return ResponseEntity.ok(new BaseResponse("ok", null, new Result() {
            public User user = newUser;
        }));
    }

    @PutMapping("/{user_id}")
    @Secured("ROLE_ADMIN")
    // На изменении пользователя мы убрали @Valid, т.к. можно изменить только некоторые поля, которые контролируются userService, а про поля name, password и т.д. можно не париться - они нам не нужны
    public ResponseEntity<?> editUser (@PathVariable Long user_id, @RequestBody User userData) throws UserNotFound {
        User updatedUser = userService.updateUser(user_id, userData);
        return ResponseEntity.ok(new BaseResponse("ok", null, new Result () {
            public User user = updatedUser;
        }));
    }

    @DeleteMapping("/{user_id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<?> deleteUser (@PathVariable Long user_id) throws UserNotFound {
        userService.deleteUser(user_id);
        return ResponseEntity.ok(new BaseResponse("ok", null));
    }
}
