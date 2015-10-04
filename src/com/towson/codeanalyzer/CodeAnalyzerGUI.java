package com.towson.codeanalyzer;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class CodeAnalyzerGUI extends JPanel {

    // Declare main panel and tab panel
    private JPanel MetricsMainPanel;
    private JTabbedPane MainTabPanel;

    // Declare project tab panel
    private JPanel ProjectPanel;
    private JLabel WelcomeLabel;
    private JLabel JLable_FileCount;

    // Declare java tab panel
    private JPanel JavaPanel;
    private JList ListJavaName;
    private JLabel JLable_JavaFileCount;

    // Declare xml tab panel
    private JPanel XmlPanel;
    private JList ListXmlName;
    private JLabel JLabel_XmlFileCount;

    private  DefaultListModel listModel;
    private  DefaultListModel XmllistModel;

    int fileCount=0;
    int javaFileCount=0;
    int xmlFileCount=0;

    // Constructor to add main panel
    public  CodeAnalyzerGUI (String projectPath) {
        setLayout(new BorderLayout());
        add(MetricsMainPanel,BorderLayout.CENTER);

        listModel = new DefaultListModel();
        ListJavaName.setModel(listModel);
        XmllistModel= new DefaultListModel();
        ListXmlName.setModel(XmllistModel);

        listFiles(projectPath);
    }

    // Print out all different files
    public void listFiles(String path) {
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                System.out.println("File " + listOfFiles[i].getName());
                String strFileName = listOfFiles[i].getName();
                if (strFileName.contains(".java")) {
                    if(!listModel.contains(listOfFiles[i].getName()))
                    {
                        listModel.addElement(listOfFiles[i].getName());
                        javaFileCount++;
                    }
                }
                else if (strFileName.contains(".xml")) {
                    if(!XmllistModel.contains(listOfFiles[i].getName()))
                    {
                        XmllistModel.addElement(listOfFiles[i].getName());
                        xmlFileCount++;
                    }
                }
                fileCount++;
            } else if (listOfFiles[i].isDirectory()) {
                System.out.println("Directory " + listOfFiles[i].getName());
                this.listFiles(listOfFiles[i].getPath());
            }
        }
        JLable_FileCount.setText("Total File Count : " + String.valueOf(fileCount));
        JLable_JavaFileCount.setText("Java File Count : " + String.valueOf(javaFileCount));
        JLabel_XmlFileCount.setText("Xml File Count : " + String.valueOf(xmlFileCount));
    }
}