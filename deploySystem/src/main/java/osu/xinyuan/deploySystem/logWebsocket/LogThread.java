package osu.xinyuan.deploySystem.logWebsocket;

import jdk.nashorn.tools.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.util.HtmlUtils;
import osu.xinyuan.deploySystem.util.ShellUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * will manage the log reading process
 * will output the output stream of that process to one websocket
 */
public class LogThread implements Runnable {
    private Process logProcess;
    private WebSocketSession webSocketSession;
    private BufferedReader reader;

    private Logger logger = LoggerFactory.getLogger(LogThread.class);

    private String[] killCommand;

    public LogThread(WebSocketSession session, Process logProcess, String[] killCommand) throws UnsupportedEncodingException {
        this.logProcess = logProcess;
        if (logProcess != null) {
            this.reader = new BufferedReader(new InputStreamReader(logProcess.getInputStream(), "UTF-8"));
        } else {
            this.reader = null;
        }
        this.webSocketSession = session;
        this.killCommand = killCommand;
    }

    @Override
    public void run() {
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                webSocketSession.sendMessage(new TextMessage(HtmlUtils.htmlEscape(line) + "<br>"));
            }
        } catch (IOException e) {

            try {
                webSocketSession.sendMessage(new TextMessage(HtmlUtils.htmlEscape(e.getMessage()) + "<br>"));
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            logger.error(e.getMessage());
        }
    }

    public void close() throws IOException, InterruptedException {
        destroyProcess();
        logger.info("start closing the reader");
        reader.close();
        logger.info("finish closing the reader");
    }

    private void destroyProcess() throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(killCommand);
        System.out.println(">>> command: " + pb.command());
        new ProcessBuilder(killCommand).start().waitFor();
    }
}
