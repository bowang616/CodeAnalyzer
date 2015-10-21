package com.towson.codeanalyzer.render;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.DecimalFormat;

// Subclass to extend and implement interface
// Render the colors of text and background to jLabel objects

public class SummaryJavaRender extends DefaultTableCellRenderer
        implements SummaryConstants
{
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0%");

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        JLabel jLabel = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        String text = value.toString();
        text = value.toString();
        jLabel.setText("<html><b><font face=\"Verdana\" color=\"navy\">" + text + "</font></b></html>");
        jLabel.setFont(FONT);
        jLabel.setBackground(BACKGROUND_COLOR);
        jLabel.setForeground(FOREGROUND_COLOR);
        return jLabel;
    }
}