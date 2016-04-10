package actors;

import game.Card;
import game.Deck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import messages.Messages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import utils.CardUtils;
import data.DataGrid;
import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.actor.UntypedActor;

public class DealerActor extends UntypedActor {

	private static final Logger log = LogManager.getLogger();
	private static final int DEALER_HIT_THRESHOLD = 16;

	private List<ActorRef> players;
	private Deck cardsDeck;
	private boolean gameHasStarted;
	private Card holeCard;
	private int betCounter;
	private int bank;

	public DealerActor() {
		this.players = new ArrayList<ActorRef>();
		this.gameHasStarted = false;
		this.bank = 100000;
	}

	@Override
	public void onReceive(Object message) throws Exception {
		log.debug("Dealer {} received the message {}, from {}", getSelf().path().name(), message, getSender().path().name());
		if (message instanceof String) {
			if (!gameHasStarted) {
				switch ((String) message) {
				case Messages.START_GAME:
					startBlackjack();
					break;
				case Messages.REGISTER_PLAYER:
					register();
					break;
				default:
					unhandled(message);
				}
			} else {
				switch ((String) message) {
				case Messages.BUSTED:
					playerBusted();
					break;
				case Messages.PLAY_TURN:
					endRound();
					break;
				case Messages.HIT:
					hitHandler();
					break;
				case Messages.BET_DONE:
					applyBet();
					break;
				case Messages.QUIT:
					removePlayer();
					break;
				case Messages.STOP_GAME:
					stopBlackJack();
					break;
				default:
					unhandled(message);
				}
			}
		} else if (message instanceof Terminated) { // when a PlayerActor terminates
			removePlayer();
		} else
			unhandled(message);
	}

	private void playerBusted() {
		log.info("Removing {}'s cards from the table", getSender().path().name());
		Map<ActorRef, List<Card>> cardsOnTable = DataGrid.getInstance().getCardsOnTable();
		cardsOnTable.remove(getSender());
		Map<ActorRef, Integer> betsOnTable = DataGrid.getInstance().getBetsOnTable();
		takeMoney(betsOnTable, getSender());
		Queue<ActorRef> turns = DataGrid.getInstance().getTurnsQueue();
		turns.remove().tell(Messages.PLAY_TURN, getSelf());
	}

	private void takeMoney(Map<ActorRef, Integer> betsOnTable, ActorRef ref) {
		Integer playerBet = betsOnTable.remove(ref);
		this.bank += playerBet;
	}

	private void endRound() {
		Map<ActorRef, List<Card>> cardsOnTable = DataGrid.getInstance().getCardsOnTable();
		List<Card> cards = cardsOnTable.get(getSelf());
		cards.add(holeCard);
		log.info("The hole card is: {}", holeCard);
		cardsOnTable.put(getSelf(), cards);
		Integer dealerHandPoints = CardUtils.calculatePoints(cards);
		while (dealerHandPoints <= DEALER_HIT_THRESHOLD) {
			Card nextCard = this.cardsDeck.nextCard();
			log.info("Dealer HIT: {}", nextCard);
			cards.add(nextCard);
			cardsOnTable.put(getSelf(), cards);
			dealerHandPoints += nextCard.getValue();
		}
		log.info("Dealer has the following cards: {}, with hand weight: {}", cards, dealerHandPoints);
		Map<ActorRef, Integer> betsOnTable = DataGrid.getInstance().getBetsOnTable();
		if (dealerHandPoints > 21) {
			log.info("Dealer BUSTED");
			for (ActorRef a : cardsOnTable.keySet()){
				if (!a.equals(getSelf())){
					giveMoney(betsOnTable, a);
				}
			}
			log.info("Dealer has now a bank of: {}$", this.bank);
		} else {
			handleResults(cardsOnTable, betsOnTable, dealerHandPoints);
		}
		log.info("--- ROUND END ---");
		startRound();
	}

	private void handleResults(Map<ActorRef, List<Card>> cardsOnTable, Map<ActorRef, Integer> betsOnTable, Integer dealerHandPoints) {
		ArrayList<ActorRef> playersToHandleResults = new ArrayList<>(cardsOnTable.keySet());
		playersToHandleResults.remove(getSelf());
		for (ActorRef player : playersToHandleResults) {
			List<Card> playerCards = cardsOnTable.get(player);
			int comparison = CardUtils.calculatePoints(playerCards).compareTo(dealerHandPoints);
			if (comparison > 0) {
				log.info("Player {} wins!", player.path().name());
				giveMoney(betsOnTable, player);
			} else if (comparison < 0) {
				log.info("Player {} loses!", player.path().name());
				takeMoney(betsOnTable, player);
			} else {
				log.info("Player {} ties!", player.path().name());
				//TODO: implement tie case
			}
		}
	}

	private void giveMoney(Map<ActorRef, Integer> betsOnTable, ActorRef player) {
		Integer win = betsOnTable.get(player) * 2;
		betsOnTable.put(player, win);
		this.bank -= win;
		log.info("Dealer tells GRAB_WIN to Player {}", player.path().name());
		player.tell(Messages.GRAB_WIN, getSelf());
	}

	private void hitHandler() {
		Map<ActorRef, List<Card>> cardsOnTable = DataGrid.getInstance().getCardsOnTable();
		List<Card> senderCards = cardsOnTable.get(getSender());
		Card hitCard = this.cardsDeck.nextCard();
		log.info("Dealer is deals card {} to {}", hitCard, getSender().path().name());
		senderCards.add(hitCard);
		cardsOnTable.put(getSender(), senderCards);
		getSender().tell(Messages.PLAY_TURN, getSelf());
	}

	private void removePlayer() {
		log.info("Player {} has quit", getSender().path().name());
		this.getContext().unwatch(getSender());
		this.players.remove(getSender());
		this.betCounter--;
		DataGrid.getInstance().getCardsOnTable().remove(getSender());
		DataGrid.getInstance().getBetsOnTable().remove(getSender());
		applyBet();
	}

	private void applyBet() {
		log.info("applying bet from {}", getSender().path().name());
		if (++this.betCounter == players.size()) {
			log.info("Starting to deal cards to players");
			Map<ActorRef, List<Card>> cardsOnTable = DataGrid.getInstance().getCardsOnTable();
			cardsOnTable.clear();
			dealCardsToPlayers(cardsOnTable);
			cardsOnTable.put(getSelf(), new ArrayList<Card>(Collections.singletonList(this.cardsDeck.nextCard())));
			dealCardsToPlayers(cardsOnTable);
			Queue<ActorRef> turns = DataGrid.getInstance().getTurnsQueue();
			for (Map.Entry<ActorRef, List<Card>> a : sortMapByValue(cardsOnTable).entrySet()){	
				if(!a.equals(getSelf())){
					turns.add(a.getKey());
				}
			}
			turns.add(getSelf());
			turns.remove().tell(Messages.PLAY_TURN, getSelf());
		}
	}

	private void dealCardsToPlayers(Map<ActorRef, List<Card>> cardsOnTable) {
		for (ActorRef p : this.players){
			List<Card> cards = cardsOnTable.get(p);
			if (cards == null) {
				cards = new ArrayList<Card>();
			}
			cards.add(this.cardsDeck.nextCard());
			cardsOnTable.put(p, cards);
		}
	}

	private void register() {
		this.getContext().watch(getSender());
		this.players.add(getSender());
		if (this.players.size() >= 1 && !gameHasStarted){
			getSelf().tell(Messages.START_GAME, getSelf());
		}
	}

	private void stopBlackJack() {
		this.gameHasStarted = false;
		this.players = new ArrayList<ActorRef>();
	}

	private void startRound() {
		if (this.players.size() >= 1) {
			log.info("--- ROUND START ---");
			this.betCounter = 0;
			this.cardsDeck = new Deck();
			this.holeCard = this.cardsDeck.nextCard();
			DataGrid.getInstance().getCardsOnTable().clear();
			DataGrid.getInstance().getBetsOnTable().clear();
			DataGrid.getInstance().getTurnsQueue().clear();
			for (ActorRef a : this.players){
				a.tell(Messages.PLACE_BET, getSelf());
			}
		} else {
			stopBlackJack();
		}
	}

	private void startBlackjack() {
		this.gameHasStarted = true;
		startRound();
	}

	@Override
	public void preStart() throws Exception {
		log.info("Starting...");
	}

	private <K, V extends Comparable<? super V>> Map<ActorRef, List<Card>> sortMapByValue( Map<ActorRef, List<Card>> map ){
		List<Map.Entry<ActorRef, List<Card>>> list = new LinkedList<>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<ActorRef, List<Card>>>(){
			@Override
			public int compare( Map.Entry<ActorRef, List<Card>> o1, Map.Entry<ActorRef, List<Card>> o2 ){
				return CardUtils.calculatePoints(o2.getValue()).compareTo(CardUtils.calculatePoints(o1.getValue()));
			}
		});

		Map<ActorRef, List<Card>> result = new LinkedHashMap<>();
		for (Map.Entry<ActorRef, List<Card>> entry : list){
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

}