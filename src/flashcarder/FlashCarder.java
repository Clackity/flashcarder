package flashcarder;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;

/**
 * @author Atlee
 * @version 2013.09.24
 */
public class FlashCarder {
	private static final String TITLE = "FlashCarder";
	private static final String VERSION = "Version 2013.09.24";

// Look & Feel constants
	private static final String LOOK_AND_FEEL = UIManager.getSystemLookAndFeelClassName();
	private static final int MARGIN = 4;

// GUI fields
	private JFrame frame;
	private JLabel fileNameLabel;
	private FlashCardPanel flashCardPanel;
	private CardStackPanel cardStackWorkingPanel;
	private CardStackPanel cardStackHardPanel;
	private CardStackPanel cardStackEasyPanel;

// file fields
	private static JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
	private String fileName;

// data fields
	private Card card;
	private CardStack cardStackWorking; // pull cards from here
	private CardStack cardStackHard; // put difficult cards here when done
	private CardStack cardStackEasy; // put easy cards here when done
	private boolean modified; // whether the file ought to be saved or not
	private boolean showSideBFirst;

// public methods
	public FlashCarder() {
		setLookAndFeel();
		frame = makeFrame();
		clearCards();
		frame.setVisible(true);
	}

// private methods
	private void clearCards() {
		showSideBFirst = false;
		setCard(null);
		cardStackWorking = null;
		cardStackHard = null;
		cardStackEasy = null;
		modified = false;
	}

	private boolean getNextCard() {
		if(cardStackWorking != null) {
			setCard(cardStackWorking.removeNextCard());
			cardStackWorkingPanel.setCurrentCount(cardStackWorking.getCount());
			return card != null;
		}
		return false;
	}

	/**
	 * Puts the current Card back into the working set.
	 * Does not mark the card as seen.
	 */
	private void putCardInWorkingStack() {
		if(cardStackWorking != null && card != null) {
			cardStackWorking.addCardRandomly(card);
			cardStackWorkingPanel.setCurrentCount(cardStackWorking.getCount());
			card = null;
		}
	}

	/**
	 * Puts the current Card into the To Review set.
	 * Marks the card as seen and not easy.
	 * Doesn't call Card.randomize().
	 */
	private void putCardInHardStack() {
		if(cardStackHard != null && card != null) {
			card.setSeen(false);
			cardStackHard.addCard(card);
			cardStackHardPanel.setCurrentCount(cardStackHard.getCount());
			card = null;
			modified = true;
		}
	}

	/**
	 * Puts the current Card into the Retired set.
	 * Marks the card as seen and easy.
	 * Doesn't call Card.randomize().
	 */
	private void putCardInEasyStack() {
		if(cardStackEasy != null && card != null) {
			card.setSeen(true);
			cardStackEasy.addCard(card);
			cardStackEasyPanel.setCurrentCount(cardStackEasy.getCount());
			card = null;
			modified = true;
		}
	}

	private void setCard(Card card) {
		this.card = card;
		if(card != null) {
			flashCardPanel.set(card.getSideA(), card.getSideB(), showSideBFirst);
		} else {
			flashCardPanel.set("", "", showSideBFirst);
		}
	}

	/**
	 * If a file is open and it's changed (card stats and whatnot),
	 * then this method will ask the user whether to save it,
	 * and then save it.
	 */
	private void saveProgress() {
		if(modified && fileName != null) {
			if(JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(frame
				,new JLabel("Save progress with " + fileName + " ?")
				,"Unsaved Progress"
				,JOptionPane.YES_NO_OPTION
			)) {
				saveToFile(fileName);
			}
		}
	}

	private boolean saveToFile(String fileName) {
		if(cardStackWorking == null) return true; // nothing saved

		CardStack setToWrite = new CardStack(cardStackWorking);
		setToWrite.addCardStack(cardStackHard);
		setToWrite.addCardStack(cardStackEasy);
		if(card != null) setToWrite.addCard(card);

		if(!setToWrite.writeToFile(fileName)) {
			JOptionPane.showMessageDialog(frame
				,new JLabel("There was a problem saving " + fileName + ", sorry.")
				,"File / Save As problem"
				,JOptionPane.ERROR_MESSAGE
			);
			return false;
		}

		modified = false;
		setFileName(fileName);

		return true;
	}

// private GUI methods
	private JPanel makeBottomButtonPanel() {
		JPanel buttonPanel = new JPanel(new GridLayout(1, 3, MARGIN * 3, MARGIN * 3));
		buttonPanel.setPreferredSize(new Dimension(1, 100));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, MARGIN, MARGIN, MARGIN));

		cardStackEasyPanel = new CardStackPanel("Easy Cards", Color.GREEN, false);
		cardStackEasyPanel.addMouseListener(new MouseListenerDefault() { public void mousePressed(MouseEvent e) { handleEasyStackAction(); }});
		buttonPanel.add(cardStackEasyPanel);

		cardStackWorkingPanel = new CardStackPanel("Fresh Cards", Color.BLUE, true);
		cardStackWorkingPanel.addMouseListener(new MouseListenerDefault() { public void mousePressed(MouseEvent e) { handleWorkingStackAction(); }});
		buttonPanel.add(cardStackWorkingPanel);

		cardStackHardPanel = new CardStackPanel("Hard Cards", Color.RED, false);
		cardStackHardPanel.addMouseListener(new MouseListenerDefault() { public void mousePressed(MouseEvent e) { handleHardStackAction(); }});
		buttonPanel.add(cardStackHardPanel);

		return buttonPanel;
	}

	private JFrame makeFrame() {
		JFrame frame = new JFrame(TITLE);
		frame.addWindowListener(new WindowAdapter() { public void  windowClosing(WindowEvent e) { handleQuitRequest(); }});
		frame.addKeyListener(new KeyListenerDefault() { public void keyPressed(KeyEvent e) { handleKeyPressed(e); }});

		frame.setJMenuBar(makeMenuBar());

		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new BorderLayout(MARGIN, MARGIN));

		fileNameLabel = new JLabel();
		fileNameLabel.setBorder(BorderFactory.createEmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN));
		setFileName(null);
		contentPane.add(fileNameLabel, BorderLayout.NORTH);

		flashCardPanel = new FlashCardPanel();
		contentPane.add(flashCardPanel, BorderLayout.CENTER);

		contentPane.add(makeBottomButtonPanel(), BorderLayout.SOUTH);

		frame.pack();

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation((d.width - frame.getWidth()) / 2, (d.height - frame.getHeight()) / 2);

		return frame;
	}

	private JMenuBar makeMenuBar() {
		final int SHORTCUT_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

		JMenuBar menuBar = new JMenuBar();

		JMenu menu;
		JMenuItem item;

		// File
		menu = new JMenu("File");
		menuBar.add(menu);
		// File / Open
		item = new JMenuItem("Open...");
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, SHORTCUT_MASK));
		item.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { handleFileOpen(); }});
		menu.add(item);
		// File / Save
		item = new JMenuItem("Save");
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, SHORTCUT_MASK));
		item.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { handleFileSave(); }});
		menu.add(item);
		// File / Save As
		item = new JMenuItem("Save As...");
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, SHORTCUT_MASK));
		item.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { handleFileSaveAs(); }});
		menu.add(item);
		// File / Exit
		item = new JMenuItem("Exit");
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, SHORTCUT_MASK));
		item.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { handleQuitRequest(); }});
		menu.add(item);

		// Help
		menu = new JMenu("Help");
		menuBar.add(menu);
		// Help / About
		item = new JMenuItem("About");
		item.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { handleHelpAbout(); }});
		menu.add(item);

		return menuBar;
	}

	private void setLookAndFeel() {
		try { UIManager.setLookAndFeel(LOOK_AND_FEEL); }
		catch(Exception e) { /* no big deal, just a look and feel thing */ }
		System.setProperty("awt.useSystemAAFontSettings", "on");
		System.setProperty("swing.aatext", "true");
	}

// private GUI helper methods
	private void setFileName(String newFileName) {
		fileName = newFileName;
		String prefix = modified ? "*" : "";
		String suffix = fileName == null ? "(no file opened)" : fileName;
		fileNameLabel.setText(prefix + suffix);
	}

// private GUI handler methods
	private void handleQuitRequest() {
		saveProgress();

		System.exit(0);
	}

	private void handleFileOpen() {
		saveProgress();

		setFileName(null);

		String chosenFileName;

		if(JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(frame)) {
			File file = fileChooser.getSelectedFile();
			chosenFileName = file.getAbsolutePath();
		} else return;

		clearCards();
		cardStackWorking = CardStack.createFromFile(chosenFileName);
		if(cardStackWorking == null) {
			JOptionPane.showMessageDialog(frame
				,new JLabel("There was a problem opening " + chosenFileName + ", sorry.")
				,"File / Open problem"
				,JOptionPane.ERROR_MESSAGE
			);
		} else {
			cardStackWorking.shuffle();
			int totalCards = cardStackWorking.getCount();
			cardStackWorkingPanel.setCapacity(totalCards);
			cardStackWorkingPanel.setCurrentCount(cardStackWorking.getCount());
			cardStackEasyPanel.setCapacity(totalCards);
			cardStackEasyPanel.setCurrentCount(0);
			cardStackHardPanel.setCapacity(totalCards);
			cardStackHardPanel.setCurrentCount(0);
			cardStackHard = new CardStack();
			cardStackEasy = new CardStack();
			modified = false;
			setFileName(chosenFileName);
			getNextCard();
		}
	}

	private void handleFileSave() {
		if(fileName == null) {
			JOptionPane.showMessageDialog(frame, "There isn't anything to save.", "File / Save", JOptionPane.INFORMATION_MESSAGE);
		} else {
			saveToFile(fileName);
		}
	}

	private void handleFileSaveAs() {
		String chosenFileName = null;
		while(JFileChooser.APPROVE_OPTION == fileChooser.showSaveDialog(frame)) {
			File file = fileChooser.getSelectedFile();
			chosenFileName = file.getAbsolutePath();
			if(file.exists()) {
				int decision = JOptionPane.showConfirmDialog(frame
					,new JLabel("Overwrite " + chosenFileName + " ?")
					,"Confirm File Overwrite"
					,JOptionPane.YES_NO_CANCEL_OPTION
				);
				if(decision == JOptionPane.YES_OPTION) break;
				if(decision == JOptionPane.CANCEL_OPTION) return;
			} else break;
		}

		if(chosenFileName == null) return;

		saveToFile(chosenFileName);
	}

	private void handleHelpAbout() {
		JOptionPane.showMessageDialog(frame, TITLE + "\n" + VERSION, "About", JOptionPane.INFORMATION_MESSAGE);
	}

	private void handleEasyStackAction() {
		putCardInEasyStack();
		getNextCard();
	}

	private void handleWorkingStackAction() {
		putCardInWorkingStack();
		getNextCard();
	}

	private void handleHardStackAction() {
		putCardInHardStack();
		getNextCard();
	}

	private void handleKeyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_KP_LEFT:
				handleEasyStackAction();
				break;
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_KP_RIGHT:
				handleHardStackAction();
				break;
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_KP_DOWN:
				handleWorkingStackAction();
				break;
			case KeyEvent.VK_UP:
			case KeyEvent.VK_KP_UP:
			case KeyEvent.VK_SPACE:
				flashCardPanel.flip();
				break;
		}
	}
}
