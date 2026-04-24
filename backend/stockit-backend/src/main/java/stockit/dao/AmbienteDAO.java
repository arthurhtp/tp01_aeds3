package stockit.dao;

import java.util.List;
import stockit.model.Ambiente;
import stockit.dao.HashExtensivel.*;

public class AmbienteDAO {

    private Arquivo<Ambiente> arq;
    private HashExtensivel<ParIntLong>    hashPK;
    private HashExtensivel<ParStringLong> hashNome;

    public AmbienteDAO() throws Exception {
        arq    = new Arquivo<>("Ambiente", Ambiente.class.getConstructor());
        hashPK   = new HashExtensivel<>("Ambiente", "AmbientePK",   new ParIntLong());
        hashNome = new HashExtensivel<>("Ambiente", "AmbienteNome", new ParStringLong());
    }

    public int inserir(Ambiente a) throws Exception {
        long[] res = arq.createComPosicao(a);
        int id = (int) res[0]; long end = res[1];
        hashPK.inserir(new ParIntLong(id, end));
        hashNome.inserir(new ParStringLong(a.getNome(), end));
        return id;
    }

    public Ambiente buscar(int id) throws Exception {
        ParIntLong r = hashPK.buscar(new ParIntLong(id, 0));
        return r == null ? null : arq.readPorPosicao(r.valor);
    }

    public Ambiente buscarPorNome(String nome) throws Exception {
        ParStringLong r = hashNome.buscar(new ParStringLong(nome, 0));
        return r == null ? null : arq.readPorPosicao(r.valor);
    }

    public boolean alterar(Ambiente novo) throws Exception {
        ParIntLong r = hashPK.buscar(new ParIntLong(novo.getId(), 0));
        if (r == null) return false;
        Ambiente antigo = arq.readPorPosicao(r.valor);
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
        Ambiente a = arq.readPorPosicao(r.valor);
        if (a == null) return false;

        boolean ok = arq.deleteAtPosition(r.valor);
        if (ok) {
            hashPK.excluir(new ParIntLong(id, 0));
            hashNome.excluir(new ParStringLong(a.getNome(), 0));
        }
        return ok;
    }

    public List<Ambiente> listar() throws Exception { return arq.listar(); }
}
