package net.glxn.jmsutility.log;

/**
 * LogAppenderFactory
 * TODO: Add Class documentation
 *
 * @author <a href="mailto:ken@glxn.net">Ken Gullaksen</a>
 * @version $Revision: $
 */
public class LogAppenderFactory {

    private static LogWindow APPENDER; 

    public static LogWindow getLogWindow() {
        if(APPENDER == null) {
            APPENDER = new LogWindow();
        }
        return APPENDER;
    }
}
