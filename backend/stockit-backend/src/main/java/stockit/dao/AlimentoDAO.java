package stockit.dao;

import java.util.ArrayList;
import java.util.List;
import stockit.model.Alimento;
import stockit.dao.HashExtensivel.*;
import stockit.busca.KMP;
import stockit.busca.BoyerMoore;

public class AlimentoDAO {

    private Arquivo<Alimento> arq;
    private HashExtensivel<ParIntLong>     hashPK;        // ID → posição
    private HashExtensivel<ParStringLong>  hashNome;      // Nome → posição
    private HashExtensivel<ParIntListaInt> hashCategoria;  // idCategoria → lista de IDs

    public AlimentoDAO() throws Exception {
        arq           = new Arquivo<>("Alimento", Alimento.class.getConstructor());
        hashPK        = new HashExtensivel<>("Alimento", "AlimentoPK",        new ParIntLong());
        hashNome      = new HashExtensivel<>("Alimento", "AlimentoNome",      new ParStringLong());
        hashCategoria = new HashExtensivel<>("Alimento", "AlimentoCategoria",  new ParIntListaInt());
    }

    public int inserir(Alimento a) throws Exception {
        long[] res = arq.createComPosicao(a);
        int id = (int) res[0];
        long end = res[1];

        hashPK.inserir(new ParIntLong(id, end));
        hashNome.inserir(new ParStringLong(a.getNome(), end));
        if (a.getIdCategoriaAlimento() > 0) {
            inserirRelacionamento(hashCategoria, a.getIdCategoriaAlimento(), id);
        }
        return id;
    }

    public Alimento buscar(int id) throws Exception {
        ParIntLong r = hashPK.buscar(new ParIntLong(id, 0));
        if (r == null) return null;
        return arq.readPorPosicao(r.valor);
    }

    public Alimento buscarPorNome(String nome) throws Exception {
        ParStringLong r = hashNome.buscar(new ParStringLong(nome, 0));
        if (r == null) return null;
        return arq.readPorPosicao(r.valor);
    }

    public List<Integer> buscarIdsPorCategoria(int idCategoria) throws Exception {
        ParIntListaInt r = hashCategoria.buscar(new ParIntListaInt(idCategoria, 0));
        return r != null ? r.listar() : new ArrayList<>();
    }

    public boolean alterar(Alimento novo) throws Exception {
        ParIntLong r = hashPK.buscar(new ParIntLong(novo.getId(), 0));
        if (r == null) return false;

        Alimento antigo = arq.readPorPosicao(r.valor);
        if (antigo == null) return false;

        long novoEnd = arq.updateComPosicao(novo, r.valor);
        if (novoEnd < 0) return false;

        if (novoEnd != r.valor) hashPK.inserir(new ParIntLong(novo.getId(), novoEnd));

        if (!antigo.getNome().equals(novo.getNome())) {
            hashNome.excluir(new ParStringLong(antigo.getNome(), 0));
            hashNome.inserir(new ParStringLong(novo.getNome(), novoEnd));
        } else if (novoEnd != r.valor) {
            hashNome.inserir(new ParStringLong(novo.getNome(), novoEnd));
        }

        if (antigo.getIdCategoriaAlimento() != novo.getIdCategoriaAlimento()) {
            if (antigo.getIdCategoriaAlimento() > 0)
                removerRelacionamento(hashCategoria, antigo.getIdCategoriaAlimento(), novo.getId());
            if (novo.getIdCategoriaAlimento() > 0)
                inserirRelacionamento(hashCategoria, novo.getIdCategoriaAlimento(), novo.getId());
        }
        return true;
    }

    public boolean excluir(int id) throws Exception {
        ParIntLong r = hashPK.buscar(new ParIntLong(id, 0));
        if (r == null) return false;

        Alimento a = arq.readPorPosicao(r.valor);
        if (a == null) return false;

        boolean ok = arq.deleteAtPosition(r.valor);
        if (ok) {
            hashPK.excluir(new ParIntLong(id, 0));
            hashNome.excluir(new ParStringLong(a.getNome(), 0));
            if (a.getIdCategoriaAlimento() > 0)
                removerRelacionamento(hashCategoria, a.getIdCategoriaAlimento(), id);
        }
        return ok;
    }

    public List<Alimento> listar() throws Exception { return arq.listar(); }

    /**
     * Pesquisa por casamento de padrões no campo textual "nome".
     * Percorre todos os alimentos e retorna aqueles cujo nome CONTÉM o padrão,
     * usando o algoritmo escolhido (KMP ou Boyer-Moore).
     *
     * @param padrao    string a procurar dentro do nome
     * @param algoritmo "kmp" ou "bm" (qualquer outro valor cai em KMP)
     */
    public List<Alimento> buscarPorPadraoNome(String padrao, String algoritmo) throws Exception {
        List<Alimento> resultado = new ArrayList<>();
        if (padrao == null || padrao.isEmpty()) return resultado;

        boolean usarBM = algoritmo != null
                && (algoritmo.equalsIgnoreCase("bm") || algoritmo.equalsIgnoreCase("boyer-moore"));

        for (Alimento a : arq.listar()) {
            String nome = a.getNome();
            boolean casou = usarBM
                    ? BoyerMoore.contem(nome, padrao)
                    : KMP.contem(nome, padrao);
            if (casou) resultado.add(a);
        }
        return resultado;
    }

    // --- helpers para 1:N ---
    private void inserirRelacionamento(HashExtensivel<ParIntListaInt> h, int chave, int valor) throws Exception {
        ParIntListaInt r = h.buscar(new ParIntListaInt(chave, 0));
        if (r == null) { h.inserir(new ParIntListaInt(chave, valor)); return; }
        r.adicionar(valor);
        h.inserir(r);
    }

    private void removerRelacionamento(HashExtensivel<ParIntListaInt> h, int chave, int valor) throws Exception {
        ParIntListaInt r = h.buscar(new ParIntListaInt(chave, 0));
        if (r == null) return;
        r.remover(valor);
        if (r.qtd == 0) h.excluir(r); else h.inserir(r);
    }
}
