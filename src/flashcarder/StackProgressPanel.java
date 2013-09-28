package flashcarder;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * @author Atlee
 * A customized JPanel to act similar to a vertical progress bar.
 * While a JProgressBar almost works, there is no way to customize the
 * bar rendering, so I've made this instead.
 */
public class StackProgressPanel extends JPanel {
// Look & Feel constants
	private static final Border BORDER = BorderFactory.createLineBorder(Color.BLACK, 1);

// UI elements
	private JPanel top;
	private JPanel bottom;

// bar value
	private int max;
	private int current;

// state
	private Rectangle2D size;
	private Insets insets;
	private Dimension interior;

// public methods
	public StackProgressPanel(Color barColor) {
		max = 0;
		current = 0;

		setLayout(null);

		setBorder(BORDER);

		top = new JPanel();
		add(top);

		bottom = new JPanel();
		bottom.setBackground(barColor);
		add(bottom);
	}

	public void setMaximum(int max) {
		int newMax = max >= 0 ? max : 0;
		if(newMax != this.max) {
			this.max = newMax;
			updateBarSize();
		}
	}

	public void setValue(int current) {
		int newCurrent = current < 0 ? 0 : current > max ? max : current;
		if(newCurrent != this.current) {
			this.current = newCurrent;
			updateBarSize();
		}
	}

// private GUI handler methods
	private void updateBarSize() {
		float topPortion = 1.0f - (max == 0 ? 0.0f : (float)current / (float)max);
		Dimension topSize = new Dimension(interior.width, (int)((float)interior.height * topPortion));
		top.setSize(topSize);
		top.setLocation(insets.left, insets.top);
		Dimension bottomSize = new Dimension(interior.width, interior.height - topSize.height);
		bottom.setSize(bottomSize);
		bottom.setLocation(insets.left, insets.top + topSize.height);
	}

	private void updateDimension() {
		Rectangle2D newSize = getBounds();
		if(size == null
			|| newSize.getWidth() != size.getWidth()
			|| newSize.getHeight() != size.getHeight()
		) {
			size = newSize;
			insets = getInsets();
			interior = new Dimension(
				(int)size.getWidth() - insets.left - insets.right
				,(int)size.getHeight() - insets.top - insets.bottom
			);
			updateBarSize();
		}
	}

// Component overrides
	@Override
	public void paint(Graphics g) {
		updateDimension();
		super.paint(g);
	}
}
