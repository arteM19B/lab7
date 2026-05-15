package main;

import Exceptions.RecursiveScriptException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class ExecuteScriptCommand {
    private final ClientCommandExecutor commandExecutor;
    private final Set<String> executingScripts = new HashSet<>();

    public ExecuteScriptCommand(ClientCommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    public void execute(String fileName, String login, String password) {
        if (fileName == null || fileName.trim().isEmpty()) {
            System.out.println("Error: script file name is required");
            return;
        }

        File file = new File(fileName);
        String normalizedPath = normalizePath(file);

        if (executingScripts.contains(normalizedPath)) {
            throw new RecursiveScriptException(fileName);
        }

        System.out.println("Executing script: " + fileName);
        executingScripts.add(normalizedPath);

        int lineNumber = 0;
        try (Scanner scriptScanner = new Scanner(file)) {
            while (scriptScanner.hasNextLine()) {
                lineNumber++;
                String line = scriptScanner.nextLine().trim();

                if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) {
                    continue;
                }

                commandExecutor.executeScriptLine(line, scriptScanner, fileName, lineNumber, login, password);
            }

            System.out.println("Script completed: " + fileName);
        } catch (FileNotFoundException e) {
            System.out.println("Script file was not found: " + fileName);
        } catch (Exception e) {
            System.out.println("Script execution error: " + e.getMessage());
        } finally {
            executingScripts.remove(normalizedPath);
        }
    }

    private String normalizePath(File file) {
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            return file.getAbsolutePath();
        }
    }
}
