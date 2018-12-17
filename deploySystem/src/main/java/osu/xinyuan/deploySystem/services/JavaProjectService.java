package osu.xinyuan.deploySystem.services;

import osu.xinyuan.deploySystem.domains.JavaProjectInfo;
import osu.xinyuan.deploySystem.domains.JavaProjectStatus;

import java.io.IOException;
import java.util.List;

public interface JavaProjectService {
    List<JavaProjectInfo> getAllJavaProjects();

    JavaProjectInfo getJavaProjectById(int id);

    JavaProjectInfo update(JavaProjectInfo info);

    void addJavaProject(JavaProjectInfo info) throws IllegalArgumentException, IOException;

    void deploy(int id) throws IOException;

    void start(int id) throws IOException;

    void stop(int id) throws IOException;

    JavaProjectStatus getProjectStatus(int id) throws IOException;

    void updateStatus(int id, JavaProjectStatus status);

    Process getDeployLogProcess(int id) throws IOException;

    Process getRunningLogProcess(int id) throws IOException;

    String[] killDeployLogProcessCommand(int id);

    String[] killRunningLogProcessCommand(int id);

    void delete(int id) throws IOException;
}
