package ro.p2p.node.cli.commands;

import ro.p2p.node.app.CommandDispatcher.RunnableCommand;
import ro.p2p.node.app.NodeRuntime;

public class SendMessageCommand implements RunnableCommand {

    private final NodeRuntime runtime;

    public SendMessageCommand(NodeRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public boolean run(String[] args) {
        if (args.length < 2 || args[1].isBlank()) {
            System.out.println("Usage: send <peerId> <message>");
            return true;
        }
        String messageId = runtime.sendMessage(args[0], args[1]);
        System.out.println("Sent encrypted message " + messageId + " to " + args[0]);
        return true;
    }
}
