package kg.balance.test.controllers;

import kg.balance.test.models.User;
import kg.balance.test.dto.BaseResponse;
import kg.balance.test.security.JWTProvider;
import kg.balance.test.dto.SigninRequest;
import kg.balance.test.dto.SuccessfulSignupResponse;
import kg.balance.test.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class Auth {

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
            //
            UserPrincipal up = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = up.getUser();

            String jwtToken = tokenProvider.generateToken(user.getId());
            return ResponseEntity.ok(new SuccessfulSignupResponse(jwtToken, user));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(500).body(new BaseResponse("authentication_error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(new BaseResponse("unhandled_exception", ex.getMessage()));
        }

    }
}
