package osgi;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceListener;
import org.osgi.util.tracker.ServiceTracker;

import akka.actor.ActorSystem;
import akka.osgi.ActorSystemActivator;

import org.osgi.framework.ServiceEvent;

import osgi.services.DealerServices;
import osgi.services.DealerServicesImpl;

public class Activator extends ActorSystemActivator implements BundleActivator, ServiceListener{
	
	private List<ServiceTracker> serviceTrackers;

	public void start(BundleContext context) {
		serviceTrackers = new LinkedList<ServiceTracker>();
		Hashtable<String, String> properties = new Hashtable<>();
		properties.put("GP", "BlackjackExercise-engine");
		context.registerService(DealerServices.class.getName(), new DealerServicesImpl(), properties);
		//context.registerService(PlayerServices.class.getName(), new PlayerServicesImpl(), properties);
		System.out.println("Starting to listen for service events.");
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

	@Override
	public void configure(BundleContext context, ActorSystem system) {
		registerService(context, system);	
	}
}