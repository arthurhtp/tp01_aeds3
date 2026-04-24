package stockit.dao;

import java.io.*;

/**
 * Hash Extensível para índice primário (PK).
 * Mapeia chave inteira (ID) → posição no arquivo de dados (long).
 *
 * Arquivos em disco:
 *   <nome>.dir : [int profundidadeGlobal][long endereço]^(2^profGlobal)
 *   <nome>.bkt : sequência de baldes de tamanho fixo:
 *                [int profundidadeLocal][int numElementos][CAPACIDADE × (int chave + long valor)]
 */
public class HashExtensivel {

    private static final int CAPACIDADE  = 4;
    private static final int TAM_ENTRADA = Integer.BYTES + Long.BYTES;   // 12 bytes por entrada
    private static final int TAM_BALDE   = Integer.BYTES + Integer.BYTES
                                         + CAPACIDADE * TAM_ENTRADA;     // 56 bytes por balde

    private final RandomAccessFile dir; // arquivo de diretório
    private final RandomAccessFile bkt; // arquivo de baldes

    private static class Balde {
        int    profLocal;
        int    count;
        int[]  chaves  = new int [CAPACIDADE];
        long[] valores = new long[CAPACIDADE];
        long   endereco;

        Balde(int profLocal, long endereco) {
            this.profLocal = profLocal;
            this.endereco  = endereco;
        }
    }

    // ------------------------------------------------------------------
    // Inicialização
    // ------------------------------------------------------------------

    public HashExtensivel(String nomeBase) throws Exception {
        File pasta = new File("./data/" + nomeBase);
        if (!pasta.exists()) pasta.mkdirs();

        String prefixo = "./data/" + nomeBase + "/" + nomeBase;
        dir = new RandomAccessFile(prefixo + ".dir", "rw");
        bkt = new RandomAccessFile(prefixo + ".bkt", "rw");

        if (dir.length() == 0) {
            // Estado inicial: profundidade global 0, um único balde vazio
            setProfGlobal(0);
            setEndBalde(0, 0L);
            escreverBalde(new Balde(0, 0L));
        }
    }

    // ------------------------------------------------------------------
    // Primitivas de diretório
    // ------------------------------------------------------------------

    private int getProfGlobal() throws Exception {
        dir.seek(0);
        return dir.readInt();
    }

    private void setProfGlobal(int p) throws Exception {
        dir.seek(0);
        dir.writeInt(p);
    }

    private long getEndBalde(int idx) throws Exception {
        dir.seek(4L + (long) idx * Long.BYTES);
        return dir.readLong();
    }

    private void setEndBalde(int idx, long end) throws Exception {
        dir.seek(4L + (long) idx * Long.BYTES);
        dir.writeLong(end);
    }

    // ------------------------------------------------------------------
    // Primitivas de balde
    // ------------------------------------------------------------------

    private Balde lerBalde(long endereco) throws Exception {
        bkt.seek(endereco);
        Balde b = new Balde(bkt.readInt(), endereco);
        b.count = bkt.readInt();
        for (int i = 0; i < CAPACIDADE; i++) {
            b.chaves[i]  = bkt.readInt();
            b.valores[i] = bkt.readLong();
        }
        return b;
    }

    private void escreverBalde(Balde b) throws Exception {
        bkt.seek(b.endereco);
        bkt.writeInt(b.profLocal);
        bkt.writeInt(b.count);
        for (int i = 0; i < CAPACIDADE; i++) {
            bkt.writeInt (i < b.count ? b.chaves[i]  : 0);
            bkt.writeLong(i < b.count ? b.valores[i] : 0L);
        }
    }

    // ------------------------------------------------------------------
    // Função de hash: usa os últimos 'prof' bits da chave
    // ------------------------------------------------------------------

    private int hash(int chave, int prof) {
        return prof == 0 ? 0 : (chave & ((1 << prof) - 1));
    }

    // ------------------------------------------------------------------
    // Operações públicas
    // ------------------------------------------------------------------

    /** Retorna a posição no arquivo de dados, ou -1 se não encontrada. */
    public long buscar(int chave) throws Exception {
        int   prof = getProfGlobal();
        Balde b    = lerBalde(getEndBalde(hash(chave, prof)));
        for (int i = 0; i < b.count; i++)
            if (b.chaves[i] == chave) return b.valores[i];
        return -1;
    }

    /** Insere ou atualiza a entrada (chave → valor). */
    public void inserir(int chave, long valor) throws Exception {
        int   prof = getProfGlobal();
        Balde b    = lerBalde(getEndBalde(hash(chave, prof)));

        // Atualiza se a chave já existe
        for (int i = 0; i < b.count; i++) {
            if (b.chaves[i] == chave) {
                b.valores[i] = valor;
                escreverBalde(b);
                return;
            }
        }

        if (b.count < CAPACIDADE) {
            b.chaves[b.count]  = chave;
            b.valores[b.count] = valor;
            b.count++;
            escreverBalde(b);
        } else {
            dividirEInserir(b, chave, valor, prof);
        }
    }

    /** Remove a entrada com a chave dada. Retorna true se encontrada. */
    public boolean excluir(int chave) throws Exception {
        int   prof = getProfGlobal();
        Balde b    = lerBalde(getEndBalde(hash(chave, prof)));

        for (int i = 0; i < b.count; i++) {
            if (b.chaves[i] == chave) {
                System.arraycopy(b.chaves,  i + 1, b.chaves,  i, b.count - i - 1);
                System.arraycopy(b.valores, i + 1, b.valores, i, b.count - i - 1);
                b.count--;
                escreverBalde(b);
                return true;
            }
        }
        return false;
    }

    public void close() throws Exception {
        dir.close();
        bkt.close();
    }

    // ------------------------------------------------------------------
    // Divisão de balde
    // ------------------------------------------------------------------

    private void dividirEInserir(Balde b, int chave, long valor, int profGlobal) throws Exception {
        if (b.profLocal == profGlobal) {
            dobrarDiretorio(profGlobal);
            profGlobal++;
        }

        int  novaProfLocal = b.profLocal + 1;
        int  bitMask       = 1 << (novaProfLocal - 1);
        long endNovo       = bkt.length();

        Balde b0 = new Balde(novaProfLocal, b.endereco); // reutiliza espaço do balde antigo
        Balde b1 = new Balde(novaProfLocal, endNovo);    // novo balde no fim do arquivo

        // Redistribui as entradas existentes pelo novo bit discriminante
        for (int i = 0; i < b.count; i++) {
            if ((b.chaves[i] & bitMask) == 0) {
                b0.chaves[b0.count]  = b.chaves[i];
                b0.valores[b0.count] = b.valores[i];
                b0.count++;
            } else {
                b1.chaves[b1.count]  = b.chaves[i];
                b1.valores[b1.count] = b.valores[i];
                b1.count++;
            }
        }

        // Persiste: novo balde primeiro (estende o arquivo), depois sobrescreve o antigo
        escreverBalde(b1);
        escreverBalde(b0);

        // Atualiza ponteiros do diretório
        int tamDir = 1 << profGlobal;
        for (int i = 0; i < tamDir; i++) {
            if (getEndBalde(i) == b.endereco) {
                setEndBalde(i, (i & bitMask) == 0 ? b0.endereco : endNovo);
            }
        }

        // Tenta inserir novamente (pode ocorrer nova divisão)
        inserir(chave, valor);
    }

    private void dobrarDiretorio(int profAtual) throws Exception {
        int    tam     = 1 << profAtual;
        long[] antigos = new long[tam];
        for (int i = 0; i < tam; i++) antigos[i] = getEndBalde(i);

        setProfGlobal(profAtual + 1);
        for (int i = 0; i < tam * 2; i++) setEndBalde(i, antigos[i % tam]);
    }
}
