package com.towson.codeanalyzer;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;
import javax.swing.*;

public class CodeAnalyzerComponents implements ProjectComponent {

    private final Project project;
    private CodeAnalyzerGUI panel;
    private static final ImageIcon ICON = new ImageIcon(CodeAnalyzerComponents.class.getResource("/resources/logo.png"));
    public String fileTypes = "class;svn-base;svn-work;Extra;gif;png;jpg;jpeg;bmp;tga;tiff;ear;war;zip;jar;iml;iws;ipr;bz2;gz;";

    // Constructor
    public  CodeAnalyzerComponents(Project project) {
        this.project = project;
    }

    // Setting up menu in View/Tool Windows
    @Override
    public void projectOpened() {
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(this.project);
        ToolWindow toolWindow = toolWindowManager.registerToolWindow("Code Analyzer", false, ToolWindowAnchor.BOTTOM);
        toolWindow.setIcon(ICON);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(this.panel, null, true);
        toolWindow.getContentManager().addContent(content);
    }

    @Override
    public void projectClosed() {
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(this.project);
        toolWindowManager.unregisterToolWindow("Code Analyzer");
    }

    // Initial activate plugin on start
    @Override
    public void initComponent() {
        System.out.println("Code Analyzer Plugin has been activated for project " + this.project.getName());
        try
        {
            this.panel = new CodeAnalyzerGUI(this.project.getBasePath());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // Deactivate plugin for project
    @Override
    public void disposeComponent() {
        System.out.println("Code Analyzer Plugin  has been deactivated for project " + this.project.getName());
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "CodeAnalyzerComponents";
    }
}