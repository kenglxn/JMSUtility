package net.glxn.jmsutility.dispatch;

import net.glxn.jmsutility.log.LogAppender;
import net.glxn.jmsutility.log.LogAppenderFactory;

import java.util.HashMap;

public class JMSMessageDispatcherFactory {

    private final HashMap<String, JMSMessageDispatcher> messageDispatcherCache = new HashMap<String, JMSMessageDispatcher>();

    public JMSMessageDispatcher getJMSMessageDispatcher(String jms_server_url) {
        JMSMessageDispatcher jmsMessageDispatcher = messageDispatcherCache.get(jms_server_url);
        if (jmsMessageDispatcher == null) {
            jmsMessageDispatcher = new JMSMessageDispatcher(jms_server_url, LogAppenderFactory.getLogWindow());
            messageDispatcherCache.put(jms_server_url, jmsMessageDispatcher);
        }
        return jmsMessageDispatcher;
    }
}