package net.glxn.jmsutility;

import javax.swing.*;

class LogWindow extends JFrame {
    private JTextArea textArea = new JTextArea();

    @SuppressWarnings({"UndesirableClassUsage"})
    public LogWindow(int x, int y) {
        super("");
        setLocation(x, y);
        setSize(500, 300);
        add(new JScrollPane(textArea));
        setAlwaysOnTop(true);
        setVisible(true);
    }

    public void log(String msg) {
        textArea.append(msg);
        this.validate();
    }
}