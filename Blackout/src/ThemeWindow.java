import java.awt.*;
import javax.swing.*;
import javax.swing.event.ChangeListener;

public class ThemeWindow extends JFrame {
	
	private JColorChooser colorChooser;
	private JSlider mainOppacitySlider = new JSlider(0, 20, 10);
	private JSlider textOppacitySlider = new JSlider(0, 100, 10);
	private JSlider hoverOppacityAddSlider = new JSlider(0, 100, 20);
	private JSlider secondaryColorMultSlider = new JSlider(0, 200, 100);
	
	public ThemeWindow() {
		setTitle("Color Picker with Sliders");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		
		// Color chooser
		colorChooser = new JColorChooser(Color.BLACK);
		add(colorChooser, BorderLayout.NORTH);
		
		// Panel for sliders
		JPanel sliderPanel = new JPanel(new GridLayout(4, 2, 10, 10));
		
		ChangeListener sliderListener = e -> {
			Color selectedColor = colorChooser.getColor();
			float secondaryColorMult = secondaryColorMultSlider.getValue() / 100f;
			float mainOpacity = mainOppacitySlider.getValue();
			float textOpacity = textOppacitySlider.getValue();
			float hoverOppacityAdd = hoverOppacityAddSlider.getValue();
			
			System.out.println("Selected Color: " + selectedColor);
			Blackout.setNewColors(selectedColor, secondaryColorMult, mainOpacity, textOpacity, hoverOppacityAdd);
		};
		colorChooser.getSelectionModel().addChangeListener(sliderListener);
		mainOppacitySlider.addChangeListener(sliderListener);
		textOppacitySlider.addChangeListener(sliderListener);
		hoverOppacityAddSlider.addChangeListener(sliderListener);
		secondaryColorMultSlider.addChangeListener(sliderListener);
		
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
}
