package hangouts;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.Random;

public class KeypressHangoutsBot {

	/**
	 * Spams messages by pressing keys machine-manually
	 * 
	 * @param args
	 * @throws Exception
	 */
	
	public static void main(String... args) throws Exception {
		
		System.out.println("Initiating >:)");
		
		Robot r = new Robot();
		Thread.sleep(5000);
		
		InsultGenerator ig = new KeypressHangoutsBot().new InsultGenerator();
		for(int i = 0; i < 20; i++) {
			send(ig.randomInsult(), r, 900, 1);
			System.out.println((i+1) + " insults sent");
		}
	}
	
	public static String type(String text, Robot r) throws Exception {
		
		try {
			for(char c : text.toCharArray()) {
				if(Character.isUpperCase(c)) {
					r.keyPress(KeyEvent.VK_SHIFT);
					r.keyPress(KeyEvent.getExtendedKeyCodeForChar(c));
					r.keyRelease(KeyEvent.getExtendedKeyCodeForChar(c));
					r.keyRelease(KeyEvent.VK_SHIFT);
				} else {
					r.keyPress(KeyEvent.getExtendedKeyCodeForChar(c));
					r.keyRelease(KeyEvent.getExtendedKeyCodeForChar(c));
				}
			}
		} catch(IllegalArgumentException e) {
			throw new Exception("Keycode not recognized - try to exclude symbols");
		}
		return text;
	}
	
	public static String send(String text, Robot r, int delayInMillis, int repetitions) throws Exception {
		
		for(int i = 0; i < repetitions; i++) {
			type(text, r);
			r.keyPress(KeyEvent.VK_ENTER);
			r.keyRelease(KeyEvent.VK_ENTER);
			Thread.sleep(delayInMillis);
		}
		return text;
	}
	
	class InsultGenerator {
		
		// can add more later
		String[] nouns = {"carrot", "tomato", "potato", "bean", "Cheezit", "Allen", "Nathan"};
		String[] adjectives = {"funny", "hot", "cool", "short", "fat", "lazy", "hungry"};
		String[] verbs = {"talks", "works", "poops", "argues", "farts", "eats Cheezits", "does math"};
		
		public String randomInsult() {
			
			Random r = new Random();
			return "You are a " + adjectives[r.nextInt(adjectives.length)] + " " + nouns[r.nextInt(nouns.length)] + " who " + verbs[r.nextInt(verbs.length)];
		}
	}
}

/*
 * 
 * 
 * 
 * 
 * 
 * 
 */