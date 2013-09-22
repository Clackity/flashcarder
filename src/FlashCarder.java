import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;

/**
 * @author Atlee
 * @version 2013.09.22
 */
public class FlashCarder {
	private static final String TITLE = "FlashCarder";
	private static final String VERSION = "Version 2013.09.22";

// Look And Feel constants
	private static final String LOOK_AND_FEEL = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
	private static final int MARGIN = 4;

// GUI fields
	private JFrame frame;
	private JLabel fileNameLabel;
	private FlashCard flashCard;

// File fields
	private static JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
	private String fileName;

// public methods
	public FlashCarder() {
		setLookAndFeel();
		frame = makeFrame();
		frame.setVisible(true);
	}

// private GUI methods
	private JPanel makeCardPanel() {
		return new JPanel(new BorderLayout(MARGIN, MARGIN));
	}

	private JFrame makeFrame() {
		JFrame frame = new JFrame(TITLE);
		frame.addWindowListener(new WindowAdapter() { public void  windowClosing(WindowEvent we) { handleQuitRequest(); }});

		frame.setJMenuBar(makeMenuBar());

		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new BorderLayout(MARGIN, MARGIN));

		fileNameLabel = new JLabel();
		fileNameLabel.setBorder(BorderFactory.createEmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN));
		setFileName(null);
		contentPane.add(fileNameLabel, BorderLayout.NORTH);

		flashCard = new FlashCard();
		contentPane.add(flashCard, BorderLayout.CENTER);

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
		item = new JMenuItem("Open");
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, SHORTCUT_MASK));
		item.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { handleFileOpen(); }});
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
	}

// private GUI helper methods
	private void setFileName(String newFileName) {
		fileName = newFileName;
		String text = newFileName == null ? "(no file opened)" : newFileName;
		fileNameLabel.setText(text);
	}

// private GUI handler methods
	private void handleQuitRequest() {
		System.exit(0);
	}

	private void handleFileOpen() {
		String chosenFileName;

		if(JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(frame)) {
			File file = fileChooser.getSelectedFile();
			chosenFileName = fileChooser.getName(file);
		} else return;

		// todo: load file
	}

	private void handleHelpAbout() {
		JOptionPane.showMessageDialog(frame, TITLE + "\n" + VERSION, "About", JOptionPane.INFORMATION_MESSAGE);
	}
}
