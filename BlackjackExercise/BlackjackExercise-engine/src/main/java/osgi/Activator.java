package osgi;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceEvent;

import osgi.services.DealerServices;
import osgi.services.DealerServicesImpl;

public class Activator implements BundleActivator, ServiceListener {

	public void start(BundleContext context) {
		Hashtable<String, String> properties = new Hashtable<>();
		properties.put("GP", "BlackjackExercise-engine");
		context.registerService(DealerServices.class.getName(), new DealerServicesImpl(), properties);
		context.addServiceListener(this);
		System.out.println("Starting to listen for service events.");
	}

	public void stop(BundleContext context) {
		context.removeServiceListener(this);
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