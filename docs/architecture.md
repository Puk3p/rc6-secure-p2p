# Arhitectura Sistemului

## Prezentare generală

Proiectul este organizat ca **Maven multi-module project**, fiecare modul având un rol clar în arhitectura sistemului.

```
rc6-secure-p2p/
│
├── pom.xml                         # parent pom
├── README.md
├── docs/
│   ├── architecture.md
│   ├── protocol.md
│   ├── topology.md
│   └── rc6.md
│
├── common/
│   ├── pom.xml
│   └── src/main/java/ro/p2p/common/
│       ├── constants/
│       ├── enums/
│       ├── model/
│       ├── util/
│       └── exception/
│
├── crypto-rc6/
│   ├── pom.xml
│   └── src/main/java/ro/p2p/crypto/
│       ├── core/
│       ├── mode/
│       ├── key/
│       ├── integrity/
│       └── facade/
│
├── protocol/
│   ├── pom.xml
│   └── src/main/java/ro/p2p/protocol/
│       ├── packet/
│       ├── codec/
│       ├── serializer/
│       └── validator/
│
├── network/
│   ├── pom.xml
│   └── src/main/java/ro/p2p/network/
│       ├── server/
│       ├── client/
│       ├── connection/
│       ├── io/
│       └── handshake/
│
├── messaging/
│   ├── pom.xml
│   └── src/main/java/ro/p2p/messaging/
│       ├── service/
│       ├── tracker/
│       └── mapper/
│
├── file-transfer/
│   ├── pom.xml
│   └── src/main/java/ro/p2p/filetransfer/
│       ├── service/
│       ├── chunk/
│       ├── storage/
│       └── mapper/
│
├── node-app/
│   ├── pom.xml
│   └── src/main/java/ro/p2p/node/
│       ├── bootstrap/
│       ├── config/
│       ├── app/
│       ├── cli/
│       └── handler/
│
└── node-configs/
    ├── node-a.properties
    ├── node-b.properties
    └── node-c.properties
```

Separarea modulelor permite:

- arhitectură clară
- reutilizarea componentelor
- testare mai ușoară
- separarea responsabilităților

---

## Dependențe între module

```
node-app
├── messaging
│   ├── protocol
│   │   └── common
│   ├── crypto-rc6
│   │   └── common
│   └── network
│       ├── protocol
│       └── common
├── file-transfer
│   ├── protocol
│   ├── crypto-rc6
│   ├── network
│   └── common
└── common
```

---

## Descrierea modulelor

### common

Acest modul conține componente comune utilizate de toate celelalte module.

Include:

- constante globale
- modele de date
- enum-uri
- utilitare
- excepții personalizate

Clase principale:

| Pachet       | Clase                                              |
|--------------|----------------------------------------------------|
| constants/   | AppConstants, PacketConstants, CryptoConstants     |
| enums/       | PacketType, ConnectionState, TransferStatus        |
| model/       | NodeInfo, PeerAddress, SessionInfo, FileMetadata, ChunkInfo |
| util/        | ByteUtils, IdGenerator, TimeUtils, ValidationUtils |
| exception/   | ProtocolException, CryptoException, TransferException |

---

### crypto-rc6

Acest modul conține implementarea completă a algoritmului **RC6**.

Responsabilități:

- generarea subcheilor
- criptarea blocurilor
- decriptarea blocurilor
- moduri de operare (CTR, CBC)
- verificarea integrității datelor

Clase principale:

| Pachet       | Clase                                    |
|--------------|------------------------------------------|
| core/        | RC6Cipher, RC6KeySchedule, RC6BlockEncryptor, RC6BlockDecryptor |
| mode/        | RC6CtrMode, RC6CbcMode                  |
| key/         | SessionKeyDeriver, NonceGenerator        |
| integrity/   | SimpleMac, IntegrityVerifier             |
| facade/      | CryptoFacade                             |

Acest modul este complet independent de rețea.

---

### protocol

Acest modul definește **protocolul de comunicație** dintre noduri.

Responsabilități:

- definirea tipurilor de pachete
- serializarea și deserializarea datelor
- validarea pachetelor
- codarea și decodarea datelor pe fir

Clase principale:

| Pachet        | Clase                                                |
|---------------|------------------------------------------------------|
| packet/       | Packet, HelloPacket, MessagePacket, FileMetaPacket, FileChunkPacket, AckPacket, ErrorPacket |
| codec/        | PacketEncoder, PacketDecoder, FrameWriter, FrameReader |
| serializer/   | BinarySerializer, BinaryDeserializer                 |
| validator/    | PacketValidator                                      |

---

### network

Acest modul gestionează **conexiunile TCP dintre noduri**.

Responsabilități:

- deschiderea serverului TCP
- acceptarea conexiunilor de la alți peers
- inițierea conexiunilor către alți peers
- gestionarea conexiunilor active
- citirea și scrierea datelor pe socket

Clase principale:

| Pachet        | Clase                                         |
|---------------|-----------------------------------------------|
| server/       | TcpPeerServer, IncomingConnectionHandler      |
| client/       | TcpPeerClient, OutgoingConnectionFactory      |
| connection/   | PeerConnection, ConnectionRegistry, ConnectionSupervisor |
| io/           | SocketReader, SocketWriter                    |
| handshake/    | HelloService, SessionBootstrapService         |

---

### messaging

Acest modul gestionează **trimiterea și primirea mesajelor criptate**.

Responsabilități:

- trimiterea mesajelor
- primirea mesajelor
- criptarea și decriptarea mesajelor
- gestionarea confirmărilor (ACK)
- numerotarea secvențelor

Clase principale:

| Pachet    | Clase                                                    |
|-----------|----------------------------------------------------------|
| service/  | MessageService, SecureMessageService, MessageReceiveService |
| tracker/  | SequenceManager, AckTracker, PendingMessageStore         |
| mapper/   | MessagePacketMapper                                      |

---

### file-transfer

Acest modul gestionează **transferul fișierelor criptate**.

Responsabilități:

- citirea fișierelor de pe disc
- împărțirea în bucăți (chunks)
- criptarea fiecărei bucăți
- trimiterea bucăților prin rețea
- primirea și stocarea temporară a bucăților
- reconstruirea fișierului original

Clase principale:

| Pachet    | Clase                                                    |
|-----------|----------------------------------------------------------|
| service/  | FileTransferService, FileSendService, FileReceiveService, FileAssemblyService |
| chunk/    | FileChunker, ChunkEncryptor, ChunkDecryptor              |
| storage/  | TempChunkStorage, ReceivedFileRepository                 |
| mapper/   | FileMetaPacketMapper, FileChunkPacketMapper               |

---

### node-app

Acest modul reprezintă **aplicația finală care rulează pe fiecare nod**.

Responsabilități:

- pornirea nodului
- încărcarea configurației
- inițializarea conexiunilor către peers
- gestionarea topologiei full mesh
- rularea interfeței CLI
- rutarea pachetelor primite

Clase principale:

| Pachet      | Clase                                                    |
|-------------|----------------------------------------------------------|
| bootstrap/  | NodeApplication, NodeContext                             |
| config/     | NodeConfig, PeerConfig, ConfigLoader                     |
| app/        | NodeRuntime, PeerMeshManager, CommandDispatcher          |
| cli/        | CliRunner, CommandParser                                 |
| cli/commands/ | HelpCommand, PeersCommand, SendMessageCommand, SendFileCommand, ExitCommand |
| handler/    | PacketRouter, MessagePacketHandler, FilePacketHandler, AckPacketHandler |

---

## Configurarea nodurilor

Fiecare nod utilizează un fișier `.properties` din directorul `node-configs/`.

Exemplu de configurare:

```properties
node.id=node-a
node.port=9001
node.peers=192.168.1.2:9002,192.168.1.3:9003
node.key=0123456789ABCDEF0123456789ABCDEF
```

Fișiere disponibile:

- `node-configs/node-a.properties`
- `node-configs/node-b.properties`
- `node-configs/node-c.properties`
