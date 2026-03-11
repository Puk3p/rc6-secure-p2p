# Protocolul de Comunicare

## Prezentare generală

Comunicarea între noduri se realizează folosind **TCP**.

Avantajele utilizării TCP:

- livrare sigură a datelor
- ordonarea corectă a pachetelor
- retransmisie automată în caz de pierdere
- conexiune stabilă între noduri

Peste TCP este implementat **un protocol de aplicație propriu** care definește structura mesajelor transmise între noduri.

---

## Tipuri de pachete

Protocolul definește următoarele tipuri de pachete:

| Tip pachet   | Cod  | Descriere                            |
|--------------|------|--------------------------------------|
| HELLO        | 0x01 | inițializarea conexiunii între noduri |
| MESSAGE      | 0x02 | mesaj text criptat                   |
| FILE_META    | 0x03 | informații despre fișier             |
| FILE_CHUNK   | 0x04 | bucată din fișier                    |
| ACK          | 0x05 | confirmare primire                   |
| ERROR        | 0x06 | semnalare eroare                     |

---

## Structura unui pachet

Fiecare pachet transmis prin rețea are următoarea structură:

```
+----------------+----------------+------------------+
| Tip pachet     | Lungime payload| Payload          |
| (1 byte)       | (4 bytes)      | (N bytes)        |
+----------------+----------------+------------------+
```

- **Tip pachet** — identifică tipul mesajului (HELLO, MESSAGE, etc.)
- **Lungime payload** — dimensiunea în bytes a conținutului
- **Payload** — datele efective ale pachetului

---

## Descrierea pachetelor

### HELLO

Trimis la inițializarea conexiunii între două noduri.

Conținut:

- ID-ul nodului sursă
- portul pe care ascultă nodul sursă

Flux:

1. Node A deschide conexiune TCP către Node B
2. Node A trimite pachet HELLO
3. Node B primește și validează pachetul
4. Node B trimite ACK

---

### MESSAGE

Trimis pentru transmiterea unui mesaj text criptat.

Conținut:

- ID mesaj (unic)
- timestamp
- date criptate (mesajul criptat cu RC6)

Flux:

1. Utilizatorul scrie un mesaj
2. Mesajul este convertit în bytes
3. Bytes-urile sunt criptate folosind RC6
4. Pachetul MESSAGE este construit și trimis
5. Destinatarul primește pachetul
6. Datele sunt decriptate
7. Mesajul este afișat

```
Plaintext
    ↓
RC6 Encrypt
    ↓
Ciphertext
    ↓
TCP Transmission
    ↓
RC6 Decrypt
    ↓
Plaintext
```

---

### FILE_META

Trimis înainte de transferul unui fișier.

Conținut:

- ID transfer (unic)
- numele fișierului
- dimensiunea totală a fișierului
- numărul total de chunk-uri
- hash-ul fișierului original

Acest pachet informează destinatarul despre fișierul care urmează să fie transmis.

---

### FILE_CHUNK

Trimis pentru fiecare bucată dintr-un fișier.

Conținut:

- ID transfer
- index chunk (numărul bucății)
- date criptate (chunk-ul criptat cu RC6)

Flux pentru transferul unui fișier:

1. Fișierul este citit de pe disc
2. Fișierul este împărțit în bucăți (chunks)
3. Se trimite pachet FILE_META
4. Fiecare chunk este criptat cu RC6
5. Se trimite câte un pachet FILE_CHUNK pentru fiecare bucată
6. Destinatarul primește și decriptează fiecare chunk
7. Destinatarul reconstruiește fișierul original

```
file.pdf
    ↓
chunk1 → RC6 Encrypt → FILE_CHUNK → TCP → RC6 Decrypt → chunk1
chunk2 → RC6 Encrypt → FILE_CHUNK → TCP → RC6 Decrypt → chunk2
chunk3 → RC6 Encrypt → FILE_CHUNK → TCP → RC6 Decrypt → chunk3
chunk4 → RC6 Encrypt → FILE_CHUNK → TCP → RC6 Decrypt → chunk4
    ↓
file.pdf (reconstruit)
```

---

### ACK

Trimis pentru confirmarea primirii unui pachet.

Conținut:

- tipul pachetului confirmat
- ID-ul pachetului confirmat

ACK este utilizat pentru:

- confirmarea primirii unui MESSAGE
- confirmarea primirii unui FILE_CHUNK
- confirmarea primirii unui FILE_META
- confirmarea conexiunii (răspuns la HELLO)

---

### ERROR

Trimis pentru semnalarea unei erori.

Conținut:

- cod eroare
- mesaj descriptiv

Exemple de erori:

- pachet invalid
- eroare de decriptare
- transfer eșuat
- chunk lipsă

---

## Fluxul complet de conectare

```
Node A                          Node B
  |                                |
  |--- TCP Connect --------------->|
  |                                |
  |--- HELLO (id=A, port=9001) -->|
  |                                |
  |<-- ACK ------------------------|
  |                                |
  |    (conexiune stabilită)       |
  |                                |
```

---

## Fluxul complet de trimitere mesaj

```
Node A                          Node B
  |                                |
  |--- MESSAGE (criptat) -------->|
  |                                |
  |                     decriptare |
  |                     afișare    |
  |                                |
  |<-- ACK ------------------------|
  |                                |
```

---

## Fluxul complet de transfer fișier

```
Node A                          Node B
  |                                |
  |--- FILE_META ---------------->|
  |<-- ACK ------------------------|
  |                                |
  |--- FILE_CHUNK (0) ----------->|
  |<-- ACK ------------------------|
  |                                |
  |--- FILE_CHUNK (1) ----------->|
  |<-- ACK ------------------------|
  |                                |
  |--- FILE_CHUNK (2) ----------->|
  |<-- ACK ------------------------|
  |                                |
  |    (transfer complet)          |
  |         reconstruire fișier    |
  |                                |
```

---

## Codare și decodare

Modulul `protocol` oferă următoarele componente pentru procesarea pachetelor:

- **PacketEncoder** — convertește un obiect Packet în bytes pentru transmisie
- **PacketDecoder** — convertește bytes primiți într-un obiect Packet
- **FrameWriter** — scrie un frame complet pe socket (header + payload)
- **FrameReader** — citește un frame complet de pe socket
- **BinarySerializer** — serializează câmpurile unui pachet în format binar
- **BinaryDeserializer** — deserializează bytes în câmpurile unui pachet
- **PacketValidator** — validează structura și conținutul unui pachet
