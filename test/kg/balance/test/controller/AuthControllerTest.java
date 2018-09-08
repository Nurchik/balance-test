package kg.balance.test.controller;


import kg.balance.test.configs.RESTConfiguration;
import kg.balance.test.configs.SecurityConfiguration;
import kg.balance.test.models.User;
import kg.balance.test.services.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import javax.servlet.Filter;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { RESTConfiguration.class, SecurityConfiguration.class})
@WebAppConfiguration
@Transactional
public class AuthControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    UserService userService;

    private MockMvc mockMVC;

    @Autowired
    @Qualifier("springSecurityFilterChain")
    private Filter springSecurityFilterChain;

    @Before
    public void setup() throws Exception {
        mockMVC = MockMvcBuilders
                .webAppContextSetup(this.wac)
                .apply(springSecurity(springSecurityFilterChain))
                .build();

        User user = new User();
        user.setName("balance_admin");
        user.setPassword("Balance@Admin");
        user.setIsAdmin(true);
        userService.createUser(user);
        User user_reg = new User();
        user_reg.setName("balance_user");
        user_reg.setPassword("Balance@User");
        userService.createUser(user_reg);
    }

    @Test
    public void testUnathorizedAccess () throws Exception {
        mockMVC.perform(get("/users/"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode", is("access_denied")))
                .andExpect(jsonPath("$.errorText", containsString("Full authentication is required")));
    }

    @Test
    public void testSuccessfulAdminLogin () throws Exception {
        mockMVC.perform(post("/auth/signin/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"balance_admin\", \"password\":\"Balance@Admin\"}")
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.errorCode", is("ok")))
                .andExpect(jsonPath("$.errorText", nullValue()))
                .andExpect(jsonPath("$.result.auth_token", notNullValue(String.class)))
                .andExpect(jsonPath("$.result.user.name", is("balance_admin")))
                .andExpect(jsonPath("$.result.user.is_admin", is(true)));
    }

    @Test
    public void testSuccessfulRegularUserLogin () throws Exception {
        mockMVC.perform(post("/auth/signin/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"balance_user\", \"password\":\"Balance@User\"}")
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.errorCode", is("ok")))
                .andExpect(jsonPath("$.errorText", nullValue()))
                .andExpect(jsonPath("$.result.auth_token", notNullValue(String.class)))
                .andExpect(jsonPath("$.result.user.name", is("balance_user")))
                .andExpect(jsonPath("$.result.user.is_admin", is(false)));
    }

    @Test
    public void testWrongPasswordLogin () throws Exception {
        mockMVC.perform(post("/auth/signin/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"balance_user\", \"password\":\"Balance@User123\"}")
        ).andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode", is("authentication_error")))
                .andExpect(jsonPath("$.errorText", notNullValue(String.class)))
                .andExpect(jsonPath("$.result", nullValue()));
    }

    @Test
    public void testWrongUsernameLogin () throws Exception {
        mockMVC.perform(post("/auth/signin/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"balance_user123\", \"password\":\"Balance@User123\"}")
        ).andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode", is("authentication_error")))
                .andExpect(jsonPath("$.errorText", notNullValue(String.class)))
                .andExpect(jsonPath("$.result", nullValue()));
    }

    @Test
    public void testWrongRequestLogin () throws Exception {
        mockMVC.perform(post("/auth/signin/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"balance_user\", \"password\":\"Balance@User\", \"username\": null}")
        ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("validation_error")))
                .andExpect(jsonPath("$.errorText", notNullValue(String.class)))
                .andExpect(jsonPath("$.result", nullValue()));
    }
}
