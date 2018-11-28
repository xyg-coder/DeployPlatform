package osu.xinyuan.deploySystem.services;

import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import osu.xinyuan.deploySystem.domains.JavaProjectInfo;
import osu.xinyuan.deploySystem.repositories.JavaProjectInfoRepo;

@Service
public class JavaProjectServiceImpl implements JavaProjectService {
    @Autowired
    private JavaProjectInfoRepo javaProjectInfoRepo;

    @Override
    public void deploy(JavaProjectInfo info) {

        throw new IllegalArgumentException();
    }

    @Override
    public void stop(int id) {

    }

    @Override
    public void restart(int id) {

    }

    @Override
    public void update(int id) {

    }

    @Override
    public String getLog(int id) {
        return null;
    }
}
