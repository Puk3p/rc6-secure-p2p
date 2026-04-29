package ro.p2p.node.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import ro.p2p.common.model.PeerAddress;

public class ConfigLoader {

    public NodeConfig load(Path path) throws IOException {
        Properties properties = new Properties();
        try (InputStream input = Files.newInputStream(path)) {
            properties.load(input);
        }

        String nodeId = require(properties, "node.id");
        String host = properties.getProperty("node.host", "127.0.0.1").trim();
        int port = Integer.parseInt(require(properties, "node.port"));
        List<PeerConfig> peers = parsePeers(properties.getProperty("peers", ""));
        return new NodeConfig(nodeId, host, port, peers);
    }

    private List<PeerConfig> parsePeers(String value) {
        List<PeerConfig> peers = new ArrayList<>();
        if (value == null || value.isBlank()) {
            return peers;
        }
        for (String entry : value.split(",")) {
            String normalized = entry.trim();
            if (normalized.isEmpty()) {
                continue;
            }
            int atIndex = normalized.indexOf('@');
            int colonIndex = normalized.lastIndexOf(':');
            if (atIndex <= 0
                    || colonIndex <= atIndex + 1
                    || colonIndex == normalized.length() - 1) {
                throw new IllegalArgumentException("Invalid peer entry: " + normalized);
            }
            String peerId = normalized.substring(0, atIndex);
            String host = normalized.substring(atIndex + 1, colonIndex);
            int port = Integer.parseInt(normalized.substring(colonIndex + 1));
            peers.add(new PeerConfig(peerId, new PeerAddress(host, port)));
        }
        return peers;
    }

    private String require(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing required config property: " + key);
        }
        return value.trim();
    }
}
