import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * @author Atlee
 */
public class Main {
	public static void main(String args[]) throws Exception {
		// temporary test
		CardSet cardSet = CardSet.createFromFile("c:\\testcard.txt");
		cardSet.writeToFile("c:\\testcardout.txt");

		//new FlashCarder();
	}
}
