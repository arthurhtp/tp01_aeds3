package stockit.controller;

import stockit.dao.CategoriaAlimentoDAO;
import stockit.model.CategoriaAlimento;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categoria-alimento")
@CrossOrigin(origins = "*")
public class CategoriaAlimentoController {

    private final CategoriaAlimentoDAO dao;

    public CategoriaAlimentoController() throws Exception {
        this.dao = new CategoriaAlimentoDAO();
    }

    @PostMapping
    public ResponseEntity<CategoriaAlimento> criar(@RequestBody CategoriaAlimento categoria) {
        try {
            int id = dao.inserir(categoria);
            categoria.setId(id);
            return ResponseEntity.ok(categoria);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaAlimento> buscar(@PathVariable int id) {
        try {
            CategoriaAlimento categoria = dao.buscar(id);
            if (categoria == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(categoria);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<CategoriaAlimento>> listar() {
        try {
            return ResponseEntity.ok(dao.listar());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Boolean> atualizar(@PathVariable int id, @RequestBody CategoriaAlimento categoria) {
        try {
            categoria.setId(id);
            boolean ok = dao.alterar(categoria);
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
