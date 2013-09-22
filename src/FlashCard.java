import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import javax.swing.*;
import javax.swing.border.Border;

/**
 * @author Atlee
 *
 * A FlashCard JPanel class.
 * Acts as a two sided text flash card that flips over when clicked with the mouse.
 * Also includes a flip method in case you want to flip it with buttons or the keyboard or something.
 *
 * Example:
 *		Container contentPane = frame.getContentPane(); // or whatever container you want to display it in
 *		FlashCard card = new FlashCard(); // defaults to 400 x 300 with a red side A and a blue side B.
 *		contentPane.add(card);
 *		.. setup rest of UI, and then:
 *		card.set("el mar", "the sea", false);
 *		.. and when you want to change it, just call the set method again:
 *		card.set("la silla", "the chair", false);
 *
 * Features:
 * 		Click-to-flip ease of use.
 * 		Auto-downscaling font for long-winded text.
 * 		Sturdy card stock.
 *
 * Feel free to poke around with the Look & Feel constants below to further customize your card.
 */
public class FlashCard extends JPanel {
// Look & Feel constants
	private static final Border OUTER_BORDER = BorderFactory.createEmptyBorder(3, 3, 3, 3);
	private static final Border INNER_BORDER = BorderFactory.createLineBorder(Color.BLACK, 3);
	private static final Border BORDER = BorderFactory.createCompoundBorder(OUTER_BORDER, INNER_BORDER);
	private static final Border BAR_BORDER = BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK);
	private static final int WIDTH = 400; // the actual size will be this plus the borders
	private static final int HEIGHT = 300;
	private static final float BAR_RATIO = 1.0f / 5.0f; // keep this less than 1.0 or beware
	private static final Color COLOR_A = new Color(0.95f, 0.2f, 0.2f);
	private static final Color COLOR_B = new Color(0.2f, 0.3f, 0.95f);
	private static final String FONT_NAME = "Arial Bold";
	private static final int FONT_STYLE = Font.PLAIN;
	private static final int FONT_SIZE = 32; // consider this a suggestion
	private static final Font FONT = new Font(FONT_NAME, FONT_STYLE, FONT_SIZE);

// UI elements
	private JPanel colorBar;
	private JLabel contentLabel;
	private Color colorA, colorB;
	private int width;

// card content
	private String sideA, sideB;

// state
	private boolean showingSideB;

// public methods
	/**
	 * Creates this card with default dimensions and colors.
	 */
	public FlashCard() {
		this(WIDTH, HEIGHT, COLOR_A, COLOR_B);
	}

	/**
	 * Creates this card with specific dimensions and default colors.
	 * @param width Interior width.
	 * @param height Interior height.
	 */
	public FlashCard(int width, int height) {
		this(width, height, COLOR_A, COLOR_B);
	}

	/**
	 * Creates this card with specific dimensions and colors.
	 * @param width Interior width.
	 * @param height Interior height.
	 * @param colorA The Color of side A.
	 * @param colorB The Color of side B.
	 */
	public FlashCard(int width, int height, Color colorA, Color colorB) {
		super(new BorderLayout(0, 0));

		this.width = width;
		this.colorA = colorA;
		this.colorB = colorB;

		setBorder(BORDER);
		addMouseListener(new MouseListenerDefault() { public void mousePressed(MouseEvent e) { handleMousePressed(); }});

		int barHeight = (int)((float)height * BAR_RATIO);

		colorBar = new JPanel();
		colorBar.setPreferredSize(new Dimension(width, barHeight));
		colorBar.setBorder(BAR_BORDER);
		add(colorBar, BorderLayout.NORTH);

		contentLabel = new JLabel("content", JLabel.CENTER);
		contentLabel.setPreferredSize(new Dimension(width, height - barHeight));
		contentLabel.setVerticalAlignment(JLabel.CENTER);
		JPanel contentPanel = new JPanel();
		contentPanel.add(contentLabel);
		add(contentLabel, BorderLayout.CENTER);

		set("", "", false);
	}

	/**
	 * Flip this card over.
	 */
	public void flip() {
		showSide(!showingSideB);
	}

	/**
	 * Getter for checking which side is currently showing.
	 * @return Whether this card is currently showing side B or not.
	 */
	public boolean isShowingSideB() {
		return showingSideB;
	}

	/**
	 * Set the contents of this card and which side is initially showing.
	 * @param sideA The text to show on side A.
	 * @param sideB The text to show on side B.
	 * @param showSideB Whether to show side B or side A.
	 */
	public void set(String sideA, String sideB, boolean showSideB) {
		this.sideA = sideA;
		this.sideB = sideB;
		showSide(showSideB);
	}

	/**
	 * Show a specific side of this card.
	 * @param showSideB Whether to show side B or side A.
	 */
	public void showSide(boolean showSideB) {
		showingSideB = showSideB;
		colorBar.setBackground(showingSideB ? colorB : colorA);

		String text = showingSideB ? sideB : sideA;
		Font font = findBestFontSize(FONT, text, 4, FONT_SIZE, FONT_SIZE);

		contentLabel.setFont(font);
		contentLabel.setText(text);
	}

// private GUI handler methods
	private void handleMousePressed() {
		flip();
	}

// private GUI helper methods
	private boolean isFontSmallEnough(Font font, String text) {
		FontMetrics metrics = new FontMetrics(font) {};
		Rectangle2D bounds = metrics.getStringBounds(text, null);
		int widthInPixels = (int)bounds.getWidth();
		return widthInPixels <= width;
	}

	private Font findBestFontSize(Font font, String text, int minFontSize, int fontSize, int maxFontSize) {
		if(isFontSmallEnough(font, text)) { // the best size is between fontSize and maxFontSize
			if(fontSize >= maxFontSize - 1) return font; // fontSize is the best
			minFontSize = fontSize;
			fontSize = (fontSize + maxFontSize) / 2;
		} else if(fontSize > minFontSize) { // the best size is between minFontSize and maxFontSize
			maxFontSize = fontSize;
			fontSize = (minFontSize + fontSize) / 2;
		} else return font; // can't get any smaller

		font = new Font(FONT_NAME, FONT_STYLE, fontSize);
		return findBestFontSize(font, text, minFontSize, fontSize, maxFontSize);
	}
}
