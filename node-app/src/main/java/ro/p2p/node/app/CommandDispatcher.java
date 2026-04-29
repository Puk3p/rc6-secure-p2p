package ro.p2p.node.app;

import java.io.IOException;
import java.util.Map;
import ro.p2p.node.cli.commands.ExitCommand;
import ro.p2p.node.cli.commands.HelpCommand;
import ro.p2p.node.cli.commands.PeersCommand;
import ro.p2p.node.cli.commands.SendFileCommand;
import ro.p2p.node.cli.commands.SendMessageCommand;
import ro.p2p.node.cli.parser.CommandParser;
import ro.p2p.node.cli.parser.CommandParser.ParsedCommand;

public class CommandDispatcher {

    private final Map<String, RunnableCommand> commands;

    public CommandDispatcher(NodeRuntime runtime, PeerMeshManager meshManager) {
        commands =
                Map.of(
                        "help", new HelpCommand(),
                        "peers", new PeersCommand(runtime, meshManager),
                        "send", new SendMessageCommand(runtime),
                        "send-file", new SendFileCommand(runtime),
                        "exit", new ExitCommand(),
                        "quit", new ExitCommand());
    }

    public boolean dispatch(String line) throws IOException {
        ParsedCommand parsed = new CommandParser().parse(line);
        if (parsed.command().isEmpty()) {
            return true;
        }
        RunnableCommand command = commands.get(parsed.command());
        if (command == null) {
            System.out.println("Unknown command. Type 'help' for available commands.");
            return true;
        }
        return command.run(parsed.arguments());
    }

    public interface RunnableCommand {
        boolean run(String[] args) throws IOException;
    }
}
