package view;

import java.awt.Color;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;

import model.Location;

// Angel Zhan 

// This class a new JFrame that lets the user to change the color of specific infromation in the pie chart on LocationIncidentFrame
public class ChangeColorFrame {

	private static JFrame changeColorFrame;

	@SuppressWarnings("unused")
	private JFreeChart chart;

	@SuppressWarnings("unused")
	private final static int SPACING = 40; // the spacing in between each JLabel

	private static Border spacing = BorderFactory.createLineBorder(Color.WHITE, 10);
	@SuppressWarnings("unused")
	private static Location[] dataArray;

	private static JLabel[] nameLabels;
	private static JPanel namePanel;

	private static JButton[] colorButtons;
	private static JScrollPane scrollPane;

	@SuppressWarnings("unused")
	private static PiePlot plot = LocationIncidentFrame.getPlot(); // update your type of plot here
	private static JButton updateInformation;

//=========== **** Change your own array type here in the constructor ***** ================
	@SuppressWarnings("static-access")
	public ChangeColorFrame(JFreeChart chart, Location[] dataArray) {

		this.chart = chart;
		this.dataArray = dataArray;
		changeColorFrame = new JFrame("Change Colors");
		changeColorFrame.setSize(600, 700);
		changeColorFrame.setVisible(true);
		changeColorFrame.setLayout(null);
		changeColorFrame.getContentPane().setBackground(Color.WHITE);

		updateInformation = new JButton("Update Information");

		updateInformation.setFont(new Font("Calibri", Font.PLAIN, 14));
		updateInformation.setBackground(LocationIncidentFrame.getNavyBlue());
		updateInformation.setForeground(Color.WHITE);
		updateInformation.setBounds(240, 600, 130, 30);

/// action listener for update Information button, closes the change color Jframe 		
		updateInformation.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				changeColorFrame.dispose();
			}

		});
/////////////////////		

		// creates all the panels on the JFrame
		createPanels();

	}

	private void createPanels() { // creates all the panels and everything inside (buttons etc)

		namePanel = new JPanel();
		namePanel.setBackground(Color.WHITE);

		namePanel.setBounds(0, 0, 30, 500);
		createLocationLabels();

		scrollPane = new JScrollPane(namePanel);
		namePanel.setLayout(new GridLayout(20, 2, 10, 10));

		// add the panels to the JFrame
		scrollPane.setBounds(50, 50, 500, 500);
		scrollPane.setBorder(spacing);
		updateInformation.setBounds(200, 600, 200, 30);

		scrollPane.setBackground(Color.WHITE);
		changeColorFrame.add(scrollPane);
		changeColorFrame.add(updateInformation);

	}

	@SuppressWarnings("deprecation")
	public static void createLocationLabels() {

		nameLabels = new JLabel[LocationIncidentFrame.getCurrentData().size()]; // initialize the JLabel array

		colorButtons = new JButton[LocationIncidentFrame.getCurrentData().size()];
		for (int i = 0; i < LocationIncidentFrame.getCurrentData().size(); i++) { // this loop creates all of the labels and the buttons 

/// **** Change "getLocation()" method to your method******* ====================

			// creates labels and buttons for each location 
			nameLabels[i] = new JLabel(LocationIncidentFrame.getCurrentData().get(i).getLocation()); 

			nameLabels[i].setBorder(spacing);

			

			colorButtons[i] = new JButton("");

			
			// Retrieves the color from the pie plot and sets it as the button color for user 
			colorButtons[i].setBackground((Color) LocationIncidentFrame.getPlot().getSectionPaint(i));

			nameLabels[i].setFont(new Font("Calibri", Font.PLAIN, 14));
			
			// adds and updates the panel
			namePanel.add(nameLabels[i]);
			namePanel.add(colorButtons[i]);
			namePanel.repaint();
			namePanel.revalidate();
			

			
			// add action listener to each new color button made 
			colorButtons[i].addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					int clickedIndex = Arrays.asList(colorButtons).indexOf(e.getSource()); // finds which index button was clicked 
					
					// opens color chooser 
					Color color = JColorChooser.showDialog(changeColorFrame, "Select a color", Color.RED);

					// update the chart and the button to the color the user chose 
					LocationIncidentFrame.getPlot().setSectionPaint(clickedIndex, color);
					colorButtons[clickedIndex].setBackground(color);
					System.out.println(clickedIndex);

					LocationIncidentFrame.getPieChartPanel().revalidate();
					LocationIncidentFrame.getPieChartPanel().repaint();

				}

			});

		}

	}

}