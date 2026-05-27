package stockit.dao;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

// Arvore B+ generica persistente em disco. Pares (chave int, valor long) ordenados.
public class ArvoreBMais {

    private static final int ORDEM = 5;
    private static final int MIN_CHAVES = (ORDEM + 1) / 2 - 1;

    private RandomAccessFile arquivo;
    private long endRaiz;

    // No interno: 1(tipo) + 4(numChaves) + ORDEM*4(chaves) + (ORDEM+1)*8(filhos)
    // Folha:      1(tipo) + 4(numChaves) + ORDEM*4(chaves) + ORDEM*8(valores) + 8(proxFolha) = 1+4+20+40+8 = 73
    private static final int TAM_NO = 1 + 4 + ORDEM * 4 + (ORDEM + 1) * 8;

    private static final byte TIPO_INTERNO = 0;
    private static final byte TIPO_FOLHA = 1;

    public ArvoreBMais(String pastaEntidade, String nomeIndice) throws Exception {
        File pasta = new File("./data/" + pastaEntidade);
        if (!pasta.exists()) pasta.mkdirs();
        String caminho = "./data/" + pastaEntidade + "/" + nomeIndice + ".bplus";
        arquivo = new RandomAccessFile(caminho, "rw");

        if (arquivo.length() == 0) {
            // Criar cabeçalho + raiz vazia (folha)
            arquivo.writeInt(ORDEM);
            endRaiz = 12; // logo após o cabeçalho
            arquivo.writeLong(endRaiz);
            escreverFolha(endRaiz, new int[0], new long[0], -1);
        } else {
            arquivo.seek(0);
            arquivo.readInt(); // ordem
            endRaiz = arquivo.readLong();
        }
    }

    // ==================== INSERÇÃO ====================

    public void inserir(int chave, long valor) throws Exception {
        ResultadoInsercao res = inserirRec(endRaiz, chave, valor);
        if (res != null) {
            // Raiz dividiu — criar nova raiz interna
            long novaRaiz = arquivo.length();
            int[] chaves = new int[] { res.chaveProm };
            long[] filhos = new long[] { endRaiz, res.novoNo };
            escreverInterno(novaRaiz, chaves, filhos);
            endRaiz = novaRaiz;
            arquivo.seek(4);
            arquivo.writeLong(endRaiz);
        }
    }

    private ResultadoInsercao inserirRec(long endNo, int chave, long valor) throws Exception {
        arquivo.seek(endNo);
        byte tipo = arquivo.readByte();

        if (tipo == TIPO_FOLHA) {
            return inserirFolha(endNo, chave, valor);
        } else {
            // Nó interno — encontrar filho
            int numChaves = arquivo.readInt();
            int[] chaves = new int[numChaves];
            long[] filhos = new long[numChaves + 1];
            for (int i = 0; i < numChaves; i++) chaves[i] = arquivo.readInt();
            // Pular bytes restantes de chaves não usadas
            arquivo.skipBytes((ORDEM - numChaves) * 4);
            for (int i = 0; i <= numChaves; i++) filhos[i] = arquivo.readLong();

            int idx = numChaves;
            for (int i = 0; i < numChaves; i++) {
                if (chave < chaves[i]) { idx = i; break; }
            }

            ResultadoInsercao res = inserirRec(filhos[idx], chave, valor);
            if (res == null) return null;

            // Precisa inserir chaveProm no nó interno
            if (numChaves < ORDEM) {
                // Cabe
                int[] novasChaves = new int[numChaves + 1];
                long[] novosFilhos = new long[numChaves + 2];
                int pos = 0;
                for (int i = 0; i < numChaves; i++) {
                    if (i == idx) {
                        novasChaves[pos] = res.chaveProm;
                        novosFilhos[pos] = filhos[i];
                        novosFilhos[pos + 1] = res.novoNo;
                        pos++;
                    }
                    novasChaves[pos] = chaves[i];
                    if (i != idx) novosFilhos[pos] = filhos[i];
                    pos++;
                }
                if (idx == numChaves) {
                    novasChaves[pos] = res.chaveProm;
                    novosFilhos[pos] = filhos[numChaves];
                    novosFilhos[pos + 1] = res.novoNo;
                } else {
                    novosFilhos[numChaves + 1] = filhos[numChaves];
                }
                escreverInterno(endNo, novasChaves, novosFilhos);
                return null;
            } else {
                // Dividir nó interno
                return dividirInterno(endNo, chaves, filhos, numChaves, idx, res.chaveProm, res.novoNo);
            }
        }
    }

    private ResultadoInsercao inserirFolha(long endNo, int chave, long valor) throws Exception {
        arquivo.seek(endNo + 1); // pular tipo
        int numChaves = arquivo.readInt();
        int[] chaves = new int[numChaves];
        long[] valores = new long[numChaves];
        for (int i = 0; i < numChaves; i++) chaves[i] = arquivo.readInt();
        arquivo.skipBytes((ORDEM - numChaves) * 4);
        for (int i = 0; i < numChaves; i++) valores[i] = arquivo.readLong();
        arquivo.skipBytes((ORDEM - numChaves) * 8);
        long proxFolha = arquivo.readLong();

        // Verificar se chave já existe (atualizar)
        for (int i = 0; i < numChaves; i++) {
            if (chaves[i] == chave) {
                valores[i] = valor;
                escreverFolha(endNo, chaves, valores, proxFolha);
                return null;
            }
        }

        if (numChaves < ORDEM) {
            // Cabe — inserir ordenado
            int[] novasChaves = new int[numChaves + 1];
            long[] novosValores = new long[numChaves + 1];
            int pos = 0;
            boolean inserido = false;
            for (int i = 0; i < numChaves; i++) {
                if (!inserido && chave < chaves[i]) {
                    novasChaves[pos] = chave;
                    novosValores[pos] = valor;
                    pos++;
                    inserido = true;
                }
                novasChaves[pos] = chaves[i];
                novosValores[pos] = valores[i];
                pos++;
            }
            if (!inserido) {
                novasChaves[pos] = chave;
                novosValores[pos] = valor;
            }
            escreverFolha(endNo, novasChaves, novosValores, proxFolha);
            return null;
        } else {
            // Dividir folha
            return dividirFolha(endNo, chaves, valores, numChaves, chave, valor, proxFolha);
        }
    }

    private ResultadoInsercao dividirFolha(long endNo, int[] chaves, long[] valores,
                                            int numChaves, int novaChave, long novoValor, long proxFolha) throws Exception {
        // Juntar tudo e ordenar
        int total = numChaves + 1;
        int[] todas = new int[total];
        long[] todosVal = new long[total];
        int pos = 0;
        boolean inserido = false;
        for (int i = 0; i < numChaves; i++) {
            if (!inserido && novaChave < chaves[i]) {
                todas[pos] = novaChave;
                todosVal[pos] = novoValor;
                pos++;
                inserido = true;
            }
            todas[pos] = chaves[i];
            todosVal[pos] = valores[i];
            pos++;
        }
        if (!inserido) {
            todas[pos] = novaChave;
            todosVal[pos] = novoValor;
        }

        int meio = total / 2;

        // Esquerda fica no nó atual
        int[] chavesEsq = new int[meio];
        long[] valoresEsq = new long[meio];
        System.arraycopy(todas, 0, chavesEsq, 0, meio);
        System.arraycopy(todosVal, 0, valoresEsq, 0, meio);

        // Direita vai para novo nó
        int tamDir = total - meio;
        int[] chavesDir = new int[tamDir];
        long[] valoresDir = new long[tamDir];
        System.arraycopy(todas, meio, chavesDir, 0, tamDir);
        System.arraycopy(todosVal, meio, valoresDir, 0, tamDir);

        long novoEnd = arquivo.length();
        escreverFolha(novoEnd, chavesDir, valoresDir, proxFolha);
        escreverFolha(endNo, chavesEsq, valoresEsq, novoEnd);

        return new ResultadoInsercao(chavesDir[0], novoEnd);
    }

    private ResultadoInsercao dividirInterno(long endNo, int[] chaves, long[] filhos,
                                              int numChaves, int idxInsercao, int chaveProm, long novoFilho) throws Exception {
        // Inserir na posição correta
        int total = numChaves + 1;
        int[] todas = new int[total];
        long[] todosFilhos = new long[total + 1];

        int pos = 0;
        for (int i = 0; i < numChaves; i++) {
            if (i == idxInsercao) {
                todas[pos] = chaveProm;
                todosFilhos[pos] = filhos[i];
                todosFilhos[pos + 1] = novoFilho;
                pos++;
            }
            todas[pos] = chaves[i];
            if (i != idxInsercao) todosFilhos[pos] = filhos[i];
            pos++;
        }
        if (idxInsercao == numChaves) {
            todas[pos] = chaveProm;
            todosFilhos[pos] = filhos[numChaves];
            todosFilhos[pos + 1] = novoFilho;
        } else {
            todosFilhos[total] = filhos[numChaves];
        }

        int meio = total / 2;
        int chaveSubir = todas[meio];

        // Esquerda
        int[] chavesEsq = new int[meio];
        long[] filhosEsq = new long[meio + 1];
        System.arraycopy(todas, 0, chavesEsq, 0, meio);
        System.arraycopy(todosFilhos, 0, filhosEsq, 0, meio + 1);

        // Direita
        int tamDir = total - meio - 1;
        int[] chavesDir = new int[tamDir];
        long[] filhosDir = new long[tamDir + 1];
        System.arraycopy(todas, meio + 1, chavesDir, 0, tamDir);
        System.arraycopy(todosFilhos, meio + 1, filhosDir, 0, tamDir + 1);

        long novoEnd = arquivo.length();
        escreverInterno(endNo, chavesEsq, filhosEsq);
        escreverInterno(novoEnd, chavesDir, filhosDir);

        return new ResultadoInsercao(chaveSubir, novoEnd);
    }

    // ==================== BUSCA ====================

    /** Busca o valor associado a uma chave. Retorna -1 se não encontrada. */
    public long buscar(int chave) throws Exception {
        return buscarRec(endRaiz, chave);
    }

    private long buscarRec(long endNo, int chave) throws Exception {
        arquivo.seek(endNo);
        byte tipo = arquivo.readByte();
        int numChaves = arquivo.readInt();
        int[] chaves = new int[numChaves];
        for (int i = 0; i < numChaves; i++) chaves[i] = arquivo.readInt();

        if (tipo == TIPO_FOLHA) {
            arquivo.skipBytes((ORDEM - numChaves) * 4);
            long[] valores = new long[numChaves];
            for (int i = 0; i < numChaves; i++) valores[i] = arquivo.readLong();
            for (int i = 0; i < numChaves; i++) {
                if (chaves[i] == chave) return valores[i];
            }
            return -1;
        } else {
            arquivo.skipBytes((ORDEM - numChaves) * 4);
            long[] filhos = new long[numChaves + 1];
            for (int i = 0; i <= numChaves; i++) filhos[i] = arquivo.readLong();

            int idx = numChaves;
            for (int i = 0; i < numChaves; i++) {
                if (chave < chaves[i]) { idx = i; break; }
            }
            return buscarRec(filhos[idx], chave);
        }
    }

    /** Retorna todos os pares (chave, valor) em ordem crescente de chave. */
    public List<long[]> listarOrdenado() throws Exception {
        List<long[]> resultado = new ArrayList<>();
        long folha = encontrarPrimeiraFolha(endRaiz);
        while (folha != -1) {
            arquivo.seek(folha);
            byte tipo = arquivo.readByte();
            int numChaves = arquivo.readInt();
            int[] chaves = new int[numChaves];
            for (int i = 0; i < numChaves; i++) chaves[i] = arquivo.readInt();
            arquivo.skipBytes((ORDEM - numChaves) * 4);
            long[] valores = new long[numChaves];
            for (int i = 0; i < numChaves; i++) valores[i] = arquivo.readLong();
            arquivo.skipBytes((ORDEM - numChaves) * 8);
            long proxFolha = arquivo.readLong();

            for (int i = 0; i < numChaves; i++) {
                resultado.add(new long[] { chaves[i], valores[i] });
            }
            folha = proxFolha;
        }
        return resultado;
    }

    private long encontrarPrimeiraFolha(long endNo) throws Exception {
        arquivo.seek(endNo);
        byte tipo = arquivo.readByte();
        if (tipo == TIPO_FOLHA) return endNo;
        int numChaves = arquivo.readInt();
        arquivo.skipBytes(ORDEM * 4); // pular chaves
        long primeiroFilho = arquivo.readLong();
        return encontrarPrimeiraFolha(primeiroFilho);
    }

    // ==================== EXCLUSÃO ====================

    public boolean excluir(int chave) throws Exception {
        return excluirDaFolha(endRaiz, chave);
    }

    private boolean excluirDaFolha(long endNo, int chave) throws Exception {
        arquivo.seek(endNo);
        byte tipo = arquivo.readByte();
        int numChaves = arquivo.readInt();
        int[] chaves = new int[numChaves];
        for (int i = 0; i < numChaves; i++) chaves[i] = arquivo.readInt();

        if (tipo == TIPO_FOLHA) {
            arquivo.skipBytes((ORDEM - numChaves) * 4);
            long[] valores = new long[numChaves];
            for (int i = 0; i < numChaves; i++) valores[i] = arquivo.readLong();
            arquivo.skipBytes((ORDEM - numChaves) * 8);
            long proxFolha = arquivo.readLong();

            int idx = -1;
            for (int i = 0; i < numChaves; i++) {
                if (chaves[i] == chave) { idx = i; break; }
            }
            if (idx == -1) return false;

            int novoNum = numChaves - 1;
            int[] novasChaves = new int[novoNum];
            long[] novosValores = new long[novoNum];
            int pos = 0;
            for (int i = 0; i < numChaves; i++) {
                if (i != idx) {
                    novasChaves[pos] = chaves[i];
                    novosValores[pos] = valores[i];
                    pos++;
                }
            }
            escreverFolha(endNo, novasChaves, novosValores, proxFolha);
            return true;
        } else {
            arquivo.skipBytes((ORDEM - numChaves) * 4);
            long[] filhos = new long[numChaves + 1];
            for (int i = 0; i <= numChaves; i++) filhos[i] = arquivo.readLong();

            int idx = numChaves;
            for (int i = 0; i < numChaves; i++) {
                if (chave < chaves[i]) { idx = i; break; }
            }
            return excluirDaFolha(filhos[idx], chave);
        }
    }

    // ==================== VISUALIZAÇÃO ====================

    /** Retorna a estrutura da árvore para visualização. */
    public NoVisualizacao getEstrutura() throws Exception {
        return lerNoVisualizacao(endRaiz);
    }

    private NoVisualizacao lerNoVisualizacao(long endNo) throws Exception {
        arquivo.seek(endNo);
        byte tipo = arquivo.readByte();
        int numChaves = arquivo.readInt();
        int[] chaves = new int[numChaves];
        for (int i = 0; i < numChaves; i++) chaves[i] = arquivo.readInt();
        arquivo.skipBytes((ORDEM - numChaves) * 4);

        NoVisualizacao no = new NoVisualizacao();
        no.endereco = endNo;
        no.folha = (tipo == TIPO_FOLHA);
        no.chaves = chaves;

        if (tipo == TIPO_FOLHA) {
            long[] valores = new long[numChaves];
            for (int i = 0; i < numChaves; i++) valores[i] = arquivo.readLong();
            arquivo.skipBytes((ORDEM - numChaves) * 8);
            no.valores = valores;
            no.proxFolha = arquivo.readLong();
        } else {
            long[] filhos = new long[numChaves + 1];
            for (int i = 0; i <= numChaves; i++) filhos[i] = arquivo.readLong();
            no.filhos = new ArrayList<>();
            for (int i = 0; i <= numChaves; i++) {
                no.filhos.add(lerNoVisualizacao(filhos[i]));
            }
        }
        return no;
    }

    public static class NoVisualizacao {
        public long endereco;
        public boolean folha;
        public int[] chaves;
        public long[] valores; // só folhas
        public long proxFolha; // só folhas
        public List<NoVisualizacao> filhos; // só internos
    }

    // ==================== I/O ====================

    private void escreverFolha(long endereco, int[] chaves, long[] valores, long proxFolha) throws Exception {
        arquivo.seek(endereco);
        arquivo.writeByte(TIPO_FOLHA);
        arquivo.writeInt(chaves.length);
        for (int i = 0; i < ORDEM; i++) arquivo.writeInt(i < chaves.length ? chaves[i] : 0);
        for (int i = 0; i < ORDEM; i++) arquivo.writeLong(i < chaves.length ? valores[i] : 0);
        arquivo.writeLong(proxFolha);
    }

    private void escreverInterno(long endereco, int[] chaves, long[] filhos) throws Exception {
        arquivo.seek(endereco);
        arquivo.writeByte(TIPO_INTERNO);
        arquivo.writeInt(chaves.length);
        for (int i = 0; i < ORDEM; i++) arquivo.writeInt(i < chaves.length ? chaves[i] : 0);
        for (int i = 0; i <= ORDEM; i++) arquivo.writeLong(i < filhos.length ? filhos[i] : 0);
    }

    public void close() throws Exception {
        arquivo.getFD().sync();
        arquivo.close();
    }

    private static class ResultadoInsercao {
        int chaveProm;
        long novoNo;
        ResultadoInsercao(int c, long n) { this.chaveProm = c; this.novoNo = n; }
    }
}
