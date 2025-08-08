import java.awt.*;
import java.util.prefs.Preferences;
import javax.swing.*;
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
		
		// Panel for sliders
		JPanel sliderPanel = new JPanel(new GridLayout(5, 2, 10, 10));
		
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
		
		sliderPanel.add(new JLabel("Inverse Opacity:"));
		sliderPanel.add(inverseOpacityCheckbox);
		
		sliderPanel.add(new JLabel("Main Opacity:"));
		sliderPanel.add(mainOppacitySlider);
		sliderPanel.add(new JLabel("Secondary Opacity:"));
		sliderPanel.add(secondaryOpacitySlider);
		sliderPanel.add(new JLabel("Text Opacity:"));
		sliderPanel.add(textOppacitySlider);
		sliderPanel.add(new JLabel("Hover Opacity Add:"));
		sliderPanel.add(hoverOpacityMultSlider);
		
		
		add(sliderPanel, BorderLayout.CENTER);
		
		setSize(600, 500);
		setLocationRelativeTo(null); // Center on screen
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
}
