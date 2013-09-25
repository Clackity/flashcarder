import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ProgressBarUI;
import javax.swing.plaf.basic.BasicProgressBarUI;
import javax.swing.plaf.metal.MetalProgressBarUI;
import javax.swing.plaf.synth.SynthProgressBarUI;
import java.awt.*;

/**
 * @author Atlee
 */
public class CardStackPanel extends JPanel {
// Look & Feel constants
	private static final String FONT_NAME = "Arial Bold";
	private static final int FONT_STYLE = Font.PLAIN;
	private static final int FONT_SIZE = 14;
	private static final Font FONT = new Font(FONT_NAME, FONT_STYLE, FONT_SIZE);

// UI elements
	private JLabel countLabel;
	private JProgressBar progressBar;
	private JLabel titleLabel;

// public methods
	public CardStackPanel(String title, Color meterColor) {
		super(new BorderLayout(0, 0));
		//setBorder(BorderFactory.createEmptyBorder(0, 4, 2, 4));

		titleLabel = new JLabel(title, JLabel.CENTER);
		titleLabel.setFont(FONT);
		add(titleLabel, BorderLayout.NORTH);

		progressBar = new JProgressBar(JProgressBar.VERTICAL);
		progressBar.setUI(new BasicProgressBarUI());
		progressBar.setForeground(meterColor);
		progressBar.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		progressBar.setMinimum(0);
		add(progressBar, BorderLayout.CENTER);

		countLabel = new JLabel("0", JLabel.CENTER);
		countLabel.setFont(FONT);
		add(countLabel, BorderLayout.SOUTH);
	}

	public void setCapacity(int capacity) {
		progressBar.setMaximum(capacity);
	}

	public void setCurrentCount(int count) {
		progressBar.setValue(count);
		countLabel.setText(Integer.toString(count));
	}
}
