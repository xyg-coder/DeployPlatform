package osu.xinyuan.deploySystem.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import osu.xinyuan.deploySystem.domains.JavaProjectInfo;
import osu.xinyuan.deploySystem.repositories.JavaProjectInfoRepo;
import osu.xinyuan.deploySystem.repositories.JavaProjectStatus;
import osu.xinyuan.deploySystem.util.DeployFailureException;
import osu.xinyuan.deploySystem.util.ShellUtil;

import java.io.IOException;
import java.util.List;

@Service
public class JavaProjectServiceImpl implements JavaProjectService {

    @Autowired
    private JavaProjectInfoRepo javaProjectInfoRepo;

    private Logger logger = LoggerFactory.getLogger(JavaProjectServiceImpl.class);


    public JavaProjectInfoRepo getJavaProjectInfoRepo() {
        return javaProjectInfoRepo;
    }

    public void setJavaProjectInfoRepo(JavaProjectInfoRepo javaProjectInfoRepo) {
        this.javaProjectInfoRepo = javaProjectInfoRepo;
    }

    @Override
    public List<JavaProjectInfo> getAllJavaProjects() {
        return javaProjectInfoRepo.findAll();
    }

    /**
     * check the git url of this project, add it to the database
     * @param info
     * @throws IllegalArgumentException this url is illegal
     * @throws IOException something unknown happens
     */
    @Override
    public void addJavaProject(JavaProjectInfo info) throws IllegalArgumentException, IOException {
        if (!ShellUtil.isValidRepo(info.getUrl())) {
            logger.error("addJavaProject", "invalid url: " + info.getUrl());
            throw new IllegalArgumentException("invalid git url");
        }

        javaProjectInfoRepo.save(info);
        logger.info("save new info: " + info.getName());
    }

    /**
     * deploy the project in the database, delete existed files if already deployed
     * @param id
     * @throws DeployFailureException DeployFailure
     * @throws IOException something unknown happens
     */
    @Override
    public void deploy(int id) throws DeployFailureException, IOException {
        JavaProjectInfo info = javaProjectInfoRepo.findById(id);
        if (info == null) {
            logger.error("deploy", "no such info");
            throw new IOException("no such info");
        }

        ShellUtil.deployJavaProject(info);
        info.setIsDeployed(true);
        javaProjectInfoRepo.save(info);
    }

    /**
     * get deployLog
     * @param id
     * @return
     * @throws IOException not deployed yet or something unknown happens
     */
    @Override
    public String getDeployLog(int id) throws IOException {
        JavaProjectInfo info = javaProjectInfoRepo.findById(id);
        if (info == null) {
            logger.error("getDeployLog", "no such info");
            throw new IOException("no such info");
        }

        if (!info.getIsDeployed()) {
            logger.error("getDeployLog", "Not deployed yet");
            throw new IOException("Not deployed yet");
        }

        return ShellUtil.getDeployedLog(info);
    }

    /**
     * start one javaproject
     * @param id
     * @throws IOException not deployed yet or something unknown happens
     */
    @Override
    public void start(int id) throws IOException {
        JavaProjectInfo info = javaProjectInfoRepo.findById(id);
        if (info == null) {
            logger.error("start", "no such info");
            throw new IOException("no such info");
        }

        if (!info.getIsDeployed()) {
            logger.error("start", "Not deployed yet");
            throw new IOException("Not deployed yet");
        }

        ShellUtil.startJavaProject(info);
    }

    /**
     * get the log of one java project
     * @param id
     * @return
     * @throws IOException no such log or something unknown happens
     */
    @Override
    public String getRunningLog(int id) throws IOException {
        JavaProjectInfo info = javaProjectInfoRepo.findById(id);
        if (info == null) {
            logger.error("getRunningLog", "no such info");
            throw new IOException("no such info");
        }

        if (!info.getIsDeployed()) {
            logger.error("getRunningLog", "Not deployed yet");
            throw new IOException("Not deployed yet");
        }

        return ShellUtil.getRunningLog(info);
    }

    /**
     * stop the java project
     * @param id
     * @throws IOException
     */
    @Override
    public void stop(int id) throws IOException {
        JavaProjectInfo info = javaProjectInfoRepo.findById(id);
        if (info == null) {
            logger.error("stop", "no such info");
            throw new IOException("no such info");
        }

        ShellUtil.killJavaProject(info);
    }

    /**
     * get the status of java project
     * @param id
     * @return
     * @throws IOException
     */
    @Override
    public JavaProjectStatus getProjectStatus(int id) throws IOException {
        JavaProjectInfo info = javaProjectInfoRepo.findById(id);
        if (info == null) {
            logger.error("getProjectStatus", "no such info");
            throw new IOException("no such info");
        }

        if (!info.getIsDeployed()) {
            return JavaProjectStatus.UNDEPLOYED;
        } else if (ShellUtil.javaProjectIsRunning(info)) {
            return JavaProjectStatus.RUNNING;
        } else {
            return JavaProjectStatus.STOP;
        }
    }

    /**
     * stop if the project is running. start later
     * @param id
     * @throws IOException
     */
    @Override
    public void restart(int id) throws IOException {
        stop(id);
        start(id);
    }

    /**
     * update with the latest code. deploy
     * @param id
     * @throws IOException
     */
    @Override
    public void update(int id) throws DeployFailureException, IOException {
        deploy(id);
    }
}