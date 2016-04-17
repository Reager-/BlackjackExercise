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

import messages.*;
import utils.CardUtils;
import data.DataGrid;
import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Procedure;

public class DealerActor extends UntypedActor {

	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	private static final int DEALER_HIT_THRESHOLD = 16;

	private Deck cardsDeck;
	private Card holeCard;
	private int betCounter;
	private int bank;

	public DealerActor() {
		this.bank = 100000;
	}

	Procedure<Object> gameNotStarted = new Procedure<Object>() {
		@Override
		public void apply(Object message) {
			if (message instanceof MessageStartGame){
				getContext().become(gameStarted);
				startBlackjack();
			}
			else if(message instanceof MessageStopGame){
				log.info("Game is already stopped!");
			} else{
				unhandled(message);
			}
		}
	};

	Procedure<Object> gameStarted = new Procedure<Object>() {
		@Override
		public void apply(Object message) {
			if (message instanceof MessageStartGame){
				log.info("Game already started, waiting for players!");
			} else if(message instanceof MessageStopGame){
				getContext().become(gameNotStarted);
				stopBlackJack();
			} else if(message instanceof MessageStartRound){
				startRound();
			} else if(message instanceof MessageBusted){
				playerBusted();
			} else if(message instanceof MessageLastPlayerPlayed){
				endRound();
			} else if(message instanceof MessageHit){
				hitHandler();
			} else if(message instanceof MessageBetDone){
				applyBet();
			} else if(message instanceof MessageQuit){
				removePlayer();
			} else if (message instanceof Terminated){
				removePlayer();
			} else{
				unhandled(message);
			}
		}
	};

	@Override
	public void onReceive(Object message) throws Exception {
		log.debug("Dealer {} received the message {}, from {}", getSelf().path().name(), message, getSender().path().name());
		if (message instanceof MessageStartGame){
			getContext().become(gameStarted);
			startBlackjack();
		} else if(message instanceof MessageStopGame){
			getContext().become(gameNotStarted);
			stopBlackJack();
		} else if(message instanceof MessageRegisterPlayer){
			register();
		} else{
			unhandled(message);
		}
	}

	private void playerBusted() {
		log.info("Removing {}'s cards from the table", getSender().path().name());
		Map<ActorRef, Integer> betsOnTable = DataGrid.getInstance().getBetsOnTable();
		takeMoney(betsOnTable, getSender());
		Map<ActorRef, List<Card>> cardsOnTable = DataGrid.getInstance().getPlayerCardsOnTable();
		cardsOnTable.remove(getSender());
		if (cardsOnTable.size()>1){
			Queue<ActorRef> turns = DataGrid.getInstance().getTurns();
			turns.remove().tell(new MessagePlayTurn(), getSelf());
		} else{
			endRound();
		}
	}

	private void takeMoney(Map<ActorRef, Integer> betsOnTable, ActorRef ref) {
		Integer playerBet = betsOnTable.remove(ref);
		this.bank += playerBet;
		log.info("Dealer took {} $ from the table and now has a bank of {} $", playerBet, this.bank);
	}

	private void endRound() {
		Map<ActorRef, List<Card>> cardsOnTable = DataGrid.getInstance().getPlayerCardsOnTable();
		Map<ActorRef, List<Card>> dealerCardsOnTable = DataGrid.getInstance().getDealerCardsOnTable();
		Map<ActorRef, Integer> betsOnTable = DataGrid.getInstance().getBetsOnTable();
		if (cardsOnTable.size()>0){
			List<Card> cards = dealerCardsOnTable.get(getSelf());
			cards.add(holeCard);
			log.info("The hole card is: {}", holeCard);
			dealerCardsOnTable.put(getSelf(), cards);
			Integer dealerHandPoints = CardUtils.calculatePoints(cards);
			while (dealerHandPoints <= DEALER_HIT_THRESHOLD) {
				Card nextCard = this.cardsDeck.nextCard();
				log.info("Dealer HIT: {}", nextCard);
				cards.add(nextCard);
				dealerCardsOnTable.put(getSelf(), cards);
				dealerHandPoints += nextCard.getValue();
			}
			log.info("Dealer has the following cards: {}, with points: {}", cards, dealerHandPoints);
			if (dealerHandPoints > 21) {
				log.info("Dealer BUSTED");
				for (ActorRef a : cardsOnTable.keySet()){
					if (DataGrid.getInstance().getPlayers().contains(a)){
						giveMoney(betsOnTable, a);
					}
				}
				log.info("Dealer has now a bank of: {}$", this.bank);
			} else {
				handleResults(cardsOnTable, betsOnTable, dealerHandPoints);
			}
		}
		log.info("--- ROUND END ---");
		getSelf().tell(new MessageStartRound(), getSelf());
	}

	private void handleResults(Map<ActorRef, List<Card>> cardsOnTable, Map<ActorRef, Integer> betsOnTable, Integer dealerHandPoints) {
		ArrayList<ActorRef> playersToHandleResults = new ArrayList<>(cardsOnTable.keySet());
		for (ActorRef player : playersToHandleResults) {
			List<Card> playerCards = cardsOnTable.get(player);
			int comparison = CardUtils.calculatePoints(playerCards).compareTo(dealerHandPoints);
			if (comparison > 0) {
				log.info("Player {} wins!", player.path().name());
				giveMoney(betsOnTable, player);
			} else if (comparison < 0) {
				log.info("Player {} loses!", player.path().name());
				takeMoney(betsOnTable, player);
			} else if (comparison == 0){
				log.info("Player {} ties!", player.path().name());
				takeMoney(betsOnTable, player);
				//TODO: implement tie case
			}
		}
	}

	private void giveMoney(Map<ActorRef, Integer> betsOnTable, ActorRef player) {
		Integer playerBetAmount = betsOnTable.get(player);
		Integer playerWinAmount = playerBetAmount * 2;
		betsOnTable.put(player, playerWinAmount);
		this.bank -= playerBetAmount;
		log.info("Dealer tells GRAB_WIN to Player {}", player.path().name());
		player.tell(new MessageGrabWin(), getSelf());
	}

	private void hitHandler() {
		Map<ActorRef, List<Card>> cardsOnTable = DataGrid.getInstance().getPlayerCardsOnTable();
		List<Card> senderCards = cardsOnTable.get(getSender());
		Card hitCard = this.cardsDeck.nextCard();
		log.info("Dealer deals card {} to {}", hitCard, getSender().path().name());
		senderCards.add(hitCard);
		cardsOnTable.put(getSender(), senderCards);
		getSender().tell(new MessagePlayTurn(), getSelf());
	}

	private void removePlayer() {
		log.info("Player {} has quit", getSender().path().name());
		this.getContext().unwatch(getSender());
		DataGrid.getInstance().getPlayerCardsOnTable().remove(getSender());
		DataGrid.getInstance().getBetsOnTable().remove(getSender());
		DataGrid.getInstance().getPlayers().remove(getSender());
		if (this.betCounter == DataGrid.getInstance().getPlayers().size()) {
			this.betCounter--;
			applyBet();
		}
	}

	private void applyBet() {
		this.betCounter++;
		if (this.betCounter > 0 && this.betCounter == DataGrid.getInstance().getPlayers().size()) {
			log.info("applying bet from {}", getSender().path().name());
			log.info("Starting to deal cards to players");
			Map<ActorRef, List<Card>> cardsOnTable = DataGrid.getInstance().getPlayerCardsOnTable();
			Card holeCard = this.cardsDeck.nextCard();
			dealCardsToPlayers(cardsOnTable);
			Queue<ActorRef> turns = DataGrid.getInstance().getTurns();
			for (Map.Entry<ActorRef, List<Card>> a : sortMapByValue(cardsOnTable).entrySet()){	
				turns.add(a.getKey());
			}
			List<Card> dealerCards = new ArrayList<Card>();
			dealerCards.add(holeCard);
			Map<ActorRef, List<Card>> dealerCardsOnTable = DataGrid.getInstance().getDealerCardsOnTable();
			dealerCardsOnTable.put(getSelf(), dealerCards);
			turns.remove().tell(new MessagePlayTurn(), getSelf());
		} else if (this.betCounter == 0){
			getSelf().tell(new MessageStopGame(), getSelf());
		}
	}

	private void dealCardsToPlayers(Map<ActorRef, List<Card>> cardsOnTable) {
		for (ActorRef p : DataGrid.getInstance().getPlayers()){
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
		DataGrid.getInstance().getPlayers().add(getSender());
		getSelf().tell(new MessageStartGame(), getSelf());
	}

	private void stopBlackJack() {
		log.info("Stopping Blackjack game!");
		clearHazelcastDataGrid();
	}

	private void clearHazelcastDataGrid() {
		DataGrid.getInstance().getDealerCardsOnTable().clear();
		DataGrid.getInstance().getPlayerCardsOnTable().clear();
		DataGrid.getInstance().getBetsOnTable().clear();
		DataGrid.getInstance().getTurns().clear();
		DataGrid.getInstance().getPlayers().clear();
	}

	private void startRound() {
		if (DataGrid.getInstance().getPlayers().size() >= 1) {
			log.info("--- ROUND START ---");
			this.betCounter = 0;
			this.cardsDeck = new Deck();
			this.holeCard = this.cardsDeck.nextCard();
			DataGrid.getInstance().getDealerCardsOnTable().clear();
			DataGrid.getInstance().getPlayerCardsOnTable().clear();
			DataGrid.getInstance().getBetsOnTable().clear();
			DataGrid.getInstance().getTurns().clear();
			for (ActorRef a : DataGrid.getInstance().getPlayers()){
				a.tell(new MessagePlaceBet(), getSelf());
			}
		} else {
			getSelf().tell(new MessageStopGame(), getSelf());
		}
	}

	private void startBlackjack() {
		log.info("Starting Blackjack game!");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		startRound();
	}

	@Override
	public void preStart() throws Exception {
		log.info("DealerActor.preStart()...");
	}

	@Override
	public void postStop() throws Exception {
		log.info("DealerActor.postStop()...");
	}

	private <K, V extends Comparable<? super V>> Map<ActorRef, List<Card>> sortMapByValue( Map<ActorRef, List<Card>> map ){
		List<Map.Entry<ActorRef, List<Card>>> list = new LinkedList<Map.Entry<ActorRef, List<Card>>>(map.entrySet());
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