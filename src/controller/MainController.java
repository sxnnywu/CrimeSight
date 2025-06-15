package controller;

import view.CrimeSightFrame;
import view.CrimeSightStartFrame;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainController {
	private CrimeSightStartFrame startFrame;

	public MainController() {
		startFrame = new CrimeSightStartFrame();
		initControllers();
	}

	private void initControllers() {
		// Start button action
		startFrame.getStartButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new CrimeSightFrame();
				startFrame.dispose();
			}
		});

		// Help button action
		startFrame.getHelpButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(startFrame.getFrame(),
						"This is the start screen for CrimeSight ON.\nClick 'Start' to view charts or 'Help' for more info.",
						"Help", JOptionPane.INFORMATION_MESSAGE);
			}
		});
	}
}