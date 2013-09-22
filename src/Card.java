import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * @author Atlee
 *
 * A two-sided card.
 */
public class Card {
	private final String sideA, sideB;
	private long lastSeenTime; // unix time in milliseconds
	private int viewCount; // number of times viewed

	/**
	 * Attempt to read one Card from the BufferedReader.
	 * @param input An extant BufferedReader from a previous call to new BufferedReader(new FileReader(filename))
	 * @return Either a new Card or null if there isn't an entire card to be read from the given input.
	 */
	public static Card loadFromFile(BufferedReader input) {
		String sideA, sideB;
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
			String lastSeenTimeString = input.readLine();
			if(lastSeenTimeString != null) {
				if(!lastSeenTimeString.isEmpty()) {
					try { lastSeenTime = Long.parseLong(lastSeenTimeString); }
					catch(NumberFormatException e) { /* no big deal */ }
					String viewCountString = input.readLine();
					if(viewCountString != null) {
						if(!viewCountString.isEmpty()) {
							try { viewCount = Integer.parseInt(viewCountString); }
							catch(NumberFormatException e) { /* no big deal */ }
						}
					}
				}
			}
		} catch(IOException e) { /* don't care */ }

		return new Card(sideA, sideB, lastSeenTime, viewCount);
	}

	public Card(String sideA, String sideB, long lastSeenTime, int viewCount) {
		this.sideA = sideA == null ? "" : sideA;
		this.sideB = sideB == null ? "" : sideB;
		this.lastSeenTime = lastSeenTime;
		this.viewCount = viewCount;
	}

	public long getLastSeenTime() {
		return lastSeenTime;
	}

	public int getViewCount() {
		return viewCount;
	}

	public String getSideA() {
		return sideA;
	}

	public String getSideB() {
		return sideB;
	}

	public void incrementViewCount() {
		++viewCount;
	}

	public void setLastSeenTimeToNow() {
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
			output.write(Long.toString(lastSeenTime)); output.newLine();
			output.write(Integer.toString(viewCount)); output.newLine();
			output.newLine();
		} catch(IOException e) { return false; }
		return true;
	}
}
