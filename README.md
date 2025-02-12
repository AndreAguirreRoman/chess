# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```


### LINK TO SEQUENCE DIAGRAM


Click me for sequence [diagram]!

[diagram]: https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAHZM9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtAzqBJoIei0azF5vDgHYsgwr5ks9K+KDlB+SwAucBaymqwEwAgB48rCcGHqi6KxNiCaGC6YZumSFKwgaAByo4mkS4YWhyMAAGY8rahGjpoOhJgS2EsuUQGhqRbqdhhbHlqOMAAGQCTAoHvDAsIAGqUEglFhDJMCbGAICpGo6FyphyYQSW-ZOAA3HalABkGs7oNi-4glBJTlAaRl1o2pnthZhTZD2BlwtOJloNiHFMlxTFFNBCpKggMAAD6hTAlDeLAAAsTgAIxhRFUXQFRqUcBAahoAA5MwKAAB7Qo6zqnC5GDlHAEAcIYECUZFUDRdZHREaMMgEQAoklMA8iA0CQuAIn6K8SymGYnCrt4fiBF4KDoHuB6+Mwx7pJkmBlcwlnXtI7W7u19Ttc0LQPqoT7dB59loI5bLmeU51zpg5lsjxsELb6tmpJAc5qdBGosaSMDkmA72eSRvnSpGFHUbysYzhdDGdq6rEwHd6A+WaEaWeCyPGXDGG-ZxSPVdwmTAxdoPo+R5RExShik19aNkf5N0wIqypdQArAOXVxYl4UwHFMUM35mlAiW2a5g9WlOQFVkwJCOYIFgP6dutfZc-JObhpRwVdSlsDyWgEDMJRvjjWNK6eJNG6Qrau7QjAADio6sstp5reeG2XtQ14O3th32KOZ04-dP4FMzKOXY9mPygDFIw9ASAAF4oBwZDyGA7R0w5GEFIj-2AzDWdeUL4OFJaVE0TAMOefD-nPazKp40zUvlHrMCc043MJV3gtR6VHutw1qWG8w04J8ntqpaoECjH6PKOMk5gwMk3gOC4tfRzBEfFYmzEE-nFJO6MqhF+TjNlxRgNH2oduxBve9g-nzu31gecY3+LcA8-9vi4rkui9LH8VwA7H3KBUUsICUAAElpCzAAOorUMHRN8MAABCCBQANjci1FAsxr44L2I0b8V5paqxgDpboEDnZgKoaMGB8DEHYNHLMdBmCmEoPwaOQhBxzYTXXIEbAPgoDYG4PAXUmRHb8VdqtdaT0SFgNqA0f2gdgjB3QEOSBBCroi1TOUGA+jsZ2TnJQ2hKAtFRy9k6GOno9TX1Pv-VMcjAqxzAPHKAScU5pzkJnCO30rEoFzn9d0LjC6+JLuaCG5QoYxjUZdYw3F1JBWVDvDSH8AGD2iu3LmfMeY9wcZBfuOQwAZOHkbQxqRx4pxgFPGeFJ0gOGXkvFeiR7Dr3iXXRJ5Sa5NwfujCq4iUB2M0cwrpZNwkRgvrdG01975nHScjaZ-Ff55ilgk72aYeiQJgcQ9ZKsB7kK5j0a+2zeGW34QESwKBlQQGSDAAAUhAHkkjWoBB0BgkADZ3ZFKcbLaolI7wtEgUHIx6jegiOAJcqAlU4JQFmFs6QOzjg6OBFccFkLoXQDwaOE5FiZZYwLkGSpqd04+NiX40QgT97BIJX6exb9KYV2hrE2Z9dgopM7MzNuHcu68wigLfJHZ-JkLbiPcpRLqmwGnrPepC8mmr1aSyzp28ekyCCeUAAVo8tAQzsWwJEhg9FEAYVnz8pMhZHAqgGsoPWGZ7TkUlk1Tya+6YFYrIAWs2WyztGFNcjpUwy4+FTQCF4CFXYvSwGANgERhB4iJBSIgr5S9NppgqNtXa+1DrGG0Wk3RMgdp7XagK38eKY7U0yO1Eolg54OHZVhKlVMrk00ZHoAw4yGVlrEHa5y+y-UYXNkAA
