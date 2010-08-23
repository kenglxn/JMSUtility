package net.glxn.jmsutility.dispatch;

import java.util.HashMap;

public class JMSMessageDispatcherFactory {

    private static HashMap<String,JMSMessageDispatcher> messageDispatcherCache = new HashMap<String, JMSMessageDispatcher>();

    public static JMSMessageDispatcher getJMSMessageDispatcher(String jms_server_url) {
        JMSMessageDispatcher jmsMessageDispatcher = messageDispatcherCache.get(jms_server_url);
        if(jmsMessageDispatcher == null) {
            jmsMessageDispatcher = new JMSMessageDispatcher(jms_server_url);
            messageDispatcherCache.put(jms_server_url, jmsMessageDispatcher);
        }
        return jmsMessageDispatcher;
    }
}