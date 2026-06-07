package stockit.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stockit.dao.CompressaoHuffman;
import stockit.dao.CompressaoLZW;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Comprime todos os arquivos de dados (.dat, .dir, .bkt, .bplus) de uma entidade
 * e retorna a visualização lado a lado: bytes originais vs bytes comprimidos.
 *
 * Endpoints:
 *   POST /compressao/{entidade}/huffman  → retorna original + comprimido + métricas
 *   POST /compressao/{entidade}/lzw     → retorna original + comprimido + métricas
 */
@RestController
@RequestMapping("/compressao")
@CrossOrigin(origins = "*")
public class CompressaoController {

    private static final String DATA_PATH = "./data/";
    private static final String BACKUP_PATH = "./data/backups/";

    // ==========================================================
    // Huffman
    // ==========================================================

    @PostMapping("/{entidade}/huffman")
    public ResponseEntity<?> comprimirHuffman(@PathVariable String entidade) {
        return executarCompressao(entidade, "Huffman");
    }

    // ==========================================================
    // LZW
    // ==========================================================

    @PostMapping("/{entidade}/lzw")
    public ResponseEntity<?> comprimirLzw(@PathVariable String entidade) {
        return executarCompressao(entidade, "LZW");
    }

    // ==========================================================
    // Lógica comum
    // ==========================================================

    private ResponseEntity<?> executarCompressao(String entidade, String algoritmo) {
        try {
            garantirPastaBackup();
            List<File> arquivos = listarArquivosEntidade(entidade);
            if (arquivos.isEmpty()) return ResponseEntity.notFound().build();

            // Concatenar todos os arquivos da entidade em um único temp
            String caminhoTemp = BACKUP_PATH + entidade + "_temp.bin";
            String ext = algoritmo.equals("Huffman") ? "huff" : "lzw";
            String caminhoSaida = BACKUP_PATH + entidade + "_backup." + ext;
            concatenarArquivos(arquivos, caminhoTemp);

            byte[] bytesOriginais = Files.readAllBytes(Path.of(caminhoTemp));

            long tamanhoComprimido;
            if (algoritmo.equals("Huffman")) {
                tamanhoComprimido = CompressaoHuffman.comprimir(caminhoTemp, caminhoSaida);
            } else {
                tamanhoComprimido = CompressaoLZW.comprimir(caminhoTemp, caminhoSaida);
            }
            new File(caminhoTemp).delete();

            byte[] bytesComprimidos = tamanhoComprimido > 0
                    ? Files.readAllBytes(Path.of(caminhoSaida))
                    : new byte[0];

            long tamanhoOriginal = bytesOriginais.length;

            // Construir resultado
            Map<String, Object> resultado = new LinkedHashMap<>();
            resultado.put("algoritmo", algoritmo);
            resultado.put("entidade", entidade);

            // Métricas
            Map<String, Object> metricas = new LinkedHashMap<>();
            metricas.put("tamanhoOriginal", tamanhoOriginal);
            metricas.put("tamanhoComprimido", tamanhoComprimido);
            metricas.put("taxaCompressao", tamanhoOriginal > 0
                    ? String.format("%.1f%%", (1.0 - (double) tamanhoComprimido / tamanhoOriginal) * 100)
                    : "0%");
            metricas.put("fatorCompressao", tamanhoOriginal > 0
                    ? String.format("%.2fx", (double) tamanhoOriginal / Math.max(tamanhoComprimido, 1))
                    : "1.00x");
            resultado.put("metricas", metricas);

            // Arquivos incluídos
            resultado.put("arquivosIncluidos", arquivos.stream().map(File::getName).toList());

            // Visualização dos bytes originais (hex dump — limitado a 2KB para não pesar)
            int limiteVisualizacao = Math.min(bytesOriginais.length, 2048);
            resultado.put("hexOriginal", formatHexDump(bytesOriginais, limiteVisualizacao));
            resultado.put("hexOriginalTruncado", bytesOriginais.length > 2048);

            // Visualização dos bytes comprimidos (hex dump — limitado a 2KB)
            int limiteComp = Math.min(bytesComprimidos.length, 2048);
            resultado.put("hexComprimido", formatHexDump(bytesComprimidos, limiteComp));
            resultado.put("hexComprimidoTruncado", bytesComprimidos.length > 2048);

            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("erro", e.getMessage()));
        }
    }

    // ==========================================================
    // Helpers
    // ==========================================================

    private List<File> listarArquivosEntidade(String entidade) {
        File pasta = new File(DATA_PATH + entidade);
        if (!pasta.exists()) return Collections.emptyList();
        File[] arquivos = pasta.listFiles(f -> !f.isDirectory());
        if (arquivos == null) return Collections.emptyList();
        return Arrays.asList(arquivos);
    }

    private void concatenarArquivos(List<File> arquivos, String destino) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(destino);
             DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(fos))) {
            dos.writeInt(arquivos.size());
            for (File f : arquivos) {
                byte[] nome = f.getName().getBytes("UTF-8");
                dos.writeInt(nome.length);
                dos.write(nome);
                byte[] conteudo = Files.readAllBytes(f.toPath());
                dos.writeLong(conteudo.length);
                dos.write(conteudo);
            }
        }
    }

    private void garantirPastaBackup() {
        File pasta = new File(BACKUP_PATH);
        if (!pasta.exists()) pasta.mkdirs();
    }

    private String formatHexDump(byte[] bytes, int limite) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < limite; i += 16) {
            sb.append(String.format("%06X  ", i));
            for (int j = 0; j < 16; j++) {
                if (i + j < limite) sb.append(String.format("%02X ", bytes[i + j]));
                else sb.append("   ");
                if (j == 7) sb.append(" ");
            }
            sb.append("|");
            for (int j = 0; j < 16 && i + j < limite; j++) {
                byte b = bytes[i + j];
                sb.append(b >= 32 && b < 127 ? (char) b : '.');
            }
            sb.append("|\n");
        }
        return sb.toString();
    }
}
