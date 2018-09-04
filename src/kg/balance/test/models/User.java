package kg.balance.test.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.org.apache.xpath.internal.operations.Bool;

import javax.persistence.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(schema = "public", name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    @Column
    private String name;

    @Column
    private String password;

    @JsonProperty("fullname")
    @Column(name = "fullname")
    private String fullName;

    @JsonProperty("phone_number")
    @Column(name = "phone_number")
    private String phoneNumber;

    @JsonProperty("is_admin")
    @Column(name = "is_admin")
    private Boolean isAdmin;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    // Игнорируем геттер, чтобы в JSON-представлении данного объекта не было пароля
    @JsonIgnore
    public String getPassword () {
        return password;
    }

    // А сеттер оставляем, чтобы была возможность задать пароль при создании объекта
    @JsonProperty
    public void setPassword (String password) {
        this.password = password;
    }

    public String getFullName () {
        return fullName;
    }

    public void setFullName (String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber () {
        return phoneNumber;
    }

    public void setPhoneNumber (String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Boolean getIsAdmin () {
        return isAdmin;
    }

    public void setIsAdmin (Boolean isaAdmin) {
        this.isAdmin = isAdmin;
    }
}
