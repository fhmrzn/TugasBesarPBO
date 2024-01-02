package user;

import java.awt.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import javax.swing.JPanel;

public class RoundedPanel extends JPanel {
    private Color backgroundColor;
    private int cornerRadius = 15;
    
    public RoundedPanel(LayoutManager layout, int radius, Color bgColor) {
       super(layout);
       cornerRadius = radius;
       backgroundColor = bgColor;
    }
    
    public RoundedPanel(int radius) {
        super();
        cornerRadius = radius;
    }
    
    public RoundedPanel(int radius, Color bgColor) {
        super();
        cornerRadius = radius;
        backgroundColor = bgColor;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension arcs = new Dimension(cornerRadius, cornerRadius);
        int width = getWidth();
        int height = getHeight();
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (backgroundColor != null) {
            graphics.setColor(backgroundColor);
        } else {
            graphics.setColor(getBackground());
        }
        graphics.fillRoundRect(0,0,width-1,height-1,arcs.width,arcs.height);
        graphics.setColor(getForeground());
    }
}