package osu.xinyuan.deploySystem.util;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class ShellUtil {

    /**
     * check the validation of the git url(must be public repo)
     * @return true if the repo is valid
     */
    public static boolean isValidRepo(String url) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("./shell/check_git_remote.sh").append(" ").append(url);
        String command = sb.toString();
        Process process = Runtime.getRuntime().exec(command);
        try(InputStream errorStream = process.getErrorStream()) {
            String err = IOUtils.toString(errorStream, "UTF-8");
            System.out.println(">>> error: " + err + " >>> \nerror finishes");
            return err.isEmpty();
        }
    }
}
