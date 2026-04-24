package stockit.controller;

import stockit.dao.AmbienteDAO;
import stockit.model.Ambiente;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ambientes")
@CrossOrigin(origins = "*")
public class AmbienteController {

    private final AmbienteDAO dao;

    public AmbienteController() throws Exception {
        this.dao = new AmbienteDAO();
    }

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

    @GetMapping("/{id}")
    public ResponseEntity<Ambiente> buscar(@PathVariable int id) {
        try {
            Ambiente ambiente = dao.buscar(id);
            if (ambiente == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(ambiente);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Ambiente>> listar() {
        try {
            return ResponseEntity.ok(dao.listar());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Boolean> atualizar(@PathVariable int id, @RequestBody Ambiente ambiente) {
        try {
            ambiente.setId(id);
            boolean ok = dao.alterar(ambiente);
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
