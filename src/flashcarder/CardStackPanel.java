package flashcarder;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * @author Atlee
 */
public class CardStackPanel extends JPanel {
// Look & Feel constants
	private static final int BORDER_LINE_WIDTH = 3;
	private static final Color BORDER_LINE_COLOR = Color.BLACK;
	private static final int BORDER_SPACE_WIDTH = 2;
	private static final int BORDER_WIDTH = BORDER_LINE_WIDTH + BORDER_SPACE_WIDTH;
	private static final Border OUTER_BORDER = BorderFactory.createLineBorder(BORDER_LINE_COLOR, BORDER_LINE_WIDTH);
	private static final Border INNER_BORDER = BorderFactory.createEmptyBorder(BORDER_SPACE_WIDTH, BORDER_SPACE_WIDTH, BORDER_SPACE_WIDTH, BORDER_SPACE_WIDTH);
	private static final Border ACTIVE_BORDER = BorderFactory.createCompoundBorder(OUTER_BORDER, INNER_BORDER);
	private static final Border INACTIVE_BORDER = BorderFactory.createEmptyBorder(BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH);
	private static final String FONT_NAME = "Arial Bold";
	private static final int FONT_STYLE = Font.PLAIN;
	private static final int FONT_SIZE = 14;
	private static final Font FONT = new Font(FONT_NAME, FONT_STYLE, FONT_SIZE);

// UI elements
	private JLabel countLabel;
	private StackProgressPanel progressBar;
	private JLabel titleLabel;

// public methods
	public CardStackPanel(String title, Color meterColor, boolean active) {
		super(new BorderLayout(0, 0));

		setActive(active);

		titleLabel = new JLabel(title, JLabel.CENTER);
		titleLabel.setFont(FONT);
		add(titleLabel, BorderLayout.NORTH);

		progressBar = new StackProgressPanel(meterColor);
		add(progressBar, BorderLayout.CENTER);

		countLabel = new JLabel("0", JLabel.CENTER);
		countLabel.setFont(FONT);
		add(countLabel, BorderLayout.SOUTH);
	}

	public void setActive(boolean isActive) {
		setBorder(isActive ? ACTIVE_BORDER : INACTIVE_BORDER);
	}

	public void setCapacity(int capacity) {
		progressBar.setMaximum(capacity);
	}

	public void setCurrentCount(int count) {
		progressBar.setValue(count);
		countLabel.setText(Integer.toString(count));
	}
}
