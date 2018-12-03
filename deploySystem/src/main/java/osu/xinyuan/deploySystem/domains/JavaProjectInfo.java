package osu.xinyuan.deploySystem.domains;

import javax.persistence.*;

@Entity
public class JavaProjectInfo {

    @Id
    @Column(name = "PROJECT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "URL")
    private String url;

    @Column(name = "ROOT_PATH")
    private String rootPath;

    @Column(name = "MAIN_NAME")
    private String mainName;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private JavaProjectStatus status;

    public JavaProjectInfo() {
        this.status = JavaProjectStatus.UNDEPLOYED;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public String getMainName() {
        return mainName;
    }

    public void setMainName(String mainName) {
        this.mainName = mainName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JavaProjectStatus getStatus() {
        return status;
    }

    public void setStatus(JavaProjectStatus status) {
        this.status = status;
    }
}
