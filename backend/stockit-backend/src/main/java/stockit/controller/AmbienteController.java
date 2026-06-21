package stockit.controller;

import stockit.dao.AmbienteDAO;
import stockit.dao.ItemAmbienteDAO;
import stockit.model.Ambiente;
import stockit.model.ItemAmbiente;
import stockit.seguranca.XORCipher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/ambientes")
@CrossOrigin(origins = "*")
public class AmbienteController {

    private final AmbienteDAO dao;
    private final ItemAmbienteDAO itemDao;

    public AmbienteController() throws Exception {
        this.dao = new AmbienteDAO();
        this.itemDao = new ItemAmbienteDAO();
    }

    public static class ItemSimples {
        public int id;
        public int alimentoId;
        public int quantidade;
        public String cadastro;
        public String vencimento;
    }

    public static class AmbienteComItens {
        public int id;
        public String nome;
        public int tipo;
        public List<ItemSimples> itens;

        public AmbienteComItens(Ambiente a, List<ItemSimples> itens) {
            this.id = a.getId();
            this.nome = a.getNome();
            this.tipo = a.getTipo();
            this.itens = itens;
        }
    }

    public static class PatchItensRequest {
        public List<ItemSimples> itens;
    }

    private String intParaData(int d) {
        if (d == 0) return "";
        String s = String.valueOf(d);
        if (s.length() != 8) return s;
        return s.substring(0, 4) + "-" + s.substring(4, 6) + "-" + s.substring(6, 8);
    }

    private int dataParaInt(String s) {
        if (s == null || s.isEmpty()) return 0;
        return Integer.parseInt(s.replace("-", ""));
    }

    private ItemSimples toSimples(ItemAmbiente i) {
        ItemSimples s = new ItemSimples();
        s.id = i.getId();
        s.alimentoId = i.getAlimentoId();
        s.quantidade = i.getQuantidade();
        s.cadastro = intParaData(i.getDataCadastro());
        s.vencimento = intParaData(i.getDataVencimento());
        return s;
    }

    private AmbienteComItens toComItens(Ambiente a) throws Exception {
        List<ItemAmbiente> items = itemDao.listarPorAmbiente(a.getId());
        List<ItemSimples> simples = new ArrayList<>();
        for (ItemAmbiente i : items) simples.add(toSimples(i));
        return new AmbienteComItens(a, simples);
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
    public ResponseEntity<AmbienteComItens> buscar(@PathVariable int id) {
        try {
            Ambiente ambiente = dao.buscar(id);
            if (ambiente == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(toComItens(ambiente));
        } catch (Exception e) {
            e.printStackTrace();
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

    // DTO da visualização da criptografia: nome claro vs nome cifrado (hex).
    public static class NomeCifrado {
        public int id;
        public int tipo;
        public String nomeClaro;
        public String nomeCifradoHex;
        public int tamanhoBytes;
    }

    /**
     * Mostra como o campo sensível "nome" é armazenado: texto claro (o que a API
     * normalmente devolve, já decifrado) ao lado do conteúdo cifrado com XOR,
     * exatamente como fica gravado no arquivo .dat. Serve para visualizar a
     * criptografia no front.
     */
    @GetMapping("/criptografia")
    public ResponseEntity<List<NomeCifrado>> visualizarCriptografia() {
        try {
            List<NomeCifrado> saida = new ArrayList<>();
            for (Ambiente a : dao.listar()) {
                NomeCifrado nc = new NomeCifrado();
                nc.id = a.getId();
                nc.tipo = a.getTipo();
                nc.nomeClaro = a.getNome();
                nc.nomeCifradoHex = XORCipher.cifrarParaHex(a.getNome());
                nc.tamanhoBytes = XORCipher.cifrar(a.getNome()).length;
                saida.add(nc);
            }
            return ResponseEntity.ok(saida);
        } catch (Exception e) {
            e.printStackTrace();
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

    @PatchMapping("/{id}")
    public ResponseEntity<Void> patchItens(@PathVariable int id, @RequestBody PatchItensRequest req) {
        try {
            List<ItemAmbiente> atuais = itemDao.listarPorAmbiente(id);
            for (ItemAmbiente i : atuais) itemDao.excluir(i.getId());

            if (req.itens != null) {
                for (ItemSimples s : req.itens) {
                    ItemAmbiente novo = new ItemAmbiente(
                        s.alimentoId, id, (short) s.quantidade,
                        dataParaInt(s.cadastro), dataParaInt(s.vencimento)
                    );
                    itemDao.inserir(novo);
                }
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable int id) {
        try {
            // Integridade referencial: verificar se há itens vinculados
            List<Integer> itensVinculados = itemDao.buscarIdsPorAmbiente(id);
            if (!itensVinculados.isEmpty()) {
                return ResponseEntity.status(409).body(java.util.Map.of(
                    "erro", "Não é possível excluir: este ambiente possui " + itensVinculados.size() + " alimento(s) vinculado(s). Remova os vínculos primeiro."
                ));
            }
            boolean ok = dao.excluir(id);
            if (!ok) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
