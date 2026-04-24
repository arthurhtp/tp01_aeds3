package stockit.dao;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Hash Extensível genérico e persistente em disco usando RandomAccessFile.
 *
 * Funciona com qualquer tipo de entrada que implemente {@link RegistroHash}.
 * Cada entrada tem tamanho fixo em bytes (definido pelo RegistroHash),
 * o que garante acesso aleatório uniforme dentro dos buckets.
 *
 * Arquivos em disco:
 *   {@code <nome>.dir} : [int profGlobal][long endereço]^(2^profGlobal)
 *   {@code <nome>.bkt} : sequência de baldes de tamanho fixo:
 *                [int profLocal][int count][CAPACIDADE × (byte lapide + byte[tamRegistro])]
 *
 * Uso:
 *   - Índice primário (int→long):     {@code new HashExtensivel<>("PK", new ParIntLong())}
 *   - Índice por nome (String→long):  {@code new HashExtensivel<>("Nome", new ParStringLong())}
 *   - Relacionamento 1:N (int→int[]): {@code new HashExtensivel<>("Rel", new ParIntListaInt())}
 */
public class HashExtensivel<T extends HashExtensivel.RegistroHash> {

    private static final int CAPACIDADE = 4;

    private final RandomAccessFile dir;
    private final RandomAccessFile bkt;
    private final T prototipo;
    private final int tamRegistro;
    private final int tamBalde;

    public interface RegistroHash {
        int tamanho();
        byte[] toByteArray() throws IOException;
        void fromByteArray(byte[] b) throws IOException;
        int hashCode_();
        boolean chaveIgual(RegistroHash outro);
        RegistroHash novaInstancia();
    }

    /** Índice primário: int chave → long valor. */
    public static class ParIntLong implements RegistroHash {
        public int  chave;
        public long valor;

        public ParIntLong()                    { this.chave = 0; this.valor = -1; }
        public ParIntLong(int c, long v)       { this.chave = c; this.valor = v; }
        public int tamanho()                   { return Integer.BYTES + Long.BYTES; }
        public int hashCode_()                 { return chave; }
        public boolean chaveIgual(RegistroHash o) { return o instanceof ParIntLong && ((ParIntLong) o).chave == this.chave; }
        public RegistroHash novaInstancia()    { return new ParIntLong(); }
        public byte[] toByteArray() throws IOException {
            ByteArrayOutputStream ba = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(ba);
            dos.writeInt(chave); dos.writeLong(valor);
            return ba.toByteArray();
        }
        public void fromByteArray(byte[] b) throws IOException {
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(b));
            chave = dis.readInt(); valor = dis.readLong();
        }
    }

    /** Índice secundário: String chave (120 bytes fixos) → long valor. */
    public static class ParStringLong implements RegistroHash {
        public static final int TAM_CHAVE = 120;
        public String chave;
        public long   valor;
        public ParStringLong()                      { this.chave = ""; this.valor = -1; }
        public ParStringLong(String c, long v)      { this.chave = c;  this.valor = v; }
        public int tamanho()                        { return TAM_CHAVE + Long.BYTES; }
        public RegistroHash novaInstancia()         { return new ParStringLong(); }
        public int hashCode_() {
            int h = 0;
            for (byte b : chave.getBytes(StandardCharsets.UTF_8)) h = 31 * h + (b & 0xFF);
            return h & 0x7FFFFFFF;
        }
        public boolean chaveIgual(RegistroHash o) {
            return o instanceof ParStringLong && ((ParStringLong) o).chave.equals(this.chave);
        }
        public byte[] toByteArray() throws IOException {
            byte[] cb = new byte[TAM_CHAVE];
            byte[] orig = chave.getBytes(StandardCharsets.UTF_8);
            System.arraycopy(orig, 0, cb, 0, Math.min(orig.length, TAM_CHAVE));
            ByteArrayOutputStream ba = new ByteArrayOutputStream();
            ba.write(cb);
            DataOutputStream dos = new DataOutputStream(ba);
            dos.writeLong(valor);
            return ba.toByteArray();
        }
        public void fromByteArray(byte[] b) throws IOException {
            int len = 0;
            while (len < TAM_CHAVE && b[len] != 0) len++;
            chave = new String(b, 0, len, StandardCharsets.UTF_8);
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(b, TAM_CHAVE, Long.BYTES));
            valor = dis.readLong();
        }
    }

    /** Relacionamento 1:N: int chave → lista de até MAX_VALORES ints. */
    public static class ParIntListaInt implements RegistroHash {
        public static final int MAX_VALORES = 20;
        public int   chave;
        public int   qtd;
        public int[] valores;
        public ParIntListaInt() { this.chave = -1; this.qtd = 0; this.valores = new int[MAX_VALORES]; }
        public ParIntListaInt(int c, int v) { this(); this.chave = c; this.qtd = 1; this.valores[0] = v; }
        public int tamanho() { return Integer.BYTES + Integer.BYTES + MAX_VALORES * Integer.BYTES; }
        public int hashCode_() { return chave; }
        public boolean chaveIgual(RegistroHash o) { return o instanceof ParIntListaInt && ((ParIntListaInt) o).chave == this.chave; }
        public RegistroHash novaInstancia() { return new ParIntListaInt(); }
        public byte[] toByteArray() throws IOException {
            ByteArrayOutputStream ba = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(ba);
            dos.writeInt(chave); dos.writeInt(qtd);
            for (int i = 0; i < MAX_VALORES; i++) dos.writeInt(i < qtd ? valores[i] : 0);
            return ba.toByteArray();
        }
        public void fromByteArray(byte[] b) throws IOException {
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(b));
            chave = dis.readInt(); qtd = dis.readInt();
            valores = new int[MAX_VALORES];
            for (int i = 0; i < MAX_VALORES; i++) valores[i] = dis.readInt();
        }
        public boolean adicionar(int v) {
            for (int i = 0; i < qtd; i++) if (valores[i] == v) return false;
            if (qtd >= MAX_VALORES) return false;
            valores[qtd++] = v; return true;
        }
        public boolean remover(int v) {
            for (int i = 0; i < qtd; i++) {
                if (valores[i] == v) {
                    System.arraycopy(valores, i + 1, valores, i, qtd - i - 1);
                    qtd--; return true;
                }
            }
            return false;
        }
        public List<Integer> listar() {
            List<Integer> l = new ArrayList<>();
            for (int i = 0; i < qtd; i++) l.add(valores[i]);
            return l;
        }
    }

    private class Balde {
        int profLocal; int count; boolean[] ativos; byte[][] dados; long endereco;
        Balde(int profLocal, long endereco) {
            this.profLocal = profLocal; this.endereco = endereco; this.count = 0;
            this.ativos = new boolean[CAPACIDADE]; this.dados = new byte[CAPACIDADE][tamRegistro];
        }
    }

    @SuppressWarnings("unchecked")
    public HashExtensivel(String nomeBase, T prototipo) throws Exception {
        this.prototipo = prototipo;
        this.tamRegistro = prototipo.tamanho();
        this.tamBalde = Integer.BYTES + Integer.BYTES + CAPACIDADE * (1 + tamRegistro);
        File pasta = new File("./data/" + nomeBase);
        if (!pasta.exists()) pasta.mkdirs();
        String prefixo = "./data/" + nomeBase + "/" + nomeBase;
        dir = new RandomAccessFile(prefixo + ".dir", "rw");
        bkt = new RandomAccessFile(prefixo + ".bkt", "rw");
        if (dir.length() == 0) { setProfGlobal(0); setEndBalde(0, 0L); escreverBalde(new Balde(0, 0L)); }
    }

    private int getProfGlobal() throws Exception { dir.seek(0); return dir.readInt(); }
    private void setProfGlobal(int p) throws Exception { dir.seek(0); dir.writeInt(p); }
    private long getEndBalde(int idx) throws Exception { dir.seek(4L + (long) idx * Long.BYTES); return dir.readLong(); }
    private void setEndBalde(int idx, long end) throws Exception { dir.seek(4L + (long) idx * Long.BYTES); dir.writeLong(end); }

    private Balde lerBalde(long endereco) throws Exception {
        bkt.seek(endereco);
        Balde b = new Balde(bkt.readInt(), endereco);
        b.count = bkt.readInt();
        for (int i = 0; i < CAPACIDADE; i++) { b.ativos[i] = bkt.readByte() == 1; bkt.readFully(b.dados[i]); }
        return b;
    }

    private void escreverBalde(Balde b) throws Exception {
        bkt.seek(b.endereco);
        bkt.writeInt(b.profLocal); bkt.writeInt(b.count);
        for (int i = 0; i < CAPACIDADE; i++) { bkt.writeByte(b.ativos[i] ? 1 : 0); bkt.write(b.dados[i]); }
    }

    private int hash(int h, int prof) { return prof == 0 ? 0 : (h & ((1 << prof) - 1)); }

    private T decodificar(byte[] dados) throws IOException {
        @SuppressWarnings("unchecked") T reg = (T) prototipo.novaInstancia();
        reg.fromByteArray(dados); return reg;
    }

    /** Busca a entrada pela chave. Retorna null se não encontrada. */
    public T buscar(T chave) throws Exception {
        int prof = getProfGlobal();
        Balde b = lerBalde(getEndBalde(hash(chave.hashCode_(), prof)));
        for (int i = 0; i < b.count; i++) {
            if (b.ativos[i]) { T reg = decodificar(b.dados[i]); if (reg.chaveIgual(chave)) return reg; }
        }
        return null;
    }

    /** Insere ou atualiza a entrada. */
    public void inserir(T registro) throws Exception {
        byte[] regBytes = registro.toByteArray();
        int prof = getProfGlobal();
        Balde b = lerBalde(getEndBalde(hash(registro.hashCode_(), prof)));
        for (int i = 0; i < b.count; i++) {
            if (b.ativos[i]) {
                T existente = decodificar(b.dados[i]);
                if (existente.chaveIgual(registro)) { b.dados[i] = regBytes; escreverBalde(b); return; }
            }
        }
        for (int i = 0; i < b.count; i++) {
            if (!b.ativos[i]) { b.ativos[i] = true; b.dados[i] = regBytes; escreverBalde(b); return; }
        }
        if (b.count < CAPACIDADE) {
            b.ativos[b.count] = true; b.dados[b.count] = regBytes; b.count++; escreverBalde(b);
        } else {
            dividirEInserir(b, registro, regBytes, prof);
        }
    }

    /** Exclusão lógica (lápide). Retorna true se encontrou. */
    public boolean excluir(T chave) throws Exception {
        int prof = getProfGlobal();
        Balde b = lerBalde(getEndBalde(hash(chave.hashCode_(), prof)));
        for (int i = 0; i < b.count; i++) {
            if (b.ativos[i]) {
                T reg = decodificar(b.dados[i]);
                if (reg.chaveIgual(chave)) { b.ativos[i] = false; escreverBalde(b); return true; }
            }
        }
        return false;
    }

    public void close() throws Exception { dir.getFD().sync(); bkt.getFD().sync(); dir.close(); bkt.close(); }

    private void dividirEInserir(Balde b, T registro, byte[] regBytes, int profGlobal) throws Exception {
        if (b.profLocal == profGlobal) { dobrarDiretorio(profGlobal); profGlobal++; }
        int novaProfLocal = b.profLocal + 1;
        int bitMask = 1 << (novaProfLocal - 1);
        long endNovo = bkt.length();
        Balde b0 = new Balde(novaProfLocal, b.endereco);
        Balde b1 = new Balde(novaProfLocal, endNovo);
        for (int i = 0; i < b.count; i++) {
            if (!b.ativos[i]) continue;
            T reg = decodificar(b.dados[i]);
            Balde dest = (hash(reg.hashCode_(), novaProfLocal) & bitMask) == 0 ? b0 : b1;
            dest.ativos[dest.count] = true; dest.dados[dest.count] = b.dados[i]; dest.count++;
        }
        escreverBalde(b1); escreverBalde(b0);
        int tamDir = 1 << profGlobal;
        for (int i = 0; i < tamDir; i++) {
            if (getEndBalde(i) == b.endereco) setEndBalde(i, (i & bitMask) == 0 ? b0.endereco : endNovo);
        }
        inserir(registro);
    }

    private void dobrarDiretorio(int profAtual) throws Exception {
        int tam = 1 << profAtual;
        long[] antigos = new long[tam];
        for (int i = 0; i < tam; i++) antigos[i] = getEndBalde(i);
        setProfGlobal(profAtual + 1);
        for (int i = 0; i < tam * 2; i++) setEndBalde(i, antigos[i % tam]);
    }
}
