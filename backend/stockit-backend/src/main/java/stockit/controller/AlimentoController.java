package stockit.controller;

import stockit.dao.AlimentoDAO;
import stockit.dao.ItemAmbienteDAO;
import stockit.model.Alimento;
import stockit.model.ItemAmbiente;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/alimentos")
@CrossOrigin(origins = "*")
public class AlimentoController {

    private final AlimentoDAO dao;
    private final ItemAmbienteDAO itemDao;

    public AlimentoController() throws Exception {
        this.dao = new AlimentoDAO();
        this.itemDao = new ItemAmbienteDAO();
    }

    @PostMapping
    public ResponseEntity<Alimento> criar(@RequestBody Alimento alimento) {
        try {
            int id = dao.inserir(alimento);
            alimento.setId(id);
            return ResponseEntity.ok(alimento);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alimento> buscar(@PathVariable int id) {
        try {
            Alimento alimento = dao.buscar(id);
            if (alimento == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(alimento);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Alimento>> listar() {
        try {
            return ResponseEntity.ok(dao.listar());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Boolean> atualizar(@PathVariable int id, @RequestBody Alimento alimento) {
        try {
            alimento.setId(id);
            boolean ok = dao.alterar(alimento);
            if (!ok) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable int id) {
        try {
            // Integridade referencial: verificar se há itens vinculados
            List<Integer> itensVinculados = itemDao.buscarIdsPorAlimento(id);
            if (!itensVinculados.isEmpty()) {
                return ResponseEntity.status(409).body(java.util.Map.of(
                    "erro", "Não é possível excluir: este alimento está vinculado a " + itensVinculados.size() + " ambiente(s). Remova os vínculos primeiro."
                ));
            }
            boolean ok = dao.excluir(id);
            if (!ok) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    @GetMapping("/{id}/ambientes")
    public ResponseEntity<List<ItemAmbiente>> listarAmbientesDoAlimento(@PathVariable int id) {
        try {
            List<ItemAmbiente> itens = itemDao.listarPorAlimento(id);
            return ResponseEntity.ok(itens);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
