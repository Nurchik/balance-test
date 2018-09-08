package kg.balance.test.controller;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import kg.balance.test.configs.RESTConfiguration;
import kg.balance.test.configs.SecurityConfiguration;
import kg.balance.test.models.Company;
import kg.balance.test.models.SellPoint;
import kg.balance.test.models.User;
import kg.balance.test.services.CompanyService;
import kg.balance.test.services.SellPointService;
import kg.balance.test.services.UserService;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { RESTConfiguration.class, SecurityConfiguration.class})
@WebAppConfiguration
@Transactional
public class SellPointControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    UserService userService;

    @Autowired
    SellPointService sellPointService;

    @Autowired
    CompanyService companyService;

    private MockMvc mockMVC;

    private String adminAuthToken;
    private String userAuthToken;
    private String user2AuthToken;

    private Long adminId;
    private Long userId;
    private Long user2Id;

    private User adminUser;
    private User regularUser;
    private User regularUser2;

    private Company firstCompany;
    private Company secondCompany;

    private SellPoint firstSellPoint;
    private SellPoint secondSellPoint;
    private SellPoint thirdSellPoint;
    private SellPoint fourthSellPoint;
    private SellPoint fifthSellPoint;



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
        adminUser = userService.createUser(user);
        User user_reg = new User();
        user_reg.setName("balance_user");
        user_reg.setPassword("Balance@User");
        regularUser = userService.createUser(user_reg);

        User user_reg2 = new User();
        user_reg2.setName("balance_user2");
        user_reg2.setPassword("Balance@User2");
        regularUser2 = userService.createUser(user_reg2);

        Company company = new Company();
        company.setName("TestCompany");
        company.setWebsite("https://test.kg/");

        Company company2 = new Company();
        company2.setName("AnotherTestCompany");

        firstCompany = companyService.createCompany(company);
        secondCompany = companyService.createCompany(company2);



        SellPoint firstSellPoint = new SellPoint();
        firstSellPoint.setUserId(regularUser.getId());
        firstSellPoint.setCompanyId(firstCompany.getId());
        firstSellPoint.setName("FirstSPName");
        firstSellPoint.setPhoneNumber("+996123456001");

        SellPoint secondSellPoint = new SellPoint();
        secondSellPoint.setUserId(regularUser.getId());
        secondSellPoint.setCompanyId(secondCompany.getId());
        secondSellPoint.setName("SecondSPName");
        secondSellPoint.setPhoneNumber("+996123456002");

        SellPoint thirdSellPoint = new SellPoint();
        thirdSellPoint.setUserId(regularUser2.getId());
        thirdSellPoint.setCompanyId(secondCompany.getId());
        thirdSellPoint.setName("ThirdSPName");
        thirdSellPoint.setPhoneNumber("+996123456003");

        SellPoint fourthSellPoint = new SellPoint();
        fourthSellPoint.setUserId(regularUser2.getId());
        fourthSellPoint.setCompanyId(firstCompany.getId());
        fourthSellPoint.setName("FourthSPName");
        fourthSellPoint.setPhoneNumber("+996123456004");

        SellPoint fifthSellPoint = new SellPoint();
        fifthSellPoint.setUserId(adminUser.getId());
        fifthSellPoint.setCompanyId(firstCompany.getId());
        fifthSellPoint.setName("FifthSPName");
        fifthSellPoint.setPhoneNumber("+996123456005");


        MvcResult res = mockMVC.perform(post("/auth/signin/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"balance_admin\", \"password\":\"Balance@Admin\"}"))
                .andExpect(status().isOk())
                .andReturn();
        adminAuthToken = JsonPath.read(res.getResponse().getContentAsString(), "$.result.auth_token");
        adminId = new Long(JsonPath.parse(res.getResponse().getContentAsString()).read("$.result.user.id"));

        MvcResult res_user = mockMVC.perform(post("/auth/signin/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"balance_user\", \"password\":\"Balance@User\"}"))
                .andExpect(status().isOk())
                .andReturn();
        userAuthToken = JsonPath.read(res_user.getResponse().getContentAsString(), "$.result.auth_token");
        userId = new Long(JsonPath.read(res_user.getResponse().getContentAsString(), "$.result.user.id"));

        MvcResult res_user2 = mockMVC.perform(post("/auth/signin/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"balance_user2\", \"password\":\"Balance@User2\"}"))
                .andExpect(status().isOk())
                .andReturn();
        user2AuthToken = JsonPath.read(res_user2.getResponse().getContentAsString(), "$.result.auth_token");
        user2Id = new Long(JsonPath.read(res_user2.getResponse().getContentAsString(), "$.result.user.id"));
    }

    @Test
    public void testGetSellPointsByAdmin() throws Exception {
        MvcResult result = mockMVC.perform(get("/sellpoints/")
                .header("Authorization", "Bearer " + adminAuthToken)
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errorCode", is("ok")))
                .andExpect(jsonPath("$.errorText", nullValue()))
                .andReturn();

        DocumentContext json = JsonPath.parse(result.getResponse().getContentAsString());
        int sellpoints_count = json.read("$.result.sellpoints.length()");
        int first_sp_idx = sellpoints_count - 5;
        int second_sp_idx = sellpoints_count - 4;
        int third_sp_idx = sellpoints_count - 3;
        int fourth_sp_idx = sellpoints_count - 2;
        int fifth_sp_idx = sellpoints_count - 1;

        Map<String, Object> firstSPData = json.read(String.format("$.result.sellpoints[%d]", first_sp_idx));
        Map<String, Object> secondSPData = json.read(String.format("$.result.sellpoints[%d]", second_sp_idx));
        Map<String, Object> thirdSPData = json.read(String.format("$.result.sellpoints[%d]", third_sp_idx));
        Map<String, Object> fourthSPData = json.read(String.format("$.result.sellpoints[%d]", fourth_sp_idx));
        Map<String, Object> fifthSPData = json.read(String.format("$.result.sellpoints[%d]", fifth_sp_idx));
        assertThat(sellpoints_count, greaterThanOrEqualTo(5));
        assertThat(firstSPData.get("name"), is("FirstSPName"));
        assertThat(secondSPData.get("name"), is("SecondSPName"));
        assertThat(thirdSPData.get("name"), is("ThirdSPName"));
        assertThat(fourthSPData.get("name"), is("FourthSPName"));
        assertThat(fifthSPData.get("name"), is("FifthSPName"));
    }

    @Test
    public void testGetSellPointsByUser() throws Exception {
        MvcResult result = mockMVC.perform(get("/sellpoints/")
                .header("Authorization", "Bearer " + userAuthToken)
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errorCode", is("ok")))
                .andExpect(jsonPath("$.errorText", nullValue()))
                .andReturn();

        DocumentContext json = JsonPath.parse(result.getResponse().getContentAsString());
        int sellpoints_count = json.read("$.result.sellpoints.length()");
        int first_sp_idx = sellpoints_count - 2;
        int second_sp_idx = sellpoints_count - 1;

        Map<String, Object> firstSPData = json.read(String.format("$.result.sellpoints[%d]", first_sp_idx));
        Map<String, Object> secondSPData = json.read(String.format("$.result.sellpoints[%d]", second_sp_idx));
        assertThat(sellpoints_count, greaterThanOrEqualTo(2));
        assertThat(firstSPData.get("name"), is("FirstSPName"));
        assertThat(firstSPData.get("id"), is(firstSellPoint.getId().toString()));
        assertThat(secondSPData.get("name"), is("SecondSPName"));

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