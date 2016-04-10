package osgi.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import com.typesafe.config.ConfigFactory;

import data.DataGrid;
import scala.Option;
import actors.DealerActor;
import akka.actor.ActorSystem;
import akka.actor.ExtendedActorSystem;
import akka.actor.Props;
import akka.osgi.OsgiActorSystemFactory;

public class DealerServicesImpl implements DealerServices{

	private static final Logger log = LogManager.getLogger();

	@Override
	public boolean instantiate(String actorSystemName, String dealerID) {
		boolean result = false;

		try {
			ClassLoader loader = OsgiActorSystemFactory.akkaActorClassLoader();
			Option<ClassLoader> option = Option.apply(loader);
			BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
			OsgiActorSystemFactory factory = new OsgiActorSystemFactory(context, option, ConfigFactory.load());
			ActorSystem system = factory.createActorSystem(actorSystemName);
			DataGrid.getInstance().addActorsSystem((ExtendedActorSystem) system);
			system.actorOf(Props.create(DealerActor.class), dealerID);
			context.registerService(ActorSystem.class.getName(), system, null);
			result = true;
			log.info("Dealer instantiation successful!");
		} catch (Throwable t) {
			t.printStackTrace();
			System.out.println(t.getMessage());
			log.error("Dealer instantiation failed");
		}

		return result;

		/*  try {
	            System.setProperty("config.resource", "application-dealer.conf");
	            ActorSystem system = ExtendedActorSystem.create(actorSystemName);
	            DataGrid.getInstance().addActorsSystem((ExtendedActorSystem) system);
	            system.actorOf(Props.create(DealerActor.class), dealerID);
	            log.info("Dealer instantiation successful!");
	            result = true;
	        } catch (Exception e) {
	            log.error("Dealer instantiation failed" + e.getMessage());
	        }
	        return result;*/
	}

}