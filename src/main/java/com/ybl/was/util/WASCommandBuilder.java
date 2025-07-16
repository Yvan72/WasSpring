package com.ybl.was.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class WASCommandBuilder {
    private final StringBuilder command = new StringBuilder();
    private final boolean isWindows;

    public WASCommandBuilder(String commandBase) {
        this.isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        command.append(commandBase);
    }

    public WASCommandBuilder addArg(String arg) {
        command.append(" ").append(arg);
        return this;
    }

    public WASCommandBuilder addOption(String opt, String val) {
        command.append(" ").append(opt).append(" ").append(val);
        return this;
    }

    public String build() {
        return command.toString();
    }

    public String executeAndGetOutput() throws IOException {
        String shell = isWindows ? "cmd.exe" : "/bin/sh";
        String flag = isWindows ? "/c" : "-c";
        Process process = new ProcessBuilder(shell, flag, build()).start();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
}
