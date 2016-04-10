package utils;

import actors.DealerActor;
import akka.actor.ActorSystem;
import akka.actor.ExtendedActorSystem;
import akka.actor.Props;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import data.DataGrid;

public class DealerMain {
    private static final Logger log = LogManager.getLogger();

    public static void main(String[] args) {
        log.info("DealerMain was started.");
        
        System.setProperty("config.resource", "application-dealer.conf");
        ActorSystem system = ExtendedActorSystem.create("BlackJackExercise");
        DataGrid.getInstance().addActorsSystem((ExtendedActorSystem) system);
        system.actorOf(Props.create(DealerActor.class), "dealerActor");

        log.info("Exiting DealerMain");
    }

}