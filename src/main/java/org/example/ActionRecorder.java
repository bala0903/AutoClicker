package org.example;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseMotionListener;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

class Action {
    String type;
    int x, y;
    int keyCode;
    long timestamp;

    public Action(String type, int x, int y, int keyCode, long timestamp) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.keyCode = keyCode;
        this.timestamp = timestamp;
    }
}

public class ActionRecorder implements NativeMouseListener, NativeKeyListener, NativeMouseMotionListener {
    private static final List<Action> recordedActions = new ArrayList<>();
    private static boolean isRecording = true;

    public static void ActionRecorder(String[] args) {
        try {
            // Register global mouse and keyboard listeners
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeMouseListener(new ActionRecorder());
            GlobalScreen.addNativeKeyListener(new ActionRecorder());
            GlobalScreen.addNativeMouseMotionListener(new ActionRecorder());

            System.out.println("Recording started... Press 'ESC' to stop.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void nativeMouseClicked(NativeMouseEvent e) {
        if (isRecording) {
            recordedActions.add(new Action("mouse_click", e.getX(), e.getY(), -1, System.currentTimeMillis()));
            System.out.println("Mouse Click at: " + e.getX() + ", " + e.getY());
        }
    }

    @Override
    public void nativeMousePressed(NativeMouseEvent e) {
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent e) {
    }

    @Override
    public void nativeMouseMoved(NativeMouseEvent e) {
    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent e) {
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        if (e.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
            isRecording = false;
            System.out.println("Recording stopped.");
            saveActionsToFile();
            try {
                GlobalScreen.unregisterNativeHook();
                AppSelectorUI.frame.dispose();
            } catch (NativeHookException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
    }

    private void saveActionsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("actions.json"))) {
            writer.write("ApplicationName:" + AppSelectorUI.selectedApp);
            writer.newLine();
            for (Action action : recordedActions) {
                writer.write(action.type + "," + action.x + "," + action.y + "," + action.keyCode + "," + action.timestamp);
                writer.newLine();
            }
            System.out.println("Actions saved to file!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
