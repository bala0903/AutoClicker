package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AppSelector {
    public static List<String> getRunningApps() {
        List<String> apps = new ArrayList<>();
        try {
            Process process = Runtime.getRuntime().exec("powershell \"Get-Process | Select-Object ProcessName\"");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty() && !line.contains("ProcessName")) {
                    apps.add(line.trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return apps;
    }

    public static void main(String[] args) {
        List<String> runningApps = getRunningApps();
        System.out.println("Running Applications:");
        for (String app : runningApps) {
            System.out.println("- " + app);
        }
    }
}
