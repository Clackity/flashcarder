import java.io.*;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Atlee
 */
public class CardSet {
// defaults
	private static final Comparator<Card> COMPARATOR = Card.lastSeenTimeComparator;

// data
	private Set<Card> set;

// public static methods
	/**
	 * Creates a new CardSet by reading it from a file.
	 * @param fileName The name of the file to open and read from.
	 * @return A new CardSet or null if something goes wrong.
	 */
	public static CardSet createFromFile(String fileName) {
		return createFromFile(fileName, COMPARATOR);
	}

	public static CardSet createFromFile(String fileName, Comparator<Card> cardSortingComparator) {
		FileReader reader;
		try { reader = new FileReader(fileName); }
		catch(FileNotFoundException e) { return null; }
		BufferedReader bufferedReader = new BufferedReader(reader);

		Set<Card> newSet = new TreeSet<Card>(cardSortingComparator);
		for(;;) {
			Card card = Card.createFromFile(bufferedReader);
			if(card == null) break;
			newSet.add(card);
		}

		try {
			bufferedReader.close();
			reader.close(); // just in case
		} catch(IOException e) { /* don't care */ }

		return new CardSet(newSet);
	}

// public methods
	/**
	 * Default constructor.
	 * Starts empty and will sort by the default COMPARATOR defined up top.
	 */
	public CardSet() {
		this(Card.lastSeenTimeComparator);
	}

	/**
	 * Particular sorting constructor.
	 * Starts empty and will sort by the indicated Card comparator.
	 * @param cardSortingComparator The Card comparator from the Card class.
	 */
	public CardSet(Comparator<Card> cardSortingComparator) {
		this(new TreeSet<Card>(cardSortingComparator));
	}

	/**
	 * Copy constructor.
	 * Starts with the given Set.
	 * @param extantSet The Set to copy from.
	 */
	public CardSet(Set<Card> extantSet) {
		set = extantSet;
	}

	/**
	 * Attempt to write this set to a file.
	 * @param fileName The name of the file that will be created or overwritten.
	 * @return True on success, false if something goes wrong.
	 */
	public boolean writeToFile(String fileName) {
		FileWriter writer;
		try { writer = new FileWriter(fileName); }
		catch(IOException e) { return false; }
		BufferedWriter bufferedWriter = new BufferedWriter(writer);

		boolean error = false;
		for(Card card : set) {
			if(!card.writeToFile(bufferedWriter)) {
				error = true;
				break;
			}
		}

		try {
			bufferedWriter.close();
			writer.close();
		} catch(IOException e) { return false; }

		return !error;
	}
}
