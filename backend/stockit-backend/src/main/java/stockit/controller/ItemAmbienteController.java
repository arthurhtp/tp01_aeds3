package stockit.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stockit.dao.ItemAmbienteDAO;
import stockit.model.ItemAmbiente;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/itens-ambiente")
@CrossOrigin(origins = "http://localhost:5173")
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
        return dto;
    }

    @PostMapping
    public ResponseEntity<ItemAmbienteDTO> criar(@RequestBody ItemAmbienteDTO dto) {
        try {
            ItemAmbiente item = converterParaModel(dto);
            int id = dao.inserir(item);
            item.setId(id);
            return ResponseEntity.status(HttpStatus.CREATED).body(converterParaDTO(item));
        } catch (Exception e) {
            e.printStackTrace();
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

    @PutMapping("/{id}")
    public ResponseEntity<Boolean> atualizar(@PathVariable int id, @RequestBody ItemAmbienteDTO dto) {
        try {
            dto.id = id;
            ItemAmbiente item = converterParaModel(dto);
            boolean ok = dao.alterar(item);
            if (!ok) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            e.printStackTrace();
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
}
