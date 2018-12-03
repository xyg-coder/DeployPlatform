package osu.xinyuan.deploySystem.util;

import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.ExecuteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import osu.xinyuan.deploySystem.domains.JavaProjectStatus;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * Be used to handle the result of the command execution of java project
 */
public class JavaProjectExecuteResultHandler extends DefaultExecuteResultHandler {

    private static Logger logger =
            LoggerFactory.getLogger(JavaProjectExecuteResultHandler.class);

    private int projectId;

    private JavaProjectStatus targetStatus;

    @Autowired
    private JmsTemplate jmsTemplate;

    public JavaProjectExecuteResultHandler(int projectId, JavaProjectStatus targetStatus) {
        super();

        this.projectId = projectId;
        this.targetStatus = targetStatus;
    }

    @Override
    public void onProcessComplete(int exitValue) {
        super.onProcessComplete(exitValue);

        logger.info("Process Complete");

        jmsTemplate.send("javaProjectStatus", new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                System.out.println(">>> send message fro the process complete: " + targetStatus.name());
                return session.createTextMessage("javaProject-" + projectId + "=" + targetStatus.name());
            }
        });
    }

    @Override
    public void onProcessFailed(ExecuteException e) {
        super.onProcessFailed(e);

        logger.error("Process Failed");

    }

    public JmsTemplate getJmsTemplate() {
        return jmsTemplate;
    }

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }
}
