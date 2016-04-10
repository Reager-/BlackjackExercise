package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.UntypedActor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import utils.CardUtils;
import data.DataGrid;
import game.Card;

import java.util.List;
import java.util.Map;
import java.util.Queue;

import messages.Messages;

public class PlayerActor extends UntypedActor {
	
    private static final Logger log = LogManager.getLogger();

    private int bankroll;
    private ActorSelection dealerActor;

    public PlayerActor(ActorSelection dealerActor) {
    	this.bankroll = 1000; // default
        this.dealerActor = dealerActor;
    }
    
    @Override
    public void preStart() throws Exception {
        log.info("Starting PlayerActor...");
        this.dealerActor.tell(Messages.REGISTER_PLAYER, getSelf());
    }

    @Override
    public void postStop() throws Exception {
        log.info("Stopping PlayerActor...");
    }
    
    @Override
    public void onReceive(Object message) throws Exception {
        log.debug("Player {} received the message {}, from {}", getSelf().path().name(), message, getSender().path().name());
        if (message instanceof String){
            switch ((String) message) {
                case Messages.PLACE_BET: 
                	placeBet();
                	break;
                case Messages.PLAY_TURN:
                	playTurn();
                	break;
                case Messages.GRAB_WIN:
                	grabWin();
                	break;
                default:
                	unhandled(message);
            }
        } else
            unhandled(message);
    }
    
    private void placeBet() {
        Map<ActorRef, Integer> map = DataGrid.getInstance().getBetsOnTable();
        if (this.bankroll > 0) {
            int moneyToBet = this.bankroll > 100 ? 100 : 10; // fixed amount of bet, TODO: take input from keyboard?
            log.info("Player {} is betting {} out of {}", getSelf().path().name(), moneyToBet, bankroll);
            map.put(getSelf(), moneyToBet);
            bankroll -= moneyToBet;
            this.dealerActor.tell(Messages.BET_DONE, getSelf());
        } else {
            log.info("{}: Player tells QUIT to Dealer", getSelf().path().name());
            this.dealerActor.tell(Messages.QUIT, getSelf());
        }
    }
    
    private void playTurn() {
        List<Card> playerCards = DataGrid.getInstance().getCardsOnTable().get(getSelf());
        log.info("Player {} has the following cards: {}", getSelf().path().name(), playerCards);
        Integer playerHandPoints = CardUtils.calculatePoints(playerCards);
        /*
         * TODO: take input from keyboard instead of fixed hitting and standing based on points.
         */
        if (playerHandPoints < 17) {
            log.info("Player {} tells HIT with {} points to Dealer", getSelf().path().name(), playerHandPoints);
            this.dealerActor.tell(Messages.HIT, getSelf());
        } else if (playerHandPoints > 21) {
            log.info("Player {} tells BUSTED with {} points to Dealer", getSelf().path().name(), playerHandPoints);
            this.dealerActor.tell(Messages.BUSTED, getSelf());
        } else {
            log.info("Player {} tells STAND with {} points", getSelf().path().name(), playerHandPoints);
            Queue<ActorRef> turns = DataGrid.getInstance().getTurnsQueue();
            turns.remove().tell(Messages.PLAY_TURN, getSelf());
        }
    }
    
    private void grabWin() {
        this.bankroll += DataGrid.getInstance().getBetsOnTable().get(getSelf());
        log.info("Player {} has now {}$", getSelf().path().name(), this.bankroll);
    }
    
}