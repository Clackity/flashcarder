import java.io.*;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.TreeSet;

/**
 * @author Atlee
 *
 * A set of Cards.
 * Supports sorting, though it isn't strictly required.
 *
 * Example:
 * 		CardSet cardSet = CardSet.createFromFile("somecards.txt");
 *	 	if(cardSet == null) throw new Exception("problem reading file");
 *	 	CardSet seenCards = new CardSet();
 *		while(!cardSet.isEmpty()) {
 * 			Card card = cardSet.removeNextCard();
 * 			// .. show card ...
 * 			card.setSeen(); // tell the card that we've seen it
 *			seenCards.addCard(card);
 *		}
 *		seenCards.writeToFile("somecards.txt");
 *
 */
public class CardSet {
// defaults
	private static final String CHARSET = "ISO-8859-1";
	private static final Comparator<Card> COMPARATOR = Card.randomComparator;
	private static final int INITIAL_CAPACITY = 11; // default PriorityQueue capacity

// data
	private PriorityQueue<Card> set;

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
		InputStreamReader inStream;
		try { inStream = new InputStreamReader(new FileInputStream(fileName), CHARSET); }
		catch(IOException e) { return null; }
		BufferedReader bufferedReader = new BufferedReader(inStream);

		PriorityQueue<Card> newSet = new PriorityQueue<Card>(INITIAL_CAPACITY, cardSortingComparator);
		for(;;) {
			Card card = Card.createFromFile(bufferedReader);
			if(card == null) break;
			newSet.add(card);
		}

		try {
			bufferedReader.close();
			inStream.close(); // just in case
		} catch(IOException e) { /* don't care */ }

		return new CardSet(newSet);
	}

// public methods
	/**
	 * Default constructor.
	 * Starts empty and will sort by the default COMPARATOR defined up top.
	 */
	public CardSet() {
		this(COMPARATOR);
	}

	/**
	 * Particular sorting constructor.
	 * Starts empty and will sort by the indicated Card comparator.
	 * @param cardSortingComparator The Card comparator from the Card class.
	 */
	public CardSet(Comparator<Card> cardSortingComparator) {
		this(new PriorityQueue<Card>(INITIAL_CAPACITY, cardSortingComparator));
	}

	/**
	 * Share constructor.
	 * Starts with the given PriorityQueue.
	 * @param extantSet The PriorityQueue to share.
	 */
	public CardSet(PriorityQueue<Card> extantSet) {
		set = extantSet;
	}

	/**
	 * Copy constructor.
	 * Starts with a shallow copy of the given set.
	 * @param copyFromSet The CardSet to copy from.
	 */
	public CardSet(CardSet copyFromSet) {
		if(copyFromSet != null && copyFromSet.set != null) {
			set = new PriorityQueue<Card>(copyFromSet.set);
		} else {
			set = new PriorityQueue<Card>(INITIAL_CAPACITY, COMPARATOR);
		}
	}

	/**
	 * Adds a Card to this set in sorted order.
	 * @param card The Card to add to this set.
	 */
	public void addCard(Card card) {
		set.add(card);
	}

	/**
	 * Adds an entire CardSet to this set in sorted order.
	 * @param cardSet The CardSet to add to this set.
	 */
	public void addCardSet(CardSet cardSet) {
		if(cardSet != null) set.addAll(cardSet.set);
	}

	/**
	 * Empties this set of all cards.
	 */
	public void clear() {
		set.clear();
	}

	/**
	 * Test emptiness of this set.
	 * @return True if empty else false.
	 */
	public boolean isEmpty() {
		return set.isEmpty();
	}

	/**
	 * Removes the next card from this set and returns it.
	 * @return The card that was first in this set.
	 */
	public Card removeNextCard() {
		return set.poll();
	}

	/**
	 * Attempt to write this set to a file.
	 * @param fileName The name of the file that will be created or overwritten.
	 * @return True on success, false if something goes wrong.
	 */
	public boolean writeToFile(String fileName) {
		OutputStreamWriter outStream;
		try { outStream = new OutputStreamWriter(new FileOutputStream(fileName), CHARSET); }
		catch(IOException e) { return false; }
		BufferedWriter bufferedWriter = new BufferedWriter(outStream);

		boolean error = false;
		for(Card card : set) {
			if(!card.writeToFile(bufferedWriter)) {
				error = true;
				break;
			}
		}

		try {
			bufferedWriter.close();
			outStream.close();
		} catch(IOException e) { return false; }

		return !error;
	}
}
