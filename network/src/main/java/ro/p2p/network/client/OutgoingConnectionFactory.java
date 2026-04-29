package ro.p2p.network.client;

public class OutgoingConnectionFactory {

    public TcpPeerClient create(String localNodeId, int localListenPort) {
        return new TcpPeerClient(localNodeId, localListenPort);
    }
}
