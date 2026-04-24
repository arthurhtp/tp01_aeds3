package stockit.dao;

import java.util.List;
import stockit.model.CategoriaAlimento;
import stockit.dao.HashExtensivel.*;

public class CategoriaAlimentoDAO {

    private Arquivo<CategoriaAlimento> arq;
    private HashExtensivel<ParIntLong>    hashPK;
    private HashExtensivel<ParStringLong> hashNome;

    public CategoriaAlimentoDAO() throws Exception {
        arq      = new Arquivo<>("CategoriaAlimento", CategoriaAlimento.class.getConstructor());
        hashPK   = new HashExtensivel<>("CategoriaAlimentoPK",   new ParIntLong());
        hashNome = new HashExtensivel<>("CategoriaAlimentoNome", new ParStringLong());
    }

    public int inserir(CategoriaAlimento c) throws Exception {
        long[] res = arq.createComPosicao(c);
        int id = (int) res[0]; long end = res[1];
        hashPK.inserir(new ParIntLong(id, end));
        hashNome.inserir(new ParStringLong(c.getNome(), end));
        return id;
    }

    public CategoriaAlimento buscar(int id) throws Exception {
        ParIntLong r = hashPK.buscar(new ParIntLong(id, 0));
        return r == null ? null : arq.readPorPosicao(r.valor);
    }

    public CategoriaAlimento buscarPorNome(String nome) throws Exception {
        ParStringLong r = hashNome.buscar(new ParStringLong(nome, 0));
        return r == null ? null : arq.readPorPosicao(r.valor);
    }

    public boolean alterar(CategoriaAlimento novo) throws Exception {
        ParIntLong r = hashPK.buscar(new ParIntLong(novo.getId(), 0));
        if (r == null) return false;
        CategoriaAlimento antigo = arq.readPorPosicao(r.valor);
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
        return true;
    }

    public boolean excluir(int id) throws Exception {
        ParIntLong r = hashPK.buscar(new ParIntLong(id, 0));
        if (r == null) return false;
        CategoriaAlimento c = arq.readPorPosicao(r.valor);
        if (c == null) return false;

        boolean ok = arq.deleteAtPosition(r.valor);
        if (ok) {
            hashPK.excluir(new ParIntLong(id, 0));
            hashNome.excluir(new ParStringLong(c.getNome(), 0));
        }
        return ok;
    }

    public List<CategoriaAlimento> listar() throws Exception { return arq.listar(); }
}
