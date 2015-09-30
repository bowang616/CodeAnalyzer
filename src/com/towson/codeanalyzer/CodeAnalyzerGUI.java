package com.towson.codeanalyzer;

import javax.swing.*;
import java.io.File;

public class CodeAnalyzerGUI extends JPanel {

    // Declare variables from .form file
    private JPanel CodeAnalyzerMainPanel;
    private JLabel JLabel_Welcome;
    private JLabel JLabel_FileCount;

    int fileCount = 0;

    // Constructor to add main panel
    public  CodeAnalyzerGUI (String projectPath) {

        add(CodeAnalyzerMainPanel, "center");
        listFiles(projectPath);
    }

    // Print out all directories and files in console
    public void listFiles(String path) {

        File folder = new File(path);

        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                System.out.println("File " + listOfFiles[i].getName());
                fileCount++;
            } else if (listOfFiles[i].isDirectory()) {
                System.out.println("Directory " + listOfFiles[i].getName());
                this.listFiles(listOfFiles[i].getPath());
            }
        }
        JLabel_FileCount.setText("Total File Count: " + String.valueOf(fileCount));
    }
}