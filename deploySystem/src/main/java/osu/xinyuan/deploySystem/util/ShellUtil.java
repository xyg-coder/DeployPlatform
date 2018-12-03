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

        CommandLine commandLine = CommandLine.parse(sb.toString());
        DefaultExecutor executor = new DefaultExecutor();
        executor.setExitValues(null);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
        executor.setStreamHandler(streamHandler);
        executor.execute(commandLine);

        return outputStream.toString();
    }

    /**
     * try to deploy the project
     * should be called after the check of url
     * If deployed success, will send message to server
     * @param info
     * @throws DeployFailureException
     */
    public static void deployJavaProject(JavaProjectInfo info, JmsTemplate jmsTemplate) throws DeployFailureException, IOException {
        String rootPath = Paths.get("codes/deploy/", Integer.toString(info.getId()), info.getRootPath()).toString();

        StringBuilder sb = new StringBuilder();
        sb.append("./shell/java-project/java_deploy.sh ")
                .append(info.getId()).append(" ")
                .append(info.getUrl()).append(" ")
                .append(rootPath);

        ExecuteWatchdog watchdog = new ExecuteWatchdog(60*1000);
        Executor executor = new DefaultExecutor();
        executor.setWatchdog(watchdog);

        int exitValue = executor.execute(CommandLine.parse(sb.toString()));

        if (exitValue == 1) {
            throw new DeployFailureException();
        } else {
            jmsTemplate.send("javaProjectStatus",
                    session -> session.createTextMessage("javaProject-" + info.getId() + "=" + JavaProjectStatus.DEPLOYED.name()));
        }
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

        JavaProjectExecuteResultHandler handler =
                new JavaProjectExecuteResultHandler(info.getId(), JavaProjectStatus.STOP);

        ExecuteWatchdog watchdog = new ExecuteWatchdog(120 * 1000);
        Executor executor = new DefaultExecutor();
        executor.setWatchdog(watchdog);

        executor.execute(CommandLine.parse(sb.toString()), handler);

        jmsTemplate.send("javaProjectStatus",
                session -> session.createTextMessage("javaProject-" + info.getId() + "=" + JavaProjectStatus.RUNNING.name()));
    }

    /**
     * return true if the corresponding javaProject is running now
     * @param info
     * @return
     * @throws IOException
     */
    public static boolean javaProjectIsRunning(JavaProjectInfo info, JmsTemplate jmsTemplate) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("./shell/java-project/java_project_is_running.sh ")
                .append(info.getId());

        Executor executor = new DefaultExecutor();
        executor.setExitValues(null);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);

        executor.setStreamHandler(streamHandler);
        executor.execute(CommandLine.parse(sb.toString()));

        return !outputStream.toString().isEmpty();
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
