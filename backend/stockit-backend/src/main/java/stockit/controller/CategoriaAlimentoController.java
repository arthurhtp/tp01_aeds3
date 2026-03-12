package stockit.controller;

import stockit.dao.CategoriaAlimentoDAO;
import stockit.model.CategoriaAlimento;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias-alimento")
@CrossOrigin(origins = "http://localhost:5173") // React (Vite)
public class CategoriaAlimentoController {

    private final CategoriaAlimentoDAO dao;

    public CategoriaAlimentoController() throws Exception {
        this.dao = new CategoriaAlimentoDAO();
    }

    // ==============================
    // CREATE
    // ==============================
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

    // ==============================
    // READ by ID
    // ==============================
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaAlimento> buscar(@PathVariable int id) {
        try {
            CategoriaAlimento categoria = dao.buscar(id);

            if (categoria == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(categoria);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ==============================
    // READ ALL
    // ==============================
    @GetMapping
    public ResponseEntity<List<CategoriaAlimento>> listar() {
        try {
            List<CategoriaAlimento> lista = dao.listar();
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ==============================
    // UPDATE
    // ==============================
    @PutMapping("/{id}")
    public ResponseEntity<Boolean> atualizar(
            @PathVariable int id,
            @RequestBody CategoriaAlimento categoria) {

        try {
            categoria.setId(id);
            boolean atualizado = dao.alterar(categoria);

            if (!atualizado) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(true);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ==============================
    // DELETE
    // ==============================
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deletar(@PathVariable int id) {
        try {
            boolean deletado = dao.excluir(id);

            if (!deletado) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(true);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}