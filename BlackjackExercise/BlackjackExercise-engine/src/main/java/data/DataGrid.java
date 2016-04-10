package data;

import akka.actor.ActorRef;
import akka.actor.ExtendedActorSystem;
import akka.serialization.JavaSerializer;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import game.Card;

import java.util.List;
import java.util.Map;
import java.util.Queue;

public final class DataGrid {

    private static final String BETS_ON_TABLE = "BetsOnTable";
    private static final String CARDS_ON_TABLE = "CardsOnTable";
    private static final String TURNS = "Turns";

    private static class Initializer {
        static DataGrid INSTANCE = new DataGrid();
    }

    public static DataGrid getInstance() {
        return Initializer.INSTANCE;
    }

    private final HazelcastInstance hazelcastInstance;
    public ExtendedActorSystem system;
    
    private DataGrid() {
        Config config = new Config();
        //config.setProperty("hazelcast.logging.type", "slf4j"); //uncomment this to enable hazelcast logging
        this.hazelcastInstance = Hazelcast.newHazelcastInstance(config);
    }
    
    public void addActorsSystem(ExtendedActorSystem system) {
        this.system = system;
    }

    public <K, V> Map<K, V> getMap(String mapName) {
    	JavaSerializer.currentSystem().value_$eq(system);
        return hazelcastInstance.getMap(mapName);
    }

    public <E> Queue<E> getQueue(String s) {
    	JavaSerializer.currentSystem().value_$eq(system);
        return hazelcastInstance.getQueue(s);
    }

    public Map<ActorRef, Integer> getBetsOnTable(){
        return getMap(DataGrid.BETS_ON_TABLE);
    }
    
    public Map<ActorRef, List<Card>> getCardsOnTable(){
        return getMap(DataGrid.CARDS_ON_TABLE);
    }
    
    public Queue<ActorRef> getTurnsQueue(){
        return getQueue(DataGrid.TURNS);
    }

}