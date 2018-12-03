package osu.xinyuan.deploySystem.util;

import org.eclipse.jgit.api.LsRemoteCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
}
