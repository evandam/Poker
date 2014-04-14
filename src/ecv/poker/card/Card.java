package ecv.poker.card;


/**
 * Copied from Crazy Eights With small modifications. Implements comparable so
 * hands can easily be sorted
 * 
 * @author Evan
 */
public class Card implements Comparable<Card> {

	private int id;
	private int suit;
	private int rank;
	private int resId;

	public Card(int newId) {
		id = newId;
		suit = Math.round((id / 100) * 100);
		rank = id - suit;
	}

	public int getId() {
		return id;
	}

	public int getResId() {
		return resId;
	}

	public void setResId(int resId) {
		this.resId = resId;
	}

	public int getSuit() {
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
}
