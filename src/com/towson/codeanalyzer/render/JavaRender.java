package com.towson.codeanalyzer.render;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.DecimalFormat;

public class JavaRender extends DefaultTableCellRenderer {

    // Format decimal numbers
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0%");

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        // Set font of the data in each row
        JLabel jLabel = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (column == 0)
        {
            jLabel.setText("<html><b><font face=\"Verdana\" size=\"3\" color=\"navy\">" + value + "</font></b></html>");
        }
        else if ((column == 7) || (column == 5) || (column == 3))
        {
            String s = DECIMAL_FORMAT.format(value);
            jLabel.setText(s);
        }
        else
        {
            jLabel.setText(value.toString());
        }
        return jLabel;
    }
}