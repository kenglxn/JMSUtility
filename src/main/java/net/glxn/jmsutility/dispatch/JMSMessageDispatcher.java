package net.glxn.jmsutility.dispatch;

import java.util.Map;
import java.util.Map.Entry;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;


import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class JMSMessageDispatcher {


    private final String username = ActiveMQConnection.DEFAULT_USER;
    private final String password = ActiveMQConnection.DEFAULT_PASSWORD;
    private final String JMS_SERVER_URL;

    private Connection connection;

    /**
     * Constructs a new dispatcher with the given jms_server_url
     *
     * @param jms_server_url the url of the JMS server to send messages e.g. tcp://localhost:61616
     */
    protected JMSMessageDispatcher(String jms_server_url) {
        JMS_SERVER_URL = jms_server_url;
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                System.out.println("tearing down JMSMessageDispatcher");
                closeConnection();
            }
        }));
    }


    /**
     * This method creates a {@link javax.jms.ConnectionFactory} using the
     * {@link JMSMessageDispatcher#JMS_SERVER_URL} that was passed into the constructor
     * upon the instantiation of this {@link JMSMessageDispatcher}
     * The method uses this {@link javax.jms.ConnectionFactory} to send a {@link javax.jms.TextMessage}.
     * The message is constructed with the given payload and properties, and dispatched to the given queue.
     * @param queue the name of the {@link javax.jms.Destination} JMS Queue to send the message to. value is required
     * @param payload the text body of the {@link javax.jms.TextMessage}. value is required
     * @param messageProperties map of properties to add to the message. each map entry is added to message with
     * {@link javax.jms.TextMessage#setObjectProperty(String, Object)}. Value is optional
     * @throws IllegalArgumentException if required argument is missing
     */
    public void sendMelding(final String queue, final String payload, final Map<String, Object> messageProperties) throws IllegalArgumentException {
        if(queue == null) {
            throw new IllegalArgumentException("queue can not be null");
        }
        if(payload == null) {
            throw new IllegalArgumentException("payload can not be null");
        }
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(username, password, JMS_SERVER_URL);

        Session session = null;
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(queue);

            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);

            TextMessage message = session.createTextMessage();
            message.setText(payload);

            for (Entry<String, Object> elem : messageProperties.entrySet()) {
                message.setObjectProperty(elem.getKey(), elem.getValue());
            }

            producer.send(message);

            session.commit();
        } catch (JMSException e) {
            silentRollback(session);
            closeConnection();
            throw new RuntimeException("Failed to send JMS Message", e);
        } finally {
            silentSessionClose(session);
        }
    }

    private void closeConnection() {
        if (connection == null) {
            return;
        }
        try {
            System.out.println("Closing JMS Connection");
            connection.close();
            System.out.println("JMS Connection closed");
        } catch (JMSException e) {
            System.err.println("Failed to close JMS Connection");
            e.printStackTrace();
        }
        connection = null;
    }


    private void silentSessionClose(Session session) {
        if (session != null) {
            try {
                session.close();
            } catch (JMSException e) {
                System.err.println("Failed to close JMS Session");
                e.printStackTrace();
                closeConnection();
            }
        }
    }

    private void silentRollback(Session session) {
        if (session != null) {
            try {
                session.rollback();
            } catch (JMSException e) {
                System.err.println("Failed to rollback JMS Session");
                e.printStackTrace();
                closeConnection();
            }
        }
    }
}
