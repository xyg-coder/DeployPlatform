
package osu.xinyuangui.springbootvuejs.FileReadingWebsocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.util.HtmlUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * This class will manage the log reading process
 * Will output stream of that process to one websocket
 */
public class FileReadingWebsocketThread implements Runnable{
    private Logger logger = LoggerFactory.getLogger(FileReadingWebsocketThread.class);

    private Process fileReadingProcess;
    private WebSocketSession webSocketSession;
    private BufferedReader reader;

    private String[] killCommand;

    public FileReadingWebsocketThread(Process fileReadingProcess, WebSocketSession webSocketSession, String[] killCommand)
            throws UnsupportedEncodingException {
        this.fileReadingProcess = fileReadingProcess;
        this.webSocketSession = webSocketSession;
        this.killCommand = killCommand;
        if (fileReadingProcess != null) {
            this.reader = new BufferedReader(new InputStreamReader(fileReadingProcess.getInputStream(),
                    "UTF-8"));
        } else {
            this.reader = null;
        }
    }

    public void close() throws IOException, InterruptedException {
        destroyProcess();
        logger.info("start closing the reader");
        reader.close();
        logger.info("finish closing the reader");
    }

    public void destroyProcess() throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(killCommand);
        pb.start().waitFor();
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
        } catch (IllegalStateException e) {
            logger.error(e.getMessage());
        }
    }
}
