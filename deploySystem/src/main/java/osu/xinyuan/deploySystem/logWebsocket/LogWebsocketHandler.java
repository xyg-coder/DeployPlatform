package osu.xinyuan.deploySystem.logWebsocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import osu.xinyuan.deploySystem.services.JavaProjectService;
import osu.xinyuan.deploySystem.util.Util;

import java.util.concurrent.ConcurrentHashMap;

/**
 * handler for the logWebsocket of log
 * will run a process for every websocket to get log
 * a thread will hold this process to get outputstream for the websocket
 * when connection close, this process will also close
 */
@Component
public class LogWebsocketHandler extends AbstractWebSocketHandler {
    private ConcurrentHashMap<String, LogThread> map = new ConcurrentHashMap<>();

    private Logger logger = LoggerFactory.getLogger(LogWebsocketHandler.class);

    @Autowired
    private JavaProjectService javaProjectService;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);

        String projectType = Util.queryStringParse(session.getUri().getQuery()).get("projectType");
        String idStr = Util.queryStringParse(session.getUri().getQuery()).get("id");
        String logType = Util.queryStringParse(session.getUri().getQuery()).get("logType");
        if (projectType == null || idStr == null || logType == null) {
            throw new IllegalArgumentException("need project-type ,id and log-type in the query");
        }
        int id = Integer.parseInt(idStr);
        String killCommand[];
        Process process = null;
        if ("javaProject".equals(projectType)) {
            if ("deploy".equals(logType)) {
                process = javaProjectService.getDeployLogProcess(id);
                killCommand = javaProjectService.killDeployLogProcessCommand(id);
            } else if ("running".equals(logType)) {
                process = javaProjectService.getRunningLogProcess(id);
                killCommand = javaProjectService.killRunningLogProcessCommand(id);
            } else {
                throw new IllegalArgumentException("currently only support deploy and running log for java-project");
            }
        } else {
            throw new IllegalArgumentException("currently only support java-project log");
        }

        LogThread logThread = new LogThread(session, process, killCommand);
        taskExecutor.execute(logThread);

        map.put(session.getId(), logThread);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
        exception.printStackTrace();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        LogThread thread = map.get(session.getId());
        map.remove(session.getId());
        thread.close();
        logger.info("one websocket closes");

        logger.info("current map size: " + map.size());
    }
}
