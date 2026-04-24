package stockit.controller;

import stockit.dao.AlimentoDAO;
import stockit.model.Alimento;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alimentos")
@CrossOrigin(origins = "http://localhost:5173") // React (Vite)
public class AlimentoController {

    private final AlimentoDAO dao;

    public AlimentoController() throws Exception {
        this.dao = new AlimentoDAO();
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
    public ResponseEntity<Boolean> deletar(@PathVariable int id) {
        try {
            boolean ok = dao.excluir(id);
            if (!ok) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
