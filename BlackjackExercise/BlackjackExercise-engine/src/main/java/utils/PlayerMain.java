package utils;

import actors.PlayerActor;
import akka.actor.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import data.DataGrid;

import java.util.Random;

public class PlayerMain {
    private static final Logger log = LogManager.getLogger();

    public static void main(String[] args) {
        log.info("PlayerMain was started.");

        System.setProperty("config.resource", "application-player.conf");
        ActorSystem system = ExtendedActorSystem.create("BlackJackExercise");
        DataGrid.getInstance().addActorsSystem((ExtendedActorSystem)system);
        ActorSelection actorSelection = system.actorSelection("akka.tcp://BlackJackExercise@127.0.0.1:8469/user/dealerActor");
        system.actorOf(Props.create(PlayerActor.class, actorSelection), "player"+ new Random().nextInt(1000));

        log.info("Exiting PlayerMain");
    }
}