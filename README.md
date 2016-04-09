# BlackjackExercise
A Java implementation of a simplified Blackjack multiplayer game, built with Akka, Hazelcast and Felix.

Running Dealer and Player main classes manually without Felix:
- From pom's folder:
  1) mvn clean install <br />
  2) mvn exec:java@dealer <br />
  3) mvn exec:java@player <br />
  4) repeat step 3) to add other players <br />
