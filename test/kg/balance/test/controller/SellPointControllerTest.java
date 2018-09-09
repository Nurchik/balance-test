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
import org.junit.Before;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

        this.firstSellPoint = sellPointService.createSellPoint(adminUser, firstSellPoint);
        this.secondSellPoint = sellPointService.createSellPoint(adminUser, secondSellPoint);
        this.thirdSellPoint = sellPointService.createSellPoint(adminUser, thirdSellPoint);
        this.fourthSellPoint = sellPointService.createSellPoint(adminUser, fourthSellPoint);
        this.fifthSellPoint = sellPointService.createSellPoint(adminUser, fifthSellPoint);


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
        assertThat(secondSPData.get("id"), is(secondSellPoint.getId().toString()));
    }

    @Test
    public void testGetSellPointsByAdminByCompanyId() throws Exception {
            MvcResult result = mockMVC.perform(get("/sellpoints/?company_id=" + secondCompany.getId().toString())
                    .header("Authorization", "Bearer " + adminAuthToken)
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
            assertThat(firstSPData.get("id"), is(secondSellPoint.getId().toString()));
            assertThat(secondSPData.get("id"), is(thirdSellPoint.getId().toString()));
    }

    @Test
    public void testGetSellPointsByUserByCompanyId() throws Exception {
        MvcResult result = mockMVC.perform(get("/sellpoints/?company_id=" + secondCompany.getId().toString())
                .header("Authorization", "Bearer " + userAuthToken)
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errorCode", is("ok")))
                .andExpect(jsonPath("$.errorText", nullValue()))
                .andReturn();

        DocumentContext json = JsonPath.parse(result.getResponse().getContentAsString());
        int sellpoints_count = json.read("$.result.sellpoints.length()");
        int first_sp_idx = sellpoints_count - 1;

        Map<String, Object> firstSPData = json.read(String.format("$.result.sellpoints[%d]", first_sp_idx));

        assertThat(sellpoints_count, greaterThanOrEqualTo(1));
        assertThat(firstSPData.get("id"), is(secondSellPoint.getId().toString()));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetSellPointByIdByAdmin() throws Exception {
        MvcResult result = mockMVC.perform(get(String.format("/sellpoints/%s", fifthSellPoint.getId().toString()))
                .header("Authorization", "Bearer " + adminAuthToken)
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errorCode", is("ok")))
                .andExpect(jsonPath("$.errorText", nullValue()))
                .andReturn();

        DocumentContext json = JsonPath.parse(result.getResponse().getContentAsString());

        Map<String, Object> sellPointData = json.read("$.result.sellpoint");
        assertThat(sellPointData.get("id"), is(fifthSellPoint.getId().toString()));
        assertThat(((Map<String, String>) sellPointData.get("company")).get("id"), is(firstCompany.getId().toString()));
        assertThat(((Map<String, String>) sellPointData.get("company")).get("name"), is(firstCompany.getName()));
        assertThat(((Map<String, String>) sellPointData.get("company")).keySet(), hasSize(2));
        assertThat(((Map<String, String>) sellPointData.get("user")).get("id"), is(adminUser.getId().toString()));
        assertThat(((Map<String, String>) sellPointData.get("user")).get("name"), is(adminUser.getName()));
        assertThat(((Map<String, String>) sellPointData.get("user")).keySet(), hasSize(2));
        assertThat(sellPointData.keySet().containsAll(Arrays.asList("id", "name", "phone_number", "address", "latitude", "longitude", "user", "company")), is(true));
    }

    @Test
    public void testGetSellPointByIdByUserNotOwner() throws Exception {
        mockMVC.perform(get(String.format("/sellpoints/%s", firstSellPoint.getId().toString()))
                .header("Authorization", "Bearer " + user2AuthToken)
        ).andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errorCode", is("access_denied")))
                .andExpect(jsonPath("$.errorText", notNullValue(String.class)))
                .andExpect(jsonPath("$.result", nullValue()));
    }

    @Test
    public void testGetSellPointByIdNotFound() throws Exception {
        mockMVC.perform(get(String.format("/sellpoints/%s", "99999999999"))
                .header("Authorization", "Bearer " + user2AuthToken)
        ).andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errorCode", is("sellpoint_not_found")))
                .andExpect(jsonPath("$.errorText", notNullValue(String.class)))
                .andExpect(jsonPath("$.result", nullValue()));
    }

    @Test
    public void testCreateSellPoint() throws Exception {
        MvcResult result = mockMVC.perform(post("/sellpoints/")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminAuthToken)
                .content(String.format("{\"id\": \"123\",\"name\": \"first sellpoint\",\"company\": \"%s\"," +
                        "\"phone_number\": \"+996770000001\",\"address\": \"test address\"," +
                        "\"latitude\": \"45.7654\",\"user\": \"%s\"}", secondCompany.getId().toString(), regularUser.getId().toString()))
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errorCode", is("ok")))
                .andExpect(jsonPath("$.errorText", nullValue()))
                .andReturn();
        DocumentContext json = JsonPath.parse(result.getResponse().getContentAsString());

        Map<String, Object> sellPointData = json.read("$.result.sellpoint");
        assertThat(sellPointData.get("id"), not(equalTo("123")));
        assertThat(sellPointData.get("name"), is("first sellpoint"));
        assertThat(sellPointData.get("phone_number"), is("+996770000001"));
        assertThat(sellPointData.get("address"), is("test address"));
        assertThat(sellPointData.get("latitude"), is(45.7654));
        assertThat(sellPointData.get("longitude"), nullValue());
        assertThat(((Map<String, String>) sellPointData.get("company")).get("id"), is(secondCompany.getId().toString()));
        assertThat(((Map<String, String>) sellPointData.get("company")).get("name"), is(secondCompany.getName()));
        assertThat(((Map<String, String>) sellPointData.get("company")).keySet(), hasSize(2));
        assertThat(((Map<String, String>) sellPointData.get("user")).get("id"), is(regularUser.getId().toString()));
        assertThat(((Map<String, String>) sellPointData.get("user")).get("name"), is(regularUser.getName()));
        assertThat(((Map<String, String>) sellPointData.get("user")).keySet(), hasSize(2));
        assertThat(sellPointData.keySet().containsAll(Arrays.asList("id", "name", "phone_number", "address", "latitude", "longitude", "user", "company")), is(true));
    }

    @Test
    public void testCreateSellPointNoRequiredFields() throws Exception {
        mockMVC.perform(post("/sellpoints/")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminAuthToken)
                .content(String.format("{\"id\": \"123\",\"name\": \"second sellpoint\"," +
                        "\"phone_number\": \"+996770000001\",\"address\": \"test address\"," +
                        "\"latitude\": \"45.7654\",\"user\": \"%s\"}", regularUser.getId().toString()))
        ).andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errorCode", is("validation_error")))
                .andExpect(jsonPath("$.errorText", notNullValue(String.class)))
                .andExpect(jsonPath("$.result", nullValue()));
    }

    @Test
    public void testCreateSellPointNotFoundCompany() throws Exception {
        mockMVC.perform(post("/sellpoints/")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminAuthToken)
                .content(String.format("{\"id\": \"123\",\"name\": \"second sellpoint\", \"company\": \"999999999\"," +
                        "\"phone_number\": \"+996770000001\",\"address\": \"test address\"," +
                        "\"latitude\": \"45.7654\",\"user\": \"%s\"}", regularUser.getId().toString()))
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errorCode", is("company_not_found")))
                .andExpect(jsonPath("$.errorText", notNullValue(String.class)))
                .andExpect(jsonPath("$.result", nullValue()));
    }

    @Test
    public void testCreateSellPointNotFoundUser() throws Exception {
        mockMVC.perform(post("/sellpoints/")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminAuthToken)
                .content(String.format("{\"id\": \"123\",\"name\": \"second sellpoint\", \"company\": \"%s\"," +
                        "\"phone_number\": \"+996770000001\",\"address\": \"test address\"," +
                        "\"latitude\": \"45.7654\",\"user\": \"999999999\"}", secondCompany.getId().toString()))
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errorCode", is("user_not_found")))
                .andExpect(jsonPath("$.errorText", notNullValue(String.class)))
                .andExpect(jsonPath("$.result", nullValue()));
    }

    @Test
    public void testCreateSellPointByUserSetUserId() throws Exception {
        MvcResult result = mockMVC.perform(post("/sellpoints/")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + userAuthToken)
                .content(String.format("{\"id\": \"123\",\"name\": \"first sellpoint\",\"company\": \"%s\"," +
                        "\"phone_number\": \"+996770000002\",\"address\": \"test address\"," +
                        "\"user\": \"%s\"}", firstCompany.getId().toString(), regularUser2.getId().toString()))
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errorCode", is("ok")))
                .andExpect(jsonPath("$.errorText", nullValue()))
                .andReturn();
        DocumentContext json = JsonPath.parse(result.getResponse().getContentAsString());

        Map<String, Object> sellPointData = json.read("$.result.sellpoint");
        assertThat(sellPointData.get("id"), not(equalTo("123")));
        assertThat(sellPointData.get("phone_number"), is("+996770000002"));
        assertThat(((Map<String, String>) sellPointData.get("company")).get("id"), is(firstCompany.getId().toString()));
        assertThat(((Map<String, String>) sellPointData.get("user")).get("id"), is(regularUser.getId().toString()));
    }

    @Test
    public void testCreateSellPointByAdminWithoutUserId() throws Exception {
        MvcResult result = mockMVC.perform(post("/sellpoints/")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminAuthToken)
                .content(String.format("{\"name\": \"first sellpoint\",\"company\": \"%s\"," +
                        "\"phone_number\": \"+996770000003\"}", firstCompany.getId().toString()))
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errorCode", is("ok")))
                .andExpect(jsonPath("$.errorText", nullValue()))
                .andReturn();
        DocumentContext json = JsonPath.parse(result.getResponse().getContentAsString());

        Map<String, Object> sellPointData = json.read("$.result.sellpoint");
        assertThat(sellPointData.get("phone_number"), is("+996770000003"));
        assertThat(((Map<String, String>) sellPointData.get("company")).get("id"), is(firstCompany.getId().toString()));
        assertThat(((Map<String, String>) sellPointData.get("user")).get("id"), is(adminUser.getId().toString()));
    }

    @Test
    public void testCreateSellPointByUserWithoutUserId() throws Exception {
        MvcResult result = mockMVC.perform(post("/sellpoints/")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + user2AuthToken)
                .content(String.format("{\"name\": \"second sellpoint\",\"company\": \"%s\"," +
                        "\"phone_number\": \"+996770000004\"}", secondCompany.getId().toString()))
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errorCode", is("ok")))
                .andExpect(jsonPath("$.errorText", nullValue()))
                .andReturn();
        DocumentContext json = JsonPath.parse(result.getResponse().getContentAsString());

        Map<String, Object> sellPointData = json.read("$.result.sellpoint");
        assertThat(sellPointData.get("phone_number"), is("+996770000004"));
        assertThat(((Map<String, String>) sellPointData.get("company")).get("id"), is(secondCompany.getId().toString()));
        assertThat(((Map<String, String>) sellPointData.get("user")).get("id"), is(regularUser2.getId().toString()));
    }

    @Test
    public void testUpdateSellPointByAdmin() throws Exception {
        MvcResult result = mockMVC.perform(put(String.format("/sellpoints/%s", firstSellPoint.getId().toString()))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminAuthToken)
                .content(String.format("{\"id\": \"99999999\",\"name\": \"first updated sellpoint\",\"company\": \"%s\"," +
                        "\"user\": \"%s\", \"longitude\": \"67.0345\"}", secondCompany.getId().toString(), regularUser2.getId().toString()))
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errorCode", is("ok")))
                .andExpect(jsonPath("$.errorText", nullValue()))
                .andReturn();
        DocumentContext json = JsonPath.parse(result.getResponse().getContentAsString());

        Map<String, Object> sellPointData = json.read("$.result.sellpoint");
        assertThat(sellPointData.get("id"), not(equalTo("99999999")));
        assertThat(sellPointData.get("name"), is("first updated sellpoint"));
        assertThat(sellPointData.get("phone_number"), is("+996123456001"));
        assertThat(sellPointData.get("address"), nullValue());
        assertThat(sellPointData.get("latitude"), nullValue());
        assertThat(sellPointData.get("longitude"), is(67.0345));
        assertThat(((Map<String, String>) sellPointData.get("company")).get("id"), is(firstCompany.getId().toString()));
        assertThat(((Map<String, String>) sellPointData.get("user")).get("id"), is(regularUser2.getId().toString()));
    }

    @Test
    public void testUpdateSellPointByIdNotFound() throws Exception {
        MvcResult result = mockMVC.perform(put(String.format("/sellpoints/%s", "999999999"))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + user2AuthToken)
                .content(String.format("{\"id\": \"999999999\",\"name\": \"first updated sellpoint\",\"company\": \"%s\"," +
                        "\"user\": \"%s\", \"longitude\": \"67.0345\"}", secondCompany.getId().toString(), regularUser2.getId().toString()))
        ).andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errorCode", is("sellpoint_not_found")))
                .andExpect(jsonPath("$.errorText", notNullValue(String.class)))
                .andExpect(jsonPath("$.result", nullValue()))
                .andReturn();
    }

    @Test
    public void testUpdateSellPointByUserNotOwner() throws Exception {
        mockMVC.perform(put(String.format("/sellpoints/%s", firstSellPoint.getId().toString()))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + user2AuthToken)
                .content(String.format("{\"id\": \"999999999\",\"name\": \"first updated sellpoint\",\"company\": \"%s\"," +
                        "\"user\": \"%s\", \"longitude\": \"67.0345\"}", secondCompany.getId().toString(), regularUser2.getId().toString()))
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errorCode", is("access_denied")))
                .andExpect(jsonPath("$.errorText", is("Cannot update foreign sell point")))
                .andExpect(jsonPath("$.result", nullValue()));
    }

    @Test
    public void testUpdateSellPointByUser() throws Exception {
        MvcResult result = mockMVC.perform(put(String.format("/sellpoints/%s", secondSellPoint.getId().toString()))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + userAuthToken)
                .content(String.format("{\"address\": \"some address\",\"user\": \"%s\"}", regularUser2.getId().toString()))
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errorCode", is("ok")))
                .andExpect(jsonPath("$.errorText", nullValue()))
                .andReturn();
        DocumentContext json = JsonPath.parse(result.getResponse().getContentAsString());

        Map<String, Object> sellPointData = json.read("$.result.sellpoint");
        assertThat(sellPointData.get("name"), is("SecondSPName"));
        assertThat(sellPointData.get("address"), is("some address"));
        assertThat(((Map<String, String>) sellPointData.get("company")).get("id"), is(secondCompany.getId().toString()));
        assertThat(((Map<String, String>) sellPointData.get("user")).get("id"), is(regularUser.getId().toString()));
    }

    @Test
    public void testUpdateSellPointByUserNotSetUser() throws Exception {
        MvcResult result = mockMVC.perform(put(String.format("/sellpoints/%s", secondSellPoint.getId().toString()))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + userAuthToken)
                .content("{\"address\": \"some address 123\"}")
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errorCode", is("ok")))
                .andExpect(jsonPath("$.errorText", nullValue()))
                .andReturn();

        DocumentContext json = JsonPath.parse(result.getResponse().getContentAsString());
        Map<String, Object> sellPointData = json.read("$.result.sellpoint");
        assertThat(sellPointData.get("name"), is("SecondSPName"));
        assertThat(sellPointData.get("address"), is("some address 123"));
        assertThat(((Map<String, String>) sellPointData.get("user")).get("id"), is(regularUser.getId().toString()));
    }

    @Test
    public void testUpdateSellPointByAdminNotSetUser() throws Exception {
        MvcResult result = mockMVC.perform(put(String.format("/sellpoints/%s", thirdSellPoint.getId().toString()))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminAuthToken)
                .content("{\"address\": \"some address, 123\"}")
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errorCode", is("ok")))
                .andExpect(jsonPath("$.errorText", nullValue()))
                .andReturn();
        DocumentContext json = JsonPath.parse(result.getResponse().getContentAsString());

        Map<String, Object> sellPointData = json.read("$.result.sellpoint");
        assertThat(sellPointData.get("name"), is("ThirdSPName"));
        assertThat(sellPointData.get("address"), is("some address, 123"));
        assertThat(((Map<String, String>) sellPointData.get("user")).get("id"), is(regularUser2.getId().toString()));
    }

    @Test
    public void testUpdateSellPointByIdByAdminSetUserIdNotFoundUser() throws Exception {
        mockMVC.perform(put(String.format("/sellpoints/%s", firstSellPoint.getId().toString()))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminAuthToken)
                .content(String.format("{\"name\": \"updated sellpoint\", \"user\": \"%s\"}", "99999999"))
        ).andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errorCode", is("user_not_found")))
                .andExpect(jsonPath("$.errorText", notNullValue(String.class)))
                .andExpect(jsonPath("$.result", nullValue()));
    }

    @Test
    public void testDeleteSellPointByAdmin() throws Exception {
        mockMVC.perform(delete(String.format("/sellpoints/%s", firstSellPoint.getId().toString()))
                .header("Authorization", "Bearer " + adminAuthToken)
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errorCode", is("ok")))
                .andExpect(jsonPath("$.errorText", nullValue()))
                .andExpect(jsonPath("$.result", nullValue()));

        mockMVC.perform(get(String.format("/sellpoints/%s", firstSellPoint.getId().toString()))
                .header("Authorization", "Bearer " + adminAuthToken)
        ).andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errorCode", is("sellpoint_not_found")))
                .andExpect(jsonPath("$.errorText", notNullValue(String.class)))
                .andExpect(jsonPath("$.result", nullValue()));
    }

    @Test
    public void testDeleteSellPointByAdminNotFound() throws Exception {
        mockMVC.perform(delete(String.format("/sellpoints/%s", "99999999"))
                .header("Authorization", "Bearer " + adminAuthToken)
        ).andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errorCode", is("sellpoint_not_found")))
                .andExpect(jsonPath("$.errorText", notNullValue(String.class)))
                .andExpect(jsonPath("$.result", nullValue()));
    }

    @Test
    public void testDeleteSellPointByUser() throws Exception {
        mockMVC.perform(delete(String.format("/sellpoints/%s", firstSellPoint.getId().toString()))
                .header("Authorization", "Bearer " + userAuthToken)
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errorCode", is("ok")))
                .andExpect(jsonPath("$.errorText", nullValue()))
                .andExpect(jsonPath("$.result", nullValue()));
    }

    @Test
    public void testDeleteSellPointByUserNotOwner() throws Exception {
        mockMVC.perform(delete(String.format("/sellpoints/%s", firstSellPoint.getId().toString()))
                .header("Authorization", "Bearer " + user2AuthToken)
        ).andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errorCode", is("access_denied")))
                .andExpect(jsonPath("$.errorText", is("Cannot delete foreign sell point")))
                .andExpect(jsonPath("$.result", nullValue()));
    }
}