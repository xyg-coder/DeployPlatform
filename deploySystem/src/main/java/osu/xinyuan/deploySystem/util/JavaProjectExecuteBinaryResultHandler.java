package osu.xinyuan.deploySystem.util;

import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.ExecuteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import osu.xinyuan.deploySystem.domains.JavaProjectStatus;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * used to handle the result of the command execution of java project
 * will send message to controller if status change
 * will send two different messages to controller when success or fails
 */
public class JavaProjectExecuteBinaryResultHandler extends DefaultExecuteResultHandler {
    private static Logger logger =
            LoggerFactory.getLogger(JavaProjectExecuteBinaryResultHandler.class);

    private int projectId;

    private JavaProjectStatus successStatus;

    private JavaProjectStatus failStatus;

    private JmsTemplate jmsTemplate;

    public JavaProjectExecuteBinaryResultHandler(int projectId,
                                                 JavaProjectStatus successStatus,
                                                 JavaProjectStatus failStatus, JmsTemplate jmsTemplate) {
        super();

        this.projectId = projectId;
        this.successStatus = successStatus;
        this.failStatus = failStatus;
        this.jmsTemplate = jmsTemplate;
    }

    /**
     * 0 is success
     * otherwise fail
     * @param exitValue
     */
    @Override
    public void onProcessComplete(int exitValue) {
        super.onProcessComplete(exitValue);

        logger.info("Process complete: " + exitValue + " is null? " + (jmsTemplate == null));

        if (exitValue == 0) { // success
            jmsTemplate.send("javaProjectStatus", new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    logger.info("send message for the process success: " + successStatus.name());
                    return session.createTextMessage("javaProject-" + projectId + "=" + successStatus.name());
                }
            });
        } else { // fail
            jmsTemplate.send("javaProjectStatus", new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    logger.info("send message for the process fails: " + successStatus.name());
                    return session.createTextMessage("javaProject-" + projectId + "=" + failStatus.name());
                }
            });
        }
    }

    @Override
    public void onProcessFailed(ExecuteException e) {
        super.onProcessFailed(e);

        logger.error("Process failed");

        jmsTemplate.send("javaProjectStatus", new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage("javaProject-" + projectId + "=" + failStatus.name());
            }
        });
    }
}
