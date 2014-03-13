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
			communityCards.add(deal());
			communityCards.add(deal());
			communityCards.add(deal());
		} else if(communityCards.size() < 5)
			communityCards.add(deal());
	}

	/**
	 * Deal out cards to players and start the round
	 */
	public void setupHand() {
		Collections.shuffle(deck, random);
		for (int i = 0; i < 2; i++) {
			user.getCards().add(deal());
			bot.getCards().add(deal());
		}
	 }
	
	public Card deal() {
		return deck.remove(deck.size() - 1);
	}
	
	/**
	 * List because there is a chance that it is a split pot.
	 * @return list of players who won the pot.
	 */
	public List<Player> getWinners() {
		List<Player> winners = new ArrayList<Player>();
		
		List<Card> userCards = new ArrayList<Card>(user.getCards());
		userCards.addAll(communityCards);
		List<Card> botCards = new ArrayList<Card>(bot.getCards());
		botCards.addAll(communityCards);
		
		int userRank = Evaluator.evaluate(userCards);
		int botRank = Evaluator.evaluate(botCards);
		
		if(userRank >= botRank)
			winners.add(user);
		if(userRank <= botRank)
			winners.add(bot);
		return winners;
	}

	/**
	 * Reset the deck, clear players' hands, award chips to the winner(s), and
	 * reset the pot.
	 * 
	 * @return message to alert user of outcome
	 */
	public void endHand() {
		List<Player> winners = getWinners();
		for(Player player : winners) {
			player.addChips(pot / winners.size());
		}
		pot = 0;
		deck.addAll(user.getCards());
		deck.addAll(bot.getCards());
		deck.addAll(communityCards);
		user.getCards().clear();		
		bot.getCards().clear();		
		communityCards.clear();
	}
}
