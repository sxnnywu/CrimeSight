package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

@SuppressWarnings("serial")
public class RoundedButton extends JButton {
	
//	FIELDS --------------------------------------------------------------------------------------------------------
    private Color backgroundColor;
    private Color hoverColor;
    private boolean hovered = false;

// 	CONSTRUCTOR ---------------------------------------------------------------------------------------------------
    public RoundedButton(String text, Color backgroundColor, Color hoverColor) {
        super(text);
        this.backgroundColor = backgroundColor;
        this.hoverColor = hoverColor;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

//    	Hover effect
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }
            public void mouseExited(MouseEvent e) {
                hovered = false;
                repaint();
            }
        });
    }

// 	PAINT COMPONENT -----------------------------------------------------------------------------------------------
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        int arc = 30;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color fill = hovered ? hoverColor : backgroundColor;
        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

        super.paintComponent(g2);
        g2.dispose();
    }
    @Override
    protected void paintBorder(Graphics g) {
        // No border
    }
    @Override
    public boolean isOpaque() {
        return false;
    }
}