package kg.balance.test.models;

import com.fasterxml.jackson.annotation.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "sellpoint", schema = "public")
public class SellPoint {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column
    private Long id;

    @Column
    @NotBlank
    private String name;

    @JsonIgnore
    @Transient
    private Long companyId;

    @JsonIgnore
    @ManyToOne
    private Company company;

    @JsonProperty("phone_number")
    @Column(name = "phone_number")
    private String phoneNumber;

    @Column
    private String address;

    @Column
    private float latitude;

    @Column
    private float longitude;

    @JsonIgnore
    @Transient
    private Long userId;

    @JsonIgnore
    @ManyToOne
    private User user;

    @PostLoad
    public void setIdsOnLoad () {
        setUserId(user.getId());
        setCompanyId(company.getId());
    }

    @JsonProperty("id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public Long getId() {
        return id;
    }

    @JsonIgnore
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    // Здесь мы запрашиваем id Company, чтобы при получении объекта из БД, временная перемененная получила id company, которая будет отдаваться в JSON-виде
    public Long getCompanyId() { return companyId; }

    @JsonSetter("company")
    @NotBlank
    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Company getCompany() {
        return company;
    }

    @JsonGetter("company")
    public Map<String, String> getTrimmedCompanyData () {
        Map<String, String> companyData = new HashMap<>();
        companyData.put("id", getCompany().getId().toString());
        companyData.put("name", getCompany().getName());
        return companyData;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public Long getUserId() {
        return userId;
    }

    @JsonSetter("user")
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @JsonGetter("user")
    public Map<String, String> getTrimmedUserData () {
        Map<String, String> userData = new HashMap<>();
        userData.put("id", getUser().getId().toString());
        userData.put("name", getUser().getName());
        return userData;
    }
}
