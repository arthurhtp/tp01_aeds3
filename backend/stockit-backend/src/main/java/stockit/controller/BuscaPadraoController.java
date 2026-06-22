package stockit.controller;

import stockit.dao.AlimentoDAO;
import stockit.model.Alimento;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pesquisa por casamento de padrões (Fase V).
 *
 * Expõe a busca de um padrão (string) sobre o campo textual "nome" da tabela
 * Alimento, deixando o usuário escolher o algoritmo: KMP ou Boyer-Moore.
 *
 * GET /busca-padrao/alimentos?padrao=arr&algoritmo=kmp
 * GET /busca-padrao/alimentos?padrao=arr&algoritmo=bm
 */
@RestController
@RequestMapping("/busca-padrao")
@CrossOrigin(origins = "*")
public class BuscaPadraoController {

    private final AlimentoDAO dao;

    public BuscaPadraoController() throws Exception {
        this.dao = new AlimentoDAO();
    }

    @GetMapping("/alimentos")
    public ResponseEntity<?> buscarAlimentos(
            @RequestParam String padrao,
            @RequestParam(defaultValue = "kmp") String algoritmo) {
        try {
            if (padrao == null || padrao.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "erro", "Informe um padrão (string) para pesquisar."));
            }

            long inicio = System.nanoTime();
            List<Alimento> encontrados = dao.buscarPorPadraoNome(padrao, algoritmo);
            long tempoMicros = (System.nanoTime() - inicio) / 1000;

            String algoNome = (algoritmo != null
                    && (algoritmo.equalsIgnoreCase("bm") || algoritmo.equalsIgnoreCase("boyer-moore")))
                    ? "Boyer-Moore" : "KMP";

            Map<String, Object> resposta = new HashMap<>();
            resposta.put("campo", "nome");
            resposta.put("tabela", "Alimento");
            resposta.put("padrao", padrao);
            resposta.put("algoritmo", algoNome);
            resposta.put("totalEncontrados", encontrados.size());
            resposta.put("tempoMicrossegundos", tempoMicros);
            resposta.put("resultados", encontrados);
            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                    "erro", "Falha ao executar a busca: " + e.getMessage()));
        }
    }
}
