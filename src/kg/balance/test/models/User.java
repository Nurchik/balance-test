package kg.balance.test.models;

import com.fasterxml.jackson.annotation.*;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Table(name = "user", schema = "public")
public class User {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column
    private Long id;

    @NaturalId
    @Column(unique = true, nullable = false)
    @NotBlank
    private String name;

    @Column(nullable = false)
    @NotBlank
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

    public void setName (String name) {
        this.name = name;
    }

    // Игнорируем геттер, чтобы в JSON-представлении данного объекта не было пароля
    @JsonIgnore
    public String getPassword () {
        return password;
    }

    // А сеттер оставляем, чтобы была возможность задать пароль при создании объекта
    @JsonProperty("password")
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

    public void setIsAdmin (Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
}
