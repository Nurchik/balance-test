package kg.balance.test.controller;

import kg.balance.test.configs.RESTConfiguration;
import kg.balance.test.configs.SecurityConfiguration;
import kg.balance.test.models.User;
import kg.balance.test.services.UserService;
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

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { RESTConfiguration.class, SecurityConfiguration.class})
@WebAppConfiguration
@Transactional
public class SellPointControllerTest {

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
    public void testGetSellPointsByAdmin() throws Exception {}

    @Test
    public void testGetSellPointsByUser() throws Exception {}

    @Test
    public void testGetSellPointsByAdminByCompanyId() throws Exception {}

    @Test
    public void testGetSellPointsByUserByCompanyId() throws Exception {}

    @Test
    public void testGetSellPointByIdByAdmin() throws Exception {}

    @Test
    public void testGetSellPointByIdByUserNotOwner() throws Exception {}

    @Test
    public void testGetSellPointByIdNotFound() throws Exception {}

    @Test
    public void testCreateSellPoint() throws Exception {}

    @Test
    public void testCreateSellPointNoRequiredFields() throws Exception {}

    @Test
    public void testCreateSellPointNotFoundCompany() throws Exception {}

    @Test
    public void testCreateSellPointByAdminSetUserId() throws Exception {}

    @Test
    public void testCreateSellPointByAdminSetUserIdNotFoundUser() throws Exception {}

    @Test
    public void testCreateSellPointByUserSetUserId() throws Exception {}

    @Test
    public void testCreateSellPointByAdminWithoutUserId() throws Exception {}

    @Test
    public void testCreateSellPointByUserWithoutUserId() throws Exception {}

    @Test
    public void testUpdateSellPointByIdNotFound() throws Exception {}

    @Test
    public void testUpdateSellPointByIdByUserNotOwner() throws Exception {}

    @Test
    public void testUpdateSellPointByIdByUser() throws Exception {}

    @Test
    public void testUpdateSellPointByIdByAdmin() throws Exception {}

    @Test
    public void testUpdateSellPointByIdByAdminSetUserId() throws Exception {}

    @Test
    public void testUpdateSellPointByIdByAdminSetUserIdNotFoundUser() throws Exception {}

    @Test
    public void testDeleteSellPointByAdmin() throws Exception {}

    @Test
    public void testDeleteSellPointByAdminNotFound() throws Exception {}

    @Test
    public void testDeleteSellPointByUser() throws Exception {}

    @Test
    public void testDeleteSellPointByUserNotOwner() throws Exception {}

}