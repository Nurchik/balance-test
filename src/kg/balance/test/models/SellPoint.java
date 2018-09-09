package kg.balance.test.models;

import com.fasterxml.jackson.annotation.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
    @JoinColumn(name = "company_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Company company;

    @JsonProperty("phone_number")
    @Column(name = "phone_number")
    private String phoneNumber;

    @Column
    private String address;

    @Column
    private Float latitude;

    @Column
    private Float longitude;

    @JsonIgnore
    @Transient
    private Long userId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
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
    @NotNull
    public Long getCompanyId() {
        if (companyId != null) {
            return companyId;
        }
        return getCompany().getId();
    }

    @JsonSetter("company")
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
        this.companyId = company.getId();
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

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
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
