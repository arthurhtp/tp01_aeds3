package stockit.controller;

import stockit.dao.AmbienteDAO;
import stockit.model.Ambiente;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ambientes")
@CrossOrigin(origins = "http://localhost:5173") // React (Vite)
public class AmbienteController {

    private final AmbienteDAO dao;

    public AmbienteController() throws Exception {
        this.dao = new AmbienteDAO();
    }

    // ==============================
    // CREATE
    // ==============================
    @PostMapping
    public ResponseEntity<Ambiente> criar(@RequestBody Ambiente ambiente) {
        try {
            int id = dao.inserir(ambiente);
            ambiente.setId(id);
            return ResponseEntity.ok(ambiente);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ==============================
    // READ by ID
    // ==============================
    @GetMapping("/{id}")
    public ResponseEntity<Ambiente> buscar(@PathVariable int id) {
        try {
            Ambiente ambiente = dao.buscar(id);

            if (ambiente == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(ambiente);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ==============================
    // READ ALL
    // ==============================
    @GetMapping
    public ResponseEntity<List<Ambiente>> listar() {
        try {
            List<Ambiente> lista = dao.listar();
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
            @RequestBody Ambiente ambiente) {

        try {
            ambiente.setId(id);
            boolean atualizado = dao.alterar(ambiente);

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