# Algoritmul de Criptare RC6

## Prezentare generală

**RC6** este un algoritm de criptare simetric derivat din RC5. A fost proiectat de Ronald Rivest, Matt Robshaw, Ray Sidney și Yiqun Lisa Yin și a fost unul dintre cei **5 finaliști** ai competiției **AES (Advanced Encryption Standard)**.

Implementarea din acest proiect este realizată **manual**, fără utilizarea bibliotecilor externe de criptografie.

---

## Caracteristici principale

| Parametru            | Valoare                    |
|----------------------|----------------------------|
| Tip algoritm         | criptare simetrică pe bloc |
| Dimensiune bloc      | 128 biți                   |
| Dimensiune cheie     | 128 / 192 / 256 biți      |
| Număr de runde       | 20                         |
| Registre de lucru    | 4 × 32 biți (A, B, C, D)  |

---

## Operațiile utilizate

RC6 folosește următoarele operații matematice:

- **XOR** (⊕) — exclusive or pe biți
- **Adunare modulo** (+) — adunare modulo 2³²
- **Scădere modulo** (-) — scădere modulo 2³²
- **Multiplicare** (×) — multiplicare modulo 2³²
- **Rotație la stânga** (<<<) — rotație circulară a biților la stânga
- **Rotație la dreapta** (>>>) — rotație circulară a biților la dreapta

Toate operațiile sunt realizate pe cuvinte de **32 biți**.

---

## Parametrii algoritmului

RC6 este parametrizat ca **RC6-w/r/b** unde:

- **w** = dimensiunea cuvântului în biți (32)
- **r** = numărul de runde (20)
- **b** = dimensiunea cheii în bytes (16 / 24 / 32)

Configurația standard utilizată: **RC6-32/20/16** (cheie de 128 biți, 20 runde).

---

## Constante magice

Algoritmul utilizează două constante derivate din numerele e și φ (golden ratio):

- **P32** = 0xB7E15163 (derivat din e - 2)
- **Q32** = 0x9E3779B9 (derivat din φ - 1)

Aceste constante sunt utilizate în procesul de generare a subcheilor.

---

## Generarea subcheilor (Key Schedule)

### Intrare

- cheia utilizatorului de b bytes

### Ieșire

- un array de subchei S[0..2r+3]

### Algoritm

1. Se convertește cheia în array de cuvinte L[0..c-1]
2. Se inițializează S[0] = P32
3. Pentru i = 1 la 2r+3: S[i] = S[i-1] + Q32
4. Se realizează amestecarea:

```
A = B = 0
i = j = 0
v = 3 × max(c, 2r + 4)

pentru s = 1 la v:
    A = S[i] = (S[i] + A + B) <<< 3
    B = L[j] = (L[j] + A + B) <<< (A + B)
    i = (i + 1) mod (2r + 4)
    j = (j + 1) mod c
```

Numărul total de subchei generate: **2 × 20 + 4 = 44 subchei**.

---

## Criptarea unui bloc

### Intrare

- 4 registre de 32 biți: A, B, C, D (blocul de 128 biți)
- subcheile S[0..43]

### Algoritm

```
B = B + S[0]
D = D + S[1]

pentru i = 1 la r:
    t = (B × (2B + 1)) <<< log2(w)
    u = (D × (2D + 1)) <<< log2(w)
    A = ((A ⊕ t) <<< u) + S[2i]
    C = ((C ⊕ u) <<< t) + S[2i + 1]
    (A, B, C, D) = (B, C, D, A)

A = A + S[2r + 2]
C = C + S[2r + 3]
```

### Pași detaliați

1. **Pre-whitening** — se adaugă S[0] la B și S[1] la D
2. **Runde de criptare** (20 runde):
   - se calculează funcția f(x) = x × (2x + 1)
   - se aplică rotație pe rezultat
   - se aplică XOR între registre
   - se aplică rotație dependentă de date
   - se adaugă subcheile rundei
   - se rotesc registrele
3. **Post-whitening** — se adaugă S[42] la A și S[43] la C

---

## Decriptarea unui bloc

### Intrare

- 4 registre de 32 biți: A, B, C, D (blocul criptat)
- subcheile S[0..43]

### Algoritm

```
C = C - S[2r + 3]
A = A - S[2r + 2]

pentru i = r la 1 (descrescător):
    (A, B, C, D) = (D, A, B, C)
    u = (D × (2D + 1)) <<< log2(w)
    t = (B × (2B + 1)) <<< log2(w)
    C = ((C - S[2i + 1]) >>> t) ⊕ u
    A = ((A - S[2i]) >>> u) ⊕ t

D = D - S[1]
B = B - S[0]
```

Decriptarea aplică operațiile inverse în ordine inversă.

---

## Moduri de operare

### CBC (Cipher Block Chaining)

În modul CBC fiecare bloc de plaintext este combinat cu blocul criptat anterior prin XOR înainte de criptare.

```
C₀ = IV
Cᵢ = Encrypt(Pᵢ ⊕ Cᵢ₋₁)
Pᵢ = Decrypt(Cᵢ) ⊕ Cᵢ₋₁
```

Avantaje:

- blocuri identice de plaintext produc ciphertext diferit
- eroarea se propagă doar pe 2 blocuri

### CTR (Counter Mode)

În modul CTR un contor este criptat și rezultatul este combinat prin XOR cu plaintext-ul.

```
Cᵢ = Pᵢ ⊕ Encrypt(Nonce || Counter_i)
Pᵢ = Cᵢ ⊕ Encrypt(Nonce || Counter_i)
```

Avantaje:

- permite criptarea paralelă
- nu necesită padding
- accesul aleator la blocuri

---

## Padding

Deoarece RC6 operează pe blocuri de 128 biți (16 bytes), datele care nu sunt multiplu de 16 bytes necesită **padding**.

Se utilizează **PKCS#7 padding**:

- se adaugă N bytes, fiecare cu valoarea N
- dacă datele sunt deja multiplu de 16, se adaugă un bloc complet de padding (16 bytes cu valoarea 16)

Exemplu:

```
Date: [0x01, 0x02, 0x03] (3 bytes)
Padding necesar: 13 bytes
Rezultat: [0x01, 0x02, 0x03, 0x0D, 0x0D, 0x0D, 0x0D, 0x0D, 0x0D, 0x0D, 0x0D, 0x0D, 0x0D, 0x0D, 0x0D, 0x0D]
```

---

## Verificarea integrității

### SimpleMac

Pentru verificarea integrității datelor se utilizează un **MAC (Message Authentication Code)** simplu bazat pe RC6.

Funcționalitate:

- se calculează un tag de autentificare pe baza datelor și a cheii
- se atașează tag-ul la mesajul criptat
- destinatarul recalculează tag-ul și verifică dacă se potrivește

### IntegrityVerifier

Clasa `IntegrityVerifier` verifică tag-ul MAC al datelor primite.

Dacă tag-ul nu se potrivește, datele sunt considerate compromise.

---

## Implementarea în proiect

### Clase principale

| Clasă              | Responsabilitate                                |
|---------------------|-------------------------------------------------|
| RC6Cipher           | clasă principală pentru criptare/decriptare     |
| RC6KeySchedule      | generarea subcheilor din cheia utilizatorului    |
| RC6BlockEncryptor   | criptarea unui singur bloc de 128 biți          |
| RC6BlockDecryptor   | decriptarea unui singur bloc de 128 biți        |
| RC6CbcMode          | implementarea modului CBC                       |
| RC6CtrMode          | implementarea modului CTR                       |
| SessionKeyDeriver   | derivarea cheii de sesiune                      |
| NonceGenerator      | generarea nonce-urilor pentru modurile de operare|
| CryptoFacade        | interfață simplificată pentru criptare/decriptare|

### CryptoFacade

Clasa `CryptoFacade` oferă o interfață simplificată care ascunde complexitatea internă a modulului crypto.

Funcționalități expuse:

- criptarea unui mesaj (bytes → bytes criptați)
- decriptarea unui mesaj (bytes criptați → bytes originali)
- criptarea unui chunk de fișier
- decriptarea unui chunk de fișier
- generarea și verificarea MAC

---

## Exemplu de flux

### Criptarea unui mesaj

```
"Hello" (String)
    ↓
[0x48, 0x65, 0x6C, 0x6C, 0x6F] (bytes)
    ↓
[0x48, 0x65, 0x6C, 0x6C, 0x6F, 0x0B, 0x0B, ...] (padding PKCS#7)
    ↓
RC6 Encrypt (20 runde)
    ↓
[0xA3, 0x7F, 0x12, ...] (ciphertext)
    ↓
MAC tag adăugat
    ↓
transmisie TCP
```

### Decriptarea unui mesaj

```
[0xA3, 0x7F, 0x12, ...] (ciphertext + MAC)
    ↓
verificare MAC
    ↓
RC6 Decrypt (20 runde)
    ↓
[0x48, 0x65, 0x6C, 0x6C, 0x6F, 0x0B, 0x0B, ...] (plaintext + padding)
    ↓
eliminare padding
    ↓
[0x48, 0x65, 0x6C, 0x6C, 0x6F] (bytes originali)
    ↓
"Hello" (String)
```
