
import java.awt.Color;
import java.awt.Rectangle;
import javax.swing.JFrame;

public class BlackBox extends JFrame {
	
	public boolean isActive = false;
	private Color color;
	
	
	public void create(Rectangle monitor) {
		setType(javax.swing.JFrame.Type.UTILITY);
		setLocation(monitor.getLocation());
		setSize(monitor.getSize());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setUndecorated(true);
		setVisible(false);
	}
	
	public void setColor(Color newColor) {
		color = newColor;
		getContentPane().setBackground(color);
		
	}
	
	public void setActive(Boolean active) {
		isActive = active;
		setVisible(isActive);
	}
	
}
