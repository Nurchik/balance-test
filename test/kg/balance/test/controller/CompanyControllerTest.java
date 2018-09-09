package kg.balance.test.controller;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import kg.balance.test.configs.RESTConfiguration;
import kg.balance.test.configs.SecurityConfiguration;
import kg.balance.test.models.Company;
import kg.balance.test.models.User;
import kg.balance.test.services.CompanyService;
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

import java.util.*;

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
public class CompanyControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    CompanyService companyService;

    @Autowired
    UserService userService;

    private MockMvc mockMVC;

    private String adminAuthToken;
    private String userAuthToken;

    private Long adminId;
    private Long userId;

    private Company firstCompany;
    private Company secondCompany;

    @Autowired
    @Qualifier("springSecurityFilterChain")
    private Filter springSecurityFilterChain;

    @Before
    public void setup() throws Exception {
        mockMVC = MockMvcBuilders
                .webAppContextSetup(this.wac)
                .apply(springSecurity(springSecurityFilterChain))
                .build();

        Company company = new Company();
        company.setName("TestCompany");
        company.setWebsite("https://test.kg/");

        Company company2 = new Company();
        company2.setName("AnotherTestCompany");

        firstCompany = companyService.createCompany(company);
        secondCompany = companyService.createCompany(company2);



        User user = new User();
        user.setName("balance_admin");
        user.setPassword("Balance@Admin");
        user.setIsAdmin(true);
        userService.createUser(user);

        User user_reg = new User();
        user_reg.setName("balance_user");
        user_reg.setPassword("Balance@User");
        userService.createUser(user_reg);

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
    public void testGetCompanies() throws Exception {
        MvcResult result = mockMVC.perform(get("/companies/")
                .header("Authorization", "Bearer " + adminAuthToken)
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errorCode", is("ok")))
                .andExpect(jsonPath("$.errorText", nullValue()))
                .andReturn();

        DocumentContext json = JsonPath.parse(result.getResponse().getContentAsString());
        int companies_count = json.read("$.result.companies.length()");
        int first_company_idx = companies_count - 2;
        int second_company_idx = companies_count - 1;

        assertThat(companies_count, greaterThanOrEqualTo(2));


        Map<String, Object> firstCompanyData = json.read(String.format("$.result.companies[%d]", first_company_idx));
        Map<String, Object> secondCompanyData = json.read(String.format("$.result.companies[%d]", second_company_idx));
        assertThat(firstCompanyData.keySet().containsAll(Arrays.asList("id", "name", "website")), is(true));
        assertThat(secondCompanyData.keySet().containsAll(Arrays.asList("id", "name", "website")), is(true));
        assertThat(firstCompanyData.keySet(), hasSize(3));
    }

    @Test
    public void testGetCompanyById() throws Exception {
        MvcResult result = mockMVC.perform(get("/companies/" + secondCompany.getId().toString())
                .header("Authorization", "Bearer " + adminAuthToken)
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errorCode", is("ok")))
                .andReturn();
        DocumentContext json = JsonPath.parse(result.getResponse().getContentAsString());
        assertThat(json.read("$.result.company.name"), is("AnotherTestCompany"));
        assertThat(json.read("$.result.company.website"), nullValue());
    }

    @Test
    public void testGetCompanyByIdNotFound() throws Exception {
        mockMVC.perform(get("/companies/9999999999")
                .header("Authorization", "Bearer " + adminAuthToken)
        ).andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errorCode", is("company_not_found")))
                .andExpect(jsonPath("$.errorText", notNullValue(String.class)))
                .andExpect(jsonPath("$.result", nullValue()))
                .andReturn();
    }

    @Test
    public void testCreateCompany() throws Exception {
        MvcResult result = mockMVC.perform(post("/companies/" )
                .header("Authorization", "Bearer " + adminAuthToken)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("{\"id\": \"123\",\"name\": \"MyCompany\", \"website\": \"test.website\"}")
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errorCode", is("ok")))
                .andReturn();
        DocumentContext json = JsonPath.parse(result.getResponse().getContentAsString());
        assertThat(json.read("$.result.company.id"), not(equalTo("123")));
        assertThat(json.read("$.result.company.name"), is("MyCompany"));
        assertThat(json.read("$.result.company.website"), is("test.website"));
    }

    @Test
    public void testCreateCompanyNoRequiredFields() throws Exception {
        mockMVC.perform(post("/companies/")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminAuthToken)
                .content("{\"website\": \"balance.kg\"}")
        ).andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errorCode", is("validation_error")))
                .andExpect(jsonPath("$.errorText", notNullValue(String.class)))
                .andExpect(jsonPath("$.result", nullValue()));
    }

    @Test
    public void testCreateCompanyNonUniqueName() throws Exception {
        mockMVC.perform(post("/companies/")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminAuthToken)
                .content("{\"name\":\"TestCompany\", \"website\": \"balance.kg\"}")
        ).andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errorCode", is("duplicate_data_error")))
                .andExpect(jsonPath("$.errorText", is("Field \"name\" must be unique")))
                .andExpect(jsonPath("$.result", nullValue()));
    }

    @Test
    public void testUpdateCompany() throws Exception {
        MvcResult result = mockMVC.perform(put(String.format("/companies/%s", firstCompany.getId().toString()))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminAuthToken)
                .content("{\"name\":\"TestCompany2\", \"website\": \"test.kg\"}")
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errorCode", is("ok")))
                .andExpect(jsonPath("$.errorText", nullValue()))
                .andReturn();

        DocumentContext json = JsonPath.parse(result.getResponse().getContentAsString());
        assertThat(json.read("$.result.company.id"), equalTo(firstCompany.getId().toString()));
        assertThat(json.read("$.result.company.name"), is("TestCompany2"));
        assertThat(json.read("$.result.company.website"), is("test.kg"));
    }

    @Test
    public void testUpdateCompanyNonUniqueName() throws Exception {
        mockMVC.perform(put(String.format("/companies/%s", firstCompany.getId().toString()))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminAuthToken)
                .content("{\"name\":\"AnotherTestCompany\", \"website\": \"test.com\"}")
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errorCode", is("duplicate_data_error")))
                .andExpect(jsonPath("$.errorText", is("Field \"name\" must be unique")))
                .andExpect(jsonPath("$.result", nullValue()));

        // Проверяем, не изменилось ли ничего
        MvcResult result = mockMVC.perform(get("/companies/" + firstCompany.getId().toString())
                .header("Authorization", "Bearer " + adminAuthToken)
        ).andExpect(status().isOk()).andReturn();
        DocumentContext json = JsonPath.parse(result.getResponse().getContentAsString());
        assertThat(json.read("$.result.company.name"), is("TestCompany"));
        assertThat(json.read("$.result.company.website"), nullValue());
    }

    @Test
    public void testUpdateCompanyNotFoundId() throws Exception {
        mockMVC.perform(put(String.format("/companies/%s", "999999999"))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminAuthToken)
                .content("{\"name\":\"TestCompany2\", \"website\": \"test.kg\"}")
        ).andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errorCode", is("company_not_found")))
                .andExpect(jsonPath("$.errorText", notNullValue(String.class)))
                .andExpect(jsonPath("$.result", nullValue()));
    }

    @Test
    public void testDeleteCompany() throws Exception {
        mockMVC.perform(delete(String.format("/companies/%s", firstCompany.getId().toString()))
                .header("Authorization", "Bearer " + adminAuthToken)
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errorCode", is("ok")))
                .andExpect(jsonPath("$.errorText", nullValue()))
                .andExpect(jsonPath("$.result", nullValue()));
        // Проверяем удаление
        mockMVC.perform(get(String.format("/companies/%s", firstCompany.getId().toString()))
                .header("Authorization", "Bearer " + adminAuthToken)
        ).andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errorCode", is("company_not_found")))
                .andExpect(jsonPath("$.errorText", notNullValue(String.class)))
                .andExpect(jsonPath("$.result", nullValue()));
    }

    @Test
    public void testDeleteCompanyNotFoundId() throws Exception {
        mockMVC.perform(delete(String.format("/companies/%s", "99999999999"))
                .header("Authorization", "Bearer " + adminAuthToken)
        ).andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errorCode", is("company_not_found")))
                .andExpect(jsonPath("$.errorText", notNullValue(String.class)))
                .andExpect(jsonPath("$.result", nullValue()));
    }

}
