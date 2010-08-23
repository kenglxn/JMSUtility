package net.glxn.jmsutility.log;

import net.glxn.jmsutility.log.LogAppender;

import javax.swing.*;

public class LogWindow extends JFrame implements LogAppender {
    private JTextArea textArea = new JTextArea();

    @SuppressWarnings({"UndesirableClassUsage"})
    LogWindow() {
        super("");
        setSize(500, 300);
        add(new JScrollPane(textArea));
        setAlwaysOnTop(true);
        setVisible(true);
    }

    public void log(String msg) {
        System.out.println(msg);
        textArea.append(msg+"\n");
        this.validate();
    }
}