package controller;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;

import view.IncidentBasedCrimesView;
import model.IncidentBasedCrimesModel;

/**
 * The IncidentBasedCrimesController class manages user interactions and
 * business logic for the Incident-Based Crimes visualization. It acts as the
 * mediator between the view (IncidentBasedCrimesView) and the model
 * (IncidentBasedCrimesModel), handling all application logic and user input.
 */
public class IncidentBasedCrimesController {
    // Reference to the view component that this controller manages
    private IncidentBasedCrimesView view;

    // Static list to store crime data models loaded from data source
    private static List<IncidentBasedCrimesModel> incidentDataList;

    /**
     * Constructs a new controller with the specified view.
     * Initializes all event listeners and sets up the controller-view relationship.
     * 
     * @param view The associated IncidentBasedCrimesView instance that this controller will manage
     */
    public IncidentBasedCrimesController(IncidentBasedCrimesView view) {
        this.view = view;
        setupEventListeners(); // Initialize all event listeners for the view components
    }

    /**
     * Sets up all necessary event listeners for UI components in the view.
     * This includes listeners for buttons, sliders, and text fields.
     */
    private void setupEventListeners() {
        // Apply button listener - updates chart based on current selections
        view.getApplyButton().addActionListener(e -> handleApplyButton());

        // Year slider listeners - synchronizes slider and text field values
        view.getStartYearSlider().addChangeListener(this::handleYearSliderChange);
        view.getEndYearSlider().addChangeListener(this::handleYearSliderChange);

        // Year text field listeners - validates and synchronizes input
        view.getStartYearTextField().addActionListener(this::handleYearTextFieldAction);
        view.getEndYearTextField().addActionListener(this::handleYearTextFieldAction);

        // Change Colors button listener - handles color customization for the chart
        view.getChangeColorsButton().addActionListener(e -> handleColorChange());
        
        // Help button listener - handles help screen pop up when the user clicks it
        view.getHelpButton().addActionListener(e -> handleHelp());

        // Export PNG button listener - handles image export functionality
        view.getExportPngButton().addActionListener(e -> handleExport());
    }

    /**
     * Handles the export functionality by presenting export options to the user in a 2x2 grid.
     * Provides options for exporting different portions of the application interface.
     */
    private void handleExport() {
        // Create options in a 2D array for grid layout
        Object[][] options = { 
            { "Entire Screen (full content)", "Visible Panel Area" },
            { "Just the Chart", "Cancel" } 
        };

        // Create custom panel with GridLayout for export options
        JPanel optionPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        optionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add buttons to panel
        JButton[] buttons = new JButton[4];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                buttons[i * 2 + j] = new JButton(options[i][j].toString());
                optionPanel.add(buttons[i * 2 + j]);
            }
        }

        // Create option dialog
        JOptionPane pane = new JOptionPane(optionPanel, JOptionPane.QUESTION_MESSAGE, 
            JOptionPane.DEFAULT_OPTION, null, new Object[] {}, null);

        JDialog dialog = pane.createDialog(view.getPanel(), "Export Options");

        // Add action listeners to buttons
        buttons[0].addActionListener(e -> {
            dialog.dispose();
            exportFullContentScreen();
        });
        buttons[1].addActionListener(e -> {
            dialog.dispose();
            exportVisiblePanel();
        });
        buttons[2].addActionListener(e -> {
            dialog.dispose();
            exportChartOnly();
        });
        buttons[3].addActionListener(e -> {
            dialog.dispose();
        });

        dialog.setVisible(true);
    }

    /**
     * Exports the entire screen content including any scrolled portions that aren't currently visible.
     * This method temporarily expands the scroll pane to capture all content before exporting.
     */
    private void exportFullContentScreen() {
        JFileChooser fileChooser = createFileChooser("CrimeSight_FullContent.png");
        if (fileChooser.showSaveDialog(view.getPanel()) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = ensurePngExtension(fileChooser.getSelectedFile());

            try {
                // 1. Get the scrollable content dimensions
                JScrollPane scrollPane = findScrollPane(view.getPanel());
                Component content = scrollPane.getViewport().getView();
                Dimension contentSize = content.getPreferredSize();

                // 2. Temporarily expand the scroll pane to show all content
                scrollPane.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE);
                scrollPane.setPreferredSize(contentSize);
                scrollPane.getViewport().setSize(contentSize);
                content.setSize(contentSize);

                // 3. Get the window reference
                Window window = SwingUtilities.getWindowAncestor(view.getPanel());

                // 4. Create image of the window with expanded content
                BufferedImage image = new BufferedImage(window.getWidth(), window.getHeight(),
                        BufferedImage.TYPE_INT_RGB);

                Graphics2D g2d = image.createGraphics();
                window.paint(g2d);
                g2d.dispose();

                // 5. Restore original scroll pane state
                scrollPane.getViewport().setViewSize(scrollPane.getViewport().getExtentSize());
                scrollPane.setPreferredSize(null); // Reset to original

                // 6. Save to file
                ImageIO.write(image, "png", fileToSave);
                showExportSuccess(fileToSave);

            } catch (IOException ex) {
                showExportError("Error saving full content: " + ex.getMessage());
            } catch (NullPointerException ex) {
                showExportError("Could not find scrollable content to export");
            }
        }
    }

    /**
     * Recursively finds the first JScrollPane in a container hierarchy.
     * 
     * @param container The container to search through
     * @return The first JScrollPane found, or null if none exists
     */
    private JScrollPane findScrollPane(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JScrollPane) {
                return (JScrollPane) comp;
            }
            if (comp instanceof Container) {
                JScrollPane result = findScrollPane((Container) comp);
                if (result != null)
                    return result;
            }
        }
        return null;
    }

    /**
     * Exports only the currently visible portion of the main panel.
     * This captures exactly what the user sees in the application window.
     */
    private void exportVisiblePanel() {
        JFileChooser fileChooser = createFileChooser("CrimeSight_Panel.png");
        if (fileChooser.showSaveDialog(view.getPanel()) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = ensurePngExtension(fileChooser.getSelectedFile());

            try {
                Component panel = view.getPanel();
                BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(),
                        BufferedImage.TYPE_INT_RGB);

                Graphics2D g2d = image.createGraphics();
                panel.paint(g2d);
                g2d.dispose();

                ImageIO.write(image, "png", fileToSave);
                showExportSuccess(fileToSave);

            } catch (IOException ex) {
                showExportError("Error saving panel: " + ex.getMessage());
            }
        }
    }

    /**
     * Exports only the chart component without any surrounding UI elements.
     * This provides a clean export of just the data visualization.
     */
    private void exportChartOnly() {
        JFileChooser fileChooser = createFileChooser("CrimeSight_Chart.png");
        if (fileChooser.showSaveDialog(view.getPanel()) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = ensurePngExtension(fileChooser.getSelectedFile());

            try {
                Component chart = view.getChartPanel();
                BufferedImage image = new BufferedImage(chart.getWidth(), chart.getHeight(),
                        BufferedImage.TYPE_INT_RGB);

                Graphics2D g2d = image.createGraphics();
                chart.paint(g2d);
                g2d.dispose();

                ImageIO.write(image, "png", fileToSave);
                showExportSuccess(fileToSave);

            } catch (IOException ex) {
                showExportError("Error saving chart: " + ex.getMessage());
            } catch (NullPointerException ex) {
                showExportError("No chart available to export");
            }
        }
    }

    // HELPER METHODS ----------------------------------------------------------

    /**
     * Creates a configured file chooser dialog for PNG exports.
     * 
     * @param defaultName The default filename to suggest to the user
     * @return Configured JFileChooser instance ready for use
     */
    private JFileChooser createFileChooser(String defaultName) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save PNG Image");
        fileChooser.setSelectedFile(new File(defaultName));
        fileChooser.setFileFilter(new FileNameExtensionFilter("PNG Images", "png"));
        return fileChooser;
    }

    /**
     * Ensures the specified file has a .png extension.
     * If the file doesn't end with .png, it adds the extension.
     * 
     * @param file The original file selected by the user
     * @return File with guaranteed .png extension
     */
    private File ensurePngExtension(File file) {
        if (!file.getName().toLowerCase().endsWith(".png")) {
            return new File(file.getAbsolutePath() + ".png");
        }
        return file;
    }

    /**
     * Shows a success message after successful export.
     * 
     * @param file The file that was successfully saved
     */
    private void showExportSuccess(File file) {
        JOptionPane.showMessageDialog(view.getPanel(), 
            "Image successfully saved to:\n" + file.getAbsolutePath(),
            "Export Successful", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Shows an error message when export fails.
     * 
     * @param message The error message to display to the user
     */
    private void showExportError(String message) {
        JOptionPane.showMessageDialog(view.getPanel(), 
            message, "Export Error", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Displays comprehensive help information explaining how to use all features
     * of the Incident Based Crime analysis screen.
     */
    private void handleHelp() {
        // Create HTML-formatted help message with detailed instructions
        String helpMessage = "<html><div style='width:500px;'>" +
            "<h2 style='color:#1C3F60;'>Incident Based Crime Analysis Help</h2>" +
            "<p>This screen allows you to analyze crime statistics across different violation types.</p>" +
            
            "<h3 style='color:#1C3F60;'>Screen Layout</h3>" +
            "<ul>" +
            "<li><b>Left Side:</b> Interactive chart display area</li>" +
            "<li><b>Right Side:</b> Control panel with all selection options</li>" +
            "<li><b>Bottom:</b> Action buttons (Change Colors, Help, Export, Apply)</li>" +
            "</ul>" +
            
            "<h3 style='color:#1C3F60;'>How To Use</h3>" +
            
            "<p><b>1. Select Crime Categories</b><br>" +
            "Use the 5 dropdown menus in the control panel to select different crime types to compare. " +
            "Choose 'None' to leave a slot empty.</p>" +
            
            "<p><b>2. Set Year Range</b><br>" +
            "Use the sliders or type directly in the text fields to set your start and end years (2008-2012). " +
            "The chart will automatically update when you click Apply.</p>" +
            
            "<p><b>3. Chart Display Options</b><br>" +
            "Toggle between 2D and 3D views using the radio buttons. " +
            "3D view provides perspective while 2D may be better for precise comparisons.</p>" +
            
            "<p><b>4. Future Trends Projection</b><br>" +
            "Check 'Yes' to enable trend projection and enter how many years forward you want to project. " +
            "The system will calculate likely future crime rates based on historical data.</p>" +
            
            "<p><b>5. Mean Calculation</b><br>" +
            "Select any bar using the radio buttons to calculate and display its average crime rate " +
            "across all displayed years.</p>" +
            
            "<p><b>6. Customize Colors</b><br>" +
            "Click 'Change Colors' to modify the colors used for each crime category in the chart.</p>" +
            
            "<p><b>7. Export Options</b><br>" +
            "Use 'Export as PNG' to save your chart as an image file. You can export the full screen, " +
            "just the visible panel, or only the chart itself.</p>" +
            
            "<p><b>8. Apply Changes</b><br>" +
            "After making selections, click 'Apply' to update the chart with your current settings.</p>" +
            
            "<h3 style='color:#1C3F60;'>Tips</h3>" +
            "<ul>" +
            "<li>Hover over chart elements to see precise values</li>" +
            "<li>Use the mouse wheel to zoom in/out on the chart</li>" +
            "<li>Click and drag to pan around the chart</li>" +
            "<li>Right-click on the chart for additional viewing options</li>" +
            "</ul>" +
            "</div></html>";

        // Create and display the help dialog
        JLabel helpLabel = new JLabel(helpMessage);
        helpLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JOptionPane.showMessageDialog(
            view.getPanel(), 
            helpLabel,
            "Incident Based Crime Analysis Help",
            JOptionPane.PLAIN_MESSAGE
        );
    }

    /**
     * Handles color change functionality for the chart.
     * Shows current categories with their colors and allows user to modify them via JColorChooser.
     */
    private void handleColorChange() {
        // Get currently selected categories from the view
        String[] selectedCategories = view.getSelectedCategories();

        // Filter out "None" selections to get only active categories
        List<String> activeCategories = new ArrayList<>();
        for (String category : selectedCategories) {
            if (category != null && !category.equals("None")) {
                activeCategories.add(category);
            }
        }

        // Show warning if no valid categories are selected
        if (activeCategories.isEmpty()) {
            JOptionPane.showMessageDialog(view.getPanel(), 
                "No valid categories selected", 
                "Color Change Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Create dialog panel for color selection
        JPanel colorPanel = new JPanel(new GridLayout(activeCategories.size(), 2, 5, 5));
        colorPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Store color buttons and current colors
        List<JButton> colorButtons = new ArrayList<>();
        List<Color> currentColors = new ArrayList<>();

        // Get current renderer and colors from the chart
        CategoryPlot plot = (CategoryPlot) view.getChartPanel().getChart().getPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();

        // Create UI components for each active category
        for (int i = 0; i < activeCategories.size(); i++) {
            String category = activeCategories.get(i);

            // Get current color (use default palette if not set)
            Color currentColor = (Color) renderer.getSeriesPaint(i);
            if (currentColor == null) {
                Paint[] defaultColors = { 
                    new Color(100, 149, 237), // Cornflower Blue
                    new Color(255, 99, 71),   // Tomato Red
                    new Color(60, 179, 113),  // Medium Sea Green
                    new Color(138, 43, 226), // Blue Violet
                    new Color(255, 165, 0)    // Orange
                };
                currentColor = (Color) defaultColors[i % defaultColors.length];
            }
            currentColors.add(currentColor);

            // Create label for the category
            JLabel label = new JLabel(category);
            label.setFont(new Font("Arial", Font.PLAIN, 12));
            colorPanel.add(label);

            // Create button to change color
            JButton colorBtn = new JButton();
            colorBtn.setBackground(currentColor);
            colorBtn.setPreferredSize(new Dimension(25, 25));
            colorBtn.addActionListener(e -> {
                Color newColor = JColorChooser.showDialog(
                    colorPanel, 
                    "Choose Color for " + category,
                    currentColors.get(colorButtons.indexOf(colorBtn))
                );
                if (newColor != null) {
                    colorBtn.setBackground(newColor);
                    currentColors.set(colorButtons.indexOf(colorBtn), newColor);
                }
            });
            colorButtons.add(colorBtn);
            colorPanel.add(colorBtn);
        }

        // Show dialog with Apply/Cancel options
        int result = JOptionPane.showConfirmDialog(
            view.getPanel(), 
            colorPanel, 
            "Change Chart Colors",
            JOptionPane.OK_CANCEL_OPTION, 
            JOptionPane.PLAIN_MESSAGE
        );

        // Apply changes if user clicked OK
        if (result == JOptionPane.OK_OPTION) {
            // Update renderer with new colors
            for (int i = 0; i < activeCategories.size(); i++) {
                renderer.setSeriesPaint(i, currentColors.get(i));
            }

            // Refresh the chart display
            view.getChartPanel().repaint();

            JOptionPane.showMessageDialog(
                view.getPanel(), 
                "Colors updated successfully", 
                "Success", JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    /**
     * Handles the Apply button click event.
     * Validates inputs and updates the chart based on current selections.
     */
    private void handleApplyButton() {
        String[] selectedCategories = view.getSelectedCategories();
        int[] selectedYears = view.getSelectedYears();

        // Check if data is available
        if (getIncidentDataList() == null || getIncidentDataList().isEmpty()) {
            view.showErrorMessage("No data available");
            return;
        }

        // Validate projection years if future trends are enabled
        if (view.isFutureTrendsEnabled()) {
            try {
                int years = view.getProjectionYears();
                if (years <= 0 || years > 10) {
                    view.showErrorMessage("Please enter between 1-10 projection years");
                    return;
                }
            } catch (NumberFormatException e) {
                view.showErrorMessage("Please enter a valid number (1-10)");
                return;
            }
        }

        // Update the chart with current selections
        view.updateChart(selectedCategories, selectedYears[0], selectedYears[1]);
    }

    /**
     * Handles year slider change events.
     * Synchronizes the slider values with their corresponding text fields
     * and ensures valid year ranges (start <= end).
     * 
     * @param e The ChangeEvent from the slider
     */
    private void handleYearSliderChange(ChangeEvent e) {
        // Sync slider and text field values
        if (e.getSource() == view.getStartYearSlider()) {
            int value = view.getStartYearSlider().getValue();
            view.getStartYearTextField().setText(String.valueOf(value));

            // Ensure start year doesn't exceed end year
            if (value > view.getEndYearSlider().getValue()) {
                view.getEndYearSlider().setValue(value);
                view.getEndYearTextField().setText(String.valueOf(value));
            }
        } else if (e.getSource() == view.getEndYearSlider()) {
            int value = view.getEndYearSlider().getValue();
            view.getEndYearTextField().setText(String.valueOf(value));

            // Ensure end year doesn't precede start year
            if (value < view.getStartYearSlider().getValue()) {
                view.getStartYearSlider().setValue(value);
                view.getStartYearTextField().setText(String.valueOf(value));
            }
        }
    }

    /**
     * Handles year text field action events (when user presses Enter).
     * Validates input and synchronizes with sliders.
     * 
     * @param e The ActionEvent from the text field
     */
    private void handleYearTextFieldAction(ActionEvent e) {
        try {
            if (e.getSource() == view.getStartYearTextField()) {
                int value = Integer.parseInt(view.getStartYearTextField().getText());
                if (value >= 2008 && value <= 2012) {
                    view.getStartYearSlider().setValue(value);
                    if (value > view.getEndYearSlider().getValue()) {
                        view.getEndYearSlider().setValue(value);
                        view.getEndYearTextField().setText(String.valueOf(value));
                    }
                } else {
                    // Reset to current slider value if invalid
                    view.getStartYearTextField().setText(String.valueOf(view.getStartYearSlider().getValue()));
                }
            } else if (e.getSource() == view.getEndYearTextField()) {
                int value = Integer.parseInt(view.getEndYearTextField().getText());
                if (value >= 2008 && value <= 2012) {
                    view.getEndYearSlider().setValue(value);
                    if (value < view.getStartYearSlider().getValue()) {
                        view.getStartYearSlider().setValue(value);
                        view.getStartYearTextField().setText(String.valueOf(value));
                    }
                } else {
                    // Reset to current slider value if invalid
                    view.getEndYearTextField().setText(String.valueOf(view.getEndYearSlider().getValue()));
                }
            }
        } catch (NumberFormatException ex) {
            // Reset to current slider value if invalid number format
            if (e.getSource() == view.getStartYearTextField()) {
                view.getStartYearTextField().setText(String.valueOf(view.getStartYearSlider().getValue()));
            } else {
                view.getEndYearTextField().setText(String.valueOf(view.getEndYearSlider().getValue()));
            }
        }
    }

    /**
     * Sets the incident data list that this controller will work with.
     * 
     * @param dataList The list of IncidentBasedCrimesModel objects containing crime data
     */
    public static void setIncidentDataList(List<IncidentBasedCrimesModel> dataList) {
        incidentDataList = new ArrayList<>(dataList);
    }

    /**
     * Gets a copy of the incident data list.
     * Returns a new ArrayList to prevent external modification of the internal list.
     * 
     * @return A copy of the incident data list, or empty list if no data is available
     */
    public static List<IncidentBasedCrimesModel> getIncidentDataList() {
        return incidentDataList != null ? new ArrayList<>(incidentDataList) : new ArrayList<>();
    }
}