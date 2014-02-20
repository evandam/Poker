package ecv.poker.card;

import java.util.LinkedList;
import java.util.List;

import ecv.poker.player.Player;

/**
 * A representation of where a game of poker takes place.
 * A table has a deck of cards, community cards used in the game,
 * And two players.
 * 
 * Unsure of what package to keep this in...really only acts as a container
 * 
 * @author Evan
 */
public class Table {
	private Deck deck;
	private Player user, computer;
	private List<Card> communityCards;
	
	public Table() {
		deck = new Deck();
		communityCards = new LinkedList<Card>();
		user = new Player(this);
		computer = new Player(this);
	}
	
	public Deck getDeck() {
		return deck;
	}
	
	public Player getUser() {
		return user;
	}
	
	public Player getComputer() {
		return computer;
	}
	
	public List<Card> getCommunityCards() {
		return communityCards;
	}

}
