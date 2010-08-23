package net.glxn.jmsutility;

import net.glxn.jmsutility.dispatch.JMSMessageDispatcher;
import net.glxn.jmsutility.dispatch.JMSMessageDispatcherFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JMSUtility extends Component {
    private JPanel panel1;
    protected JTextField jmsServerUrl;
    protected JTextField queueDestinationTextField;
    protected JTextField messagesTextField;
    protected JTextPane messageTextPane;
    protected JTextPane parameterListTextPane;
    private JButton sendMessageSButton;
    private JButton helpButton;
    private int parameters;
    private String[] parameterValues;
    private Integer numberOfMessagesToSend;
    private JMSMessageDispatcher jmsMessageDispatcher;

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("JMSUtilForm");
        frame.setContentPane(new JMSUtility().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public JMSUtility() {
        helpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    showHelpDialog();
                } catch (Exception ex) {
                    showErrorPane(ex.getMessage(), ExceptionUtils.getFullStackTrace(ex));
                }
            }
        });
        sendMessageSButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    validateInputFields();
                    allocateJMSMessageDispatcher();
                    sendMessage();
                } catch (Exception ex) {
                    showErrorPane(ex.getMessage(), ExceptionUtils.getFullStackTrace(ex));
                }
            }
        });
    }

    private void allocateJMSMessageDispatcher() {
        jmsMessageDispatcher = JMSMessageDispatcherFactory.getJMSMessageDispatcher(jmsServerUrl.getText());
    }

    protected void validateInputFields() {
        if (!jmsServerUrl.getText().matches("\\w{2,5}://.*:\\d{2,6}")) {
            showErrorPane("input validation error", "JMS Server URL is not a valid URL. " +
                    "\n\n should be: [protocol]://[host]:[port]" +
                    "\n example:   tcp://localhost:61616");
        }
        if (StringUtils.isBlank(queueDestinationTextField.getText())) {
            showErrorPane("input validation error", "You must supply a Queue destination");
        }
        if (!StringUtils.isNumeric(messagesTextField.getText())) {
            showErrorPane("input validation error", "Number of messages must be a number");
        }
        if (StringUtils.isBlank(messageTextPane.getText())) {
            showErrorPane("input validation error", "Message can not be blank");
        }
        parameters = numberOfParametersInText(messageTextPane.getText());
        parameterValues = StringUtils.split(parameterListTextPane.getText(), "\n");
        if (parameters > 0 && parameterValues.length == 0) {
            showErrorPane("input validation error", "You have supplied a parameterized message but no parameters. " +
                    "\n\nPlease see the Help! dialog.");
        }
        if (parameters == 0 && parameterValues.length > 0) {
            showErrorPane("input validation error", "You have supplied parameters to a message that has no placeholders." +
                    "\n\nPlease see the Help! dialog.");
        }
        if (parameters > 1) {
            boolean parameterNumberOfFieldsMismatch = false;
            for (String parameterValue : parameterValues) {
                int numberOfFieldsInParameterValue = StringUtils.countMatches(parameterValue, "|");
                if (numberOfFieldsInParameterValue != (parameters - 1)) {
                    parameterNumberOfFieldsMismatch = true;
                }
            }
            if (parameterNumberOfFieldsMismatch) {
                showErrorPane("input validation error", "Your parameter list contains at least one entry that does not " +
                        "satisfy the parameterized message." +
                        "\n\nPlease see the Help! dialog.");
            }
        }
    }

    int numberOfParametersInText(String text) {
        return StringUtils.countMatches(text, "%s");
    }

    private void sendMessage() {
        LogWindow logWindow = new LogWindow(panel1.getX() + panel1.getWidth() + 25, panel1.getY());
        if (parameterValues.length > 0) {
            logWindow.log("Sending messages for parameters in list. \nTotal number of messages to send=" + parameterValues.length);

            //TODO, iterate list of parameters and create message merging in the value in the message and send message

        } else {
            numberOfMessagesToSend = Integer.valueOf(messagesTextField.getText());
            logWindow.log("Sending " + numberOfMessagesToSend + " message(s)");

            //TODO, send numberOfMessagesToSend number of messages using only the message field


        }
    }

    private void showHelpDialog() {
        String message = "This tool let's you send a JMS messsage to a queue. " +
                "\nSupply the input fields with correct values and click send button." +
                "\n\nThere is a special feature for the message and parameter list input fields." +
                "\nThese fields work together, so if you supply the message with a placeholder, namely %s, " +
                "\nthen the tool will attempt to fill the placeholder with values from the parameter list. If you have one %s in you message, " +
                "\nthen you can send several messages with each message being given a new value for %s with the next value in the list. " +
                "\nThe parameter list entries need to be separated by a carriage return/new line feed. " +
                "\n------" +
                "\nExample parameter list for input with one %s:" +
                "\nparam1" +
                "\nparam2" +
                "\nparam3" +
                "\n------" +
                "\nIf you have more than one placeholder, then the values should be separated by a pipe (|) in the parameter list. " +
                "\nSo message 'hello %s %s' could be given the parameter list 'mr|duke' and the resulting message would be 'hello mr duke'." +
                "\n------" +
                "\nExample parameter list for input with two %s:" +
                "\nmr|duke" +
                "\nms|daisy" +
                "\nmrs|robinson" +
                "\n------" +
                "\nIf you have given the number of messages when using parameter list, the number of messages to send will be ignored " +
                "\nand messages will be sent for entire parameter list";
        JOptionPane pane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = pane.createDialog(this, "Help!");
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
    }

    protected void showErrorPane(String title, String msg) {
        JOptionPane pane = new JOptionPane(msg, JOptionPane.ERROR_MESSAGE);
        JDialog dialog = pane.createDialog("Application says: " + title);
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
    }
}
