package ecv.poker.game;

import java.util.ArrayList;
import java.util.List;

import ecv.poker.card.Card;
import ecv.poker.card.Deck;
import ecv.poker.player.Player;

/**
 * A representation of a game of poker.
 * A game has a players,
 * a deck of cards, 
 * and community cards all players can use,
 * 
 * @author Evan
 */
public class Game {
	private Deck deck;
	private Player user, computer;
	private List<Card> communityCards;
	
	public Game() {
		deck = new Deck();
		communityCards = new ArrayList<Card>();
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
