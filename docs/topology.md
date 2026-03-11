# Topologia Rețelei

## Prezentare generală

Sistemul utilizează o **topologie Peer-to-Peer Full Mesh**.

În această topologie **fiecare nod din rețea se conectează direct la toate celelalte noduri**. Nu există un server central care să proceseze sau să ruteze mesajele.

---

## Diagrama topologiei

### Exemplu cu 3 noduri

```
        Node A
        /    \
       /      \
      /        \
Node B -------- Node C
```

Fiecare nod menține conexiuni directe cu toate celelalte noduri:

- Node A ↔ Node B
- Node A ↔ Node C
- Node B ↔ Node C

### Exemplu cu 4 noduri

```
Node A -------- Node B
|  \            /  |
|    \        /    |
|      \    /      |
|        \/        |
|        /\        |
|      /    \      |
|    /        \    |
|  /            \  |
Node C -------- Node D
```

---

## Caracteristici ale topologiei Full Mesh

### Avantaje

- **comunicare directă** — mesajele sunt trimise direct între noduri, fără intermediar
- **fără server central** — nu există un punct unic de eșec (single point of failure)
- **sistem distribuit** — fiecare nod este independent
- **latență redusă** — nu există noduri intermediare care să adauge întârziere
- **securitate** — datele nu trec prin noduri terțe

### Dezavantaje

- **număr mare de conexiuni** — pentru N noduri sunt necesare N×(N-1)/2 conexiuni
- **scalabilitate limitată** — topologia full mesh devine complexă pentru un număr mare de noduri
- **overhead de mentenanță** — fiecare nod trebuie să mențină conexiuni cu toți ceilalți peers

---

## Numărul de conexiuni

| Noduri | Conexiuni |
|--------|-----------|
| 2      | 1         |
| 3      | 3         |
| 4      | 6         |
| 5      | 10        |

Formula: `C = N × (N - 1) / 2`

Pentru demo-ul acestui proiect se utilizează **2-3 noduri**, ceea ce rezultă în **1-3 conexiuni**.

---

## Rolul fiecărui nod

Fiecare nod din rețea are **același rol**. Nu există distincție între server și client.

Fiecare nod:

- **ascultă pe un port TCP** pentru conexiuni de la alți peers
- **inițiază conexiuni TCP** către alți peers
- **trimite mesaje criptate** către orice alt nod
- **primește mesaje criptate** de la orice alt nod
- **trimite fișiere criptate** către orice alt nod
- **primește fișiere criptate** de la orice alt nod

---

## Procesul de conectare

### Pasul 1 — Pornirea nodului

Fiecare nod pornește și deschide un **server TCP** pe portul configurat.

```
Node A: ascultă pe portul 9001
Node B: ascultă pe portul 9002
Node C: ascultă pe portul 9003
```

### Pasul 2 — Inițializarea conexiunilor

Fiecare nod citește lista de peers din configurație și inițiază conexiuni TCP către fiecare peer.

```
Node A → conectare la Node B (192.168.1.2:9002)
Node A → conectare la Node C (192.168.1.3:9003)
```

### Pasul 3 — Handshake

După stabilirea conexiunii TCP, nodurile realizează un handshake prin schimbul de pachete HELLO.

```
Node A --- HELLO ---> Node B
Node A <--- ACK ----- Node B
```

### Pasul 4 — Mesh complet

După finalizarea tuturor conexiunilor, topologia full mesh este stabilită.

```
        Node A (9001)
        /          \
       /            \
Node B (9002) --- Node C (9003)
```

---

## Gestionarea conexiunilor

### ConnectionRegistry

Clasa `ConnectionRegistry` menține lista tuturor conexiunilor active.

Funcționalități:

- adăugarea unei conexiuni noi
- eliminarea unei conexiuni deconectate
- căutarea unei conexiuni după ID nod

### ConnectionSupervisor

Clasa `ConnectionSupervisor` monitorizează starea conexiunilor.

Funcționalități:

- detectarea conexiunilor pierdute
- reconectarea automată
- raportarea stării rețelei

### PeerMeshManager

Clasa `PeerMeshManager` din modulul `node-app` gestionează topologia full mesh.

Funcționalități:

- inițializarea conexiunilor la pornire
- menținerea topologiei complete
- gestionarea adăugării sau plecării unui nod

---

## Scenariu de utilizare

### 3 noduri conectate

1. Se pornesc cele 3 noduri: Node A, Node B, Node C
2. Fiecare nod se conectează la ceilalți 2 peers
3. Se stabilesc 3 conexiuni bidirecționale
4. Orice nod poate trimite mesaje sau fișiere către orice alt nod

### Comunicare între noduri

- Node A trimite mesaj criptat către Node B → **direct**
- Node C trimite fișier criptat către Node A → **direct**
- Node B trimite mesaj criptat către Node C → **direct**

Toate comunicările sunt **directe**, fără intermediar.
