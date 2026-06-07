package stockit.dao;

import java.io.*;
import java.util.*;

/**
 * Compressão Huffman — opera sobre bytes brutos de um arquivo.
 * Gera um arquivo .huff contendo:
 *   [4 bytes: tamanho da tabela serializada]
 *   [tabela: mapa char→frequência serializado]
 *   [8 bytes: total de bits válidos]
 *   [bytes comprimidos]
 */
public class CompressaoHuffman {

    private static class No implements Comparable<No> {
        byte simbolo;
        int  frequencia;
        No   esq, dir;
        No(byte s, int f)          { simbolo = s; frequencia = f; }
        No(int f, No e, No d)      { frequencia = f; esq = e; dir = d; }
        public int compareTo(No o) { return Integer.compare(frequencia, o.frequencia); }
        boolean folha()            { return esq == null && dir == null; }
    }

    /** Comprime src → dest.huff. Retorna tamanho do arquivo de saída em bytes. */
    public static long comprimir(String src, String dest) throws IOException {
        byte[] dados = lerArquivo(src);
        if (dados.length == 0) {
            new File(dest).createNewFile();
            return 0;
        }

        // Frequências
        Map<Byte, Integer> freq = new LinkedHashMap<>();
        for (byte b : dados) freq.merge(b, 1, Integer::sum);

        // Árvore
        PriorityQueue<No> fila = new PriorityQueue<>();
        for (var e : freq.entrySet()) fila.add(new No(e.getKey(), e.getValue()));
        if (fila.size() == 1) fila.add(new No(fila.peek().simbolo, 0)); // edge case
        while (fila.size() > 1) {
            No e = fila.poll(), d = fila.poll();
            fila.add(new No(e.frequencia + d.frequencia, e, d));
        }
        No raiz = fila.poll();

        // Tabela de códigos
        Map<Byte, String> tabela = new HashMap<>();
        gerarCodigos(raiz, "", tabela);

        // Codificar
        StringBuilder bits = new StringBuilder();
        for (byte b : dados) bits.append(tabela.get(b));

        // Serializar tabela de frequências
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(freq.size());
        for (var e : freq.entrySet()) { dos.writeByte(e.getKey()); dos.writeInt(e.getValue()); }
        byte[] tabelaBytes = baos.toByteArray();

        // Escrever arquivo
        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(dest)))) {
            out.writeInt(tabelaBytes.length);
            out.write(tabelaBytes);
            out.writeLong(bits.length());
            // Compactar bits em bytes
            int totalBytes = (bits.length() + 7) / 8;
            for (int i = 0; i < totalBytes; i++) {
                int byteVal = 0;
                for (int j = 0; j < 8; j++) {
                    int pos = i * 8 + j;
                    if (pos < bits.length() && bits.charAt(pos) == '1')
                        byteVal |= (1 << (7 - j));
                }
                out.writeByte(byteVal);
            }
        }
        return new File(dest).length();
    }

    /** Descomprime src.huff → dest. */
    public static long descomprimir(String src, String dest) throws IOException {
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(src)))) {
            int tamTabela = in.readInt();
            byte[] tabelaBytes = new byte[tamTabela];
            in.readFully(tabelaBytes);

            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(tabelaBytes));
            int qtd = dis.readInt();
            Map<Byte, Integer> freq = new LinkedHashMap<>();
            for (int i = 0; i < qtd; i++) freq.put(dis.readByte(), dis.readInt());

            long totalBits = in.readLong();

            // Reconstruir árvore
            PriorityQueue<No> fila = new PriorityQueue<>();
            for (var e : freq.entrySet()) fila.add(new No(e.getKey(), e.getValue()));
            if (fila.size() == 1) fila.add(new No(fila.peek().simbolo, 0));
            while (fila.size() > 1) {
                No e = fila.poll(), d = fila.poll();
                fila.add(new No(e.frequencia + d.frequencia, e, d));
            }
            No raiz = fila.poll();

            // Ler bytes restantes
            byte[] comprimido = in.readAllBytes();

            // Decodificar
            try (FileOutputStream fos = new FileOutputStream(dest)) {
                No atual = raiz;
                long bitsLidos = 0;
                for (byte b : comprimido) {
                    for (int j = 7; j >= 0 && bitsLidos < totalBits; j--) {
                        boolean bit = (b & (1 << j)) != 0;
                        atual = bit ? atual.dir : atual.esq;
                        if (atual.folha()) {
                            fos.write(atual.simbolo);
                            atual = raiz;
                        }
                        bitsLidos++;
                    }
                }
            }
        }
        return new File(dest).length();
    }

    private static void gerarCodigos(No no, String codigo, Map<Byte, String> tabela) {
        if (no == null) return;
        if (no.folha()) { tabela.put(no.simbolo, codigo.isEmpty() ? "0" : codigo); return; }
        gerarCodigos(no.esq, codigo + "0", tabela);
        gerarCodigos(no.dir, codigo + "1", tabela);
    }

    private static byte[] lerArquivo(String path) throws IOException {
        File f = new File(path);
        if (!f.exists() || f.length() == 0) return new byte[0];
        try (FileInputStream fis = new FileInputStream(f)) { return fis.readAllBytes(); }
    }
}
