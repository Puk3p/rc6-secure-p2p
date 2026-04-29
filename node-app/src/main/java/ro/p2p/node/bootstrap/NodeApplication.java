package ro.p2p.node.bootstrap;

import java.nio.file.Path;
import ro.p2p.node.app.CommandDispatcher;
import ro.p2p.node.app.NodeRuntime;
import ro.p2p.node.app.PeerMeshManager;
import ro.p2p.node.cli.CliRunner;
import ro.p2p.node.config.ConfigLoader;
import ro.p2p.node.config.NodeConfig;

public class NodeApplication {

    public static void main(String[] args) throws Exception {
        Path configPath = resolveConfigPath(args);
        NodeConfig config = new ConfigLoader().load(configPath);

        try (NodeRuntime runtime =
                new NodeRuntime(
                        config.getNodeId(),
                        message -> System.out.println("\n[message] " + message))) {
            runtime.start(config.getPort());
            System.out.println(
                    "Started "
                            + config.getNodeId()
                            + " on "
                            + config.getHost()
                            + ':'
                            + runtime.getLocalPort());

            PeerMeshManager meshManager = new PeerMeshManager(runtime, config.getPeers());
            PeerMeshManager.MeshResult result = meshManager.connectConfiguredPeers();
            if (!result.getConnected().isEmpty()) {
                System.out.println("Connected peers: " + String.join(", ", result.getConnected()));
            }
            if (!result.getFailed().isEmpty()) {
                System.out.println(
                        "Peers not available yet: " + String.join(", ", result.getFailed()));
                System.out.println("They can still connect inbound when started.");
            }

            new CliRunner(new CommandDispatcher(runtime, meshManager)).run();
        }
    }

    private static Path resolveConfigPath(String[] args) {
        if (args.length == 1) {
            return Path.of(args[0]);
        }
        if (args.length == 2 && "--config".equals(args[0])) {
            return Path.of(args[1]);
        }
        System.out.println("Usage: java -jar node-app.jar <config-file>");
        System.out.println("   or: java -jar node-app.jar --config <config-file>");
        System.out.println("Example: java -jar node-app.jar node-configs/node-a.properties");
        System.exit(1);
        return Path.of("node-configs/node-a.properties");
    }
}
