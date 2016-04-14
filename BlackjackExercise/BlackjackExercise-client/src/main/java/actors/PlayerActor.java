package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import utils.CardUtils;
import data.DataGrid;
import game.Card;

import java.util.List;
import java.util.Map;
import java.util.Queue;

import messages.*;

public class PlayerActor extends UntypedActor {

	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	private int bankroll;
	private ActorSelection dealerActor;

	public PlayerActor(ActorSelection dealerActor) {
		this.bankroll = 1000; // default
		this.dealerActor = dealerActor;
		System.out.println("PlayerCreated");
	}

	@Override
	public void preStart() throws Exception {
		log.info("PlayerActor.preStart()...");
		this.dealerActor.tell(new MessageRegisterPlayer(), getSelf());
	}

	@Override
	public void postStop() throws Exception {
		log.info("PlayerActor.postStop()...");
	}

	@Override
	public void onReceive(Object message) throws Exception {
		log.info("Player {} received the message {}, from {}", getSelf().path().name(), message, getSender().path().name());

		if (message instanceof MessagePlaceBet){
			placeBet();
		} else if(message instanceof MessagePlayTurn){
			playTurn();
		} else if(message instanceof MessageGrabWin){
			grabWin();
		} else{
			unhandled(message);
		}
	}

	private void placeBet() {
		Map<ActorRef, Integer> map = DataGrid.getInstance().getBetsOnTable();
		if (this.bankroll > 0) {
			int moneyToBet = this.bankroll > 100 ? 100 : 10; // fixed amount of bet, TODO: take input from keyboard?
			log.info("Player {} is betting {} out of {}", getSelf().path().name(), moneyToBet, bankroll);
			map.put(getSelf(), moneyToBet);
			bankroll -= moneyToBet;
			this.dealerActor.tell(new MessageBetDone(), getSelf());
		} else {
			log.info("{}: Player tells QUIT to Dealer", getSelf().path().name());
			this.dealerActor.tell(new MessageQuit(), getSelf());
		}
	}

	private void playTurn() {
		List<Card> playerCards = DataGrid.getInstance().getCardsOnTable().get(getSelf());
		log.info("Player {} has the following cards: {}", getSelf().path().name(), playerCards);
		Integer playerHandPoints = CardUtils.calculatePoints(playerCards);
		/*
		 * TODO: take input from keyboard instead of fixed hitting and standing based on points.
		 */
		if(playerHandPoints > 21){
			log.info("Player {} tells BUSTED with {} points to Dealer", getSelf().path().name(), playerHandPoints);
			this.dealerActor.tell(new MessageBusted(), getSelf());
		} else{
			log.info("Player {} has " + playerHandPoints + " points, take an action (H/S): ", getSelf()); 
			String userInput = System.console().readLine();
			if (userInput.equals("H")||userInput.equals("h")||userInput.equals("hit")||userInput.equals("Hit")){
				log.info("Player {} tells HIT with {} points to Dealer", getSelf().path().name(), playerHandPoints);
				this.dealerActor.tell(new MessageHit(), getSelf());
			} else if (userInput.equals("S")||userInput.equals("s")||userInput.equals("stand")||userInput.equals("Stand")){
				log.info("Player {} tells STAND with {} points", getSelf().path().name(), playerHandPoints);
				Queue<ActorRef> turns = DataGrid.getInstance().getTurns();
				turns.remove().tell(new MessagePlayTurn(), getSelf());
			} else {
				log.info("Player {} tells BUSTED to Dealer for not choosing a valid action", getSelf().path().name());
				this.dealerActor.tell(new MessageBusted(), getSelf());
			}
		}
		/*if (playerHandPoints < 17) {
			log.info("Player {} tells HIT with {} points to Dealer", getSelf().path().name(), playerHandPoints);
			this.dealerActor.tell(new MessageHit(), getSelf());
		} else if (playerHandPoints > 21) {
			log.info("Player {} tells BUSTED with {} points to Dealer", getSelf().path().name(), playerHandPoints);
			this.dealerActor.tell(new MessageBusted(), getSelf());
		} else {
			log.info("Player {} tells STAND with {} points", getSelf().path().name(), playerHandPoints);
			Queue<ActorRef> turns = DataGrid.getInstance().getTurns();
			turns.remove().tell(new MessagePlayTurn(), getSelf());
		}*/
	}

	private void grabWin() {
		this.bankroll += DataGrid.getInstance().getBetsOnTable().get(getSelf());
		log.info("Player {} has now {}$", getSelf().path().name(), this.bankroll);
	}

}