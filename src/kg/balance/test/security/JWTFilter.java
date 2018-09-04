package kg.balance.test.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
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
                if (userDetails == null) {
                    response.sendError(500, "User not found");
                    return;
                }
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            filterChain.doFilter(request, response);
        } catch (SignatureException ex) {
            response.sendError(500, "Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            response.sendError(500, "Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            response.sendError(500, "Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            response.sendError(500, "Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            response.sendError(500, "JWT claims string is empty.");
        } catch (Exception ex) {
            response.sendError(500, ex.getMessage());
        }
    }

    private String getJwtTokenFromRequest (HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (StringUtils.hasText(token) && token.startsWith("Bearer")) {
            return token.substring(7);
        }
        return null;
    }
}
