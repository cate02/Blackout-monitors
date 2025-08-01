// author mostly chatgpt

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingUtilities;

public class MonitorWatcher {
	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	private DisplaySnapshot lastSnapshot;
	private final Runnable onChange;
	
	public MonitorWatcher(Runnable onChange) {
		this.onChange = onChange;
		this.lastSnapshot = captureSnapshot();
	}
	
	public void start() {
		scheduler.scheduleAtFixedRate(() -> {
			DisplaySnapshot current = captureSnapshot();
			if (!current.equals(lastSnapshot)) {
				lastSnapshot = current;
				SwingUtilities.invokeLater(onChange);
			}
		}, 0, 1, TimeUnit.SECONDS); // adjust interval as needed
	}
	
	public void stop() {
		scheduler.shutdownNow();
	}
	
	private static DisplaySnapshot captureSnapshot() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] devices = ge.getScreenDevices();
		List<Rectangle> bounds = new ArrayList<>();
		List<DisplayMode> modes = new ArrayList<>();
		for (GraphicsDevice d : devices) {
			bounds.add(d.getDefaultConfiguration().getBounds());
			modes.add(d.getDisplayMode());
		}
		return new DisplaySnapshot(bounds, modes);
	}
	
	// simple value object for equality
	private record DisplaySnapshot(List<Rectangle> bounds, List<DisplayMode> modes) {
		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			DisplaySnapshot that = (DisplaySnapshot) o;
			if (bounds.size() != that.bounds.size())
				return false;
			for (int i = 0; i < bounds.size(); i++) {
				if (!bounds.get(i).equals(that.bounds.get(i)))
					return false;
				if (!modes.get(i).equals(that.modes.get(i)))
					return false;
			}
			return true;
		}
	}
}
