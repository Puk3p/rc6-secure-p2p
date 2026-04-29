package ro.p2p.network.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import ro.p2p.common.constants.AppConstants;
import ro.p2p.common.model.PeerAddress;
import ro.p2p.network.connection.PeerConnection;
import ro.p2p.network.handshake.SessionBootstrapService;

public class TcpPeerClient {

    private final SessionBootstrapService bootstrapService;
    private final int connectTimeoutMillis;

    public TcpPeerClient(String localNodeId, int localListenPort) {
        this(localNodeId, localListenPort, AppConstants.DEFAULT_CONNECT_TIMEOUT_MILLIS);
    }

    public TcpPeerClient(String localNodeId, int localListenPort, int connectTimeoutMillis) {
        this.bootstrapService = new SessionBootstrapService(localNodeId, localListenPort);
        this.connectTimeoutMillis = connectTimeoutMillis;
    }

    public PeerConnection connect(PeerAddress address) throws IOException {
        Socket socket = new Socket();
        socket.connect(
                new InetSocketAddress(address.getHost(), address.getPort()), connectTimeoutMillis);
        return bootstrapService.bootstrapInitiator(socket);
    }
}
