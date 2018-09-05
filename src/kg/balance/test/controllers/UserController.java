package kg.balance.test.controllers;

import com.fasterxml.jackson.annotation.JsonRootName;
import kg.balance.test.dto.BaseResponse;
import kg.balance.test.dto.Result;
import kg.balance.test.exceptions.UserNotFound;
import kg.balance.test.models.User;
import kg.balance.test.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> createUser (@Valid @RequestBody User userData) {
        User newUser = userService.createUser(userData);
        return ResponseEntity.ok(new BaseResponse("ok", null, new Result() {
            public User user = newUser;
        }));
    }

    @PutMapping("/{user_id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<?> editUser (@PathVariable Long user_id, @Valid @RequestBody User userData) throws UserNotFound {
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
