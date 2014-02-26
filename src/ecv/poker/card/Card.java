package ecv.poker.card;

import android.graphics.Bitmap;

/**
 * Copied from Crazy Eights With small modifications - scoreValue removed -
 * implements comparable so hands can easily be sorted - toString for easier
 * debugging
 * 
 * @author Evan
 */
public class Card implements Comparable<Card> {
	public static enum Suit {
		diamonds, clubs, hearts, spades
	};

	private int id;
	private Suit suit;
	private int rank;
	private Bitmap bmp;

	public Card(int newId) {
		id = newId;
		rank = id % 100;
		if(id > 400)
			suit = Suit.spades;
		else if(id > 300)
			suit = Suit.hearts;
		else if(id > 200)
			suit = Suit.clubs;
		else
			suit = Suit.diamonds;
	}

	public void setBitmap(Bitmap newBitmap) {
		bmp = newBitmap;
	}

	public Bitmap getBitmap() {
		return bmp;
	}

	public int getId() {
		return id;
	}

	public Suit getSuit() {
		return suit;
	}

	public int getRank() {
		return rank;
	}

	@Override
	public int compareTo(Card another) {
		if (this.getRank() < another.getRank())
			return -1;
		else if (this.getRank() > another.getRank())
			return 1;
		else
			return 0;
	}

	@Override
	public String toString() {
		return rank + "" + suit;
	}
}
