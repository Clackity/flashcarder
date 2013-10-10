package flashcarder;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

/**
 * @author Atlee
 *
 * A stack of Cards.
 *
 * Example:
 * 		CardStack cardStack = CardStack.createFromFile("somecards.txt");
 *	 	if(cardStack == null) throw new Exception("problem reading file");
 *	 	cardStack.shuffle();
 *	 	CardStack seenCards = new CardStack();
 *		while(!cardStack.isEmpty()) {
 * 			Card card = cardStack.removeNextCard();
 * 			// .. show card ...
 * 			card.setSeen(); // tell the card that we've seen it
 *			seenCards.addCard(card);
 *		}
 *		seenCards.writeToFile("somecards.txt");
 *
 */
public class CardStack {
// static helpers
	private static Random random = new Random();

// defaults
	private static final String CHARSET = "ISO-8859-1"; // important for reading and writing non-US characters

// data
	private ArrayList<Card> stack;

// public static methods
	/**
	 * Creates a new CardStack by reading it from a file.
	 * @param fileName The name of the file to open and read from.
	 * @return A new CardStack or null if something goes wrong.
	 */
	public static CardStack createFromFile(String fileName) {
		InputStreamReader inStream;
		try { inStream = new InputStreamReader(new FileInputStream(fileName), CHARSET); }
		catch(IOException e) { return null; }
		BufferedReader bufferedReader = new BufferedReader(inStream);

		ArrayList<Card> newStack = new ArrayList<Card>();
		for(;;) {
			Card card = Card.createFromFile(bufferedReader);
			if(card == null) break;
			newStack.add(card);
		}

		try {
			bufferedReader.close();
			inStream.close(); // just in case
		} catch(IOException e) { /* don't care */ }

		return new CardStack(newStack);
	}

// public methods
	public CardStack() {
		this(new ArrayList<Card>());
	}

	/**
	 * Share constructor.
	 * Starts with the given stack in the form of an ArrayList<Card>
	 * @param extantStack The ArrayList to share.
	 */
	public CardStack(ArrayList<Card> extantStack) {
		stack = extantStack;
	}

	/**
	 * Copy constructor.
	 * Starts with a shallow copy of the given stack.
	 * @param copyFromStack The CardStack to copy from.
	 */
	public CardStack(CardStack copyFromStack) {
		if(copyFromStack != null && copyFromStack.stack != null) {
			stack = new ArrayList<Card>(copyFromStack.stack);
		} else {
			stack = new ArrayList<Card>();
		}
	}

	/**
	 * Adds a Card to the top of this stack.
	 * @param card The Card to add to this stack.
	 */
	public void addCard(Card card) {
		stack.add(card);
	}

	/**
	 * Adds a Card to this stack in a random location within the beginning half.
	 * The idea is to prevent this card from resurfacing soon, but without otherwise
	 * leaving a pattern.
	 * @param card The Card to add.
	 */
//	public void addCardRandomly(Card card) {
//		int where = 0;
//		if(stack.size() >= 2) where = random.nextInt(stack.size() - 2);
//		stack.add(where, card);
//	}
	public void addCardRandomly(Card card) {
		int where = 0;
		if(stack.size() > 2) where = random.nextInt(stack.size() / 2);
		stack.add(where, card);
	}

	/**
	 * Adds an entire CardStack to the top of this stack.
	 * @param cardStack The CardStack to add to this stack.
	 */
	public void addCardStack(CardStack cardStack) {
		if(cardStack != null) stack.addAll(cardStack.stack);
	}

	/**
	 * Empties this stack of all cards.
	 */
	public void clear() {
		stack.clear();
	}

	/**
	 * @return The number of Cards in this stack.
	 */
	public int getCount() {
		return stack != null ? stack.size() : 0;
	}

	/**
	 * Test emptiness of this stack.
	 * @return True if empty else false.
	 */
	public boolean isEmpty() {
		return stack.isEmpty();
	}

	/**
	 * Removes the next card from this stack and returns it.
	 * @return The card that was first in this stack.
	 */
	public Card removeNextCard() {
		if(stack.isEmpty()) return null;
		return stack.remove(stack.size() - 1);
	}

	public void shuffle() {
		Collections.shuffle(stack);
	}

	public void sort(Comparator<Card> cardComparator) {
		Collections.sort(stack, cardComparator);
	}

	/**
	 * Attempt to write this stack to a file.
	 * @param fileName The name of the file that will be created or overwritten.
	 * @return True on success, false if something goes wrong.
	 */
	public boolean writeToFile(String fileName) {
		OutputStreamWriter outStream;
		try { outStream = new OutputStreamWriter(new FileOutputStream(fileName), CHARSET); }
		catch(IOException e) { return false; }
		BufferedWriter bufferedWriter = new BufferedWriter(outStream);

		boolean error = false;
		for(Card card : stack) {
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
