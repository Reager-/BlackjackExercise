package data;

import akka.actor.ActorRef;
import akka.actor.ExtendedActorSystem;
import akka.serialization.JavaSerializer;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import game.Card;

import java.util.List;
import java.util.Map;
import java.util.Queue;
	
	public class DataGrid {
		/*  private static DataGrid instance = new DataGrid();
	    private static final String SETTINGS_MAP = "casino";
	    private static final String BETS_ON_TABLE = "BetsOnTable";
	    private static final String CARDS_ON_TABLE = "CardsOnTable";
	    private static final String TURNS = "Turns";

	    private HazelcastInstance grid;

	    private Map<String, Object> casino;
	    private Map<ActorRef, Integer> betsOnTable;
	    private Map<ActorRef, List<Card>> cardsOnTable;
	    private Queue<ActorRef> turns;

	    private DataGrid() {
	        Config cfg = new Config();

	        MapConfig mcCasino = new MapConfig();
	        mcCasino.setName(SETTINGS_MAP);
	        mcCasino.setBackupCount(1);
	        mcCasino.setReadBackupData(true);
	        cfg.addMapConfig(mcCasino);
	        MapConfig mcBetsOnTable = new MapConfig();
	        mcBetsOnTable.setName(BETS_ON_TABLE);
	        mcBetsOnTable.setBackupCount(1);
	        mcBetsOnTable.setReadBackupData(true);
	        cfg.addMapConfig(mcBetsOnTable);
	        MapConfig mcCardsOnTable = new MapConfig();
	        mcCardsOnTable.setName(CARDS_ON_TABLE);
	        mcCardsOnTable.setBackupCount(1);
	        mcCardsOnTable.setReadBackupData(true);
	        cfg.addMapConfig(mcCardsOnTable);
	        MapConfig mcTurns = new MapConfig();
	        mcTurns.setName(TURNS);
	        mcTurns.setBackupCount(1);
	        mcTurns.setReadBackupData(true);
	        cfg.addMapConfig(mcTurns);

	        grid = Hazelcast.newHazelcastInstance(cfg);

	        this.casino = grid.getMap(SETTINGS_MAP);
	        this.betsOnTable = grid.getMap(BETS_ON_TABLE);
	        this.cardsOnTable = grid.getMap(CARDS_ON_TABLE);
	        this.turns = grid.getQueue(TURNS);
	    }

	    public static DataGrid getInstance() {
	        return instance;
	    }

	    public Map<String, Object> getCasino() {
			return casino;
		}

		public Map<ActorRef, Integer> getBetsOnTable() {
			return betsOnTable;
		}

		public Map<ActorRef, List<Card>> getCardsOnTable() {
			return cardsOnTable;
		}

		public Queue<ActorRef> getTurns() {
			return turns;
		}

		public void stop() {
	        if (grid != null) {
	            grid.shutdown();
	        }
	    }
	}*/

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
    
    public Queue<ActorRef> getTurns(){
        return getQueue(DataGrid.TURNS);
    }
	}