package ro.p2p.node.cli;

import java.io.IOException;
import java.util.Scanner;
import ro.p2p.node.app.CommandDispatcher;

public class CliRunner {

    private final CommandDispatcher dispatcher;

    public CliRunner(CommandDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void run() throws IOException {
        System.out.println("Type 'help' for commands.");
        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                System.out.print("> ");
                if (!scanner.hasNextLine()) {
                    return;
                }
                running = dispatcher.dispatch(scanner.nextLine());
            }
        }
    }
}
