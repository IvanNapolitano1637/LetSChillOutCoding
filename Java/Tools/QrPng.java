import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Generatore di QR code in PNG, senza dipendenze esterne (solo JDK).
 *
 * <h2>Uso</h2>
 * <pre>{@code
 * byte[] png = QrPng.urlToQrPng("https://www.example.com");
 * Files.write(Path.of("qr.png"), png);
 *
 * // con parametri: livello H, moduli di 6 pixel, margine di 4 moduli
 * byte[] png2 = QrPng.urlToQrPng("https://www.example.com", QrPng.Ecc.H, 6, 4);
 * }</pre>
 *
 * Da riga di comando (richiede Java 11 o successivo):
 * <pre>
 * java QrPng.java "https://www.example.com" qr.png
 * </pre>
 *
 * <h2>Requisiti</h2>
 * Java 11 o successivo, solo classi del JDK ({@code javax.imageio}, {@code java.awt.image}).
 * Funziona anche in modalità headless, cioè su server senza interfaccia grafica.
 *
 * <h2>Come funziona</h2>
 * Implementa lo standard QR code (modello 2) in modalità byte: il testo viene codificato in
 * UTF-8, si sceglie la versione più piccola in cui entra (da 1 a 40, cioè da 21x21 a 177x177
 * moduli), si calcola la correzione errori Reed-Solomon e si sceglie in automatico la
 * maschera con la penalità minore. Il risultato è un PNG in bianco e nero a 1 bit per pixel.
 * La stessa logica è implementata in {@code qr-svg.js}, che produce matrici identiche.
 *
 * <h2>Limiti</h2>
 * <ul>
 *   <li><b>Lunghezza massima</b>: dipende dal livello di correzione ed è misurata in
 *       <i>byte</i> UTF-8, non in caratteri. Un carattere ASCII occupa 1 byte, una lettera
 *       accentata 2, il simbolo dell'euro 3.
 *       <ul>
 *         <li>L (recupera circa il 7% di errori): 2953 byte</li>
 *         <li>M (circa il 15%, predefinito): 2331 byte</li>
 *         <li>Q (circa il 25%): 1663 byte</li>
 *         <li>H (circa il 30%): 1273 byte</li>
 *       </ul>
 *       Oltre questi valori viene lanciata una {@link IllegalArgumentException}.</li>
 *   <li><b>Una sola modalità di codifica</b>: viene sempre usata la modalità byte, anche per
 *       testi solo numerici o alfanumerici. Il QR code è valido e leggibile da qualsiasi lettore,
 *       ma in quei casi potrebbe essere un po' più grande del minimo possibile.</li>
 *   <li><b>Niente ECI</b>: non viene scritto l'indicatore di codifica. I lettori moderni
 *       interpretano comunque il testo come UTF-8; alcuni lettori molto vecchi potrebbero
 *       mostrare male i caratteri non ASCII. Gli URL ben formati sono già ASCII.</li>
 *   <li><b>L'URL non viene validato</b>: qualunque testo non vuoto è accettato.</li>
 *   <li><b>Dimensione dell'immagine</b>: il lato in pixel è
 *       {@code (moduli + 2 * border) * moduleSize}. Valori molto grandi di
 *       {@code moduleSize} richiedono molta memoria, perché l'immagine viene costruita per intero
 *       in memoria.</li>
 *   <li><b>Margine</b>: lo standard richiede almeno 4 moduli bianchi attorno al codice.
 *       Con {@code border} inferiore a 4 alcuni lettori potrebbero non riconoscerlo.</li>
 *   <li><b>Leggibilità</b>: per la stampa o per la lettura a schermo conviene un
 *       {@code moduleSize} di almeno 4 pixel.</li>
 *   <li><b>Riga di comando</b>: in un sistema con locale non UTF-8, la JVM può alterare i
 *       caratteri non ASCII passati come argomento del {@code main}. Il metodo
 *       {@link #urlToQrPng(String)} non ne risente, perché riceve già una {@code String}.</li>
 * </ul>
 *
 * <h2>Thread safety</h2>
 * La classe non ha stato: i metodi statici possono essere chiamati da più thread insieme.
 *
 * <h2>Verifica</h2>
 * Le matrici coincidono con quelle di una libreria di riferimento su tutti i livelli di
 * correzione e su 37 versioni diverse. I PNG generati sono stati decodificati
 * correttamente con OpenCV e ZXing.
 */
public final class QrPng {

    /** Livello di correzione degli errori. */
    public enum Ecc {
        L(0, 1), M(1, 0), Q(2, 3), H(3, 2);
        final int idx, bits;
        Ecc(int idx, int bits) { this.idx = idx; this.bits = bits; }
    }

    private QrPng() {}

    // ---------- API pubblica ----------

    /**
     * QR code PNG con livello di correzione M, moduli di 10 pixel e margine di 4 moduli.
     *
     * @param url URL (o qualunque testo non vuoto)
     * @return i byte di un file PNG, pronti per essere scritti su disco o inviati in rete
     * @throws IllegalArgumentException se l'URL è nullo, vuoto o troppo lungo (vedi i limiti
     *                                  nella documentazione della classe)
     */
    public static byte[] urlToQrPng(String url) {
        return urlToQrPng(url, Ecc.M, 10, 4);
    }

    /**
     * Restituisce i byte di un'immagine PNG con il QR code dell'URL.
     *
     * @param url        URL (o qualunque testo non vuoto, codificato in UTF-8)
     * @param ecc        livello di correzione degli errori: più è alto, più il codice resiste
     *                   a sporco o danni, ma più è grande e minore è la lunghezza massima
     * @param moduleSize lato in pixel di ogni modulo (almeno 1)
     * @param border     margine bianco in moduli (almeno 0, lo standard prevede 4)
     * @return i byte di un file PNG in bianco e nero a 1 bit per pixel
     * @throws IllegalArgumentException se l'URL è nullo, vuoto o troppo lungo per il livello
     *                                  scelto, oppure se {@code moduleSize} o {@code border}
     *                                  non sono validi
     * @throws java.io.UncheckedIOException se la scrittura del PNG in memoria fallisce
     *                                      (caso eccezionale)
     */
    public static byte[] urlToQrPng(String url, Ecc ecc, int moduleSize, int border) {
        if (url == null || url.isEmpty()) throw new IllegalArgumentException("url vuoto");
        if (moduleSize < 1 || border < 0) throw new IllegalArgumentException("moduleSize >= 1 e border >= 0");

        boolean[][] m = qrMatrix(url, ecc, -1);
        int n = m.length;
        int px = (n + border * 2) * moduleSize;
        BufferedImage img = new BufferedImage(px, px, BufferedImage.TYPE_BYTE_BINARY);
        int white = 0xFFFFFF, black = 0x000000;
        for (int y = 0; y < px; y++) {
            for (int x = 0; x < px; x++) {
                int mx = x / moduleSize - border, my = y / moduleSize - border;
                boolean dark = mx >= 0 && my >= 0 && mx < n && my < n && m[my][mx];
                img.setRGB(x, y, dark ? black : white);
            }
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(img, "png", out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        String url = args.length > 0 ? args[0] : "https://www.example.com";
        Path out = Path.of(args.length > 1 ? args[1] : "qr.png");
        Files.write(out, urlToQrPng(url));
        System.out.println("Scritto " + out.toAbsolutePath());
    }

    // ---------- Tabelle per versione (1..40) ----------

    private static final int[][] ECC_CODEWORDS_PER_BLOCK = {
        {-1, 7, 10, 15, 20, 26, 18, 20, 24, 30, 18, 20, 24, 26, 30, 22, 24, 28, 30, 28, 28, 28, 28, 30, 30, 26, 28, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30},
        {-1, 10, 16, 26, 18, 24, 16, 18, 22, 22, 26, 30, 22, 22, 24, 24, 28, 28, 26, 26, 26, 26, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28},
        {-1, 13, 22, 18, 26, 18, 24, 18, 22, 20, 24, 28, 26, 24, 20, 30, 24, 28, 28, 26, 30, 28, 30, 30, 30, 30, 28, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30},
        {-1, 17, 28, 22, 16, 22, 28, 26, 26, 24, 28, 24, 28, 22, 24, 24, 30, 28, 28, 26, 28, 30, 24, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30},
    };
    private static final int[][] NUM_ERROR_CORRECTION_BLOCKS = {
        {-1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 4, 4, 4, 4, 4, 6, 6, 6, 6, 7, 8, 8, 9, 9, 10, 12, 12, 12, 13, 14, 15, 16, 17, 18, 19, 19, 20, 21, 22, 24, 25},
        {-1, 1, 1, 1, 2, 2, 4, 4, 4, 5, 5, 5, 8, 9, 9, 10, 10, 11, 13, 14, 16, 17, 17, 18, 20, 21, 23, 25, 26, 28, 29, 31, 33, 35, 37, 38, 40, 43, 45, 47, 49},
        {-1, 1, 1, 2, 2, 4, 4, 6, 6, 8, 8, 8, 10, 12, 16, 12, 17, 16, 18, 21, 20, 23, 23, 25, 27, 29, 34, 34, 35, 38, 40, 43, 45, 48, 51, 53, 56, 59, 62, 65, 68},
        {-1, 1, 1, 2, 4, 4, 4, 5, 6, 8, 8, 11, 11, 16, 16, 18, 16, 19, 21, 25, 25, 25, 34, 30, 32, 35, 37, 40, 42, 45, 48, 51, 54, 57, 60, 63, 66, 70, 74, 77, 81},
    };

    private static int[] alignmentPositions(int ver) {
        if (ver == 1) return new int[0];
        int n = ver / 7 + 2;
        int step = ver == 32 ? 26 : (ver * 4 + 4 + n * 2 - 3) / (n * 2 - 2) * 2;
        int[] res = new int[n];
        res[0] = 6;
        for (int i = n - 1, pos = ver * 4 + 10; i >= 1; i--, pos -= step) res[i] = pos;
        return res;
    }

    private static int rawModules(int ver) {
        int r = (16 * ver + 128) * ver + 64;
        if (ver >= 2) {
            int n = ver / 7 + 2;
            r -= (25 * n - 10) * n - 55;
            if (ver >= 7) r -= 36;
        }
        return r;
    }

    private static int dataCodewords(int ver, Ecc e) {
        return rawModules(ver) / 8 - ECC_CODEWORDS_PER_BLOCK[e.idx][ver] * NUM_ERROR_CORRECTION_BLOCKS[e.idx][ver];
    }

    // ---------- Reed-Solomon su GF(256), polinomio 0x11D ----------

    private static int gfMul(int x, int y) {
        int z = 0;
        for (int i = 7; i >= 0; i--) {
            z = (z << 1) ^ ((z >>> 7) * 0x11D);
            z ^= ((y >>> i) & 1) * x;
        }
        return z;
    }

    private static int[] rsDivisor(int degree) {
        int[] res = new int[degree];
        res[degree - 1] = 1;
        int root = 1;
        for (int i = 0; i < degree; i++) {
            for (int j = 0; j < degree; j++) {
                res[j] = gfMul(res[j], root);
                if (j + 1 < degree) res[j] ^= res[j + 1];
            }
            root = gfMul(root, 0x02);
        }
        return res;
    }

    private static int[] rsRemainder(int[] data, int[] divisor) {
        int[] res = new int[divisor.length];
        for (int b : data) {
            int factor = b ^ res[0];
            System.arraycopy(res, 1, res, 0, res.length - 1);
            res[res.length - 1] = 0;
            for (int i = 0; i < res.length; i++) res[i] ^= gfMul(divisor[i], factor);
        }
        return res;
    }

    private static int[] addEccAndInterleave(int[] data, int ver, Ecc e) {
        int numBlocks = NUM_ERROR_CORRECTION_BLOCKS[e.idx][ver];
        int eccLen = ECC_CODEWORDS_PER_BLOCK[e.idx][ver];
        int raw = rawModules(ver) / 8;
        int numShort = numBlocks - raw % numBlocks;
        int shortLen = raw / numBlocks;
        int[] div = rsDivisor(eccLen);
        int[][] blocks = new int[numBlocks][shortLen + 1];
        for (int i = 0, k = 0; i < numBlocks; i++) {
            int datLen = shortLen - eccLen + (i < numShort ? 0 : 1);
            int[] dat = Arrays.copyOfRange(data, k, k + datLen);
            k += datLen;
            int[] ecc = rsRemainder(dat, div);
            System.arraycopy(dat, 0, blocks[i], 0, datLen);
            System.arraycopy(ecc, 0, blocks[i], shortLen + 1 - eccLen, eccLen);
        }
        int[] out = new int[raw];
        int r = 0;
        for (int i = 0; i < shortLen + 1; i++)
            for (int j = 0; j < numBlocks; j++)
                if (i != shortLen - eccLen || j >= numShort) out[r++] = blocks[j][i];
        return out;
    }

    // ---------- Penalità per la scelta della maschera ----------

    private static final boolean[] FINDER_A = bools(1, 0, 1, 1, 1, 0, 1, 0, 0, 0, 0);
    private static final boolean[] FINDER_B = bools(0, 0, 0, 0, 1, 0, 1, 1, 1, 0, 1);

    private static boolean[] bools(int... v) {
        boolean[] r = new boolean[v.length];
        for (int i = 0; i < v.length; i++) r[i] = v[i] != 0;
        return r;
    }

    private static int penalty(boolean[][] m) {
        int size = m.length, score = 0;
        for (int dir = 0; dir < 2; dir++) {
            for (int a = 0; a < size; a++) {
                int run = 1;
                for (int b = 1; b < size; b++) {
                    boolean cur = dir == 0 ? m[a][b] : m[b][a];
                    boolean prev = dir == 0 ? m[a][b - 1] : m[b - 1][a];
                    if (cur == prev) {
                        run++;
                        if (run == 5) score += 3; else if (run > 5) score++;
                    } else run = 1;
                }
                for (int b = 0; b + 11 <= size; b++) {
                    boolean pa = true, pb = true;
                    for (int k = 0; k < 11; k++) {
                        boolean v = dir == 0 ? m[a][b + k] : m[b + k][a];
                        if (v != FINDER_A[k]) pa = false;
                        if (v != FINDER_B[k]) pb = false;
                    }
                    if (pa) score += 40;
                    if (pb) score += 40;
                }
            }
        }
        int dark = 0;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (m[y][x]) dark++;
                if (y < size - 1 && x < size - 1) {
                    boolean c = m[y][x];
                    if (c == m[y][x + 1] && c == m[y + 1][x] && c == m[y + 1][x + 1]) score += 3;
                }
            }
        }
        int total = size * size;
        int k = (Math.abs(dark * 20 - total * 10) + total - 1) / total - 1;
        return score + k * 10;
    }

    // ---------- Costruzione della matrice ----------

    private static void push(List<Integer> bits, int val, int len) {
        for (int i = len - 1; i >= 0; i--) bits.add((val >>> i) & 1);
    }

    private static boolean bit(int n, int i) { return ((n >>> i) & 1) != 0; }

    private static boolean maskCond(int mask, int x, int y) {
        switch (mask) {
            case 0: return (x + y) % 2 == 0;
            case 1: return y % 2 == 0;
            case 2: return x % 3 == 0;
            case 3: return (x + y) % 3 == 0;
            case 4: return (x / 3 + y / 2) % 2 == 0;
            case 5: return x * y % 2 + x * y % 3 == 0;
            case 6: return (x * y % 2 + x * y % 3) % 2 == 0;
            default: return ((x + y) % 2 + x * y % 3) % 2 == 0;
        }
    }

    /** Stato interno della matrice in costruzione. */
    private static final class Grid {
        final int size, ver;
        final Ecc ecc;
        final boolean[][] m, fn;

        Grid(int ver, Ecc ecc) {
            this.ver = ver;
            this.ecc = ecc;
            this.size = ver * 4 + 17;
            this.m = new boolean[size][size];
            this.fn = new boolean[size][size];
        }

        void setFn(int x, int y, boolean v) { m[y][x] = v; fn[y][x] = true; }

        void drawFormat(int mask) {
            int d = (ecc.bits << 3) | mask;
            int rem = d;
            for (int i = 0; i < 10; i++) rem = (rem << 1) ^ ((rem >>> 9) * 0x537);
            int b15 = ((d << 10) | rem) ^ 0x5412;
            for (int i = 0; i <= 5; i++) setFn(8, i, bit(b15, i));
            setFn(8, 7, bit(b15, 6));
            setFn(8, 8, bit(b15, 7));
            setFn(7, 8, bit(b15, 8));
            for (int i = 9; i < 15; i++) setFn(14 - i, 8, bit(b15, i));
            for (int i = 0; i < 8; i++) setFn(size - 1 - i, 8, bit(b15, i));
            for (int i = 8; i < 15; i++) setFn(8, size - 15 + i, bit(b15, i));
            setFn(8, size - 8, true);
        }

        void drawVersion() {
            if (ver < 7) return;
            int rem = ver;
            for (int i = 0; i < 12; i++) rem = (rem << 1) ^ ((rem >>> 11) * 0x1F25);
            int v = (ver << 12) | rem;
            for (int i = 0; i < 18; i++) {
                int a = size - 11 + i % 3, b = i / 3;
                setFn(a, b, bit(v, i));
                setFn(b, a, bit(v, i));
            }
        }

        void drawFinder(int cx, int cy) {
            for (int dy = -4; dy <= 4; dy++) {
                for (int dx = -4; dx <= 4; dx++) {
                    int x = cx + dx, y = cy + dy;
                    if (x < 0 || x >= size || y < 0 || y >= size) continue;
                    int dist = Math.max(Math.abs(dx), Math.abs(dy));
                    setFn(x, y, dist != 2 && dist != 4);
                }
            }
        }

        void drawAlignment(int cx, int cy) {
            for (int dy = -2; dy <= 2; dy++)
                for (int dx = -2; dx <= 2; dx++)
                    setFn(cx + dx, cy + dy, Math.max(Math.abs(dx), Math.abs(dy)) != 1);
        }

        void applyMask(int mask) {
            for (int y = 0; y < size; y++)
                for (int x = 0; x < size; x++)
                    if (!fn[y][x] && maskCond(mask, x, y)) m[y][x] = !m[y][x];
        }
    }

    /**
     * Matrice del QR code (true = modulo scuro), senza margine.
     * {@code forcedMask} tra 0 e 7 forza la maschera; -1 la sceglie in automatico.
     */
    static boolean[][] qrMatrix(String text, Ecc ecc, int forcedMask) {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);

        // Versione più piccola in cui il testo entra (modalità byte).
        int ver = 1, capBits;
        while (true) {
            capBits = dataCodewords(ver, ecc) * 8;
            if (4 + (ver < 10 ? 8 : 16) + bytes.length * 8 <= capBits) break;
            if (ver == 40) throw new IllegalArgumentException("Testo troppo lungo per un QR code con questo livello di correzione");
            ver++;
        }

        // Flusso di bit: modo, lunghezza, dati, terminatore, riempimento.
        List<Integer> bits = new ArrayList<>();
        push(bits, 0x4, 4);
        push(bits, bytes.length, ver < 10 ? 8 : 16);
        for (byte b : bytes) push(bits, b & 0xFF, 8);
        push(bits, 0, Math.min(4, capBits - bits.size()));
        push(bits, 0, (8 - bits.size() % 8) % 8);
        for (int pad = 0xEC; bits.size() < capBits; pad ^= 0xEC ^ 0x11) push(bits, pad, 8);
        int[] data = new int[bits.size() / 8];
        for (int i = 0; i < bits.size(); i++) data[i >>> 3] |= bits.get(i) << (7 - (i & 7));
        int[] codewords = addEccAndInterleave(data, ver, ecc);

        Grid g = new Grid(ver, ecc);
        int size = g.size;

        // Pattern di funzione
        for (int i = 0; i < size; i++) { g.setFn(6, i, i % 2 == 0); g.setFn(i, 6, i % 2 == 0); }
        g.drawFinder(3, 3); g.drawFinder(size - 4, 3); g.drawFinder(3, size - 4);
        int[] ap = alignmentPositions(ver);
        for (int i = 0; i < ap.length; i++) {
            for (int j = 0; j < ap.length; j++) {
                boolean corner = (i == 0 && j == 0) || (i == 0 && j == ap.length - 1) || (i == ap.length - 1 && j == 0);
                if (!corner) g.drawAlignment(ap[j], ap[i]);
            }
        }
        g.drawFormat(0);
        g.drawVersion();

        // Posizionamento dei dati a zig-zag
        int k = 0;
        for (int right = size - 1; right >= 1; right -= 2) {
            if (right == 6) right = 5;
            for (int vert = 0; vert < size; vert++) {
                for (int j = 0; j < 2; j++) {
                    int x = right - j;
                    boolean upward = ((right + 1) & 2) == 0;
                    int y = upward ? size - 1 - vert : vert;
                    if (!g.fn[y][x] && k < codewords.length * 8) {
                        g.m[y][x] = bit(codewords[k >>> 3], 7 - (k & 7));
                        k++;
                    }
                }
            }
        }

        // Maschera
        int best = forcedMask;
        if (best < 0) {
            int bestScore = Integer.MAX_VALUE;
            for (int mask = 0; mask < 8; mask++) {
                g.applyMask(mask);
                g.drawFormat(mask);
                int s = penalty(g.m);
                if (s < bestScore) { bestScore = s; best = mask; }
                g.applyMask(mask); // annulla
            }
        }
        g.applyMask(best);
        g.drawFormat(best);
        return g.m;
    }
}
/*
Esempio di utilizzo (da classe esterna):

public static void main(String[] args){
		byte[] png = QrPng.urlToQrPng("https://github.com/IvanNapolitano1637/LetSChillOutCoding/tree/main/HTML_CSS_JavaScript");
		try {
			Files.write(Path.of("qr.png"), png);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
*/