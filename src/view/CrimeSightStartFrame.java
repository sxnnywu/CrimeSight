package view;

import javax.swing.*;
import java.awt.*;

/**
 * The CrimeSightStartFrame class represents the start screen of the CrimeSight
 * ON application. It provides the initial interface with logo, start button,
 * and help button.
 */
public class CrimeSightStartFrame {

	// Main frame for the start screen
	private JFrame startFrame;

	// Buttons for user interaction
	private JButton startButton, helpButton;

	// Label for displaying the application logo
	private JLabel logoLabel;

	// Color definitions for UI elements
	private Color navyBlue = Color.decode("#000080"); // Navy blue color for buttons
	private Color selectedTabPink = Color.decode("#f8d0c7"); // Pink background color

	/**
	 * Constructs the start frame and initializes all UI components.
	 */
	public CrimeSightStartFrame() {
		// Initialize the main application frame
		startFrame = new JFrame("CrimeSight ON - Start");

		// Set frame properties
		startFrame.setSize(1366, 768); // Set window size to 1366x768 pixels
		startFrame.setIconImage(new ImageIcon("images/logo.png").getImage()); // Set window icon
		startFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close application on window close
		startFrame.getContentPane().setBackground(selectedTabPink); // Set background color
		startFrame.setLayout(null); // Use absolute positioning for components

		// Initialize and configure the logo
		ImageIcon logoIcon = new ImageIcon("images/logo.png"); // Load logo image
		logoLabel = new JLabel(logoIcon); // Create label with logo
		logoLabel.setBounds(550, 100, 250, 325); // Position and size the logo
		startFrame.add(logoLabel); // Add logo to frame

		// Initialize and configure the Help button
		helpButton = new JButton("Help");
		helpButton.setFont(new Font("Calibri", Font.ITALIC, 25)); // Set font
		helpButton.setBackground(navyBlue); // Set background color
		helpButton.setForeground(Color.WHITE); // Set text color
		helpButton.setFocusPainted(false); // Remove focus border
		helpButton.setBounds(558, 500, 250, 50); // Position and size
		startFrame.add(helpButton); // Add to frame

		// Initialize and configure the Start button
		startButton = new JButton("Start");
		startButton.setFont(new Font("Calibri", Font.ITALIC, 25)); // Set font
		startButton.setBackground(navyBlue); // Set background color
		startButton.setForeground(Color.WHITE); // Set text color
		startButton.setFocusPainted(false); // Remove focus border
		startButton.setBounds(558, 575, 250, 50); // Position and size
		startFrame.add(startButton); // Add to frame

		// Center the frame on screen and make it visible
		startFrame.setLocationRelativeTo(null); // Center on screen
		startFrame.setVisible(true); // Make frame visible
	}

	// Getters for the controller to access the components
	public JButton getStartButton() {
		return startButton;
	}

	public JButton getHelpButton() {
		return helpButton;
	}

	public JFrame getFrame() {
		return startFrame;
	}

	public void dispose() {
		startFrame.dispose();
	}
}
