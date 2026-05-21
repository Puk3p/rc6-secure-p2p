# Key Agreement Diffie-Hellman

## Scop

Pentru fiecare conexiune TCP dintre doua noduri, proiectul stabileste o cheie de sesiune RC6 folosind Diffie-Hellman (DH). Cheia rezultata este folosita ulterior de `CryptoFacade` pentru criptarea mesajelor si chunk-urilor de fisier.

## Parametri DH

Implementarea foloseste explicit:

- algoritm: Diffie-Hellman modular exponentiation
- grup: RFC 3526, 2048-bit MODP Group 14
- generator: `g = 2`
- exponent privat: 256 biti generati cu `SecureRandom`
- cheie de sesiune RC6: primii 16 bytes din SHA-256 KDF

Clasa principala:

```text
crypto-rc6/src/main/java/ro/p2p/crypto/key/DhKeyAgreementService.java
```

## Flux handshake

1. Initiatorul genereaza exponent privat `a`, valoare publica `A = g^a mod p` si nonce-ul initiatorului.
2. Initiatorul trimite `HELLO(nodeId, port, A, nonceA, response=false)`.
3. Responderul valideaza `A`, genereaza exponent privat `b`, valoare publica `B = g^b mod p` si nonce-ul responderului.
4. Responderul calculeaza secretul comun `S = A^b mod p`.
5. Responderul trimite `HELLO(nodeId, port, B, nonceB, response=true)`.
6. Initiatorul valideaza `B` si calculeaza acelasi secret comun `S = B^a mod p`.
7. Ambele parti aplica acelasi KDF si obtin cheia RC6 de 128 biti.

## KDF

Cheia RC6 se deriveaza astfel:

```text
SHA-256("RC6-P2P-DH-v1" || S || A || B || nonceA || nonceB)
```

Din rezultatul SHA-256 se iau primii 16 bytes, corespunzator unei chei RC6 de 128 biti.

Nonce-urile si valorile publice sunt incluse in KDF pentru a lega cheia de transcriptul handshake-ului.

## Validari implementate

Implementarea verifica:

- nonce-urile au exact 16 bytes
- valorile publice DH au exact dimensiunea grupului MODP
- valorile publice sunt in intervalul valid `[2, p - 2]`
- exponentii privati sunt in intervalul valid `[2, p - 2]`
- secretul comun nu este valoare degenerata
- datele expuse prin getters sunt defensive copies

## Teste relevante

```text
crypto-rc6/src/test/java/ro/p2p/crypto/key/DhKeyAgreementServiceTest.java
node-app/src/test/java/ro/p2p/node/app/EncryptedCommunicationIntegrationTest.java
```

Aceste teste verifica:

- initiatorul si responderul obtin aceeasi cheie RC6
- cheia derivata are 16 bytes
- sesiuni diferite produc chei diferite
- valori publice invalide sunt respinse
- nonce-uri invalide sunt respinse
- exista un vector determinist pentru KDF
- handshake-ul TCP real produce aceeasi cheie pe ambele noduri
