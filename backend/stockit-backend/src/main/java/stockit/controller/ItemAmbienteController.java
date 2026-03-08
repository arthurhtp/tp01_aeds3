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
@CrossOrigin(origins = "http://localhost:5173") // Libera o acesso para o React
public class ItemAmbienteController {

    private final ItemAmbienteDAO dao;

    public ItemAmbienteController() throws Exception {
        this.dao = new ItemAmbienteDAO();
    }

    // ==============================================================
    // DTO E LÓGICA DE CONVERSÃO (String <-> Int)
    // ==============================================================
    
    // Objeto usado para conversar com o Frontend (React)
    public static class ItemAmbienteDTO {
        public int id;
        public int alimentoId;
        public int ambienteId;
        public short quantidade;
        public String dataCadastro;   // Recebe/Envia como "YYYY-MM-DD"
        public String dataVencimento; // Recebe/Envia como "YYYY-MM-DD"
    }

    // Converte String "2026-03-04" para o int 20260304 (4 bytes para o binário)
    private int converterDataParaInt(String dataStr) {
        if (dataStr == null || dataStr.isEmpty()) return 0;
        return Integer.parseInt(dataStr.replace("-", ""));
    }

    // Converte int 20260304 de volta para String "2026-03-04" para o React
    private String converterIntParaData(int dataInt) {
        if (dataInt == 0) return "";
        String str = String.valueOf(dataInt);
        if (str.length() != 8) return str; 
        return str.substring(0, 4) + "-" + str.substring(4, 6) + "-" + str.substring(6, 8);
    }

    // Traduz do DTO (React) para o Model (DAO)
    private ItemAmbiente converterParaModel(ItemAmbienteDTO dto) {
        return new ItemAmbiente(
            dto.id,
            dto.alimentoId,
            dto.ambienteId,
            dto.quantidade,
            converterDataParaInt(dto.dataCadastro),
            converterDataParaInt(dto.dataVencimento)
        );
    }

    // Traduz do Model (DAO) para o DTO (React)
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


    // ==============================
    // CREATE (POST)
    // ==============================
    @PostMapping
    public ResponseEntity<ItemAmbienteDTO> criar(@RequestBody ItemAmbienteDTO dto) {
        try {
            ItemAmbiente item = converterParaModel(dto); // Converte as datas
            int id = dao.inserir(item);                  // Salva no binário
            item.setId(id);                              // Atualiza com o novo ID
            
            // Retorna 201 CREATED (Melhor prática)
            return ResponseEntity.status(HttpStatus.CREATED).body(converterParaDTO(item));
        } catch (Exception e) {
            e.printStackTrace(); // Mostra o erro no terminal se der ruim!
            return ResponseEntity.internalServerError().build();
        }
    }

    // ==============================
    // READ by ID (GET)
    // ==============================
    @GetMapping("/{id}")
    public ResponseEntity<ItemAmbienteDTO> buscar(@PathVariable int id) {
        try {
            ItemAmbiente item = dao.buscar(id);

            if (item == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(converterParaDTO(item));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // ==============================
    // READ ALL (GET)
    // ==============================
    @GetMapping
    public ResponseEntity<List<ItemAmbienteDTO>> listar() {
        try {
            List<ItemAmbiente> listaModel = dao.listar();
            List<ItemAmbienteDTO> listaDTO = new ArrayList<>();
            
            // Converte todos os itens encontrados para enviar ao front
            for (ItemAmbiente item : listaModel) {
                listaDTO.add(converterParaDTO(item));
            }
            
            return ResponseEntity.ok(listaDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // ==============================
    // UPDATE (PUT)
    // ==============================
    @PutMapping("/{id}")
    public ResponseEntity<Boolean> atualizar(
            @PathVariable int id,
            @RequestBody ItemAmbienteDTO dto) {

        try {
            dto.id = id; // Garante que o ID da URL seja o usado
            ItemAmbiente item = converterParaModel(dto);
            
            boolean atualizado = dao.alterar(item);

            if (!atualizado) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(true);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // ==============================
    // DELETE (DELETE lógico)
    // ==============================
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deletar(@PathVariable int id) {
        try {
            boolean deletado = dao.excluir(id); // Altera o byte da lápide

            if (!deletado) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(true);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}