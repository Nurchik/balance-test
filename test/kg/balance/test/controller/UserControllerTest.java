package kg.balance.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { RESTConfiguration.class, SecurityConfiguration.class})
@WebAppConfiguration
@Transactional
public class UserControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    CompanyService companyService;

    @Autowired
    UserService userService;

    @Autowired
    SellPointService sellPointService;

    private MockMvc mockMVC;

    private String adminAuthToken;
    private String userAuthToken;

    private Long adminId;
    private Long userId;

    private Company firstCompany;
    private Company secondCompany;

    private SellPoint firstSellPoint;
    private SellPoint secondSellPoint;
    private SellPoint thirdSellPoint;


    @Autowired
    @Qualifier("springSecurityFilterChain")
    private Filter springSecurityFilterChain; // Подгружаем наш filter-chain, который был настроен в SecurityConfiguration

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

        Company company = new Company();
        company.setName("TestCompany");
        company.setWebsite("https://test.kg/");

        Company company2 = new Company();
        company2.setName("AnotherTestCompany");

        firstCompany = companyService.createCompany(company);
        secondCompany = companyService.createCompany(company2);

        SellPoint firstSellPoint = new SellPoint();
        firstSellPoint.setUserId(user.getId());
        firstSellPoint.setCompanyId(firstCompany.getId());
        firstSellPoint.setName("FirstSPName");
        firstSellPoint.setPhoneNumber("+996123456001");

        SellPoint secondSellPoint = new SellPoint();
        secondSellPoint.setUserId(user_reg.getId());
        secondSellPoint.setCompanyId(secondCompany.getId());
        secondSellPoint.setName("SecondSPName");
        secondSellPoint.setPhoneNumber("+996123456002");

        SellPoint thirdSellPoint = new SellPoint();
        thirdSellPoint.setUserId(user_reg.getId());
        thirdSellPoint.setCompanyId(secondCompany.getId());
        thirdSellPoint.setName("ThirdSPName");
        thirdSellPoint.setPhoneNumber("+996123456003");

        this.firstSellPoint = sellPointService.createSellPoint(user, firstSellPoint);
        this.secondSellPoint = sellPointService.createSellPoint(user, secondSellPoint);
        this.thirdSellPoint = sellPointService.createSellPoint(user, thirdSellPoint);

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

    }

    @Test
    public void testGetUsersListWithoutAdminRights() throws Exception {
        mockMVC.perform(get("/users/")
                .header("Authorization", "Bearer " + userAuthToken)
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errorCode", is("access_denied")))
                .andExpect(jsonPath("$.errorText", is("Access is denied")));
    }

    @Test
    public void testGetUsersList() throws Exception {
        MvcResult result = mockMVC.perform(get("/users/")
                .header("Authorization", "Bearer " + adminAuthToken)
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errorCode", is("ok")))
                .andExpect(jsonPath("$.errorText", nullValue()))
                .andReturn();

        DocumentContext json = JsonPath.parse(result.getResponse().getContentAsString());
        int users_count = json.read("$.result.users.length()");
        int admin_idx = users_count - 2;
        int user_idx = users_count - 1;
        Map<String, Object> adminData = json.read(String.format("$.result.users[%d]", admin_idx));
        Map<String, Object> userData = json.read(String.format("$.result.users[%d]", user_idx));
        assertThat(adminData.get("name"), is("balance_admin"));
        assertThat(userData.get("name"), is("balance_user"));
        assertThat(jsonPath(String.format("$.result.users[%d].sellpoints.length()", admin_idx)), is(1));
        assertThat(jsonPath(String.format("$.result.users[%d].sellpoints.length()", user_idx)), is(2));
    }

    @Test
    public void testGetUser() throws Exception {
        MvcResult result = mockMVC.perform(get("/users/" + adminId.toString())
                .header("Authorization", "Bearer " + adminAuthToken)
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errorCode", is("ok")))
                .andReturn();
        DocumentContext json = JsonPath.parse(result.getResponse().getContentAsString());
        assertThat(json.read("$.result.user.name"), is("balance_admin"));
        List<Map<String, String>> sellPoints = json.read("$.result.user.sellpoints");
        assertThat(sellPoints, hasSize(1));
        assertThat(sellPoints.get(0).get("id"), is(firstSellPoint.getId().toString()));
        assertThat(sellPoints.get(0).get("name"), is(firstSellPoint.getName()));
        assertThat(sellPoints.get(0).keySet(), hasSize(2));
    }

    @Test
    public void testGetUserNotFoundId() throws Exception {
        mockMVC.perform(get("/users/999999999999")
                .header("Authorization", "Bearer " + adminAuthToken)
        ).andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errorCode", is("user_not_found")))
                .andExpect(jsonPath("$.errorText", is("User not found")))
                .andExpect(jsonPath("$.result", nullValue()));
    }

    @Test
    public void testGetUserWrongIdType() throws Exception {
        mockMVC.perform(get("/users/abc545")
                .header("Authorization", "Bearer " + adminAuthToken)
        ).andExpect(status().isBadRequest());/*
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errorCode", is("parameter_error")))
                .andExpect(jsonPath("$.errorText", notNullValue(String.class)))
                .andExpect(jsonPath("$.result", nullValue()));*/
    }

    @Test
    public void testCreateUser() throws Exception {
        MvcResult result = mockMVC.perform(post("/users/")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminAuthToken)
                .content("{\"id\": 13,\"name\": \"balance_test_user\",\"password\": \"Balance@TestPass\",\"fullname\": \"Full Test Name\",\"is_admin\": true}")
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errorCode", is("ok")))
                .andExpect(jsonPath("$.errorText", nullValue()))
                .andReturn();
        Map<String, Object> userData = JsonPath.read(result.getResponse().getContentAsString(), "$.result.user");
        assertThat(userData.get("id"), not(equalTo("13")));
        assertThat(userData.getOrDefault("password", "$NOTSET$"), is("$NOTSET$"));
        assertThat(userData.get("name"), is("balance_test_user"));
        assertThat(userData.get("fullname"), is("Full Test Name"));
        assertThat(userData.get("phone_number"), nullValue());
        assertThat(userData.get("is_admin"), is(true));
        assertThat((List<Object>) userData.get("sellpoints"), hasSize(0));
        assertThat(userData.keySet(), hasSize(6));
    }

    @Test
    public void testCreateUserNoRequiredFields() throws Exception {
        mockMVC.perform(post("/users/")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminAuthToken)
                .content("{\"password\": \"Balance@TestPass\",\"fullname\": \"Full Test Name\",\"is_admin\": true}")
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errorCode", is("validation_error")))
                .andExpect(jsonPath("$.errorText", notNullValue(String.class)))
                .andExpect(jsonPath("$.result", nullValue()));
    }

    @Test
    public void testCreateUserNonUniqueName() throws Exception {
        mockMVC.perform(post("/users/")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminAuthToken)
                .content("{\"name\": \"balance_user\",\"password\": \"Balance@TestPass12\",\"fullname\": \"Full Test Name\",\"is_admin\": true}")
        ).andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errorCode", is("duplicate_data_error")))
                .andExpect(jsonPath("$.errorText", is("Field \"name\" must be unique")))
                .andExpect(jsonPath("$.result", nullValue()));
    }

    @Test
    public void testUpdateUser() throws Exception {
        MvcResult result = mockMVC.perform(put(String.format("/users/%s", userId))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminAuthToken)
                .content("{\"id\": \"123\",\"name\": \"balance_test_user_changed\",\"password\": \"SomeTest\",\"fullname\": \"Full Name\", \"phone_number\": \"996765475678\", \"is_admin\": true}")
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errorCode", is("ok")))
                .andExpect(jsonPath("$.errorText", nullValue()))
                .andReturn();

        Map<String, Object> userData = JsonPath.read(result.getResponse().getContentAsString(), "$.result.user");
        assertThat(userData.get("id"), equalTo(userId.toString()));
        assertThat(userData.get("name"), is("balance_user"));
        assertThat(userData.get("fullname"), is("Full Name"));
        assertThat(userData.get("phone_number"), is("996765475678"));
        assertThat(userData.get("is_admin"), is(true));
        assertThat((List<Object>) userData.get("sellpoints"), hasSize(0));
        assertThat(userData.keySet(), hasSize(6));

        //Проверяем, не сменился ли пароль
        mockMVC.perform(post("/auth/signin/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"balance_user\", \"password\":\"Balance@User\"}"))
                .andExpect(status().isOk());


        // проверяем, чтобы при не задании параметра is_admin, юзер не потерял права админа
        MvcResult result2 = mockMVC.perform(put(String.format("/users/%s", userId))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminAuthToken)
                .content("{\"phone_number\": \"996000010123\"}")
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk()).andReturn();
        Map<String, Object> userData2 = JsonPath.read(result2.getResponse().getContentAsString(), "$.result.user");
        assertThat(userData2.get("phone_number"), is("996000010123"));
        assertThat(userData2.get("is_admin"), is(true));
    }

    @Test
    public void testUpdateUserWrongId() throws Exception {
        mockMVC.perform(put(String.format("/users/%s", "99999999999"))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminAuthToken)
                .content("{\"id\": \"123\",\"name\": \"balance_test_user_changed\",\"password\": \"SomeTest\",\"fullname\": \"Full Name\", \"phone_number\": \"996765475678\"}")
        ).andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errorCode", is("user_not_found")))
                .andExpect(jsonPath("$.errorText", is("User not found")))
                .andExpect(jsonPath("$.result", nullValue()));
    }

    @Test
    public void testDeleteUser() throws Exception {
        mockMVC.perform(delete(String.format("/users/%s", userId))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminAuthToken)
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errorCode", is("ok")))
                .andExpect(jsonPath("$.errorText", nullValue()))
                .andExpect(jsonPath("$.result", nullValue()));
    }
}
