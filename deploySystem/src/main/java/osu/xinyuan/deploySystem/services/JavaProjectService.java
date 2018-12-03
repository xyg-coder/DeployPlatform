package osu.xinyuan.deploySystem.services;

import osu.xinyuan.deploySystem.domains.JavaProjectInfo;
import osu.xinyuan.deploySystem.domains.JavaProjectStatus;
import osu.xinyuan.deploySystem.util.DeployFailureException;

import java.io.IOException;
import java.util.List;

public interface JavaProjectService {
    List<JavaProjectInfo> getAllJavaProjects();

    void addJavaProject(JavaProjectInfo info) throws IllegalArgumentException, IOException;

    void deploy(int id) throws DeployFailureException, IOException;

    String getDeployLog(int id) throws IOException;

    void start(int id) throws IOException;

    String getRunningLog(int id) throws IOException;

    void stop(int id) throws IOException;

    JavaProjectStatus getProjectStatus(int id) throws IOException;

    void restart(int id) throws IOException;

    void update(int id) throws DeployFailureException, IOException;
}
