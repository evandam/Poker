package ecv.poker.player;

import ecv.poker.card.Card;
import ecv.poker.card.Hand;
import ecv.poker.card.Game;

public class Player {
	private int chips;
	private Hand hand;
	
	public Player(Game table) {
		chips = 1000;	// or some default number?
		hand = new Hand(table.getCommunityCards());
	}
	
	/**
	 * @return Number of chips player has left to play with
	 */
	public int getChips() {
		return chips;
	}
	
	/**
	 * @param i number of chips to be added or deducted from player
	 */
	public void addChips(int i) {
		chips += i;
	}
	
	public Hand getHand() {
		return hand;
	}
	
	/**
	 * Add a card dealt from the deck
	 * @param card
	 */
	public void drawCard(Card card) {
		hand.add(card);
	}
}
