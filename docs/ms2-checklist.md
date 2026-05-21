# MS2 Checklist

## Verificare / validare cu vectori de test pentru algoritmul simetric

Implementat pentru RC6 in:

```text
crypto-rc6/src/test/java/ro/p2p/crypto/core/RC6CipherTest.java
```

Sunt incluse vectori oficiali pentru:

- RC6 cu cheie 128 biti
- RC6 cu cheie 192 biti
- RC6 cu cheie 256 biti
- plaintext zero si plaintext custom

## Implementare / analiza key agreement

Implementat si documentat prin Diffie-Hellman explicit in:

```text
crypto-rc6/src/main/java/ro/p2p/crypto/key/DhKeyAgreementService.java
docs/key-agreement.md
```

Caracteristici:

- grup RFC 3526 2048-bit MODP
- generator `2`
- exponent privat generat cu `SecureRandom`
- validare valori publice si nonce-uri
- KDF SHA-256 pentru derivare cheie RC6 de 128 biti
- teste unitare si vector determinist pentru derivare

## Proiectare + implementare sistem de comunicare

Implementat prin modulele:

```text
protocol/
network/
node-app/
```

Componente importante:

- pachete `HELLO`, `MESSAGE`, `FILE_META`, `FILE_CHUNK`, `ACK`, `ERROR`
- encoder / decoder binar
- framing peste TCP
- server TCP si client TCP
- bootstrap de sesiune prin DH
- runtime de nod si CLI

## Transmitere mesaje simple criptate / decriptate

Implementat in:

```text
messaging/src/main/java/ro/p2p/messaging/service/SecureMessageService.java
messaging/src/main/java/ro/p2p/messaging/service/MessageReceiveService.java
```

Flux:

1. mesajul text este convertit in bytes
2. mesajul este impartit in chunk-uri
3. fiecare chunk este criptat cu RC6 + MAC
4. chunk-urile sunt trimise ca pachete `MESSAGE`
5. receptorul decripteaza chunk-urile
6. mesajul este reasamblat si afisat in CLI

## Teste pentru comunicare criptata intre doua statii

Implementat in:

```text
node-app/src/test/java/ro/p2p/node/app/EncryptedCommunicationIntegrationTest.java
```

Testul porneste doua noduri locale, realizeaza handshake DH peste TCP si trimite un mesaj criptat de la un nod la celalalt.
