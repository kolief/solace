package net.solace.loader.plugins.solacedevtools.logger;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LoggerWindow extends JFrame {
    private final LogbackSwingAppender appender;
    private final Logger rootLogger;

    public LoggerWindow() {
        var textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setBackground(Color.BLACK);
        var scrollPane = new JScrollPane(textPane);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(750, 350);
        add(scrollPane, BorderLayout.CENTER);

        var context = (LoggerContext) LoggerFactory.getILoggerFactory();

        appender = new LogbackSwingAppender();
        appender.setTextPane(textPane);
        appender.setContext(context);
        appender.start();

        rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.addAppender(appender);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                cleanup();
            }
        });
    }

    private void cleanup() {
        if (appender != null && rootLogger != null) {
            rootLogger.detachAppender(appender);
            appender.stop();
        }
    }

    @Override
    public void dispose() {
        cleanup();
        super.dispose();
    }
}