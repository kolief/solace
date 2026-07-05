package net.solace.loader.plugins.solacedevtools.logger;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import lombok.Setter;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.Color;
import java.time.Instant;

@Setter
public class LogbackSwingAppender extends AppenderBase<ILoggingEvent> {
    private JTextPane textPane;

    @Override
    protected void append(ILoggingEvent eventObject) {
        if (textPane == null) {
            return;
        }

        var logLevel = eventObject.getLevel().toString();

        Color color;
        switch (logLevel) {
            case "ERROR":
                color = Color.RED;
                break;
            case "WARN":
                color = Color.ORANGE;
                break;
            case "DEBUG":
                color = Color.CYAN;
                break;
            default:
                color = Color.WHITE;
        }

        appendToPane(eventObject, color);
    }

    private void appendToPane(ILoggingEvent event, Color color) {
        SwingUtilities.invokeLater(() -> {
            if (textPane == null) return;
            var doc = textPane.getStyledDocument();

            var attributes = new SimpleAttributeSet();
            StyleConstants.setForeground(attributes, color);

            var timestamp = Instant.ofEpochMilli(event.getTimeStamp()).toString();
            var threadName = event.getThreadName();
            var loggerName = event.getLoggerName();
            var message = event.getFormattedMessage();
            var eventString = String.format("[%s] [%s] [%s] %s", timestamp, threadName, loggerName, message);

            try {
                doc.insertString(doc.getLength(), eventString + "\n", attributes);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        });
    }
}