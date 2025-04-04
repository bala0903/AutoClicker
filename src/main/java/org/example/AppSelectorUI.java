package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

public class AppSelectorUI {
    static String selectedApp = null;
    static String selectedAction = null;
    static String fileName = null;
    static int loopCount = 1;
    static int startAfterSecond = 0;
    static JTextArea logArea;
    static JFrame frame = new JFrame("Select Application");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AppSelectorUI::createAndShowGUI);
    }

    private static void createAndShowGUI() {

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(7, 2, 5, 5));

        List<String> runningApps = AppSelector.getRunningApps();
        String[] appArray = runningApps.toArray(new String[0]);
        JComboBox<String> appDropdown = new JComboBox<>(appArray);
        JButton selectButton = new JButton("Select & Start");
        String[] actions = {"Record", "Replay"};
        JComboBox<String> actionDropdown = new JComboBox<>(actions);
        JTextField fileNameField = new JTextField(15);
        fileNameField.setEnabled(false);
        JTextField startAfterSeconds = new JTextField(10);
        startAfterSeconds.setEnabled(false);
        JButton fileSelectButton = new JButton("Select File");
        fileSelectButton.setEnabled(false);
        JFileChooser fileChooser = new JFileChooser();
        logArea = new JTextArea(10, 50);
        logArea.setEditable(false);
        JScrollPane logScroll = new JScrollPane(logArea);
        redirectConsoleOutput();

        fileSelectButton.addActionListener(e -> {
            int returnValue = fileChooser.showOpenDialog(frame);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                fileNameField.setText(selectedFile.getAbsolutePath());
            }
        });
        selectButton.addActionListener(e -> {
            selectedApp = (String) appDropdown.getSelectedItem();
            System.out.println("Selected: " + selectedApp);
            //    frame.dispose(); // Close UI
            bringToFront(selectedApp);
        });

        // Loop Count Input (For Replay)
        JSpinner loopSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100000, 1));
        loopSpinner.setEnabled(false);
        // Enable/Disable Fields Based on Action Selection
        actionDropdown.addActionListener(e -> {
            selectedAction = (String) actionDropdown.getSelectedItem();
            if ("Record".equals(selectedAction)) {
                fileNameField.setEnabled(true);
                fileSelectButton.setEnabled(false);
                loopSpinner.setEnabled(false);
                startAfterSeconds.setEnabled(false);
            } else {
                fileNameField.setEnabled(false);
                fileSelectButton.setEnabled(true);
                loopSpinner.setEnabled(true);
                startAfterSeconds.setEnabled(true);
            }
        });

        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> {
            selectedApp = (String) appDropdown.getSelectedItem();
            selectedAction = (String) actionDropdown.getSelectedItem();
            fileName = fileNameField.getText();
            loopCount = (int) loopSpinner.getValue();
            startAfterSecond = "Record".equals(selectedAction) ? 0 : Integer.parseInt(startAfterSeconds.getText());


            System.out.println("App: " + selectedApp);
            System.out.println("Action: " + selectedAction);
            System.out.println("File: " + fileName);
            System.out.println("Loops: " + loopCount);
            System.out.println("Delay Time: " + startAfterSecond);

            bringToFront(selectedApp);

            if ("Record".equals(selectedAction)) {
                ActionRecorder.ActionRecorderMethod();
            } else {
                try {
                    ActionReplayer.ActionReplayerMethod();
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });

        panel.add(new JLabel("Select Application:"));
        panel.add(appDropdown);
        panel.add(new JLabel("Select Action:"));
        panel.add(actionDropdown);
        panel.add(new JLabel("File Name (for Record):"));
        panel.add(fileNameField);
        panel.add(new JLabel("Replay File:"));
        panel.add(fileSelectButton);
        panel.add(new JLabel("Loop Count (for Replay):"));
        panel.add(loopSpinner);
        panel.add(new JLabel("Start after (seconds):"));
        panel.add(startAfterSeconds);
        panel.add(startButton);

        frame.add(panel, BorderLayout.NORTH);
        frame.add(logScroll, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    static void bringToFront(String appName) {
        try {
            Runtime.getRuntime().exec("powershell \"(Get-Process " + appName + ").MainWindowHandle | foreach { [System.Windows.Forms.SendKeys]::SendWait('% {TAB}') }\"");
            System.out.println("Focused on " + appName);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void redirectConsoleOutput() {
        PrintStream printStream = new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {
                logArea.append(String.valueOf((char) b));
                logArea.setCaretPosition(logArea.getDocument().getLength()); // Auto-scroll to bottom
            }
        });

        System.setOut(printStream);
        System.setErr(printStream);
    }

}

