package osu.xinyuangui.springbootvuejs.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

/**
 * Represent the information of one single file code
 * the jsonIgnoreProperties mainly handle the hibernate garbage
 */
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SingleFileCodeInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE")
    private SingleFileCodeType type;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "CODE")
    private String code;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SingleFileCodeType getType() {
        return type;
    }

    public void setType(SingleFileCodeType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
