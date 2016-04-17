package osgi;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceEvent;
import org.osgi.util.tracker.ServiceTracker;

import osgi.services.DealerServices;

public class Activator implements BundleActivator, ServiceListener {

	private List<ServiceTracker> serviceTrackers;
	
	public void start(BundleContext context) {
		serviceTrackers = new LinkedList<ServiceTracker>();
		Hashtable<String, String> properties = new Hashtable<>();
		properties.put("GP", "BlackjackExercise-client");
		
		ServiceTracker dealerServiceTracker = new ServiceTracker(context, DealerServices.class, null);
		
		serviceTrackers.add(dealerServiceTracker);
		
		dealerServiceTracker.open();
		
		DealerServices dealerServices = (DealerServices) dealerServiceTracker.getService();
		PlayerServices playerServices = new PlayerServicesImpl();
		
		System.out.println("Starting to listen for service events.");
		
		String dealerActorSystemName = "BlackjackExercise" + new Random().nextInt(1000);
		String dealerId = "dealerActor";
		System.out.println(dealerServices.instantiate(dealerActorSystemName, dealerId));
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String userInput = System.console().readLine("Type # of players to play Blackjack: ");
		int numberOfPlayers = Integer.parseInt(userInput);
		for (int i = 0; i <= numberOfPlayers; i++){
			System.out.println(playerServices.instantiate(dealerActorSystemName, dealerId));
		}
	}

	public void stop(BundleContext context) {
		for (ServiceTracker st : serviceTrackers){
			st.close();
		}
		System.out.println("Stopped listening for service events.");
	}

	public void serviceChanged(ServiceEvent event) {
		String[] objectClass = (String[])
				event.getServiceReference().getProperty("objectClass");

		if (event.getType() == ServiceEvent.REGISTERED) {
			System.out.println("Ex1: Service of type " + objectClass[0] + " registered.");
		}
		else if (event.getType() == ServiceEvent.UNREGISTERING) {
			System.out.println("Ex1: Service of type " + objectClass[0] + " unregistered.");
		}
		else if (event.getType() == ServiceEvent.MODIFIED) {
			System.out.println("Ex1: Service of type " + objectClass[0] + " modified.");
		}
	}
}