# BlackjackExercise
A Java implementation of a simplified Blackjack multiplayer game, built with Akka, Hazelcast and Felix.

Running Dealer and Player main classes manually without Felix: <br />
- From pom's folder: <br />
  1) mvn clean install <br />
  2) mvn exec:java@dealer <br />
  3) mvn exec:java@player <br />
  4) repeat step 3) to add other players <br />

Running from Felix with osgi bundles: <br />
  1) mvn clean install (BlackjackExercise-engine) <br />
  2) transfer target jar (bundle) into felix's "bundle" folder <br />
  3) from felix's main folder run command line: java -jar -Dorg.osgi.framework.bootdelegation=sun.\*,com.sun.\* bin/felix.jar <br />
  4) optional build BlackjackExercise-client and install in felix in order to have a bundle that calls dealer instantiation when it starts <br />
