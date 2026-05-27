package stockit.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stockit.dao.ItemAmbienteDAO;
import stockit.dao.ArvoreBMais;
import stockit.model.ItemAmbiente;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/itens_ambiente")
@CrossOrigin(origins = "*")
public class ItemAmbienteController {

    private final ItemAmbienteDAO dao;

    public ItemAmbienteController() throws Exception {
        this.dao = new ItemAmbienteDAO();
    }

    public static class ItemAmbienteDTO {
        public int id;
        public int alimentoId;
        public int ambienteId;
        public short quantidade;
        public String dataCadastro;
        public String dataVencimento;
        public String chaveComposta; // "(alimentoId,ambienteId)"
    }

    private int converterDataParaInt(String dataStr) {
        if (dataStr == null || dataStr.isEmpty()) return 0;
        return Integer.parseInt(dataStr.replace("-", ""));
    }

    private String converterIntParaData(int dataInt) {
        if (dataInt == 0) return "";
        String str = String.valueOf(dataInt);
        if (str.length() != 8) return str;
        return str.substring(0, 4) + "-" + str.substring(4, 6) + "-" + str.substring(6, 8);
    }

    private ItemAmbiente converterParaModel(ItemAmbienteDTO dto) {
        return new ItemAmbiente(dto.id, dto.alimentoId, dto.ambienteId,
                dto.quantidade, converterDataParaInt(dto.dataCadastro),
                converterDataParaInt(dto.dataVencimento));
    }

    private ItemAmbienteDTO converterParaDTO(ItemAmbiente model) {
        ItemAmbienteDTO dto = new ItemAmbienteDTO();
        dto.id = model.getId();
        dto.alimentoId = model.getAlimentoId();
        dto.ambienteId = model.getAmbienteId();
        dto.quantidade = model.getQuantidade();
        dto.dataCadastro = converterIntParaData(model.getDataCadastro());
        dto.dataVencimento = converterIntParaData(model.getDataVencimento());
        dto.chaveComposta = "(" + model.getAlimentoId() + "," + model.getAmbienteId() + ")";
        return dto;
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody ItemAmbienteDTO dto) {
        try {
            ItemAmbiente item = converterParaModel(dto);
            int id = dao.inserir(item);
            item.setId(id);
            return ResponseEntity.status(HttpStatus.CREATED).body(converterParaDTO(item));
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage() != null && e.getMessage().contains("chave composta")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("erro", e.getMessage()));
            }
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemAmbienteDTO> buscar(@PathVariable int id) {
        try {
            ItemAmbiente item = dao.buscar(id);
            if (item == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(converterParaDTO(item));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /** Busca pela chave composta (alimentoId, ambienteId). */
    @GetMapping("/composta/{alimentoId}/{ambienteId}")
    public ResponseEntity<ItemAmbienteDTO> buscarPorChaveComposta(
            @PathVariable int alimentoId, @PathVariable int ambienteId) {
        try {
            ItemAmbiente item = dao.buscarPorChaveComposta(alimentoId, ambienteId);
            if (item == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(converterParaDTO(item));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ItemAmbienteDTO>> listar() {
        try {
            List<ItemAmbiente> listaModel = dao.listar();
            if (listaModel == null) return ResponseEntity.ok(new ArrayList<>());
            List<ItemAmbienteDTO> listaDTO = new ArrayList<>();
            for (ItemAmbiente model : listaModel) listaDTO.add(converterParaDTO(model));
            return ResponseEntity.ok(listaDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /** Lista todos os itens ordenados pela chave composta usando Árvore B+ (sem sort em memória). */
    @GetMapping("/ordenado")
    public ResponseEntity<List<ItemAmbienteDTO>> listarOrdenado() {
        try {
            List<ItemAmbiente> listaModel = dao.listarOrdenado();
            List<ItemAmbienteDTO> listaDTO = new ArrayList<>();
            for (ItemAmbiente model : listaModel) listaDTO.add(converterParaDTO(model));
            return ResponseEntity.ok(listaDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /** Lista todos os ambientes que contêm um determinado alimento (N:N bidirecional). */
    @GetMapping("/por-alimento/{alimentoId}")
    public ResponseEntity<List<ItemAmbienteDTO>> listarPorAlimento(@PathVariable int alimentoId) {
        try {
            List<ItemAmbiente> listaModel = dao.listarPorAlimento(alimentoId);
            List<ItemAmbienteDTO> listaDTO = new ArrayList<>();
            for (ItemAmbiente model : listaModel) listaDTO.add(converterParaDTO(model));
            return ResponseEntity.ok(listaDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /** Lista todos os alimentos de um determinado ambiente (N:N bidirecional). */
    @GetMapping("/por-ambiente/{ambienteId}")
    public ResponseEntity<List<ItemAmbienteDTO>> listarPorAmbiente(@PathVariable int ambienteId) {
        try {
            List<ItemAmbiente> listaModel = dao.listarPorAmbiente(ambienteId);
            List<ItemAmbienteDTO> listaDTO = new ArrayList<>();
            for (ItemAmbiente model : listaModel) listaDTO.add(converterParaDTO(model));
            return ResponseEntity.ok(listaDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /** Retorna a estrutura da Árvore B+ para visualização. */
    @GetMapping("/arvore-b-mais")
    public ResponseEntity<Map<String, Object>> getArvore() {
        try {
            ArvoreBMais.NoVisualizacao raiz = dao.getEstruturaArvore();
            Map<String, Object> resultado = serializarNo(raiz);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    private Map<String, Object> serializarNo(ArvoreBMais.NoVisualizacao no) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("endereco", no.endereco);
        map.put("folha", no.folha);

        List<Integer> chaves = new ArrayList<>();
        for (int c : no.chaves) chaves.add(c);
        map.put("chaves", chaves);

        // Decodificar chaves compostas para exibição
        List<String> chavesDecodificadas = new ArrayList<>();
        for (int c : no.chaves) {
            int aliId = c / 100000;
            int ambId = c % 100000;
            chavesDecodificadas.add("(" + aliId + "," + ambId + ")");
        }
        map.put("chavesDecodificadas", chavesDecodificadas);

        if (no.folha) {
            List<Long> valores = new ArrayList<>();
            for (long v : no.valores) valores.add(v);
            map.put("valores", valores);
            map.put("proxFolha", no.proxFolha);
        } else {
            List<Map<String, Object>> filhos = new ArrayList<>();
            for (ArvoreBMais.NoVisualizacao filho : no.filhos) {
                filhos.add(serializarNo(filho));
            }
            map.put("filhos", filhos);
        }
        return map;
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable int id, @RequestBody ItemAmbienteDTO dto) {
        try {
            dto.id = id;
            ItemAmbiente item = converterParaModel(dto);
            boolean ok = dao.alterar(item);
            if (!ok) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage() != null && e.getMessage().contains("chave composta")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("erro", e.getMessage()));
            }
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deletar(@PathVariable int id) {
        try {
            boolean ok = dao.excluir(id);
            if (!ok) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /** Deleta pela chave composta. */
    @DeleteMapping("/composta/{alimentoId}/{ambienteId}")
    public ResponseEntity<Boolean> deletarPorChaveComposta(
            @PathVariable int alimentoId, @PathVariable int ambienteId) {
        try {
            boolean ok = dao.excluirPorChaveComposta(alimentoId, ambienteId);
            if (!ok) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
