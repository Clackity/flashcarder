import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.Random;

/**
 * @author Atlee
 *
 * A two-sided card.
 */
public class Card {
// private data
	private int easeBias; // increment when easy, decrement when difficult
	private long lastSeenTime; // unix time in milliseconds
	private final String sideA, sideB;
	private int viewCount; // number of times viewed

// private static data
	private static final Random random = new Random();
	private static int randomValue;

// public static Comparators
	public static final EaseBiasComparator easeBiasComparator = new EaseBiasComparator();
	public static final LastSeenTimeComparator lastSeenTimeComparator = new LastSeenTimeComparator();
	public static final RandomComparator randomComparator = new RandomComparator();
	public static final SideAComparator sideAComparator = new SideAComparator();
	public static final SideBComparator sideBComparator = new SideBComparator();
	public static final ViewCountComparator viewCountComparator = new ViewCountComparator();

// public static methods
	/**
	 * Attempt to read one Card from the BufferedReader.
	 * @param input An extant BufferedReader from a previous call to new BufferedReader(new FileReader(filename))
	 * @return Either a new Card or null if there isn't an entire card to be read from the given input.
	 */
	public static Card createFromFile(BufferedReader input) {
		String sideA, sideB;
		int easeBias = 0;
		long lastSeenTime = 0;
		int viewCount = 0;

		try {
			do { sideA = input.readLine(); }
			while (sideA != null && sideA.isEmpty()); // eat leading empty lines
			sideB = input.readLine();
			if(sideA == null || sideB == null) return null; // need both of these to exist
			if(sideA.isEmpty() || sideB.isEmpty()) return null; // and they shouldn't be empty
		} catch(IOException e) { return null; }
		try { // these won't necessarily be present in the file

			String easeBiasString = input.readLine(); // ease bias
			if(easeBiasString != null) {
				if(!easeBiasString.isEmpty()) {
					try { easeBias = Integer.parseInt(easeBiasString); }
					catch(NumberFormatException e) { /* no big deal */ }

					String lastSeenTimeString = input.readLine(); // last seen time
					if(lastSeenTimeString != null) {
						if(!lastSeenTimeString.isEmpty()) {
							try { lastSeenTime = Long.parseLong(lastSeenTimeString); }
							catch(NumberFormatException e) { /* no big deal */ }

							String viewCountString = input.readLine(); // view count
							if(viewCountString != null) {
								if(!viewCountString.isEmpty()) {
									try { viewCount = Integer.parseInt(viewCountString); }
									catch(NumberFormatException e) { /* no big deal */ }
								}
							}
						}
					}
				}
			}
		} catch(IOException e) { /* don't care */ }

		return new Card(sideA, sideB, easeBias, lastSeenTime, viewCount);
	}

	/**
	 * Randomizes the static value used by the RandomComparator.
	 */
	public static void randomize() {
		randomValue = random.nextInt();
	}

// public methods
	public Card(String sideA, String sideB) {
		this(sideA, sideB, 0, 0, 0);
	}

	public Card(String sideA, String sideB, int easeBias, long lastSeenTime, int viewCount) {
		this.easeBias = easeBias;
		this.lastSeenTime = lastSeenTime;
		this.sideA = sideA == null ? "" : sideA;
		this.sideB = sideB == null ? "" : sideB;
		this.viewCount = viewCount;
		randomize();
	}

	public int getEaseBias() {
		return easeBias;
	}

	public long getLastSeenTime() {
		return lastSeenTime;
	}

	public String getSideA() {
		return sideA;
	}

	public String getSideB() {
		return sideB;
	}

	public int getViewCount() {
		return viewCount;
	}

	/**
	 * Update this card's view statistics and ease bias.
	 * @param wasEasy Whether this card was easy or difficult.
	 */
	public void setSeen(boolean wasEasy) {
		easeBias += wasEasy ? 1 : -1;
		++viewCount;
		lastSeenTime = System.currentTimeMillis();
	}

	/**
	 * Attempt to write this card to a BufferedReader.
	 * @param output An extant BufferedWriter from a previous call to new BufferedWriter(new FileWriter(filename))
	 * @return True on success, false if there's an IO exception.
	 */
	public boolean writeToFile(BufferedWriter output) {
		try {
			output.write(sideA); output.newLine();
			output.write(sideB); output.newLine();
			output.write(Integer.toString(easeBias)); output.newLine();
			output.write(Long.toString(lastSeenTime)); output.newLine();
			output.write(Integer.toString(viewCount)); output.newLine();
			output.newLine();
		} catch(IOException e) { return false; }
		return true;
	}

// private Comparator classes
	private static class EaseBiasComparator implements Comparator<Card> {
		public int compare(Card card1, Card card2) {
			return card1.easeBias - card2.easeBias;
		}
	}

	private static class LastSeenTimeComparator implements Comparator<Card> {
		public int compare(Card card1, Card card2) {
			if(card1.lastSeenTime < card2.lastSeenTime) return -1;
			if(card1.lastSeenTime == card2.lastSeenTime) return 0;
			return 1;
		}
	}

	private static class RandomComparator implements Comparator<Card> {
		public int compare(Card card1, Card card2) {
			int hash1 = card1.hashCode() ^ randomValue;
			int hash2 = card2.hashCode() ^ randomValue;
			return hash1 - hash2;
		}
	}

	private static class SideAComparator implements Comparator<Card> {
		public int compare(Card card1, Card card2) {
			return card1.sideA.compareTo(card2.sideA);
		}
	}

	private static class SideBComparator implements Comparator<Card> {
		public int compare(Card card1, Card card2) {
			return card1.sideB.compareTo(card2.sideB);
		}
	}

	private static class ViewCountComparator implements Comparator<Card> {
		public int compare(Card card1, Card card2) {
			return card1.viewCount - card2.viewCount;
		}
	}
}
