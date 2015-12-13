package com.towson.codeanalyzer;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.towson.codeanalyzer.android.AndroidRowBean;
import com.towson.codeanalyzer.android.AndroidTableModel;
import com.towson.codeanalyzer.java.JavaRowBean;
import com.towson.codeanalyzer.java.JavaTableModel;
import com.towson.codeanalyzer.render.ActivityRender;
import com.towson.codeanalyzer.render.JavaRender;
import com.towson.codeanalyzer.render.SummaryJavaRender;
import com.towson.codeanalyzer.xml.XmlRowBean;
import com.towson.codeanalyzer.xml.XmlTableModel;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
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
	
	// Html tab panel
    private JPanel HTMLTablePanel;
    private JTable htmlTable;
	
	// Css tab panel
    private JPanel CSSTablePanel;
    private JTable cssTable;
	
	// Js tab panel
    private JPanel JSTablePanel;
    private JTable jsTable;

    // Android tab panel
    private JPanel AndroidPanel;
    private JTable atyTable;
	private JTable atyTotalTable;

	// Refresh button
    private JButton RefreshBtn;

    // Table model and render
    private final JavaTableModel modelJava = new JavaTableModel();
    private final JavaTableModel modelJavaSummary = new JavaTableModel();
    private final AndroidTableModel modelAndroid = new AndroidTableModel();
    private final AndroidTableModel modelAndroidSummary = new AndroidTableModel();
    private final XmlTableModel modelXml = new XmlTableModel();
    private final XmlTableModel modelXmlSummary = new XmlTableModel();
    private final XmlTableModel modelHtml = new XmlTableModel();
    private final XmlTableModel modelHtmlSummary = new XmlTableModel();
    private final XmlTableModel modelJs = new XmlTableModel();
    private final XmlTableModel modelJsSummary = new XmlTableModel();
    private final XmlTableModel modelCss = new XmlTableModel();
    private final XmlTableModel modelCssSummary = new XmlTableModel();
    private static final TableCellRenderer RENDERER_JAVA = new JavaRender();
    private static final TableCellRenderer RENDERER_SUMMARY_JAVA = new SummaryJavaRender();
	private static final ActivityRender RENDERER_ANDROID = new ActivityRender();
	
    public Project project;
	public String projectPath;
    int m_fileCount=0;

    public CodeAnalyzerGUI(final String projectPath, Project projectValue)
    {
        // Set layout of form
        setLayout(new BorderLayout());
        add(MetricsMainPanel,BorderLayout.CENTER);

        // Create Java table
        zCreateJavaTable();
        JavaTablePanel.add(createJavaTableSummary(), BorderLayout.SOUTH);

        // Create Android table
        zCreateAndroidTable();
        AndroidPanel.add(createAndroidTableSummary(), BorderLayout.SOUTH);

        // Create XML table
        zCreateXmlTable();
        XmlTablePanel.add(createXmlTableSummary(), BorderLayout.SOUTH);

		// Create Html table
		zCreateHtmlTable();
        HTMLTablePanel.add(createHtmlTableSummary(), BorderLayout.SOUTH);
		
		// Create Css table
		zCreateCssTable();
        CSSTablePanel.add(createCssTableSummary(), BorderLayout.SOUTH);
		
		// Create Js table
		zCreateJSTable();
        JSTablePanel.add(createJsTableSummary(), BorderLayout.SOUTH);
		
		RefreshBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {    //Refresh Button Click Event
				clickRefeshBtn();
			}
        });
		this.project = projectValue;
        this.projectPath = projectPath;
        zInit(this.projectPath, this.project);
        clickRefeshBtn();
	}

	private void clickRefeshBtn(){
	
        modelJavaSummary.removeRow();
        modelJava.removeRow();
		
        modelXml.removeRow();
        modelXmlSummary.removeRow();

        modelHtml.removeRow();
        modelHtmlSummary.removeRow();

        modelCss.removeRow();
        modelCssSummary.removeRow();

        modelJs.removeRow();
        modelJsSummary.removeRow();

        modelAndroid.removeRow();
        modelAndroidSummary.removeRow();

        m_fileCount = 0;
        zInit(projectPath, (CodeAnalyzerGUI.this).project);
	}
	
	private void zInit(String projectPath, Project projectValue)
	{
        setJavaTableValue(this.project);
        setXmlTableValue(this.project);
        setHtmlTableValue(this.project);
        setCssTableValue(this.project);
        setJsTableValue(this.project);
		setAndriodTableValue(this.project);
		atyTable.getColumnModel().getColumn(1).setCellRenderer(RENDERER_ANDROID);
        atyTable.getColumnModel().getColumn(2).setCellRenderer(RENDERER_ANDROID);
        atyTable.getColumnModel().getColumn(3).setCellRenderer(RENDERER_ANDROID);
        atyTable.getColumnModel().getColumn(0).setCellRenderer(RENDERER_ANDROID);
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
        JLabel_FileCount.setText("You have a total of " + String.valueOf(m_fileCount) + " files in the current project.");
    }

	/*
	** Setting model values for table
	*/
    private void zCreateJavaTable()
    {
        javaTable.setModel(modelJava); // Set model values of table
        javaTable.setAutoResizeMode(0x4);
        javaTable.getColumnModel().getColumn(0).setMinWidth(300);
        TableRowSorter rowSorter = new TableRowSorter(modelJava);
        ArrayList sortKeyArrayList = new ArrayList();
        sortKeyArrayList.add(new RowSorter.SortKey(0, SortOrder.ASCENDING)); // Sort key
        rowSorter.setSortKeys(sortKeyArrayList);
        rowSorter.sort();
        javaTable.setRowSorter(rowSorter);
        javaTable.getColumnModel().getColumn(0).setMinWidth(300);
        javaTable.getColumnModel().getColumn(0).setCellRenderer(RENDERER_JAVA);
        javaTable.getColumnModel().getColumn(2).setCellRenderer(RENDERER_JAVA);
        javaTable.getColumnModel().getColumn(1).setCellRenderer(RENDERER_JAVA);
		javaTable.getColumnModel().getColumn(3).setCellRenderer(RENDERER_JAVA);
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

    private void zCreateAndroidTable()
    {
        atyTable.setModel(modelAndroid);  //Set Model Values of Table
        atyTable.setAutoResizeMode(0x4);
        atyTable.getColumnModel().getColumn(0).setMinWidth(300);
        TableRowSorter rowSorter = new TableRowSorter(modelAndroid);
        ArrayList sortKeyArrayList = new ArrayList();
        sortKeyArrayList.add(new RowSorter.SortKey(0, SortOrder.ASCENDING)); //Sort Key
        rowSorter.setSortKeys(sortKeyArrayList);
        rowSorter.sort();
        atyTable.setRowSorter(rowSorter);
        atyTable.getColumnModel().getColumn(0).setMinWidth(300);
        atyTable.getColumnModel().getColumn(1).setCellRenderer(RENDERER_ANDROID);
        atyTable.getColumnModel().getColumn(2).setCellRenderer(RENDERER_ANDROID);
        atyTable.getColumnModel().getColumn(3).setCellRenderer(RENDERER_ANDROID);
        atyTable.getColumnModel().getColumn(0).setCellRenderer(RENDERER_ANDROID);

		atyTable.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
            }
            @Override
            public void mouseMoved(MouseEvent e) {
                int column = atyTable.getColumnModel().getColumnIndexAtX(e.getX());
                int row = e.getY() / atyTable.getRowHeight();
                showTooltip(row, column);
            }
        });
        atyTable.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent e) // Event handler for double mouse click to open file
            {
                if (e.getClickCount() == 2)
                {
                    int column = atyTable.getColumnModel().getColumnIndexAtX(e.getX());
                    int row = e.getY() / atyTable.getRowHeight();
                    if ((column < modelAndroid.getColumnCount()) && (row < modelAndroid.getRowCount()))
                    {
                        VirtualFile file = (VirtualFile)atyTable.getValueAt(row, -1);
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

    // Display hover text when moused over
	private void showTooltip(int row, int column)
    {
        if (row >= 0) {
            Object o = atyTable.getValueAt(row, column);
            if(column == 0) {
                int nInCnt = Integer.valueOf(modelAndroid.getValueAt(row, 2).toString());
                int nOutCnt = Integer.valueOf(modelAndroid.getValueAt(row, 3).toString());
                if( nInCnt + nOutCnt >= 10 )
                    atyTable.setToolTipText("Too Many Inputs/Outputs");
                else
                    atyTable.setToolTipText(null);
            }
            else
                atyTable.setToolTipText(null);
        }
    }

    private void zCreateXmlTable()
    {
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

	private void zCreateHtmlTable()
    {
        htmlTable.setModel(modelHtml);
        htmlTable.setAutoResizeMode(4);
        htmlTable.getColumnModel().getColumn(0).setMinWidth(300);
        TableRowSorter rowSorter = new TableRowSorter(modelHtml);
        ArrayList sortKeyArrayList = new ArrayList();
        sortKeyArrayList.add(new RowSorter.SortKey(0, SortOrder.ASCENDING)); // Sort key
        rowSorter.setSortKeys(sortKeyArrayList);
        rowSorter.sort();
        htmlTable.setRowSorter(rowSorter);
        htmlTable.getColumnModel().getColumn(0).setMinWidth(300);
        htmlTable.getColumnModel().getColumn(0).setCellRenderer(RENDERER_JAVA);
        htmlTable.getColumnModel().getColumn(2).setCellRenderer(RENDERER_JAVA);
        htmlTable.getColumnModel().getColumn(1).setCellRenderer(RENDERER_JAVA);
        htmlTable.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent e) // Event handler for double mouse click to open file
            {
                if (e.getClickCount() == 2)
                {
                    int column = htmlTable.getColumnModel().getColumnIndexAtX(e.getX());
                    int row = e.getY() / htmlTable.getRowHeight();
                    if ((column < modelXml.getColumnCount()) && (row < modelXml.getRowCount()))
                    {
                        VirtualFile file = (VirtualFile)htmlTable.getValueAt(row, -1);
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

    private void zCreateCssTable()
    {
        cssTable.setModel(modelCss);
        cssTable.setAutoResizeMode(4);
        cssTable.getColumnModel().getColumn(0).setMinWidth(300);
        TableRowSorter rowSorter = new TableRowSorter(modelCss);
        ArrayList sortKeyArrayList = new ArrayList();
        sortKeyArrayList.add(new RowSorter.SortKey(0, SortOrder.ASCENDING)); // Sort key
        rowSorter.setSortKeys(sortKeyArrayList);
        rowSorter.sort();
        cssTable.setRowSorter(rowSorter);
        cssTable.getColumnModel().getColumn(0).setMinWidth(300);
        cssTable.getColumnModel().getColumn(0).setCellRenderer(RENDERER_JAVA);
        cssTable.getColumnModel().getColumn(2).setCellRenderer(RENDERER_JAVA);
        cssTable.getColumnModel().getColumn(1).setCellRenderer(RENDERER_JAVA);
        cssTable.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent e) // Event handler for double mouse click to open file
            {
                if (e.getClickCount() == 2)
                {
                    int column = cssTable.getColumnModel().getColumnIndexAtX(e.getX());
                    int row = e.getY() / cssTable.getRowHeight();
                    if ((column < modelXml.getColumnCount()) && (row < modelXml.getRowCount()))
                    {
                        VirtualFile file = (VirtualFile)cssTable.getValueAt(row, -1);
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

    private void zCreateJSTable()
    {
        jsTable.setModel(modelJs);
        jsTable.setAutoResizeMode(4);
        jsTable.getColumnModel().getColumn(0).setMinWidth(300);
        TableRowSorter rowSorter = new TableRowSorter(modelJs);
        ArrayList sortKeyArrayList = new ArrayList();
        sortKeyArrayList.add(new RowSorter.SortKey(0, SortOrder.ASCENDING)); // Sort key
        rowSorter.setSortKeys(sortKeyArrayList);
        rowSorter.sort();
        jsTable.setRowSorter(rowSorter);
        jsTable.getColumnModel().getColumn(0).setMinWidth(300);
        jsTable.getColumnModel().getColumn(0).setCellRenderer(RENDERER_JAVA);
        jsTable.getColumnModel().getColumn(2).setCellRenderer(RENDERER_JAVA);
        jsTable.getColumnModel().getColumn(1).setCellRenderer(RENDERER_JAVA);
        jsTable.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent e) // Event handler for double mouse click to open file
            {
                if (e.getClickCount() == 2)
                {
                    int column = jsTable.getColumnModel().getColumnIndexAtX(e.getX());
                    int row = e.getY() / jsTable.getRowHeight();
                    if ((column < modelXml.getColumnCount()) && (row < modelXml.getRowCount()))
                    {
                        VirtualFile file = (VirtualFile)jsTable.getValueAt(row, -1);
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
	
	/*
	** Setting table summary for all tabs
	*/
    private JScrollPane createJavaTableSummary()
    {
        JTable table = new MyNoHeaderTable();
        table.setAutoResizeMode(4);
        table.setModel(modelJavaSummary);
        table.getColumnModel().getColumn(0).setMinWidth(300);
        table.getColumnModel().getColumn(0).setCellRenderer(RENDERER_SUMMARY_JAVA);
        table.getColumnModel().getColumn(2).setCellRenderer(RENDERER_SUMMARY_JAVA);
        table.getColumnModel().getColumn(1).setCellRenderer(RENDERER_SUMMARY_JAVA);
		table.getColumnModel().getColumn(3).setCellRenderer(RENDERER_SUMMARY_JAVA);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(22);
        JScrollBar jScrollBar = new JScrollBar();
        jScrollBar.setUI(new EmptyScrollBarUI());
        scrollPane.setVerticalScrollBar(jScrollBar);
        scrollPane.setPreferredSize(new Dimension(0, table.getRowHeight() + 5));
        return scrollPane;
    }
	
	private JScrollPane createAndroidTableSummary()
    {
        atyTotalTable = new MyNoHeaderTable();
        atyTotalTable.setAutoResizeMode(4);
        atyTotalTable.setModel(modelAndroidSummary);
        atyTotalTable.getColumnModel().getColumn(0).setMinWidth(300);
        atyTotalTable.getColumnModel().getColumn(0).setCellRenderer(RENDERER_ANDROID);
        atyTotalTable.getColumnModel().getColumn(2).setCellRenderer(RENDERER_ANDROID);
        atyTotalTable.getColumnModel().getColumn(1).setCellRenderer(RENDERER_ANDROID);
        atyTotalTable.getColumnModel().getColumn(3).setCellRenderer(RENDERER_ANDROID);
        JScrollPane scrollPane = new JScrollPane(atyTotalTable);
        scrollPane.setVerticalScrollBarPolicy(22);
        JScrollBar jScrollBar = new JScrollBar();
        jScrollBar.setUI(new EmptyScrollBarUI());
        scrollPane.setVerticalScrollBar(jScrollBar);
        scrollPane.setPreferredSize(new Dimension(0, atyTotalTable.getRowHeight() + 5));
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

	private JScrollPane createHtmlTableSummary()
    {
        JTable table = new MyNoHeaderTable();
        table.setAutoResizeMode(4);
        table.setModel(modelHtmlSummary);
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
	private JScrollPane createCssTableSummary()
    {
        JTable table = new MyNoHeaderTable();
        table.setAutoResizeMode(4);
        table.setModel(modelCssSummary);
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
	private JScrollPane createJsTableSummary()
    {
        JTable table = new MyNoHeaderTable();
        table.setAutoResizeMode(4);
        table.setModel(modelJsSummary);
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

	/*
	** Display information for all tabs
	*/
    private void setJavaTableValue(Project project)
    {
        // Get hash key file list of project
        Map<String, Set<VirtualFile>> result =  CodeAnalyzerUtils.getVirtualFiles(project);
        int nTotalLineCount = 0, nTotalCodeCount = 0, nTotalMethodCodeCount = 0;
        for (String key : result.keySet()) {
            if (key.equals("java"))
            {
                Set<VirtualFile> virtualfile = result.get(key);
                for (VirtualFile files : virtualfile)
                {
                    JavaRowBean javarowbean = new JavaRowBean(files, files.getName(), CodeAnalyzerUtils.getLineCount(files), CodeAnalyzerUtils.getCodeLineCount(files), CodeAnalyzerUtils.getCalledCodeLineCount(files) - 1);
                    nTotalLineCount += CodeAnalyzerUtils.getLineCount(files);
                    nTotalCodeCount += CodeAnalyzerUtils.getCodeLineCount(files);
                    nTotalMethodCodeCount += CodeAnalyzerUtils.getCalledCodeLineCount(files) - 1;
                    modelJava.addRow(javarowbean);
                }
            }
        }
        JavaRowBean javarowsummarybean = new JavaRowBean(null, "Total :", nTotalLineCount, nTotalCodeCount, nTotalMethodCodeCount);
        modelJavaSummary.addRow(javarowsummarybean);
    }

	private  void setAndriodTableValue(Project project)
    {
        ArrayList<VirtualFile> xmlFileList = getXmlFileList(project);
        Map<String, Set<VirtualFile>> result =  CodeAnalyzerUtils.getVirtualFiles(project);
        int nTotalInputCount = 0, nTotalOutputCount = 0;
        for (String key : result.keySet()) {
            if (key.equals("java"))
            {
                Set<VirtualFile> virtualfile = result.get(key);
                for (VirtualFile files : virtualfile)
                {
                    ArrayList<String> xmlTags = new ArrayList<String>();
                    ArrayList<String> actName = CodeAnalyzerUtils.getActivityName(files);
                    if( actName.size()==0)
                        continue;

                    for ( int i=0; i<xmlFileList.size(); i++) {
                        if( xmlFileList.get(i).getName().equals(actName.get(1)+".xml") ){
                            xmlTags = CodeAnalyzerUtils.getXmlTagNames(xmlFileList.get(i));
                            break;
                        }
                    }
					if( actName.get(1).equals("") ) continue; // If there is no xml layout file in Activity Name, don't display
                    AndroidRowBean androidrowbean;
                    if( xmlTags.size()==0){
                        androidrowbean = new AndroidRowBean(files, actName.get(0), actName.get(1), 0, 0);
                    }
                    else {
                        androidrowbean = new AndroidRowBean(files, actName.get(0), actName.get(1), Integer.valueOf(xmlTags.get(0)), Integer.valueOf(xmlTags.get(1)));
                        nTotalInputCount += Integer.valueOf(xmlTags.get(0));
                        nTotalOutputCount += Integer.valueOf(xmlTags.get(1));
                    }
                    modelAndroid.addRow(androidrowbean);
                }
            }
        }
        AndroidRowBean androidrowsummarybean = new AndroidRowBean(null, "Total :", "", nTotalInputCount, nTotalOutputCount);
        modelAndroidSummary.addRow(androidrowsummarybean);
		atyTotalTable.getColumnModel().getColumn(0).setWidth(atyTable.getColumnModel().getColumn(0).getWidth());
        atyTotalTable.getColumnModel().getColumn(0).setCellRenderer(RENDERER_ANDROID);
        atyTotalTable.getColumnModel().getColumn(1).setWidth(atyTable.getColumnModel().getColumn(1).getWidth()+10);
        atyTotalTable.getColumnModel().getColumn(2).setCellRenderer(RENDERER_ANDROID);
        atyTotalTable.getColumnModel().getColumn(2).setWidth(atyTable.getColumnModel().getColumn(2).getWidth());
        atyTotalTable.getColumnModel().getColumn(1).setCellRenderer(RENDERER_ANDROID);
        atyTotalTable.getColumnModel().getColumn(3).setWidth(atyTable.getColumnModel().getColumn(3).getWidth());
        atyTotalTable.getColumnModel().getColumn(3).setCellRenderer(RENDERER_ANDROID);
    }
    private  ArrayList<VirtualFile> getXmlFileList(Project project)
    {
        Map<String, Set<VirtualFile>> result =  CodeAnalyzerUtils.getVirtualFiles(project);
        ArrayList<VirtualFile> ret = new ArrayList<VirtualFile>();
        for (String key : result.keySet()) {
            if (key.equals("xml"))
            {
                Set<VirtualFile> virtualfile = result.get(key);
                for (VirtualFile files : virtualfile)
                {
                    ret.add(files);

                }
            }
        }
        return ret;
    }

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
                    XmlRowBean xmlrowbean = new XmlRowBean(files, files.getName(), CodeAnalyzerUtils.getLineCount(files), CodeAnalyzerUtils.getCodeLineCount(files));
                    nTotalLineCount += CodeAnalyzerUtils.getLineCount(files);
                    nTotalCodeCount += CodeAnalyzerUtils.getCodeLineCount(files);
                    modelXml.addRow(xmlrowbean);
                }
            }
        }
        XmlRowBean xmlrowsummarybean = new XmlRowBean(null, "Total :", nTotalLineCount, nTotalCodeCount);
        modelXmlSummary.addRow(xmlrowsummarybean);
    }

	private  void setHtmlTableValue(Project project)
    {
        Map<String, Set<VirtualFile>> result =  CodeAnalyzerUtils.getVirtualFiles(project);
        int nTotalLineCount = 0, nTotalCodeCount = 0;
        for (String key : result.keySet()) {
            if (key.equals("html"))
            {
                Set<VirtualFile> virtualfile = result.get(key);
                for (VirtualFile files : virtualfile)
                {
                    XmlRowBean htmlrowbean = new XmlRowBean(files, files.getName(), CodeAnalyzerUtils.getLineCount(files), CodeAnalyzerUtils.getHtmlCodeLineCount(files));
                    nTotalLineCount += CodeAnalyzerUtils.getLineCount(files);
                    nTotalCodeCount += CodeAnalyzerUtils.getHtmlCodeLineCount(files);
                    modelHtml.addRow(htmlrowbean);
                }
            }
        }
        XmlRowBean htmlrowsummarybean = new XmlRowBean(null, "Total :", nTotalLineCount, nTotalCodeCount);
        modelHtmlSummary.addRow(htmlrowsummarybean);
    }

    private void setCssTableValue(Project project)
    {
        Map<String, Set<VirtualFile>> result =  CodeAnalyzerUtils.getVirtualFiles(project);
        int nTotalLineCount = 0, nTotalCodeCount = 0;
        for (String key : result.keySet()) {
            if (key.equals("css"))
            {
                Set<VirtualFile> virtualfile = result.get(key);
                for (VirtualFile files : virtualfile)
                {
                    XmlRowBean cssrowbean = new XmlRowBean(files, files.getName(), CodeAnalyzerUtils.getLineCount(files), CodeAnalyzerUtils.getCodeLineCount(files));
                    nTotalLineCount += CodeAnalyzerUtils.getLineCount(files);
                    nTotalCodeCount += CodeAnalyzerUtils.getCodeLineCount(files);
                    modelCss.addRow(cssrowbean);
                }
            }
        }
        XmlRowBean cssrowsummarybean = new XmlRowBean(null, "Total :", nTotalLineCount, nTotalCodeCount);
        modelCssSummary.addRow(cssrowsummarybean);
    }

    private void setJsTableValue(Project project)
    {
        Map<String, Set<VirtualFile>> result =  CodeAnalyzerUtils.getVirtualFiles(project);
        int nTotalLineCount = 0, nTotalCodeCount = 0;
        for (String key : result.keySet()) {
            if (key.equals("js"))
            {
                Set<VirtualFile> virtualfile = result.get(key);
                for (VirtualFile files : virtualfile)
                {
                    XmlRowBean jsrowbean = new XmlRowBean(files, files.getName(), CodeAnalyzerUtils.getLineCount(files), CodeAnalyzerUtils.getCodeLineCount(files));
                    nTotalLineCount += CodeAnalyzerUtils.getLineCount(files);
                    nTotalCodeCount += CodeAnalyzerUtils.getCodeLineCount(files);
                    modelJs.addRow(jsrowbean);
                }
            }
        }
        XmlRowBean jsrowsummarybean = new XmlRowBean(null, "Total :", nTotalLineCount, nTotalCodeCount);
        modelJsSummary.addRow(jsrowsummarybean);
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