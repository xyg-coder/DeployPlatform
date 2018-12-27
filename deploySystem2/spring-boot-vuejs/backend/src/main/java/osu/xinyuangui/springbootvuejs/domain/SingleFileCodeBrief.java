package osu.xinyuangui.springbootvuejs.domain;

/**
 * Single file code without code
 * for web convert
 */
public class SingleFileCodeBrief {
    public int id;

    private SingleFileCodeType type;

    private String name;

    public SingleFileCodeBrief(int id, SingleFileCodeType type, String name, String description) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
    }

    private String description;

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
}
