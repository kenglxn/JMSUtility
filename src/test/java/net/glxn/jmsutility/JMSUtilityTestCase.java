package net.glxn.jmsutility;

import junit.framework.Assert;
import net.glxn.jmsutility.dispatch.JMSMessageDispatcher;
import net.glxn.jmsutility.log.LogAppenderFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Map;

public class JMSUtilityTestCase {
    private JMSUtility jmsUtility;
    private ArrayList<String> loggedMessages = new ArrayList<String>();
    private ArrayList<String> sentMessages = new ArrayList<String>();

    @Before
    public void initializeTests() {
        jmsUtility = new JMSUtility() {
            @Override
            protected void showErrorPane(String title, String msg) {
                loggedMessages.add(msg);
            }

            @Override
            protected void allocateJMSMessageDispatcher() {
                jmsMessageDispatcher = new JMSMessageDispatcher(null, LogAppenderFactory.getLogWindow()) {
                    @Override
                    public void sendMessage(String queue, String payload, Map<String, Object> messageProperties) throws IllegalArgumentException {
                        sentMessages.add(payload);
                    }
                };
            }
        };
    }

    @Test
    public void validationShouldNotFailForFreshlyInitializedTest() {
        resetMessagesAndSetValidInputFields();

        jmsUtility.validateInputFields();
        Assert.assertEquals(0, loggedMessages.size());
    }

    @Test
    public void shouldBeAbleToDetectParameterizedMessage() {
        String input = "<xml><key>%s</key><val>%s</val></xml>";
        Assert.assertEquals("jmsUtility.numberOfParametersInText did not return expected result" + input,
                2, jmsUtility.numberOfParametersInText(input));
    }

    @Test
    public void shouldSendCorrectlyFormattedParameterizedMessage() {
        resetMessagesAndSetValidInputFields();
        String inputMsg = "Hello %s. %s";
        String inputParam = "Mr:Duke";

        String expected = "Hello Mr. Duke";


        jmsUtility.messageTextPane.setText(inputMsg);
        jmsUtility.parameterListTextPane.setText(inputParam);
        jmsUtility.sendMessageActionPerformed();

        Assert.assertEquals(1, sentMessages.size());
        Assert.assertEquals(expected, sentMessages.get(0));
    }

    @Test
    public void shouldSendCorrectlyFormattedParameterizedMessages() {
        resetMessagesAndSetValidInputFields();
        String inputMsg = "Hello %s. %s";
        String inputParam = "Mr:Duke\nMs:Daisy";

        String expected1 = "Hello Mr. Duke";
        String expected2 = "Hello Ms. Daisy";


        jmsUtility.messageTextPane.setText(inputMsg);
        jmsUtility.parameterListTextPane.setText(inputParam);
        jmsUtility.sendMessageActionPerformed();

        Assert.assertEquals(2, sentMessages.size());
        Assert.assertEquals(expected1, sentMessages.get(0));
        Assert.assertEquals(expected2, sentMessages.get(1));
    }

    @Test
    public void shouldSendCorrectlyFormattedParameterizedMessageWithOnlyOnePlaceHolder() {
        resetMessagesAndSetValidInputFields();
        String inputMsg = "Hello %s";
        String inputParam = "World";

        String expected = "Hello World";


        jmsUtility.messageTextPane.setText(inputMsg);
        jmsUtility.parameterListTextPane.setText(inputParam);
        jmsUtility.sendMessageActionPerformed();

        Assert.assertEquals(1, sentMessages.size());
        Assert.assertEquals(expected, sentMessages.get(0));
    }
    
    @Test
    public void shouldSendCorrectlyFormattedParameterizedMessagesWithOnlyOnePlaceHolder() {
        resetMessagesAndSetValidInputFields();
        String inputMsg = "Hello %s";
        String inputParam = "World\nMoon";

        String expected1 = "Hello World";
        String expected2 = "Hello Moon";


        jmsUtility.messageTextPane.setText(inputMsg);
        jmsUtility.parameterListTextPane.setText(inputParam);
        jmsUtility.sendMessageActionPerformed();

        Assert.assertEquals(2, sentMessages.size());
        Assert.assertEquals(expected1, sentMessages.get(0));
        Assert.assertEquals(expected2, sentMessages.get(1));
    }


    private void resetMessagesAndSetValidInputFields() {
        loggedMessages = new ArrayList<String>();
        sentMessages = new ArrayList<String>();
        jmsUtility.jmsServerUrl.setText("tcp://localhost:61616");
        jmsUtility.queueDestinationTextField.setText("TestQueue");
        jmsUtility.messageTextPane.setText("test");
    }
}
