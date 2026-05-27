package stockit.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stockit.dao.*;
import stockit.model.*;

import java.util.*;

// Ordenacao por Intercalacao Balanceada
@RestController
@RequestMapping("/ordenacao")
@CrossOrigin(origins = "*")
public class OrdenacaoController {

    private final CategoriaAlimentoDAO categoriaDao;
    private final AlimentoDAO alimentoDao;
    private final AmbienteDAO ambienteDao;

    public OrdenacaoController() throws Exception {
        this.categoriaDao = new CategoriaAlimentoDAO();
        this.alimentoDao = new AlimentoDAO();
        this.ambienteDao = new AmbienteDAO();
    }

    public static class ResultadoDTO {
        public List<Map<String, Object>> registrosOrdenados;
        public List<String> log;
        public int totalPassos;
        public int blocosGerados;
        public int totalRegistros;
    }

    @GetMapping("/categoria-alimento")
    public ResponseEntity<ResultadoDTO> ordenarCategorias() {
        try {
            List<CategoriaAlimento> registros = categoriaDao.listar();

            IntercalacaoBalanceada<CategoriaAlimento> sorter = new IntercalacaoBalanceada<>(
                CategoriaAlimento.class.getConstructor(),
                Comparator.comparing(CategoriaAlimento::getNome, String.CASE_INSENSITIVE_ORDER),
                "CategoriaAlimento"
            );

            IntercalacaoBalanceada.ResultadoOrdenacao<CategoriaAlimento> resultado = sorter.ordenar(registros);

            ResultadoDTO dto = new ResultadoDTO();
            dto.log = resultado.log;
            dto.totalPassos = resultado.totalPassos;
            dto.blocosGerados = resultado.blocosGerados;
            dto.totalRegistros = registros.size();
            dto.registrosOrdenados = new ArrayList<>();

            for (CategoriaAlimento c : resultado.registrosOrdenados) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id", c.getId());
                map.put("nome", c.getNome());
                dto.registrosOrdenados.add(map);
            }

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/alimento")
    public ResponseEntity<ResultadoDTO> ordenarAlimentos() {
        try {
            List<Alimento> registros = alimentoDao.listar();

            IntercalacaoBalanceada<Alimento> sorter = new IntercalacaoBalanceada<>(
                Alimento.class.getConstructor(),
                Comparator.comparing(Alimento::getNome, String.CASE_INSENSITIVE_ORDER),
                "Alimento"
            );

            IntercalacaoBalanceada.ResultadoOrdenacao<Alimento> resultado = sorter.ordenar(registros);

            ResultadoDTO dto = new ResultadoDTO();
            dto.log = resultado.log;
            dto.totalPassos = resultado.totalPassos;
            dto.blocosGerados = resultado.blocosGerados;
            dto.totalRegistros = registros.size();
            dto.registrosOrdenados = new ArrayList<>();

            for (Alimento a : resultado.registrosOrdenados) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id", a.getId());
                map.put("nome", a.getNome());
                map.put("rotulos", a.getRotulos());
                map.put("idCategoriaAlimento", a.getIdCategoriaAlimento());
                dto.registrosOrdenados.add(map);
            }

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/ambiente")
    public ResponseEntity<ResultadoDTO> ordenarAmbientes() {
        try {
            List<Ambiente> registros = ambienteDao.listar();

            IntercalacaoBalanceada<Ambiente> sorter = new IntercalacaoBalanceada<>(
                Ambiente.class.getConstructor(),
                Comparator.comparing(Ambiente::getNome, String.CASE_INSENSITIVE_ORDER),
                "Ambiente"
            );

            IntercalacaoBalanceada.ResultadoOrdenacao<Ambiente> resultado = sorter.ordenar(registros);

            ResultadoDTO dto = new ResultadoDTO();
            dto.log = resultado.log;
            dto.totalPassos = resultado.totalPassos;
            dto.blocosGerados = resultado.blocosGerados;
            dto.totalRegistros = registros.size();
            dto.registrosOrdenados = new ArrayList<>();

            for (Ambiente a : resultado.registrosOrdenados) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id", a.getId());
                map.put("nome", a.getNome());
                map.put("tipo", a.getTipo());
                dto.registrosOrdenados.add(map);
            }

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
