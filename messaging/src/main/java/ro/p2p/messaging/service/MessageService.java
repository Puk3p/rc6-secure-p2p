package ro.p2p.messaging.service;

import java.io.IOException;
import ro.p2p.network.connection.PeerConnection;

public interface MessageService {

    String sendMessage(PeerConnection connection, String message) throws IOException;
}
