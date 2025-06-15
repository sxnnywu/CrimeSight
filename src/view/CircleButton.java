package view;

import javax.swing.JButton;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.FontMetrics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.RenderingHints;

@SuppressWarnings("serial")
public class CircleButton extends JButton {
    private boolean mouseOver = false;
    private boolean mousePressed = false;

    public CircleButton(String text) {
        super(text);
        setOpaque(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);

        MouseAdapter mouseListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                if(contains(me.getX(), me.getY())) {
                    mousePressed = true;
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent me) {
                mousePressed = false;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent me) {
                mouseOver = false;
                mousePressed = false;
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent me) {
                mouseOver = contains(me.getX(), me.getY());
                repaint();
            }
        };

        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);        
    }

    private int getDiameter() {
        int diameter = Math.min(getWidth(), getHeight());
        return diameter;
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics metrics = getGraphics().getFontMetrics(getFont());
        int minDiameter = 10 + Math.max(metrics.stringWidth(getText()), metrics.getHeight());
        return new Dimension(minDiameter, minDiameter);
    }

    @Override
    public boolean contains(int x, int y) {
        int radius = getDiameter()/2;
        return Point2D.distance(x, y, getWidth()/2, getHeight()/2) < radius;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        
        // Enable anti-aliasing for smooth edges
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int diameter = getDiameter();
        int radius = diameter/2;
        int x = getWidth()/2 - radius;
        int y = getHeight()/2 - radius;

        // Paint the background
        if (mousePressed) {
            g2.setColor(Color.DARK_GRAY);
        } else {
            g2.setColor(Color.WHITE);
        }
        g2.fillOval(x, y, diameter, diameter);

        // Paint the border
        if (mouseOver) {
            g2.setColor(Color.BLUE);
        } else {
            g2.setColor(Color.BLACK);
        }
        g2.drawOval(x, y, diameter, diameter);

        // Paint the text
        g2.setColor(Color.BLACK);
        g2.setFont(getFont());
        FontMetrics metrics = g2.getFontMetrics(getFont());
        int stringWidth = metrics.stringWidth(getText());
        int stringHeight = metrics.getHeight();
        g2.drawString(getText(), getWidth()/2 - stringWidth/2, getHeight()/2 + stringHeight/4);
        
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        // Empty override to prevent default border painting
    }
}