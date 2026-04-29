package ro.p2p.network.handshake;

import java.io.IOException;
import java.net.Socket;
import ro.p2p.common.exception.ProtocolException;
import ro.p2p.network.connection.PeerConnection;
import ro.p2p.protocol.codec.FrameReader;
import ro.p2p.protocol.codec.FrameWriter;
import ro.p2p.protocol.packet.HelloPacket;
import ro.p2p.protocol.packet.Packet;

public class SessionBootstrapService {

    private final String localNodeId;
    private final int localListenPort;
    private final HelloService helloService;

    public SessionBootstrapService(String localNodeId, int localListenPort) {
        this.localNodeId = localNodeId;
        this.localListenPort = localListenPort;
        this.helloService = new HelloService();
    }

    public PeerConnection bootstrapInitiator(Socket socket) throws IOException {
        FrameReader reader = new FrameReader(socket.getInputStream());
        FrameWriter writer = new FrameWriter(socket.getOutputStream());

        HelloService.InitiatorHello initiator =
                helloService.createInitiatorHello(localNodeId, localListenPort);
        writer.write(initiator.getPacket());

        Packet response =
                reader.read().orElseThrow(() -> new ProtocolException("Missing HELLO response"));
        if (!(response instanceof HelloPacket) || !((HelloPacket) response).isResponse()) {
            throw new ProtocolException("Expected HELLO response");
        }

        HelloPacket responder = (HelloPacket) response;
        byte[] sessionKey = helloService.completeInitiator(initiator, responder);
        return new PeerConnection(responder.getNodeId(), socket, sessionKey);
    }

    public PeerConnection bootstrapResponder(Socket socket) throws IOException {
        FrameReader reader = new FrameReader(socket.getInputStream());
        FrameWriter writer = new FrameWriter(socket.getOutputStream());

        Packet request =
                reader.read().orElseThrow(() -> new ProtocolException("Missing HELLO request"));
        if (!(request instanceof HelloPacket) || ((HelloPacket) request).isResponse()) {
            throw new ProtocolException("Expected HELLO request");
        }

        HelloPacket initiator = (HelloPacket) request;
        HelloService.ResponderHello responder =
                helloService.createResponderHello(localNodeId, localListenPort, initiator);
        writer.write(responder.getPacket());
        return new PeerConnection(initiator.getNodeId(), socket, responder.getSessionKey());
    }
}
