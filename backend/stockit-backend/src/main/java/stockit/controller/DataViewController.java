package stockit.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Controller utilitário para visualização dos dados brutos em disco.
 * Permite ver o conteúdo dos arquivos .dat (registros), .dir (diretório hash)
 * e .bkt (buckets do hash extensível).
 */
@RestController
@RequestMapping("/data-view")
@CrossOrigin(origins = "*")
public class DataViewController {

    private static final String DATA_PATH = "./data/";

    /**
     * Lista as entidades (pastas) disponíveis em /data
     */
    @GetMapping("/entidades")
    public ResponseEntity<List<String>> listarEntidades() {
        try {
            File dataDir = new File(DATA_PATH);
            if (!dataDir.exists()) return ResponseEntity.ok(new ArrayList<>());
            String[] dirs = dataDir.list((dir, name) -> new File(dir, name).isDirectory());
            return ResponseEntity.ok(dirs != null ? Arrays.asList(dirs) : new ArrayList<>());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Lista os arquivos de uma entidade
     */
    @GetMapping("/entidades/{entidade}/arquivos")
    public ResponseEntity<List<String>> listarArquivos(@PathVariable String entidade) {
        try {
            File dir = new File(DATA_PATH + entidade);
            if (!dir.exists()) return ResponseEntity.notFound().build();
            String[] files = dir.list();
            return ResponseEntity.ok(files != null ? Arrays.asList(files) : new ArrayList<>());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Lê o arquivo .dat e retorna os registros em formato legível (hex + interpretação)
     */
    @GetMapping("/entidades/{entidade}/dat")
    public ResponseEntity<Map<String, Object>> lerDat(@PathVariable String entidade) {
        try {
            String filePath = DATA_PATH + entidade + "/" + entidade + ".dat";
            File file = new File(filePath);
            if (!file.exists()) return ResponseEntity.notFound().build();

            RandomAccessFile raf = new RandomAccessFile(file, "r");
            Map<String, Object> resultado = new LinkedHashMap<>();

            // Cabeçalho: último ID
            int ultimoId = raf.readInt();
            resultado.put("ultimoId", ultimoId);
            resultado.put("tamanhoArquivo", raf.length());

            List<Map<String, Object>> registros = new ArrayList<>();
            while (raf.getFilePointer() < raf.length()) {
                Map<String, Object> reg = new LinkedHashMap<>();
                long posicao = raf.getFilePointer();
                reg.put("posicao", posicao);

                byte lapide = raf.readByte();
                reg.put("lapide", (char) lapide);
                reg.put("ativo", lapide == ' ');

                short tamanho = raf.readShort();
                reg.put("tamanhoBytes", tamanho);

                byte[] dados = new byte[tamanho];
                raf.read(dados);
                reg.put("hexDados", bytesToHex(dados));
                reg.put("dadosLegiveis", interpretarRegistro(entidade, dados));

                registros.add(reg);
            }
            resultado.put("registros", registros);
            raf.close();
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Lê o arquivo .dir do hash extensível e retorna a estrutura do diretório
     */
    @GetMapping("/entidades/{entidade}/hash/{nomeIndice}")
    public ResponseEntity<Map<String, Object>> lerHash(
            @PathVariable String entidade,
            @PathVariable String nomeIndice) {
        try {
            String dirPath = DATA_PATH + entidade + "/" + nomeIndice + ".dir";
            String bktPath = DATA_PATH + entidade + "/" + nomeIndice + ".bkt";

            File dirFile = new File(dirPath);
            File bktFile = new File(bktPath);
            if (!dirFile.exists() || !bktFile.exists()) return ResponseEntity.notFound().build();

            RandomAccessFile dir = new RandomAccessFile(dirFile, "r");
            RandomAccessFile bkt = new RandomAccessFile(bktFile, "r");

            Map<String, Object> resultado = new LinkedHashMap<>();

            // Ler diretório
            int profGlobal = dir.readInt();
            resultado.put("profundidadeGlobal", profGlobal);

            int tamDir = 1 << profGlobal;
            resultado.put("tamanhoDir", tamDir);

            List<Map<String, Object>> entradas = new ArrayList<>();
            for (int i = 0; i < tamDir; i++) {
                long endereco = dir.readLong();
                Map<String, Object> entrada = new LinkedHashMap<>();
                entrada.put("indice", i);
                entrada.put("binario", String.format("%" + Math.max(profGlobal, 1) + "s",
                        Integer.toBinaryString(i)).replace(' ', '0'));
                entrada.put("enderecoBucket", endereco);
                entradas.add(entrada);
            }
            resultado.put("diretorio", entradas);

            // Ler buckets
            int tamRegistro = detectarTamRegistro(nomeIndice);
            int capacidade = 4;
            List<Map<String, Object>> buckets = new ArrayList<>();
            long pos = 0;
            while (pos < bkt.length()) {
                bkt.seek(pos);
                Map<String, Object> bucket = new LinkedHashMap<>();
                bucket.put("endereco", pos);

                int profLocal = bkt.readInt();
                bucket.put("profundidadeLocal", profLocal);

                int count = bkt.readInt();
                bucket.put("count", count);

                List<Map<String, Object>> slots = new ArrayList<>();
                for (int i = 0; i < capacidade; i++) {
                    Map<String, Object> slot = new LinkedHashMap<>();
                    byte ativo = bkt.readByte();
                    slot.put("ativo", ativo == 1);

                    byte[] dados = new byte[tamRegistro];
                    bkt.read(dados);
                    slot.put("hex", bytesToHex(dados));
                    slot.put("interpretado", interpretarHashEntry(nomeIndice, dados, ativo == 1));

                    slots.add(slot);
                }
                bucket.put("slots", slots);
                buckets.add(bucket);

                pos += Integer.BYTES + Integer.BYTES + capacidade * (1 + tamRegistro);
            }
            resultado.put("buckets", buckets);

            dir.close();
            bkt.close();
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Retorna o conteúdo bruto (hex dump) de qualquer arquivo da entidade
     */
    @GetMapping("/entidades/{entidade}/raw/{arquivo}")
    public ResponseEntity<Map<String, Object>> lerRaw(
            @PathVariable String entidade,
            @PathVariable String arquivo) {
        try {
            String filePath = DATA_PATH + entidade + "/" + arquivo;
            File file = new File(filePath);
            if (!file.exists()) return ResponseEntity.notFound().build();

            byte[] bytes = Files.readAllBytes(file.toPath());
            Map<String, Object> resultado = new LinkedHashMap<>();
            resultado.put("arquivo", arquivo);
            resultado.put("tamanho", bytes.length);
            resultado.put("hexDump", formatHexDump(bytes));
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // --- Helpers ---

    private int detectarTamRegistro(String nomeIndice) {
        if (nomeIndice.endsWith("PK")) return Integer.BYTES + Long.BYTES; // ParIntLong: 12 bytes
        if (nomeIndice.endsWith("Nome")) return 120 + Long.BYTES; // ParStringLong: 128 bytes
        // ParIntListaInt: chave(4) + qtd(4) + 20*4 = 88 bytes
        return Integer.BYTES + Integer.BYTES + 20 * Integer.BYTES;
    }

    private String interpretarHashEntry(String nomeIndice, byte[] dados, boolean ativo) {
        if (!ativo) return "(vazio)";
        try {
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(dados));
            if (nomeIndice.endsWith("PK")) {
                int chave = dis.readInt();
                long valor = dis.readLong();
                return "chave=" + chave + " → posição=" + valor;
            } else if (nomeIndice.endsWith("Nome")) {
                int len = 0;
                while (len < 120 && dados[len] != 0) len++;
                String nome = new String(dados, 0, len, "UTF-8");
                DataInputStream dis2 = new DataInputStream(new ByteArrayInputStream(dados, 120, 8));
                long valor = dis2.readLong();
                return "nome=\"" + nome + "\" → posição=" + valor;
            } else {
                // ParIntListaInt (Categoria, Ambiente, Alimento)
                int chave = dis.readInt();
                int qtd = dis.readInt();
                StringBuilder sb = new StringBuilder("chave=" + chave + " → [");
                for (int i = 0; i < qtd && i < 20; i++) {
                    if (i > 0) sb.append(", ");
                    sb.append(dis.readInt());
                }
                sb.append("]");
                return sb.toString();
            }
        } catch (Exception e) {
            return "(erro ao interpretar)";
        }
    }

    private String interpretarRegistro(String entidade, byte[] dados) {
        try {
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(dados));
            switch (entidade) {
                case "CategoriaAlimento": {
                    int id = dis.readInt();
                    short tamNome = dis.readShort();
                    byte[] nomeBytes = new byte[tamNome];
                    dis.readFully(nomeBytes);
                    return "id=" + id + ", nome=\"" + new String(nomeBytes, "UTF-8") + "\"";
                }
                case "Alimento": {
                    int id = dis.readInt();
                    short tamNome = dis.readShort();
                    byte[] nomeBytes = new byte[tamNome];
                    dis.readFully(nomeBytes);
                    short qtdRotulos = dis.readShort();
                    List<String> rotulos = new ArrayList<>();
                    for (int i = 0; i < qtdRotulos; i++) {
                        short tamR = dis.readShort();
                        byte[] rBytes = new byte[tamR];
                        dis.readFully(rBytes);
                        rotulos.add(new String(rBytes, "UTF-8"));
                    }
                    int idCat = dis.readInt();
                    return "id=" + id + ", nome=\"" + new String(nomeBytes, "UTF-8") +
                           "\", rotulos=" + rotulos + ", idCategoria=" + idCat;
                }
                case "Ambiente": {
                    int id = dis.readInt();
                    short tamNome = dis.readShort();
                    byte[] nomeBytes = new byte[tamNome];
                    dis.readFully(nomeBytes);
                    byte tipo = dis.readByte();
                    return "id=" + id + ", nome=\"" + new String(nomeBytes, "UTF-8") + "\", tipo=" + tipo;
                }
                case "ItemAmbiente": {
                    int id = dis.readInt();
                    int alimentoId = dis.readInt();
                    int ambienteId = dis.readInt();
                    short qtd = dis.readShort();
                    int dataCad = dis.readInt();
                    int dataVenc = dis.readInt();
                    return "id=" + id + ", alimentoId=" + alimentoId + ", ambienteId=" + ambienteId +
                           ", qtd=" + qtd + ", cadastro=" + dataCad + ", vencimento=" + dataVenc;
                }
                default:
                    return bytesToHex(dados);
            }
        } catch (Exception e) {
            return "(erro: " + e.getMessage() + ")";
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(String.format("%02X", bytes[i]));
            if (i < bytes.length - 1) sb.append(" ");
        }
        return sb.toString();
    }

    private String formatHexDump(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i += 16) {
            sb.append(String.format("%08X  ", i));
            // Hex
            for (int j = 0; j < 16; j++) {
                if (i + j < bytes.length) sb.append(String.format("%02X ", bytes[i + j]));
                else sb.append("   ");
                if (j == 7) sb.append(" ");
            }
            sb.append(" |");
            // ASCII
            for (int j = 0; j < 16 && i + j < bytes.length; j++) {
                byte b = bytes[i + j];
                sb.append(b >= 32 && b < 127 ? (char) b : '.');
            }
            sb.append("|\n");
        }
        return sb.toString();
    }
}
