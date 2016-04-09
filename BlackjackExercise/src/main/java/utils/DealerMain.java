package utils;

import actors.DealerActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.ExtendedActorSystem;
import akka.actor.Props;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import data.DataGrid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DealerMain {
    private static final Logger log = LogManager.getLogger();

    public static void main(String[] args) {
        log.info("DealerMain was started.");

        System.setProperty("config.resource", "application-dealer.conf");
        ActorSystem system = ExtendedActorSystem.create("BlackJackExercise");
        DataGrid.getInstance().addActorsSystem((ExtendedActorSystem) system);
        ActorRef dealerActor = system.actorOf(Props.create(DealerActor.class), "dealerActor");
        dealerActor.tell(readConsole(), ActorRef.noSender());

        log.info("Exiting DealerMain");
    }

    private static String readConsole() {
        BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
        String result = "Stop";
        try {
            result = bufferRead.readLine();
        } catch (IOException e) {
            log.catching(e);
        }
        return result;
    }
}