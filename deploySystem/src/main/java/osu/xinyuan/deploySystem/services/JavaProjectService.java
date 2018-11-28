package osu.xinyuan.deploySystem.services;

import osu.xinyuan.deploySystem.domains.JavaProjectInfo;

public interface JavaProjectService {
    void deploy(JavaProjectInfo info);

    void stop(int id);

    void restart(int id);

    void update(int id);

    String getLog(int id);
}
