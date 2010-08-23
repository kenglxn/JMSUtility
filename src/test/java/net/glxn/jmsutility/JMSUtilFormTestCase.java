package net.glxn.jmsutility;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class JMSUtilFormTestCase {
    private JMSUtility jmsUtilForm;
    private ArrayList<String> messages;

    @Before
    public void initializeTests() {
        jmsUtilForm = new JMSUtility(){
            @Override
            protected void showErrorPane(String title, String msg) {
                messages.add(msg);
            }
        };
    }

    @Test
    public void validationShouldNotFailForFreshlyInitializedTest() {
        resetMessagesAndSetValidInputFields();

        jmsUtilForm.validateInputFields();
        Assert.assertEquals(0, messages.size());
    }

    @Test
    public void shouldBeAbleToDetectParameterizedMessage() {
        String input = "<xml><key>%s</key><val></val>%s</xml>";
        Assert.assertEquals("jmsUtilForm.numberOfParametersInText did not return expected result" + input,
                2, jmsUtilForm.numberOfParametersInText(input));
    }



    private void resetMessagesAndSetValidInputFields() {
        messages = new ArrayList<String>();
        jmsUtilForm.jmsServerUrl.setText("tcp://localhost:61616");
        jmsUtilForm.queueDestinationTextField.setText("TestQueue");
        jmsUtilForm.messageTextPane.setText("test");
    }
}
