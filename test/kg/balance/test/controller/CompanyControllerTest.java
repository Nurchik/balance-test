package kg.balance.test.controller;

import kg.balance.test.configs.RESTConfiguration;
import kg.balance.test.configs.SecurityConfiguration;
import kg.balance.test.models.User;
import kg.balance.test.services.UserService;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { RESTConfiguration.class, SecurityConfiguration.class})
@WebAppConfiguration
@Transactional
public class CompanyControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    UserService userService;

    private MockMvc mockMVC;

    @Autowired
    @Qualifier("springSecurityFilterChain")
    private Filter springSecurityFilterChain;

    @BeforeClass
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
    public void testGetCompanies() throws Exception {}

    @Test
    public void testGetCompanyById() throws Exception {}

    @Test
    public void testGetCompanyByIdNotFound() throws Exception {}

    @Test
    public void testCreateCompany() throws Exception {}

    @Test
    public void testCreateCompanyNoRequiredFields() throws Exception {}

    @Test
    public void testCreateCompanyNonUniqueName() throws Exception {}

    @Test
    public void testUpdateCompany() throws Exception {}

    @Test
    public void testUpdateCompanyCompanyId() throws Exception {}

    @Test
    public void testUpdateCompanyName() throws Exception {}

    @Test
    public void testUpdateCompanyNotFoundId() throws Exception {}

    @Test
    public void testDeleteCompany() throws Exception {}

}
