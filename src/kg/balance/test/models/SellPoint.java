package kg.balance.test.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class SellPoint {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column
    private Long id;

    @Column
    private String name;

    @JsonProperty("company_id")
    @Transient
    private Long companyId;

    @NotBlank
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "company_id", foreignKey = @ForeignKey(name = "COMPANY_ID_FK"))
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

    @NotBlank
    @JsonProperty("user_id")
    @Transient
    private Long userId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "USER_ID_FK"))
    private User user;

    @JsonProperty("id")
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
    public Long getCompanyId() {
        return getCompany().getId();
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Company getCompany() {
        return company;
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
        return getUser().getId();
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
