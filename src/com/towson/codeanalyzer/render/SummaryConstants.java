package com.towson.codeanalyzer.render;

import java.awt.*;

// Interface with constants to initialize the font and color of texts
// Normal rows and total row are set to default colors

public abstract interface SummaryConstants
{
    public static final Font FONT = new Font("Verdana", 1, 12);
    public static final Color BACKGROUND_COLOR = new Color(205, 220, 242);
    public static final Color FOREGROUND_COLOR = new Color(38, 55, 157);
}