package org.example;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ActionReplayer {
    public static void ActionReplayer(String[] args) throws AWTException, IOException, InterruptedException {
        Robot robot = new Robot();
        List<Action> recordedActions = new ArrayList<>();
        int loopcount = AppSelectorUI.loopCount;
        int counter = 1;
        // Read actions from file
        try (BufferedReader reader = new BufferedReader(new FileReader("actions.json"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Application")) {
                    AppSelectorUI.selectedApp = line.split(":")[1];
                    line = reader.readLine();
                }
                String[] parts = line.split(",");
                recordedActions.add(new Action(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Long.parseLong(parts[4])));
            }
        }

        System.out.println("Replaying recorded actions...");
        //AppSelectorUI.bringToFront(AppSelectorUI.selectedApp);
        int sleeptime = AppSelectorUI.startAfterSecond;

        System.out.println("Waiting for Application for start " + sleeptime + " seconds");
        Thread.sleep(sleeptime * 1000);
        while (counter <= loopcount) {
            for (Action action : recordedActions) {
                if (action.type.equals("mouse_click")) {
                    robot.mouseMove(action.x, action.y);
                    robot.mousePress(MouseEvent.BUTTON1_DOWN_MASK);
                    robot.mouseRelease(MouseEvent.BUTTON1_DOWN_MASK);
                    System.out.println("Mouse Click at: " + action.x + ", " + action.y);
                }
                Thread.sleep(500); // Delay between actions
            }
            counter++;
        }

        System.out.println("Replay finished.");
        AppSelectorUI.frame.dispose();
    }

}
