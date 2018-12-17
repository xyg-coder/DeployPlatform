package osu.xinyuan.deploySystem.util;

import org.eclipse.jgit.api.LsRemoteCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import osu.xinyuan.deploySystem.domains.JavaProjectStatus;

import java.util.HashMap;
import java.util.Map;

public class Util {

    private static Logger logger = LoggerFactory.getLogger(Util.class);

    /**
     * check the validation of the git url(must be public repo)
     * @return true if the repo is valid
     */
    public static boolean isValidRepo(String repo) {
        final LsRemoteCommand lsCmd = new LsRemoteCommand(null);

        try {
            lsCmd.setRemote(repo);
            lsCmd.call().toString();
            return true;
        } catch (Exception e) {
            logger.error(repo, "invalid url");
            return false;
        }
    }

    /**
     * parse the message to get id and status of java project
     * @param message
     * @return
     */
    public static IdAndJavaStatus parseStr(String message) {
        String[] split = message.split("=");
        int id = Integer.parseInt(split[0].substring(12));
        JavaProjectStatus status = JavaProjectStatus.valueOf(split[1]);
        return new IdAndJavaStatus(id, status);
    }

    public static class IdAndJavaStatus {
        public int id;
        public JavaProjectStatus javaProjectStatus;
        public IdAndJavaStatus(int id, JavaProjectStatus status) {
            this.id = id;
            this.javaProjectStatus = status;
        }
    }

    public static Map<String, String> queryStringParse(String query) {
        Map<String, String> result = new HashMap<>();
        String[] splits = query.split("&");
        for (String split : splits) {
            String[] eqs = split.split("=");
            if (eqs.length > 1) {
                result.put(eqs[0], eqs[1]);
            }
        }
        return result;
    }
}
