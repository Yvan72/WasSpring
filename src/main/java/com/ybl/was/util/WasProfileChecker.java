package com.ybl.was.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.ybl.was.dto.ProfileStatusResponse;

public class WasProfileChecker {

    public static ProfileStatusResponse checkProfileStatus(String serverName, String profileName, String user, String password) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "C:/IBM/WebSphere/AppServer/bin/serverStatus.bat",
                    serverName,
                    "-profileName", profileName,
                    "-user", user,
                    "-password", password
            );
            pb.redirectErrorStream(true);

            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            boolean started = false;

            while ((line = reader.readLine()) != null) {
                if (line.contains("ADMU0501I")) { // Serveur running
                    started = true;
                }
            }

            int exitCode = process.waitFor();
            if (exitCode == 0 && started) {
                String parmPath = "C:/IBM/WebSphere/AppServer/profiles/" + profileName + "/properties";
                return new ProfileStatusResponse(true, parmPath);
            }

        } catch (Exception e) {
            e.printStackTrace(); // Remplacer par logger si besoin
        }

        return new ProfileStatusResponse(false, null);
    }
}
