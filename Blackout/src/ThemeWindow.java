import java.awt.*;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.event.ChangeListener;

public class ThemeWindow extends JFrame {
	
	private JColorChooser colorChooser;
	private static JSlider mainOppacitySlider = new JSlider(0, 20, 10);
	private static JSlider textOppacitySlider = new JSlider(0, 100, 30);
	private static JSlider hoverOppacityAddSlider = new JSlider(10, 100, 30);
	private static JSlider secondaryColorMultSlider = new JSlider(0, 200, 100);
	public static boolean inverseOppacity = false; // Flag to toggle inverse opacity
	private static JCheckBox inverseOpacityCheckbox = new JCheckBox("Inverse Opacity", inverseOppacity);
	
	private static Color mainColor = Color.BLACK; // Default main color
	
	private static Preferences preferences;
	
	public ThemeWindow() {
		
		loadPrefs();
		
		
		Blackout.setInverseOpacity(inverseOppacity);
		setTitle("Color Picker with Sliders");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		
		// Color chooser
		colorChooser = new JColorChooser(Color.BLACK);
		System.out.println("Main color: " + mainColor);
		colorChooser.setColor(mainColor);
		add(colorChooser, BorderLayout.NORTH);
		
		// Panel for sliders
		JPanel sliderPanel = new JPanel(new GridLayout(5, 2, 10, 10));
		
		ChangeListener sliderListener = e -> {
			System.out.println(mainColor);
			mainColor = colorChooser.getColor();
			System.out.println("aaaa: " + mainColor);
			float secondaryColorMult = secondaryColorMultSlider.getValue() / 100f;
			float mainOpacity = mainOppacitySlider.getValue();
			float textOpacity = textOppacitySlider.getValue();
			float hoverOppacityAdd = hoverOppacityAddSlider.getValue();
			hoverOppacityAdd /= 10f;
			
			Blackout.setNewColors(mainColor, secondaryColorMult, mainOpacity, textOpacity, hoverOppacityAdd);
			preferences.putInt("mainColor", mainColor.getRGB());
			System.out.println("Main color set to: " + mainColor);
			preferences.putFloat("secondaryColorMult", secondaryColorMult);
			preferences.putFloat("mainOpacity", mainOpacity);
			preferences.putFloat("textOpacity", textOpacity);
			preferences.putFloat("hoverOppacityAdd", hoverOppacityAdd);
		};
		colorChooser.getSelectionModel().addChangeListener(sliderListener);
		mainOppacitySlider.addChangeListener(sliderListener);
		textOppacitySlider.addChangeListener(sliderListener);
		hoverOppacityAddSlider.addChangeListener(sliderListener);
		secondaryColorMultSlider.addChangeListener(sliderListener);
		
		inverseOpacityCheckbox.addActionListener(e -> {
			inverseOppacity = inverseOpacityCheckbox.isSelected();
			Blackout.setInverseOpacity(inverseOppacity);
			preferences.putBoolean("inverseOpacity", inverseOppacity);
		});
		// when colorpicker tab changed (hsv rgb etc) remember preference tab
		colorChooser.getSelectionModel().addChangeListener(e -> {
			Color selectedColor = colorChooser.getColor();
			mainColor = selectedColor;
			System.out.println("Selected color: " + selectedColor);
			preferences.putInt("mainColor", selectedColor.getRGB());
		});
		
		sliderPanel.add(new JLabel("Inverse Opacity:"));
		sliderPanel.add(inverseOpacityCheckbox);
		
		sliderPanel.add(new JLabel("Main Opacity:"));
		sliderPanel.add(mainOppacitySlider);
		sliderPanel.add(new JLabel("Text Opacity:"));
		sliderPanel.add(textOppacitySlider);
		sliderPanel.add(new JLabel("Hover Opacity Add:"));
		sliderPanel.add(hoverOppacityAddSlider);
		sliderPanel.add(new JLabel("Secondary Color Multiplier:"));
		sliderPanel.add(secondaryColorMultSlider);
		
		add(sliderPanel, BorderLayout.CENTER);
		
		setSize(600, 500);
		setLocationRelativeTo(null); // Center on screen
		setVisible(true);
	}
	
	public static void loadPrefs() {
		preferences = Preferences.userNodeForPackage(Blackout.class);
		// Mask to 0xFFFFFFFF to ensure only 8 hex digits (ARGB)
		int mainColorRGB = preferences.getInt("mainColor", Color.BLACK.getRGB());
		mainColor = new Color(mainColorRGB, true);
		float secondaryColorMult = preferences.getFloat("secondaryColorMult", 1.0f);
		float mainOpacity = preferences.getFloat("mainOpacity", 1.0f);
		float textOpacity = preferences.getFloat("textOpacity", 0.3f);
		float hoverOppacityAdd = preferences.getFloat("hoverOppacityAdd", 0.3f);
		inverseOppacity = preferences.getBoolean("inverseOpacity", false);
		
		
		mainOppacitySlider.setValue((int) (mainOpacity));
		textOppacitySlider.setValue((int) (textOpacity));
		hoverOppacityAddSlider.setValue((int) (hoverOppacityAdd * 10));
		secondaryColorMultSlider.setValue((int) (secondaryColorMult * 100));
		
		inverseOpacityCheckbox.setSelected(inverseOppacity);
		
		Blackout.setInverseOpacity(inverseOppacity);
		Blackout.setNewColors(mainColor, secondaryColorMult, mainOpacity, textOpacity, hoverOppacityAdd);
	}
}
