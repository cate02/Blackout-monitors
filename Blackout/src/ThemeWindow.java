import java.awt.*;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeListener;

public class ThemeWindow extends JFrame {
	
	// Holder for singleton instance
	private static class ThemeWindowHolder {
		private static ThemeWindow INSTANCE = null;
	}
	
	private JColorChooser colorChooser;
	private static JSlider mainOppacitySlider = new JSlider(0, 20, 10);
	private static JSlider textOppacitySlider = new JSlider(0, 100, 30);
	private static JSlider hoverOpacityMultSlider = new JSlider(10, 100, 30);
	private static JSlider secondaryOpacitySlider = new JSlider(0, 20, 15);
	public static boolean inverseOppacity = false; // Flag to toggle inverse opacity
	private static JCheckBox inverseOpacityCheckbox = new JCheckBox("Inverse Opacity", inverseOppacity);
	
	private static Color mainColor = Color.BLACK; // Default main color
	
	private static Preferences preferences;
	
	public ThemeWindow() {
		loadPrefs();
		
		setSize(preferences.getInt("themeWidth", 600), preferences.getInt("themeHeight", 500));
		if (Blackout.validMonitorSpace(preferences.getInt("themeScreenX", 0), preferences.getInt("themeScreenY", 0))) {
			setLocation(preferences.getInt("themeScreenX", 0), preferences.getInt("themeScreenY", 0));
			
		} else {
			setLocationRelativeTo(null);
		}
		
		addComponentListener(new java.awt.event.ComponentAdapter() {
			@Override
			public void componentMoved(java.awt.event.ComponentEvent e) {
				preferences.putInt("themeScreenX", getX());
				preferences.putInt("themeScreenY", getY());
			}
			
			@Override
			public void componentResized(java.awt.event.ComponentEvent e) {
				preferences.putInt("themeWidth", getWidth());
				preferences.putInt("themeHeight", getHeight());
			}
		});
		
		
		
		
		Blackout.setInverseOpacity(inverseOppacity);
		setTitle("Color Picker with Sliders");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);
		setLayout(new BorderLayout());
		
		// Color chooser
		colorChooser = new JColorChooser(Color.BLACK);
		System.out.println("Main color: " + mainColor);
		colorChooser.setColor(mainColor);
		add(colorChooser, BorderLayout.NORTH);
		
		
		
		for (AbstractColorChooserPanel panel : colorChooser.getChooserPanels()) {
			switch (panel.getDisplayName()) {
				case "Swatches":
					colorChooser.removeChooserPanel(panel);
					break;
				case "HSV":
					colorChooser.removeChooserPanel(panel);
					break;
			}
			// if (panel.getDisplayName().equals("Swatches")) {
			// colorChooser.removeChooserPanel(panel);
			// }
		}
		colorChooser.setPreviewPanel(new JPanel());
		
		
		
		
		ChangeListener sliderListener = e -> {
			// Color newMainColor=colorChooser.getColor();
			// System.out.println("ccc " + mainColor+" "+newMainColor);
			// mainColor = newMainColor;
			float secondaryOpacity = secondaryOpacitySlider.getValue();
			float mainOpacity = mainOppacitySlider.getValue();
			float textOpacity = textOppacitySlider.getValue();
			float hoverOpacityMult = hoverOpacityMultSlider.getValue();
			hoverOpacityMult /= 10f;
			
			Blackout.setNewColors(secondaryOpacity, mainOpacity, textOpacity, hoverOpacityMult);
			
			preferences.putFloat("secondaryOpacity", secondaryOpacity);
			preferences.putFloat("mainOpacity", mainOpacity);
			preferences.putFloat("textOpacity", textOpacity);
			preferences.putFloat("hoverOppacityAdd", hoverOpacityMult);
		};
		// colorChooser.getSelectionModel().addChangeListener(sliderListener);
		colorChooser.getSelectionModel().addChangeListener(ev -> {
			mainColor = colorChooser.getColor();
			Blackout.setNewMainColor(mainColor);
			preferences.putInt("mainColor", mainColor.getRGB());
			System.out.println("Main color set to: " + mainColor);
			// sync mainColor / preferences here
		});
		mainOppacitySlider.addChangeListener(sliderListener);
		textOppacitySlider.addChangeListener(sliderListener);
		hoverOpacityMultSlider.addChangeListener(sliderListener);
		secondaryOpacitySlider.addChangeListener(sliderListener);
		
		inverseOpacityCheckbox.addActionListener(e -> {
			inverseOppacity = inverseOpacityCheckbox.isSelected();
			Blackout.setInverseOpacity(inverseOppacity);
			preferences.putBoolean("inverseOpacity", inverseOppacity);
		});
		
		
		
		
		JPanel sliderPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 10, 5, 10); // top, left, bottom, right padding
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		
		int row = 0;
		
		// Row 1: Inverse Opacity (label + checkbox)
		gbc.gridx = 0;
		gbc.gridy = row;
		gbc.anchor = GridBagConstraints.WEST;
		sliderPanel.add(new JLabel("Inverse Opacity:"), gbc);
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.EAST;
		sliderPanel.add(inverseOpacityCheckbox, gbc);
		row++;
		
		// Row 2: Main Opacity
		gbc.gridx = 0;
		gbc.gridy = row;
		gbc.anchor = GridBagConstraints.WEST;
		sliderPanel.add(new JLabel("Main Opacity:"), gbc);
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.EAST;
		sliderPanel.add(mainOppacitySlider, gbc);
		row++;
		
		// Row 3: Secondary Opacity
		gbc.gridx = 0;
		gbc.gridy = row;
		gbc.anchor = GridBagConstraints.WEST;
		sliderPanel.add(new JLabel("Secondary Opacity:"), gbc);
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.EAST;
		sliderPanel.add(secondaryOpacitySlider, gbc);
		row++;
		
		// Row 4: Text Opacity
		gbc.gridx = 0;
		gbc.gridy = row;
		gbc.anchor = GridBagConstraints.WEST;
		sliderPanel.add(new JLabel("Text Opacity:"), gbc);
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.EAST;
		sliderPanel.add(textOppacitySlider, gbc);
		row++;
		
		// Row 5: Hover Opacity Add
		gbc.gridx = 0;
		gbc.gridy = row;
		gbc.anchor = GridBagConstraints.WEST;
		sliderPanel.add(new JLabel("Hover Opacity Add:"), gbc);
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.EAST;
		sliderPanel.add(hoverOpacityMultSlider, gbc);
		
		
		add(sliderPanel, BorderLayout.CENTER);
		
		setVisible(true);
		
		// on close close
		// Ensure only one instance
		if (ThemeWindowHolder.INSTANCE != null) {
			ThemeWindowHolder.INSTANCE.toFront();
			dispose();
			return;
		}
		ThemeWindowHolder.INSTANCE = this;
		
		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent e) {
				ThemeWindowHolder.INSTANCE = null;
				dispose();
			}
			
			@Override
			public void windowClosed(java.awt.event.WindowEvent e) {
				ThemeWindowHolder.INSTANCE = null;
			}
		});
	}
	
	public static void loadPrefs() {
		preferences = Preferences.userNodeForPackage(Blackout.class);
		// Mask to 0xFFFFFFFF to ensure only 8 hex digits (ARGB)
		int mainColorRGB = preferences.getInt("mainColor", Color.BLACK.getRGB());
		mainColor = new Color(mainColorRGB, true);
		float secondaryOpacity = preferences.getFloat("secondaryOpacity", 1.0f);
		float mainOpacity = preferences.getFloat("mainOpacity", 1.0f);
		float textOpacity = preferences.getFloat("textOpacity", 0.3f);
		float hoverOppacityAdd = preferences.getFloat("hoverOppacityAdd", 0.3f);
		inverseOppacity = preferences.getBoolean("inverseOpacity", false);
		
		
		mainOppacitySlider.setValue((int) (mainOpacity));
		textOppacitySlider.setValue((int) (textOpacity));
		hoverOpacityMultSlider.setValue((int) (hoverOppacityAdd * 10));
		secondaryOpacitySlider.setValue((int) (secondaryOpacity));
		
		inverseOpacityCheckbox.setSelected(inverseOppacity);
		
		Blackout.setInverseOpacity(inverseOppacity);
		Blackout.setNewColors(secondaryOpacity, mainOpacity, textOpacity, hoverOppacityAdd);
		Blackout.setNewMainColor(mainColor);
	}
	
	public static Color getMainColor() {
		return mainColor;
	}
	
	public static void saveLocation(int x, int y) {
		preferences.putInt("themeScreenX", x);
		preferences.putInt("themeScreenY", y);
	}
}
