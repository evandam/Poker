package ecv.poker.card;

import java.util.LinkedList;
import java.util.List;

import ecv.poker.player.Player;

/**
 * A representation of a game of poker.
 * A game has a players,
 * a deck of cards, 
 * and community cards all players can use,
 * 
 * Unsure of what package to keep this in...really only acts as a container
 * 
 * @author Evan
 */
public class Game {
	private Deck deck;
	private Player user, computer;
	private List<Card> communityCards;
	
	public Game() {
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
