package kg.balance.test.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import kg.balance.test.dto.BaseResponse;
import org.springframework.security.access.AccessDeniedException;
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
        httpServletResponse.setStatus(500);
        httpServletResponse.setHeader("Content-Type", "application/json");
        httpServletResponse.setCharacterEncoding("UTF-8");
        try {
            om.writeValue(httpServletResponse.getWriter(), new BaseResponse("access_denied", e.getMessage()));
        } catch (IOException ex) {
            // Залогируем в будущем
        }
    }
}
