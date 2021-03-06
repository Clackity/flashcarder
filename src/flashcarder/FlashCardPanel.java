package flashcarder;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import javax.swing.*;
import javax.swing.border.Border;

/**
 * @author Atlee
 *
 * A FlashCardPanel JPanel class.
 * Acts as a two sided text flash card that flips over when clicked with the mouse.
 * Also includes a flip method in case you want to flip it with buttons or the keyboard or something.
 *
 * Todo:
 * 		deal with resizing events by adjusting bar size and font size, if necessary
 *
 * Example:
 *		Container contentPane = frame.getContentPane(); // or whatever container you want to display it in
 *		FlashCardPanel card = new FlashCardPanel(); // defaults to 400 x 300 with a red side A and a blue side B.
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
public class FlashCardPanel extends JPanel {
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
	private static final String FONT_NAME = Font.SERIF;//"Arial Bold";
	private static final int FONT_STYLE = Font.BOLD;
	private static final int FONT_SIZE_MIN = 8; // these take effect in isFontSmallEnough(...)
	private static final int FONT_SIZE_MAX = 120;
	private static final Font FONT = new Font(FONT_NAME, FONT_STYLE, FONT_SIZE_MAX);
	private static final float TEXT_MARGIN_RATIO = 1.0f / 50.0f; // minimum space on left/right of text

// UI elements
	private JPanel colorBar;
	private JLabel contentLabel;
	private Color colorA, colorB;

// card content
	private String sideA, sideB;

// state
	private boolean showingSideB;
	private Rectangle2D totalSize;
	private Dimension interiorSize;

// public methods
	/**
	 * Creates this card with default dimensions and colors.
	 */
	public FlashCardPanel() {
		this(WIDTH, HEIGHT, COLOR_A, COLOR_B);
	}

	/**
	 * Creates this card with specific dimensions and default colors.
	 * @param width Interior width.
	 * @param height Interior height.
	 */
	public FlashCardPanel(int width, int height) {
		this(width, height, COLOR_A, COLOR_B);
	}

	/**
	 * Creates this card with specific dimensions and colors.
	 * @param width Interior width.
	 * @param height Interior height.
	 * @param colorA The Color of side A.
	 * @param colorB The Color of side B.
	 */
	public FlashCardPanel(int width, int height, Color colorA, Color colorB) {
		setLayout(null);
		setPreferredSize(new Dimension(width, height));

		this.colorA = colorA;
		this.colorB = colorB;

		setBorder(BORDER);
		addMouseListener(new MouseListenerDefault() { public void mousePressed(MouseEvent e) { handleMousePressed(); }});

		colorBar = new JPanel();
		colorBar.setBorder(BAR_BORDER);
		add(colorBar);

		contentLabel = new JLabel("", JLabel.CENTER);
		contentLabel.setAlignmentX(CENTER_ALIGNMENT);
		JPanel contentPanel = new JPanel();
		contentPanel.add(contentLabel);
		add(contentLabel);

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
		contentLabel.setText(text);
		contentLabel.setFont(findBestFontSize(FONT, text));
	}

// private GUI handler methods
	private void handleMousePressed() {
		flip();
	}

	private void updateDimension() {
		Rectangle2D newSize = getBounds();
		if(totalSize == null
			|| newSize.getWidth() != totalSize.getWidth()
			|| newSize.getHeight() != totalSize.getHeight()
		) {
			totalSize = newSize;
			Insets insets = getInsets();
			interiorSize = new Dimension(
				(int)totalSize.getWidth() - insets.left - insets.right
				,(int)totalSize.getHeight() - insets.top - insets.bottom
			);
			Dimension barSize = new Dimension(interiorSize.width, (int)((float)interiorSize.height * BAR_RATIO));
			colorBar.setSize(barSize);
			colorBar.setLocation(insets.left, insets.top);
			Dimension contentSize = new Dimension(interiorSize.width, interiorSize.height - barSize.height);
			contentLabel.setSize(contentSize);
			contentLabel.setLocation(insets.left, insets.top + barSize.height);
			contentLabel.setFont(findBestFontSize(FONT, contentLabel.getText()));
		}
	}

	private boolean isFontSmallEnough(Font font, String text) {
		FontMetrics metrics = new FontMetrics(font) {};
		Rectangle2D textBounds = metrics.getStringBounds(text, null);
		Dimension panelBounds = contentLabel.getSize();
		return textBounds.getWidth() <= panelBounds.getWidth() * (1.0f - TEXT_MARGIN_RATIO)
			&& textBounds.getHeight() <= panelBounds.getHeight()
			&& textBounds.getHeight() * 8.0 <= panelBounds.getWidth();
	}

	private Font findBestFontSize(Font font, String text) {
		return recursiveFindBestFontSize(font, text, FONT_SIZE_MIN, FONT_SIZE_MAX, FONT_SIZE_MAX);
	}

	private Font recursiveFindBestFontSize(Font font, String text, int minFontSize, int fontSize, int maxFontSize) {
		if(isFontSmallEnough(font, text)) { // the best size is between fontSize and maxFontSize
			if(fontSize >= maxFontSize - 1) return font; // fontSize is the best
			minFontSize = fontSize;
			fontSize = (fontSize + maxFontSize) / 2;
		} else if(fontSize > minFontSize) { // the best size is between minFontSize and maxFontSize
			maxFontSize = fontSize;
			fontSize = (minFontSize + fontSize) / 2;
		} else return font; // can't get any smaller

		font = new Font(FONT_NAME, FONT_STYLE, fontSize);
		return recursiveFindBestFontSize(font, text, minFontSize, fontSize, maxFontSize);
	}

// Component overrides
	@Override
	public void paint(Graphics g) {
		updateDimension();
		super.paint(g);
	}
}
