package osu.xinyuangui.springbootvuejs.FileReadingWebsocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import osu.xinyuangui.springbootvuejs.service.SingleFileCodeService;
import osu.xinyuangui.springbootvuejs.util.StringUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handler for the File Reading websocket
 * Will run a process for every websocket to get log
 * A thread will hold this process to get output stream for the websocket
 * When connection closes, this process will also close
 */
@Component
public class FileReadingWebsocketHandler extends AbstractWebSocketHandler {
    private Logger logger = LoggerFactory.getLogger(FileReadingWebsocketHandler.class);

    private ConcurrentHashMap<String, FileReadingWebsocketThread> threadMap = new ConcurrentHashMap<>();

    @Autowired
    private SingleFileCodeService singleFileCodeService;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);

        Map<String, String> queryMap = StringUtil.queryStringParse(session.getUri().getQuery());
        String type = queryMap.get("type");
        String idStr = queryMap.get("id");
        String fileName = queryMap.get("fileName");
        if (type == null || !type.equals("singleFileCode") || idStr == null || fileName == null) {
            throw new IllegalArgumentException("type currently only supports singleFileCode, id and fileName cannot be " +
                    "null");
        }
        int id = Integer.parseInt(idStr);
        String killCommand[];
        Process process = null;

        // single file code
        killCommand = singleFileCodeService.getKillFileReadingProcessCommand(id, fileName);
        process = singleFileCodeService.getFileReadingProcess(id, fileName);

        FileReadingWebsocketThread readingThread = new FileReadingWebsocketThread(process, session,
                killCommand);
        threadMap.put(session.getId(), readingThread);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
        logger.error(exception.getMessage());
        exception.printStackTrace();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);

        FileReadingWebsocketThread readingThread = threadMap.get(session.getId());
        threadMap.remove(session.getId());
        readingThread.close();
        logger.info("one websocket closes");
        logger.info("current map size: " + threadMap.size());
    }
}
