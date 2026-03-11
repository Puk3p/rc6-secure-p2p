# RC6 Secure P2P
## Sistem Distribuit de Comunicare Criptată

Acest proiect implementează un **sistem distribuit peer-to-peer (P2P)** pentru transmiterea securizată a **mesajelor și fișierelor** între noduri din rețea.

Sistemul utilizează **criptare simetrică RC6 implementată manual**, fără utilizarea bibliotecilor externe de criptografie, și comunică folosind **TCP sockets**.

Proiectul demonstrează concepte importante din:

- securitate informațională
- criptografie aplicată
- sisteme distribuite
- programare de rețea

---


## Rulare locală (Quick Start)

Pentru prima rulare a proiectului este necesară doar activarea hook-urilor Git și compilarea proiectului folosind Maven Wrapper.

### Activarea hook-urilor Git

După clonarea repository-ului, rulați scriptul de setup.

#### macOS / Linux

```bash
bash scripts/setup-git-hooks.sh
```
### Windows (Yacks)

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\setup-git-hooks.ps1
```

## Caracteristici principale

Sistemul oferă următoarele funcționalități:

- comunicare **peer-to-peer directă**
- **criptare end-to-end** folosind RC6
- **transmitere de mesaje criptate**
- **transfer de fișiere criptate**
- **împărțirea fișierelor în bucăți (chunks)**
- **reconstrucția fișierelor la destinație**
- **confirmarea recepției pachetelor (ACK)**

Sistemul poate fi rulat pe **2-3 laptopuri sau mașini virtuale** conectate în aceeași rețea.

---

## Tehnologii utilizate

Proiectul este implementat folosind:

- **Java**
- **Maven multi-module**
- **TCP sockets**
- implementare manuală a algoritmului **RC6**

Motivația utilizării Java:

- suport foarte bun pentru programare de rețea
- control asupra datelor la nivel de bytes
- performanță bună pentru algoritmi criptografici
- aplicația poate rula pe multiple sisteme de operare

---

## Topologia sistemului

Sistemul utilizează o **topologie Peer-to-Peer Full Mesh**.

În această topologie fiecare nod se conectează direct la toate celelalte noduri.

```
    Node A
    /    \
   /      \
Node B --- Node C
```

Caracteristici:

- nu există un server central
- fiecare nod poate trimite și primi mesaje
- fiecare nod poate trimite și primi fișiere
- sistemul este complet distribuit

Pentru mai multe detalii, consultați [docs/topology.md](docs/topology.md).

---

## Protocolul de comunicare

Comunicarea între noduri se realizează folosind **TCP**.

Peste TCP este implementat **un protocol de aplicație propriu**.

Tipurile principale de pachete sunt:

| Tip pachet   | Descriere                            |
|--------------|--------------------------------------|
| HELLO        | inițializarea conexiunii între noduri |
| MESSAGE      | mesaj text criptat                   |
| FILE_META    | informații despre fișier             |
| FILE_CHUNK   | bucată din fișier                    |
| ACK          | confirmare primire                   |
| ERROR        | semnalare eroare                     |

Pentru mai multe detalii, consultați [docs/protocol.md](docs/protocol.md).

---

## Algoritmul de criptare

Pentru criptarea datelor este utilizat algoritmul **RC6**.

RC6 este un algoritm de criptare simetric derivat din RC5 și a fost unul dintre finaliștii competiției AES.

Caracteristici principale:

- bloc de **128 biți**
- cheie de **128 / 192 / 256 biți**
- **20 runde de criptare**

Implementarea algoritmului este realizată **manual**, fără utilizarea bibliotecilor externe de criptografie.

Pentru mai multe detalii, consultați [docs/rc6.md](docs/rc6.md).

---

## Structura proiectului

Proiectul este organizat ca **Maven multi-module project**, fiecare modul având un rol clar în arhitectura sistemului.

```
rc6-secure-p2p/
│
├── common/          # componente comune (modele, constante, utilitare)
├── crypto-rc6/      # implementarea algoritmului RC6
├── protocol/        # definirea protocolului de comunicare
├── network/         # gestionarea conexiunilor TCP
├── messaging/       # trimiterea și primirea mesajelor criptate
├── file-transfer/   # transferul fișierelor criptate
├── node-app/        # aplicația principală (bootstrap, CLI)
├── node-configs/    # fișiere de configurare per nod
└── docs/            # documentație detaliată
```

Separarea modulelor permite:

- arhitectură clară
- reutilizarea componentelor
- testare mai ușoară
- separarea responsabilităților

Pentru mai multe detalii, consultați [docs/architecture.md](docs/architecture.md).

---

## Configurarea nodurilor

Fiecare nod utilizează un fișier de configurare.

```
node-configs/node-a.properties
node-configs/node-b.properties
node-configs/node-c.properties
```

Aceste fișiere conțin:

- ID nod
- port local
- lista de peers
- chei criptografice

---

## Scenariu de utilizare

1. Se pornesc trei noduri: **Node A**, **Node B**, **Node C**
2. Nodurile se conectează între ele
3. Un nod trimite un mesaj criptat
4. Nodul destinatar decriptează mesajul
5. Se poate trimite un fișier
6. Fișierul este împărțit în bucăți și transmis
7. Destinatarul reconstruiește fișierul

---

## Avantajele sistemului

- comunicare directă între noduri
- criptare end-to-end
- sistem distribuit
- arhitectură modulară
- extensibil pentru mai multe noduri

---

## Concluzie

Acest proiect demonstrează implementarea unui sistem distribuit securizat folosind:

- criptare simetrică RC6
- conexiuni TCP
- topologie peer-to-peer

Sistemul permite transmiterea securizată a mesajelor și fișierelor între mai multe stații, oferind un exemplu practic de aplicare a conceptelor din securitate informațională și sisteme distribuite.
