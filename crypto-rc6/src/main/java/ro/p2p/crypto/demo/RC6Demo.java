package ro.p2p.crypto.demo;

import ro.p2p.crypto.core.RC6Cipher;
import ro.p2p.crypto.facade.CryptoFacade;
import ro.p2p.crypto.integrity.SimpleMac;
import ro.p2p.crypto.mode.RC6CbcMode;
import ro.p2p.crypto.mode.RC6CtrMode;

public class RC6Demo {

    private static final byte[] KEY = {
        0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
        0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x10
    };

    private static final byte[] IV = {
        0x10,
        0x20,
        0x30,
        0x40,
        0x50,
        0x60,
        0x70,
        (byte) 0x80,
        (byte) 0x90,
        (byte) 0xA0,
        (byte) 0xB0,
        (byte) 0xC0,
        (byte) 0xD0,
        (byte) 0xE0,
        (byte) 0xF0,
        0x00
    };

    public static void main(String[] args) {
        System.out.println("=== Demo Algoritm RC6 ===\n");

        demoBlockCipher();
        demoCbcMode();
        demoCtrMode();
        demoMac();
        demoFacade();

        System.out.println("=== Toate demo-urile au trecut! ===");
    }

    private static void demoBlockCipher() {
        System.out.println("--- 1 Cifru pe bloc (un singur bloc de 16 octeti) ---");

        RC6Cipher cipher = new RC6Cipher(KEY);
        byte[] plaintext = "Hello RC6 Block!".getBytes();

        System.out.println("Text clar:  " + new String(plaintext));
        System.out.println("Text clar:  " + toHex(plaintext));

        byte[] encrypted = cipher.encryptBlock(plaintext);
        System.out.println("Criptat:    " + toHex(encrypted));

        byte[] decrypted = cipher.decryptBlock(encrypted);
        System.out.println("Decriptat:  " + new String(decrypted));
        System.out.println("Potrivire:  " + new String(plaintext).equals(new String(decrypted)));
        System.out.println();
    }

    private static void demoCbcMode() {
        System.out.println("--- 2 Modul CBC blocuri multiple, cu padding ---");

        RC6CbcMode cbc = new RC6CbcMode(KEY);
        String message = "RC6 in modul CBC functioneaza pentru mesaje de orice lungime!";
        byte[] plaintext = message.getBytes();

        System.out.println("Text clar:  \"" + message + "\"");
        System.out.println("Lungime:    " + plaintext.length + " octeti");

        byte[] encrypted = cbc.encrypt(plaintext, IV);
        System.out.println("Criptat:    " + toHex(encrypted));
        System.out.println(
                "Lung. crip: " + encrypted.length + " octeti (aliniat la dimensiunea blocului)");

        byte[] decrypted = cbc.decrypt(encrypted, IV);
        String recovered = new String(decrypted);
        System.out.println("Decriptat:  \"" + recovered + "\"");
        System.out.println("Potrivire:  " + message.equals(recovered));
        System.out.println();
    }

    private static void demoCtrMode() {
        System.out.println("--- 3 Modul CTR (tip flux, fara padding) ---");

        RC6CtrMode ctr = new RC6CtrMode(KEY);
        String message = "Modul CTR pastreaza lungimea exacta!";
        byte[] plaintext = message.getBytes();

        System.out.println("Text clar:  \"" + message + "\"");
        System.out.println("Lungime:    " + plaintext.length + " octeti");

        byte[] encrypted = ctr.encrypt(plaintext, IV);
        System.out.println("Criptat:    " + toHex(encrypted));
        System.out.println("Lung. crip: " + encrypted.length + " octeti (identica cu textul clar)");

        byte[] decrypted = ctr.decrypt(encrypted, IV);
        String recovered = new String(decrypted);
        System.out.println("Decriptat:  \"" + recovered + "\"");
        System.out.println("Potrivire:  " + message.equals(recovered));
        System.out.println();
    }

    private static void demoMac() {
        System.out.println("--- 4. MAC (verificarea integritatii) ---");

        SimpleMac mac = new SimpleMac(KEY);
        byte[] data = "Date importante".getBytes();

        byte[] tag = mac.compute(data);
        System.out.println("Date:       \"Date importante\"");
        System.out.println("Tag MAC:    " + toHex(tag));

        byte[] tag2 = mac.compute(data);
        System.out.println("Aceleasi:   " + toHex(tag2));
        System.out.println("Tag-uri ok: " + java.util.Arrays.equals(tag, tag2));

        byte[] tampered = "Date importantf".getBytes();
        byte[] tag3 = mac.compute(tampered);
        System.out.println("Modificat:  " + toHex(tag3));
        System.out.println(
                "Tag-uri ok: " + java.util.Arrays.equals(tag, tag3) + " (ar trebui false)");
        System.out.println();
    }

    private static void demoFacade() {
        System.out.println("--- 5. CryptoFacade (criptare + MAC intr-un singur apel) ---");

        CryptoFacade facade = new CryptoFacade(KEY);
        String message = "Mesaj autentificat si criptat!";

        System.out.println("Text clar:  \"" + message + "\"");

        byte[] secured = facade.encryptWithMac(message.getBytes());
        System.out.println("Securizat:  " + toHex(secured));
        System.out.println("Dim totala: " + secured.length + " octeti (IV + text criptat + MAC)");

        byte[] recovered = facade.decryptWithMac(secured);
        System.out.println("Recuperat:  \"" + new String(recovered) + "\"");
        System.out.println("Potrivire:  " + message.equals(new String(recovered)));
        System.out.println();
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b & 0xFF));
        }
        return sb.toString();
    }
}
