package ecv.poker.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import ecv.poker.card.Card;
import ecv.poker.card.Evaluator;
import ecv.poker.player.Player;

/**
 * A representation of a game of poker. A game has a players, a deck of cards,
 * and community cards all players can use, and a pot that goes to the winning
 * hand
 * 
 * @author Evan
 */
public class Game {
	
	private Random random;
	private Player user, bot;
	private List<Card> deck, communityCards;
	private int pot;
	
	public Game() {
		random = new Random();
		user = new Player(this);
		bot = new Player(this);
		communityCards = new ArrayList<Card>(5);
		deck = new ArrayList<Card>(52);
		for(int i = 100; i <= 400; i += 100) {
			for(int j = 2; j <= 14; j++) {
				deck.add(new Card(i + j));
			}
		}
	}

	public List<Card> getDeck() {
		return deck;
	}

	public Player getUser() {
		return user;
	}

	public Player getBot() {
		return bot;
	}

	public List<Card> getCommunityCards() {
		return communityCards;
	}

	/**
	 * @return Size of the pot for the current hand
	 */
	public int getPot() {
		return pot;
	}

	public void addToPot(int bet) {
		pot += bet;
	}
	
	public void dealNextCard() {
		if(communityCards.size() < 3) {
			communityCards.add(popDeck());
			communityCards.add(popDeck());
			communityCards.add(popDeck());
		} else if(communityCards.size() < 5)
			communityCards.add(popDeck());
	}

	/**
	 * Deal out cards to players and start the round
	 */
	public void setupHand() {
		Collections.shuffle(deck, random);
		for (int i = 0; i < 2; i++) {
			user.getCards().add(popDeck());
			bot.getCards().add(popDeck());
		}
	}
	
	public Card popDeck() {
		return deck.remove(deck.size() - 1);
	}

	/**
	 * Reset the deck, clear players' hands, award chips to the winner, and
	 * reset the pot.
	 * 
	 * @return message to alert user of outcome
	 */
	public String endHand() {
		String msg;
		// evaluate both players' hands with community cards available
		List<Card> cards = new ArrayList<Card>(communityCards);
		cards.addAll(user.getCards());
		int userRank = Evaluator.evaluate(cards);
		
		cards.removeAll(user.getCards());
		cards.addAll(bot.getCards());
		int computerRank = Evaluator.evaluate(cards);
		
		if (userRank > computerRank) {
			user.addChips(pot);
			bot.addChips(-pot);
			msg = "You won! " + pot + " chips added to your stack!";
		} else if (userRank < computerRank) {
			bot.addChips(pot);
			user.addChips(-pot);
			msg = "You lost! " + pot + " chips added to computer's stack";
		} else {
			user.addChips(pot / 2);
			bot.addChips(pot / 2);
			msg = "Split pot!";
		}
		pot = 0;
		deck.addAll(user.getCards());
		deck.addAll(bot.getCards());
		deck.addAll(communityCards);
		user.getCards().clear();		
		bot.getCards().clear();		
		communityCards.clear();
		return msg;
	}

}
