package ro.p2p.node.cli.commands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import ro.p2p.node.app.CommandDispatcher.RunnableCommand;
import ro.p2p.node.app.NodeRuntime;

public class SendFileCommand implements RunnableCommand {

    private final NodeRuntime runtime;

    public SendFileCommand(NodeRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public boolean run(String[] args) throws IOException {
        if (args.length < 2 || args[1].isBlank()) {
            System.out.println("Usage: send-file <peerId> <path>");
            return true;
        }
        Path path = Path.of(args[1]);
        byte[] data = Files.readAllBytes(path);
        String transferId = runtime.sendFileBytes(args[0], data);
        System.out.println(
                "Sent encrypted file bytes "
                        + transferId
                        + " to "
                        + args[0]
                        + " ("
                        + data.length
                        + " bytes)");
        return true;
    }
}
