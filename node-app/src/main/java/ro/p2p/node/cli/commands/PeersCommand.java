package ro.p2p.node.cli.commands;

import java.util.Set;
import ro.p2p.node.app.CommandDispatcher.RunnableCommand;
import ro.p2p.node.app.NodeRuntime;
import ro.p2p.node.app.PeerMeshManager;
import ro.p2p.node.config.PeerConfig;

public class PeersCommand implements RunnableCommand {

    private final NodeRuntime runtime;
    private final PeerMeshManager meshManager;

    public PeersCommand(NodeRuntime runtime, PeerMeshManager meshManager) {
        this.runtime = runtime;
        this.meshManager = meshManager;
    }

    @Override
    public boolean run(String[] args) {
        Set<String> connected = runtime.getConnectedPeerIds();
        System.out.println("Configured peers:");
        if (meshManager.getConfiguredPeers().isEmpty()) {
            System.out.println("  none");
        }
        for (PeerConfig peer : meshManager.getConfiguredPeers()) {
            String state = connected.contains(peer.getPeerId()) ? "connected" : "not connected";
            System.out.println("  " + peer + " - " + state);
        }
        System.out.println("Active connections: " + connected.size());
        return true;
    }
}
