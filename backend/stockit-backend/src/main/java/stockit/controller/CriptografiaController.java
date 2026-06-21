package stockit.controller;

import stockit.seguranca.XORCipher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Cifrar/decifrar texto com XOR de forma interativa (Fase V).
 *
 * Demonstra a criptografia usada no campo sensível Ambiente.nome, permitindo
 * que o usuário informe um texto e veja o resultado cifrado (em hex) e o
 * caminho de volta (decifrar o hex de volta para o texto original).
 *
 *   POST /criptografia/cifrar    { "texto": "..." }     -> { texto, hex }
 *   POST /criptografia/decifrar  { "hex": "0C 10 ..." } -> { hex, texto }
 */
@RestController
@RequestMapping("/criptografia")
@CrossOrigin(origins = "*")
public class CriptografiaController {

    public static class CifrarRequest {
        public String texto;
    }

    public static class DecifrarRequest {
        public String hex;
    }

    @PostMapping("/cifrar")
    public ResponseEntity<?> cifrar(@RequestBody CifrarRequest req) {
        try {
            String texto = req != null && req.texto != null ? req.texto : "";
            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("texto", texto);
            resp.put("hex", XORCipher.cifrarParaHex(texto));
            resp.put("metodo", "XOR");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("erro", e.getMessage()));
        }
    }

    @PostMapping("/decifrar")
    public ResponseEntity<?> decifrar(@RequestBody DecifrarRequest req) {
        try {
            String hex = req != null && req.hex != null ? req.hex : "";
            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("hex", hex);
            resp.put("texto", XORCipher.decifrarDeHex(hex));
            resp.put("metodo", "XOR");
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "erro", "Hex inválido. Use pares de dígitos hexadecimais (ex.: 0C 10 1B)."));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("erro", e.getMessage()));
        }
    }
}
