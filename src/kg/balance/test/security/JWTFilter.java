package kg.balance.test.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import kg.balance.test.dto.BaseResponse;
import kg.balance.test.exceptions.UserNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTFilter extends OncePerRequestFilter {

    @Autowired
    private JWTProvider jwtProvider;

    @Autowired
    private BalanceUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Достаем токен из хэдера
        try {
            String jwtToken = getJwtTokenFromRequest(request);
            if (jwtToken != null) {
                // Сразу пытаемся получить ID пользователя. Ошибки при парсинге токена обработаем позже
                Long userId = jwtProvider.getUserId(jwtToken);
                UserDetails userDetails = userDetailsService.loadUserById(userId);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            filterChain.doFilter(request, response);
        } catch (UserNotFound ex) {
            respondWithJson(new BaseResponse(ex.getCode(), ex.getMessage()), response);
        } catch (SignatureException ex) {
            respondWithJson(new BaseResponse("jwt_error", "Invalid JWT signature"), response);
        } catch (MalformedJwtException ex) {
            respondWithJson(new BaseResponse("jwt_error", "Invalid JWT token"), response);
        } catch (ExpiredJwtException ex) {
            respondWithJson(new BaseResponse("jwt_error", "Expired JWT token"), response);
        } catch (UnsupportedJwtException ex) {
            respondWithJson(new BaseResponse("jwt_error", "Unsupported JWT token"), response);
        } catch (IllegalArgumentException ex) {
            respondWithJson(new BaseResponse("jwt_error", "JWT claims string is empty"), response);
        } catch (Exception ex) {
            respondWithJson(new BaseResponse("unhandled_error", ex.getMessage()), response);
        }
    }

    private String getJwtTokenFromRequest (HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (StringUtils.hasText(token) && token.startsWith("Bearer")) {
            return token.substring(7);
        }
        return null;
    }

    private void respondWithJson (BaseResponse baseResponse, HttpServletResponse response) {
        ObjectMapper om = new ObjectMapper();
        response.setStatus(500);
        response.setHeader("Content-Type", "application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            om.writeValue(response.getWriter(), baseResponse);
        } catch (IOException ex) {
            // Залогируем в будущем
        }
    }
}
