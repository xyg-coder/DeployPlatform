package osu.xinyuan.deploySystem.util;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import osu.xinyuan.deploySystem.domains.JavaProjectInfo;
import osu.xinyuan.deploySystem.services.JavaProjectServiceImpl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

public class ShellUtil {

    private static Logger logger = LoggerFactory.getLogger(JavaProjectServiceImpl.class);

    /**
     * check the validation of the git url(must be public repo)
     * @return true if the repo is valid
     */
    public static boolean isValidRepo(String url) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("./shell/check_git_remote.sh ").append(url);
        String command = sb.toString();
        Process process = Runtime.getRuntime().exec(command);
        try(InputStream errorStream = process.getErrorStream()) {
            String err = IOUtils.toString(errorStream, "UTF-8");
            return err.isEmpty();
        }
    }

    /**
     * get the text of one file, can be used to read the log
     * @param path
     * @return
     * @throws IOException
     */
    private static String getTxtFile(String path) throws IOException {
        StringBuilder sb = new StringBuilder();
        if (!(new File(path)).exists()) {
            throw new IOException("No such file");
        }
        sb.append("./shell/read_file.sh ").append(path);
        String command = sb.toString();
        Process process = Runtime.getRuntime().exec(command);
        try(InputStream inputStream = process.getInputStream()) {
            String res = IOUtils.toString(inputStream, "UTF-8");
            return res;
        }
    }

    /**
     * try to deploy the project
     * should be called after the check of url
     * @param info
     * @throws DeployFailureException
     */
    public static void deployJavaProject(JavaProjectInfo info) throws DeployFailureException, IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("./shell/java-project/java_deploy.sh ")
                .append(info.getId()).append(" ")
                .append(info.getUrl()).append(" ")
                .append(info.getRootPath());
        String command = sb.toString();
        Process process = Runtime.getRuntime().exec(command);
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
        if (0 != process.exitValue()) {
            throw new DeployFailureException();
        }
    }

    /**
     * start java project after the deployment
     * @param info
     * @throws IOException
     */
    public static void startJavaProject(JavaProjectInfo info) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("./shell/java-project/java_start.sh ")
                .append(info.getId()).append(" ")
                .append(info.getRootPath()).append(" ")
                .append(info.getMainName());
        String command = sb.toString();
        Process process = Runtime.getRuntime().exec(command);
    }

    /**
     * return true if the corresponding javaProject is running now
     * @param info
     * @return
     * @throws IOException
     */
    public static boolean javaProjectIsRunning(JavaProjectInfo info) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("./shell/java-project/java_project_is_running.sh ")
                .append(info.getId());
        String command = sb.toString();
        Process process = Runtime.getRuntime().exec(command);

        try(InputStream inputStream = process.getInputStream()) {
            String out = IOUtils.toString(inputStream, "UTF-8");
            return !out.isEmpty();
        }
    }

    public static void killJavaProject(JavaProjectInfo info) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("./shell/java-project/java_project_kill.sh ")
                .append(info.getId());
        String command = sb.toString();
        Process process = Runtime.getRuntime().exec(command);
    }

    public static String getDeployedLog(JavaProjectInfo info) throws IOException {
        String path =
                Paths.get("codes/deploy/", Integer.toString(info.getId()), info.getRootPath(), "package_log.log")
                        .toString();

        return getTxtFile(path);
    }

    public static String getRunningLog(JavaProjectInfo info) throws IOException {
        String path =
                Paths.get("codes/deploy/", Integer.toString(info.getId()), info.getRootPath(), "nohup.out")
                        .toString();

        return getTxtFile(path);
    }
}
