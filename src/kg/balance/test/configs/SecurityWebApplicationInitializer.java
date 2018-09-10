package kg.balance.test.configs;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
// Данный класс нужен, чтобы DelegatingFilterProxy был зарегистрирован вкак фильтр сервлета.
// DelegatingFilterProxy подключит цепочку фильтров Spring Security
public class SecurityWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer {
}
