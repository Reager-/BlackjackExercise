package osgi.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import actors.DealerActor;
import akka.actor.ActorSystem;
import akka.actor.ExtendedActorSystem;
import akka.actor.Props;
import data.DataGrid;

public class DealerServicesImpl implements DealerServices{

    private static final Logger log = LogManager.getLogger();

	  @Override
	    public boolean instantiate(String actorSytemName, String dealerID) {
	        try {
	            System.setProperty("config.resource", "application-dealer.conf");
	            ActorSystem system = ExtendedActorSystem.create(actorSytemName);
	            DataGrid.getInstance().addActorsSystem((ExtendedActorSystem) system);
	            system.actorOf(Props.create(DealerActor.class), dealerID);
	            log.info("Dealer instantiation successful!");
	            return true;
	        } catch (Exception e) {
	            log.error("Dealer instantiation failed");
	        }
	        return false;
	    }
	  
}