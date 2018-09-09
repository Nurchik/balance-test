package kg.balance.test.models;

import com.fasterxml.jackson.annotation.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
//import org.hibernate.annotations.Table;

@Entity
@Table(name = "company", schema = "public")
public class Company {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank
    private String name;

    @Column
    private String website;

    @JsonIgnore
    @OneToMany(mappedBy = "company", orphanRemoval = true, fetch = FetchType.EAGER)
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    private List<SellPoint> sellPoints = new ArrayList<>();

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

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public List<SellPoint> getSellPoints() {
        return sellPoints;
    }

    @JsonGetter("sellpoints")
    public List<Map<String, String>> getSellPointsTrimmed () {
        List<SellPoint> sellPoints = getSellPoints();
        if (sellPoints == null) {
            sellPoints = new ArrayList<>();
        }
        return sellPoints.stream()
                .map(sellPoint -> {
                    Map<String, String> sellPointData = new HashMap<String, String>();
                    sellPointData.put("id", sellPoint.getId().toString());
                    sellPointData.put("name", sellPoint.getName());
                    //sellPointData.put("user", sellPoint.getUserId().toString());
                    return sellPointData;
                })
                .collect(Collectors.toList());
    }

    @JsonIgnore
    public void setSellPoints(List<SellPoint> sellPoints) {
        this.sellPoints = sellPoints;
    }
}