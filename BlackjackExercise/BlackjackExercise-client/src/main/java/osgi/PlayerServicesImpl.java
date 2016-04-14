package osgi;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import com.typesafe.config.ConfigFactory;

import actors.PlayerActor;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.ExtendedActorSystem;
import akka.actor.Props;
import akka.osgi.OsgiActorSystemFactory;
import data.DataGrid;
import scala.Option;

public class PlayerServicesImpl implements PlayerServices{

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
			ActorSelection actorSelection = system.actorSelection("akka.tcp://"+ actorSystemName +"@127.0.0.1:50000/user/" + dealerID);
	        system.actorOf(Props.create(PlayerActor.class, actorSelection), "player"+ new Random().nextInt(1000));
			context.registerService(ActorSystem.class.getName(), system, null);
			result = true;
			log.info("Player instantiation successful!");
		} catch (Throwable t) {
			t.printStackTrace();
			System.out.println(t.getMessage());
			log.error("Player instantiation failed");
		}

		return result;
	}

}