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
		FileReader reader = new FileReader("c:\\testcard.txt");
		BufferedReader bufferedReader = new BufferedReader(reader);
		Card card = Card.loadFromFile(bufferedReader);
		Card card2 = Card.loadFromFile(bufferedReader);
		Card card3 = Card.loadFromFile(bufferedReader);
		reader.close();
		if(card != null) {
			FileWriter writer = new FileWriter("c:\\testcardout.txt");
			BufferedWriter bufferedWriter = new BufferedWriter(writer);
			if(!card.writeToFile(bufferedWriter)) throw new Exception("didn't write");
			if(!card2.writeToFile(bufferedWriter)) throw new Exception("card2 didn't write");
			bufferedWriter.close();
			writer.close();
		} else throw new Exception("Card.loadFromFile didn't work");
		if(card3 != null) throw new Exception("card3 isn't null but should be");
		//new FlashCarder();
	}
}
