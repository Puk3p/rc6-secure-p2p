package ro.p2p.node.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import ro.p2p.node.config.PeerConfig;

public class PeerMeshManager {

    private final NodeRuntime runtime;
    private final List<PeerConfig> peers;

    public PeerMeshManager(NodeRuntime runtime, List<PeerConfig> peers) {
        this.runtime = runtime;
        this.peers = List.copyOf(peers);
    }

    public MeshResult connectConfiguredPeers() {
        List<String> connected = new ArrayList<>();
        List<String> failed = new ArrayList<>();

        for (PeerConfig peer : peers) {
            if (runtime.isConnected(peer.getPeerId())) {
                connected.add(peer.getPeerId());
                continue;
            }
            try {
                runtime.connectTo(peer.getAddress());
                connected.add(peer.getPeerId());
            } catch (IOException | RuntimeException e) {
                failed.add(peer.getPeerId() + " (" + e.getMessage() + ")");
            }
        }
        return new MeshResult(connected, failed);
    }

    public List<PeerConfig> getConfiguredPeers() {
        return peers;
    }

    public static final class MeshResult {
        private final List<String> connected;
        private final List<String> failed;

        public MeshResult(List<String> connected, List<String> failed) {
            this.connected = List.copyOf(connected);
            this.failed = List.copyOf(failed);
        }

        public List<String> getConnected() {
            return connected;
        }

        public List<String> getFailed() {
            return failed;
        }
    }
}
