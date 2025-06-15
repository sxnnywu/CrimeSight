package view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import controller.*;

@SuppressWarnings("serial")
public class CrimeSightFrame extends JFrame {
    private JFrame crimeSightFrame;
    private JTabbedPane tabbedPane;
    private JPanel locationIncidentPanel;
    private JPanel incidentBasedCrimePanel;
    private CrimeSeverityIndexPanel crimeSeverityIndexPanel; 
    private IncidentCountPanel incidentSummaryCount;

    // Color scheme
    private Color navyBlue = Color.decode("#1C3F60");
    private Color mistyBlue = Color.decode("#2d8bba");
    private Color blue2 = Color.decode("#41b8d5");
    private Color blue3 = Color.decode("#83abbc");
    private Color blue4 = Color.decode("#6ce5e8");
    private Color selectedTabPink = Color.decode("#f8d0c7");

    private JLabel[] tabLabels;
    
    // Persistent tooltip components
    private JWindow persistentTooltip;
    private JLabel tooltipLabel;
    private static final String INCIDENT_BASED_CRIME_DESCRIPTION = 
        "<html><div style='width:300px;padding:10px;'>" +
        "<b style='color:#1C3F60;'>Incident Based Crime Analysis</b><br><br>" +
        "This panel provides detailed analysis of crime rates across different violation types.<br><br>" +
        "<b>Features:</b>" +
        "<ul>" +
        "<li>Compare up to 5 crime categories simultaneously</li>" +
        "<li>View trends between 2008-2012</li>" +
        "<li>Project future crime rate trends</li>" +
        "<li>Calculate mean crime rates</li>" +
        "<li>Toggle between 2D and 3D chart views</li>" +
        "<li>Customize chart colors</li>" +
        "<li>Export charts as PNG images</li>" +
        "</ul>" +
        "</div></html>";

    @SuppressWarnings("static-access")
    public CrimeSightFrame() {
        crimeSightFrame = new JFrame("CrimeSight ON");
        crimeSightFrame.setSize(1366, 768);
        crimeSightFrame.setIconImage(new ImageIcon("images/logo.png").getImage()); // Set window icon
        crimeSightFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tabbedPane = new JTabbedPane();
        
        // Initialize panels
        LocationIncidentFrame locationIncidentFrame = new LocationIncidentFrame();
        locationIncidentPanel = locationIncidentFrame.getLocationIncidentPanel();

        new IncidentBasedCrimesFileReader(); 
        IncidentBasedCrimesView view = new IncidentBasedCrimesView();
        incidentBasedCrimePanel = view.getPanel();
        new IncidentBasedCrimesController(view);

        crimeSeverityIndexPanel = new CrimeSeverityIndexPanel();
        new CrimeSeverityIndexController(crimeSeverityIndexPanel);

        new IncidentCountFileReader();
        incidentSummaryCount = new IncidentCountPanel();
        new IncidentCountController(incidentSummaryCount);

        // Initialize persistent tooltip
        persistentTooltip = new JWindow();
        tooltipLabel = new JLabel(INCIDENT_BASED_CRIME_DESCRIPTION);
        tooltipLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tooltipLabel.setBackground(new Color(255, 255, 225)); // Light yellow background
        tooltipLabel.setOpaque(true);
        persistentTooltip.add(tooltipLabel);
        persistentTooltip.pack();

        String[] tabTitles = {
            " Location Incident ",
            " Incident Based Crime ",
            " Institution Incident Summary ",
            " Institution Incident Count "
        };

        JPanel[] panels = {
            locationIncidentPanel,
            incidentBasedCrimePanel,
            crimeSeverityIndexPanel,
            incidentSummaryCount
        };

        Color[] defaultColors = { mistyBlue, blue2, blue3, blue4 };
        tabLabels = new JLabel[tabTitles.length];

        for (int i = 0; i < tabTitles.length; i++) {
            final int tabIndex = i; // Create final copy of loop variable
            tabbedPane.addTab(null, panels[i]);

            JLabel label = new JLabel(tabTitles[i], SwingConstants.CENTER);
            label.setOpaque(true);
            label.setFont(new Font("Calibri", Font.PLAIN, 16));
            label.setBackground(defaultColors[i]);
            label.setPreferredSize(new Dimension(200, 30));

            // Add persistent tooltip to Incident Based Crime tab
            if (i == 1) {
                label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        Point p = label.getLocationOnScreen();
                        persistentTooltip.setLocation(p.x, p.y + label.getHeight());
                        persistentTooltip.setVisible(true);
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                        persistentTooltip.setVisible(false);
                    }
                    
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        tabbedPane.setSelectedIndex(tabIndex); // Use final variable
                    }
                });
                
                label.setCursor(Cursor.getDefaultCursor());
            }

            tabbedPane.setTabComponentAt(i, label);
            tabLabels[i] = label;
        }

        // Set first tab as selected
        tabLabels[0].setBackground(selectedTabPink);

        // Tab change listener
        tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int selected = tabbedPane.getSelectedIndex();
                for (int i = 0; i < tabLabels.length; i++) {
                    tabLabels[i].setBackground(i == selected ? selectedTabPink : defaultColors[i]);
                }
            }
        });

        // Configure frame
        tabbedPane.setBounds(20, 15, 1310, 700);
        crimeSightFrame.setLayout(null);
        crimeSightFrame.getContentPane().setBackground(navyBlue);
        crimeSightFrame.add(tabbedPane);
        crimeSightFrame.setVisible(true);
    }
}