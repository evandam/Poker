package ecv.poker.game;

import java.util.ArrayList;
import java.util.List;

import ecv.poker.card.Card;
import ecv.poker.card.Deck;
import ecv.poker.player.Player;

/**
 * A representation of a game of poker. A game has a players, a deck of cards,
 * and community cards all players can use, and a pot that goes to the winning hand
 * 
 * @author Evan
 */
public class Game {
	private Deck deck;
	private Player user, computer;
	private List<Card> communityCards;
	private int pot;

	public Game() {
		deck = new Deck();
		communityCards = new ArrayList<Card>();
		user = new Player(this);
		computer = new Player(this);
		pot = 0;
	}

	public Deck getDeck() {
		return deck;
	}
	
	/**
	 * Cards may be in deck, a player's hand, or in the community list
	 * @return List of all cards in the game
	 */
	public List<Card> getAllCards() {
		List<Card> allCards = new ArrayList<Card>();
		allCards.addAll(deck.getCards());
		allCards.addAll(user.getHoleCards());
		allCards.addAll(computer.getHoleCards());
		allCards.addAll(communityCards);
		return allCards;
	}

	/**
	 * 
	 * @return the player that is controlled by the user
	 */
	public Player getUser() {
		return user;
	}

	/**
	 * 
	 * @return the player that is controlled by the computer
	 */
	public Player getComputer() {
		return computer;
	}

	/**
	 * 
	 * @return List of cards available for all players to use
	 */
	public List<Card> getCommunityCards() {
		return communityCards;
	}
	
	/**
	 * 
	 * @return Size of the pot for the current hand
	 */
	public int getPot() {
		return pot;
	}
	
	/**
	 * Increment the pot by the bet size
	 * @param bet
	 */
	public void addToPot(int bet) {
		pot += bet;
	}
	
	/**
	 * Deal out cards to players and start the round
	 */
	public void setupHand() {
		deck.shuffle();
		for(int i = 0; i < 2; i++) {
			user.drawCard(deck.deal());
			computer.drawCard(deck.deal());
		}
	}
	
	/**
	 * Reset the deck, clear players' hands,
	 * award chips to the winner, and reset the pot.
	 */
	public void endHand() {
		deck.add(user.getHoleCards());
		user.getHand().clear();
		
		deck.add(computer.getHoleCards());
		computer.getHand().clear();
		
		deck.add(communityCards);
		communityCards.clear();

		// determine the winner of the hand, or split the pot
		int cmp = user.getHand().compareTo(computer.getHand());
		if(cmp > 1)
			user.addChips(pot);
		else if(cmp < 1)
			computer.addChips(pot);
		else {
			user.addChips(pot / 2);
			computer.addChips(pot / 2);
		}
		pot = 0;
	}

}
