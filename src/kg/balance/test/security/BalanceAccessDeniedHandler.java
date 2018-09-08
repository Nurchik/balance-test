package kg.balance.test.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import kg.balance.test.dto.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class BalanceAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        ObjectMapper om = new ObjectMapper();
        httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
        httpServletResponse.setHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
        try {
            om.writeValue(httpServletResponse.getWriter(), new BaseResponse("access_denied", e.getMessage()));
        } catch (IOException ex) {
            // Залогируем в будущем
        }
    }
}
