package kg.balance.test.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import kg.balance.test.dto.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JWTAuthenticationEntryPoint implements AuthenticationEntryPoint {
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        ObjectMapper om = new ObjectMapper();
        httpServletResponse.setStatus(httpServletResponse.SC_UNAUTHORIZED);
        httpServletResponse.setHeader("Content-Type", "application/json");
        httpServletResponse.setCharacterEncoding("UTF-8");
        try {
            om.writeValue(httpServletResponse.getWriter(), new BaseResponse("access_denied", e.getMessage()));
        } catch (IOException ex) {
            // Залогируем в будущем
        }
    }
}
