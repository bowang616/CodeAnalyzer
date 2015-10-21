package com.towson.codeanalyzer;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.towson.codeanalyzer.java.JavaRowBean;
import com.towson.codeanalyzer.java.JavaTableModel;
import com.towson.codeanalyzer.render.JavaRender;
import com.towson.codeanalyzer.render.SummaryJavaRender;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class CodeAnalyzerGUI extends JPanel {

    //  Main panel and tab panel
    private JPanel MetricsMainPanel;
    private JTabbedPane MainTabPanel;

    // Project tab panel
    private JPanel ProjectPanel;
    private JLabel WelcomeLabel;
    private JLabel JLabel_FileCount;

    // Java table panel
    private JTable javaTable;
    private JPanel JavaTablePanel;

    // Xml tab panel
    private JTable xmlTable;
    private JPanel XmlTablePanel;

    // Table model and render
    private final JavaTableModel modelJava = new JavaTableModel();
    private final JavaTableModel modelJavaSummary = new JavaTableModel();
    private final JavaTableModel modelXml = new JavaTableModel();
    private final JavaTableModel modelXmlSummary = new JavaTableModel();
    private static final TableCellRenderer RENDERER_JAVA = new JavaRender();
    private static final TableCellRenderer RENDERER_SUMMARY_JAVA = new SummaryJavaRender();

    Project project;
    int m_fileCount=0;

    public  CodeAnalyzerGUI(String projectPath, Project projectValue)
    {
        // Set layout of form
        setLayout(new BorderLayout());
        add(MetricsMainPanel,BorderLayout.CENTER);

        // Create Java table
        zCreateJavaTable();
        JavaTablePanel.add(createJavaTableSummary(), BorderLayout.SOUTH);

        // Create XML table
        zCreateXmlTable();
        XmlTablePanel.add(createXmlTableSummary(), BorderLayout.SOUTH);

        this.project = projectValue;
        setJavaTableValue(this.project);
        setXmlTableValue(this.project);
        listFilesForFolder(projectPath);
    }

    // Count and display all other file types
    public void listFilesForFolder(String path) {
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles(); // Get list of files of folder
        int nCount = 0;

        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {
                m_fileCount++;
            } else if (listOfFile.isDirectory()) {
                System.out.println("Directory " + listOfFile.getName());
                this.listFilesForFolder(listOfFile.getPath());
            }
        }
        JLabel_FileCount.setText("File Count : " + String.valueOf(m_fileCount));
    }

    private void zCreateJavaTable()
    {
        javaTable.setModel(modelJava);
        javaTable.setAutoResizeMode(0x4);
        javaTable.getColumnModel().getColumn(0).setMinWidth(300);
        TableRowSorter rowSorter = new TableRowSorter(modelJava);
        ArrayList sortKeyArrayList = new ArrayList();
        sortKeyArrayList.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        rowSorter.setSortKeys(sortKeyArrayList);
        rowSorter.sort();
        javaTable.setRowSorter(rowSorter);
        javaTable.getColumnModel().getColumn(0).setMinWidth(300);
        javaTable.getColumnModel().getColumn(0).setCellRenderer(RENDERER_JAVA);
        javaTable.getColumnModel().getColumn(2).setCellRenderer(RENDERER_JAVA);
        javaTable.getColumnModel().getColumn(1).setCellRenderer(RENDERER_JAVA);
        javaTable.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent e) // Event handler for double mouse click to open file
            {
                if (e.getClickCount() == 2)
                {
                    int column = javaTable.getColumnModel().getColumnIndexAtX(e.getX());
                    int row = e.getY() / javaTable.getRowHeight();
                    if ((column < modelJava.getColumnCount()) && (row < modelJava.getRowCount()))
                    {
                        VirtualFile file = (VirtualFile)javaTable.getValueAt(row, -1);
                        if (file != null)
                        {
                            DataContext dataContext = DataManager.getInstance().getDataContext();
                            Project project = (Project) DataKeys.PROJECT.getData(dataContext);
                            FileEditorManager.getInstance(project).openFile(file, true);
                        }
                    }
                }
            }
        });
    }

    private void zCreateXmlTable()
    {
        // Set model values for table
        xmlTable.setModel(modelXml);
        xmlTable.setAutoResizeMode(4);
        xmlTable.getColumnModel().getColumn(0).setMinWidth(300);
        TableRowSorter rowSorter = new TableRowSorter(modelXml);
        ArrayList sortKeyArrayList = new ArrayList();
        sortKeyArrayList.add(new RowSorter.SortKey(0, SortOrder.ASCENDING)); // Sort key
        rowSorter.setSortKeys(sortKeyArrayList);
        rowSorter.sort();
        xmlTable.setRowSorter(rowSorter);
        xmlTable.getColumnModel().getColumn(0).setMinWidth(300);
        xmlTable.getColumnModel().getColumn(0).setCellRenderer(RENDERER_JAVA);
        xmlTable.getColumnModel().getColumn(2).setCellRenderer(RENDERER_JAVA);
        xmlTable.getColumnModel().getColumn(1).setCellRenderer(RENDERER_JAVA);
        xmlTable.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent e) // Event handler for double mouse click to open file
            {
                if (e.getClickCount() == 2)
                {
                    int column = xmlTable.getColumnModel().getColumnIndexAtX(e.getX());
                    int row = e.getY() / xmlTable.getRowHeight();
                    if ((column < modelXml.getColumnCount()) && (row < modelXml.getRowCount()))
                    {
                        VirtualFile file = (VirtualFile)xmlTable.getValueAt(row, -1);
                        if (file != null)
                        {
                            DataContext dataContext = DataManager.getInstance().getDataContext();
                            Project project = (Project) DataKeys.PROJECT.getData(dataContext);
                            FileEditorManager.getInstance(project).openFile(file, true);
                        }
                    }
                }
            }
        });
    }

    private JScrollPane createJavaTableSummary()
    {
        JTable table = new MyNoHeaderTable();
        table.setAutoResizeMode(4);
        table.setModel(modelJavaSummary);
        table.getColumnModel().getColumn(0).setMinWidth(300);
        table.getColumnModel().getColumn(0).setCellRenderer(RENDERER_SUMMARY_JAVA);
        table.getColumnModel().getColumn(2).setCellRenderer(RENDERER_SUMMARY_JAVA);
        table.getColumnModel().getColumn(1).setCellRenderer(RENDERER_SUMMARY_JAVA);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(22);
        JScrollBar jScrollBar = new JScrollBar();
        jScrollBar.setUI(new EmptyScrollBarUI());
        scrollPane.setVerticalScrollBar(jScrollBar);
        scrollPane.setPreferredSize(new Dimension(0, table.getRowHeight() + 5));
        return scrollPane;
    }

    private JScrollPane createXmlTableSummary()
    {
        JTable table = new MyNoHeaderTable();
        table.setAutoResizeMode(4);
        table.setModel(modelXmlSummary);
        table.getColumnModel().getColumn(0).setMinWidth(300);
        table.getColumnModel().getColumn(0).setCellRenderer(RENDERER_SUMMARY_JAVA);
        table.getColumnModel().getColumn(2).setCellRenderer(RENDERER_SUMMARY_JAVA);
        table.getColumnModel().getColumn(1).setCellRenderer(RENDERER_SUMMARY_JAVA);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(22);
        JScrollBar jScrollBar = new JScrollBar();
        jScrollBar.setUI(new EmptyScrollBarUI());
        scrollPane.setVerticalScrollBar(jScrollBar);
        scrollPane.setPreferredSize(new Dimension(0, table.getRowHeight() + 5));
        return scrollPane;
    }

    // Display total lines and source code lines for Java files
    private void setJavaTableValue(Project project)
    {
        // Get hash key file list of project
        Map<String, Set<VirtualFile>> result =  CodeAnalyzerUtils.getVirtualFiles(project);
        int nTotalLineCount = 0, nTotalCodeCount = 0;
        for (String key : result.keySet()) {
            if (key.equals("java"))
            {
                Set<VirtualFile> virtualfile = result.get(key);
                for (VirtualFile files : virtualfile)
                {
                    JavaRowBean javarowbean = new JavaRowBean(files, files.getName(), CodeAnalyzerUtils.getLineCount(files), CodeAnalyzerUtils.getCodeLineCount(files));
                    nTotalLineCount += CodeAnalyzerUtils.getLineCount(files);
                    nTotalCodeCount += CodeAnalyzerUtils.getCodeLineCount(files);
                    modelJava.addRow(javarowbean);
                }
            }
        }
        JavaRowBean javarowsummarybean = new JavaRowBean(null, "Total :", nTotalLineCount, nTotalCodeCount);
        modelJavaSummary.addRow(javarowsummarybean);
    }

    // Display total lines and source code lines for Xml files
    private void setXmlTableValue(Project project)
    {
        Map<String, Set<VirtualFile>> result =  CodeAnalyzerUtils.getVirtualFiles(project);
        int nTotalLineCount = 0, nTotalCodeCount = 0;
        for (String key : result.keySet()) {
            if (key.equals("xml"))
            {
                Set<VirtualFile> virtualfile = result.get(key);
                for (VirtualFile files : virtualfile)
                {
                    JavaRowBean javarowbean = new JavaRowBean(files, files.getName(), CodeAnalyzerUtils.getLineCount(files), CodeAnalyzerUtils.getCodeLineCount(files));
                    nTotalLineCount += CodeAnalyzerUtils.getLineCount(files);
                    nTotalCodeCount += CodeAnalyzerUtils.getCodeLineCount(files);
                    modelXml.addRow(javarowbean);
                }
            }
        }
        JavaRowBean javarowsummarybean = new JavaRowBean(null, "Total :", nTotalLineCount, nTotalCodeCount);
        modelXmlSummary.addRow(javarowsummarybean);
    }

    private class MyNoHeaderTable extends JTable
    {
        protected void configureEnclosingScrollPane()
        {
            Container p = getParent();
            if ((p instanceof JViewport))
            {
                Container gp = p.getParent();
                if ((gp instanceof JScrollPane))
                {
                    JScrollPane scrollPane = (JScrollPane)gp;
                    JViewport viewport = scrollPane.getViewport();
                    if ((viewport == null) || (viewport.getView() != this))
                    {
                        return;
                    }
                    scrollPane.getViewport().setBackingStoreEnabled(true);
                    scrollPane.setBorder(UIManager.getBorder("Table.scrollPaneBorder"));
                }
            }
        }
    }

    public class EmptyScrollBarUI extends BasicScrollBarUI
    {
        protected JButton createIncreaseButton(int orientation)
        {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            return button;
        }

        protected JButton createDecreaseButton(int orientation)
        {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            return button;
        }
    }
}