package stockit.dao;

import java.io.*;
import java.util.*;

/**
 * Compressão LZW — opera sobre bytes brutos de um arquivo.
 * Gera um arquivo .lzw contendo:
 *   [8 bytes: tamanho original]
 *   [sequência de int (4 bytes cada): códigos LZW]
 */
public class CompressaoLZW {

    /** Comprime src → dest.lzw. Retorna tamanho do arquivo de saída. */
    public static long comprimir(String src, String dest) throws IOException {
        byte[] dados = lerArquivo(src);
        if (dados.length == 0) {
            new File(dest).createNewFile();
            return 0;
        }

        // Inicializar dicionário com os 256 símbolos básicos
        Map<String, Integer> dicionario = new HashMap<>();
        for (int i = 0; i < 256; i++) dicionario.put(String.valueOf((char) i), i);

        List<Integer> codigos = new ArrayList<>();
        String w = String.valueOf((char) (dados[0] & 0xFF));

        for (int i = 1; i < dados.length; i++) {
            String c = String.valueOf((char) (dados[i] & 0xFF));
            String wc = w + c;
            if (dicionario.containsKey(wc)) {
                w = wc;
            } else {
                codigos.add(dicionario.get(w));
                dicionario.put(wc, dicionario.size());
                w = c;
            }
        }
        codigos.add(dicionario.get(w));

        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(dest)))) {
            out.writeLong(dados.length);
            for (int codigo : codigos) out.writeInt(codigo);
        }
        return new File(dest).length();
    }

    /** Descomprime src.lzw → dest. */
    public static long descomprimir(String src, String dest) throws IOException {
        List<Integer> codigos = new ArrayList<>();
        long tamanhoOriginal;

        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(src)))) {
            tamanhoOriginal = in.readLong();
            try {
                while (true) codigos.add(in.readInt());
            } catch (EOFException ignored) {}
        }

        if (codigos.isEmpty()) {
            new File(dest).createNewFile();
            return 0;
        }

        // Inicializar dicionário reverso
        Map<Integer, String> dicionario = new HashMap<>();
        for (int i = 0; i < 256; i++) dicionario.put(i, String.valueOf((char) i));

        StringBuilder resultado = new StringBuilder();
        String w = dicionario.get(codigos.get(0));
        resultado.append(w);

        for (int i = 1; i < codigos.size(); i++) {
            int codigo = codigos.get(i);
            String entrada;
            if (dicionario.containsKey(codigo)) {
                entrada = dicionario.get(codigo);
            } else if (codigo == dicionario.size()) {
                entrada = w + w.charAt(0);
            } else {
                throw new IOException("Código LZW inválido: " + codigo);
            }
            resultado.append(entrada);
            dicionario.put(dicionario.size(), w + entrada.charAt(0));
            w = entrada;
        }

        // Converter string de volta para bytes
        try (FileOutputStream fos = new FileOutputStream(dest)) {
            String s = resultado.toString();
            for (int i = 0; i < s.length(); i++) fos.write(s.charAt(i) & 0xFF);
        }
        return new File(dest).length();
    }

    private static byte[] lerArquivo(String path) throws IOException {
        File f = new File(path);
        if (!f.exists() || f.length() == 0) return new byte[0];
        try (FileInputStream fis = new FileInputStream(f)) { return fis.readAllBytes(); }
    }
}
