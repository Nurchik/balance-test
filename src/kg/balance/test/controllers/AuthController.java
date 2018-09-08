package kg.balance.test.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import kg.balance.test.dto.Result;
import kg.balance.test.models.User;
import kg.balance.test.dto.BaseResponse;
import kg.balance.test.security.JWTProvider;
import kg.balance.test.dto.SigninRequest;
import kg.balance.test.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    JWTProvider tokenProvider;

    @Autowired
    AuthenticationManager authenticationManager;

    @PostMapping("/signin/")
    public ResponseEntity<?> authenticate (@Valid @RequestBody SigninRequest signupData) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(signupData.getUsername(), signupData.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
            UserPrincipal up = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            final User user = up.getUser();

            final String jwtToken = tokenProvider.generateToken(user.getId());
            return ResponseEntity.ok(new BaseResponse("ok", null, new Result() {
                @JsonProperty("auth_token")
                public String authToken = jwtToken;
                @JsonProperty("user")
                public User userData = user;
            }));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new BaseResponse("authentication_error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(new BaseResponse("unhandled_exception", ex.getMessage()));
        }

    }
}
