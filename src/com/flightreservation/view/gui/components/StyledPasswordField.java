package com.flightreservation.view.gui.components;

import com.flightreservation.view.gui.theme.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Custom styled password field matching StyledTextField
 */
public class StyledPasswordField extends JPasswordField {

    private String placeholder;

    public StyledPasswordField(int columns) {
        super(columns);
        setupStyle();
    }

    public StyledPasswordField(String placeholder, int columns) {
        super(columns);
        this.placeholder = placeholder;
        setupStyle();
    }

    private void setupStyle() {
        setFont(AppTheme.FONT_BODY);
        setForeground(AppTheme.TEXT_PRIMARY);
        setBackground(AppTheme.SURFACE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.DIVIDER, 1),
                new EmptyBorder(AppTheme.PADDING_SMALL, AppTheme.PADDING_MEDIUM,
                        AppTheme.PADDING_SMALL, AppTheme.PADDING_MEDIUM)));
        setPreferredSize(new Dimension(getPreferredSize().width, 40));
        setEchoChar('●');
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw placeholder text if field is empty
        if (placeholder != null && getPassword().length == 0 && !hasFocus()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(AppTheme.TEXT_SECONDARY);
            g2.setFont(getFont());

            Insets insets = getInsets();
            g2.drawString(placeholder, insets.left,
                    g.getFontMetrics().getMaxAscent() + insets.top);
            g2.dispose();
        }
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    public String getPlaceholder() {
        return placeholder;
    }
}
