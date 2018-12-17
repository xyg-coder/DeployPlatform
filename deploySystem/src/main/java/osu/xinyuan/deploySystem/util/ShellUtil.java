package osu.xinyuan.deploySystem.util;

import org.apache.commons.exec.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import osu.xinyuan.deploySystem.domains.JavaProjectInfo;
import osu.xinyuan.deploySystem.domains.JavaProjectStatus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class ShellUtil {

    private static Logger logger = LoggerFactory.getLogger(ShellUtil.class);

    /**
     * try to deploy the project
     * should be called after the check of url
     * If deployed success or fail, will send message to server
     * @param info
     */
    public static void deployJavaProject(JavaProjectInfo info, JmsTemplate jmsTemplate) throws IOException {
        String rootPath = Paths.get("codes/deploy/", Integer.toString(info.getId()), info.getRootPath()).toString();

        StringBuilder sb = new StringBuilder();
        sb.append("./shell/java-project/java_deploy.sh ")
                .append(info.getId()).append(" ")
                .append(info.getUrl()).append(" ")
                .append(rootPath);

        JavaProjectExecuteBinaryResultHandler deployHandler =
                new JavaProjectExecuteBinaryResultHandler(info.getId(), JavaProjectStatus.DEPLOYED,
                        JavaProjectStatus.DEPLOY_FAIL, jmsTemplate);

        ExecuteWatchdog watchdog = new ExecuteWatchdog(60*1000);
        Executor executor = new DefaultExecutor();
        executor.setWatchdog(watchdog);

        executor.execute(CommandLine.parse(sb.toString()), deployHandler);

        jmsTemplate.send("javaProjectStatus",
                session -> session.createTextMessage("javaProject-" + info.getId() + "=" + JavaProjectStatus.DEPLOYING.name()));
    }

    /**
     * start java project after the deployment
     * default running time 2 minute
     * @param info
     * @throws IOException
     */
    public static void startJavaProject(JavaProjectInfo info, JmsTemplate jmsTemplate) throws IOException {
        StringBuilder sb = new StringBuilder();
        String rootPath = Paths.get("codes/deploy/", Integer.toString(info.getId()), info.getRootPath()).toString();

        sb.append("./shell/java-project/java_start.sh ")
                .append(info.getId()).append(" ")
                .append(rootPath).append(" ")
                .append(info.getMainName());

        JavaProjectExecuteBinaryResultHandler handler =
                new JavaProjectExecuteBinaryResultHandler(info.getId(), JavaProjectStatus.STOP, JavaProjectStatus.STOP, jmsTemplate);

        ExecuteWatchdog watchdog = new ExecuteWatchdog(120 * 1000);
        Executor executor = new DefaultExecutor();
        executor.setWatchdog(watchdog);

        executor.execute(CommandLine.parse(sb.toString()), handler);

        jmsTemplate.send("javaProjectStatus",
                session -> session.createTextMessage("javaProject-" + info.getId() + "=" + JavaProjectStatus.RUNNING.name()));
    }

    public static void killJavaProject(JavaProjectInfo info, JmsTemplate jmsTemplate) throws IOException {
        if (info.getStatus() != JavaProjectStatus.RUNNING) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("./shell/java-project/java_project_kill.sh ")
                .append(info.getId());
        Executor executor = new DefaultExecutor();
        executor.setExitValues(null);

        executor.execute(CommandLine.parse(sb.toString()));

        jmsTemplate.send("javaProjectStatus",
                session -> session.createTextMessage("javaProject-" + info.getId() + "=" + JavaProjectStatus.STOP.name()));
    }

    /**
     * read one file dynamically, return the process for reading
     * caller is responsible for closing the process
     * @param filePath
     * @return
     */
    public static Process readFileDynamically(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            throw new IOException("No such path to read");
        }
        String command = "./shell/read_file_dynamic.sh " + filePath;
        return Runtime.getRuntime().exec(command);
    }

    /**
     * read java project deploy log dynamically
     * caller is responsible for closing the process
     * @param info
     * @return
     * @throws IOException
     */
    public static Process readJavaDeployLogDynamically(JavaProjectInfo info) throws IOException {
        String path =
                Paths.get("codes/deploy/", Integer.toString(info.getId()), info.getRootPath(), "package_log.log")
                        .toString();
        return readFileDynamically(path);
    }

    /**
     * read java project running log dynamically
     * caller is responsible for closing the process
     * @param info
     * @return
     * @throws IOException
     */
    public static Process readJavaRunningLogDynamically(JavaProjectInfo info) throws IOException {
        String path =
                Paths.get("codes/deploy/", Integer.toString(info.getId()), info.getRootPath(), "nohup.out")
                        .toString();

        return readFileDynamically(path);
    }

    /**
     * return the command to kill the process that dynamically read the deploy log
     * @param info
     * @return
     */
    public static String[] killJavaDeployLogProcessCommand(JavaProjectInfo info) {
        String path =
                Paths.get("codes/deploy/", Integer.toString(info.getId()), info.getRootPath(), "package_log.log")
                        .toString();
        return new String[]{"./shell/kill_with_command.sh", "tail", "-f", "-n", "500", path};
    }

    /**
     * return the Process to kill the process dynamically reading the running log
     * @param info
     * @return
     */
    public static String[] killJavaRunningLogProcessCommand(JavaProjectInfo info) {
        String path =
                Paths.get("codes/deploy/", Integer.toString(info.getId()), info.getRootPath(), "nohup.out")
                        .toString();
        return new String[]{"./shell/kill_with_command.sh", "tail", "-f", "-n", "500", path};
    }

    /**
     * delete all the files of java project with given id
     * @param id
     */
    public static void deleteJavaProject(int id) throws IOException{
        try {
            new ProcessBuilder("./shell/java-project/java_project_delete.sh", Integer.toString(id)).start().waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }
}
