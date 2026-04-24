package stockit.dao;

import java.util.ArrayList;
import java.util.List;
import stockit.model.ItemAmbiente;
import stockit.dao.HashExtensivel.*;

public class ItemAmbienteDAO {

    private Arquivo<ItemAmbiente> arq;
    private HashExtensivel<ParIntLong>     hashPK;
    private HashExtensivel<ParIntListaInt> hashAmbiente;  // idAmbiente → IDs de ItemAmbiente
    private HashExtensivel<ParIntListaInt> hashAlimento;  // idAlimento → IDs de ItemAmbiente

    public ItemAmbienteDAO() throws Exception {
        arq          = new Arquivo<>("ItemAmbiente", ItemAmbiente.class.getConstructor());
        hashPK       = new HashExtensivel<>("ItemAmbiente", "ItemAmbientePK",       new ParIntLong());
        hashAmbiente = new HashExtensivel<>("ItemAmbiente", "ItemAmbienteAmbiente", new ParIntListaInt());
        hashAlimento = new HashExtensivel<>("ItemAmbiente", "ItemAmbienteAlimento", new ParIntListaInt());
    }

    public int inserir(ItemAmbiente item) throws Exception {
        long[] res = arq.createComPosicao(item);
        int id = (int) res[0]; long end = res[1];
        hashPK.inserir(new ParIntLong(id, end));
        if (item.getAmbienteId() > 0) inserirRel(hashAmbiente, item.getAmbienteId(), id);
        if (item.getAlimentoId() > 0) inserirRel(hashAlimento, item.getAlimentoId(), id);
        return id;
    }

    public ItemAmbiente buscar(int id) throws Exception {
        ParIntLong r = hashPK.buscar(new ParIntLong(id, 0));
        return r == null ? null : arq.readPorPosicao(r.valor);
    }

    public List<Integer> buscarIdsPorAmbiente(int idAmbiente) throws Exception {
        ParIntListaInt r = hashAmbiente.buscar(new ParIntListaInt(idAmbiente, 0));
        return r != null ? r.listar() : new ArrayList<>();
    }

    public List<Integer> buscarIdsPorAlimento(int idAlimento) throws Exception {
        ParIntListaInt r = hashAlimento.buscar(new ParIntListaInt(idAlimento, 0));
        return r != null ? r.listar() : new ArrayList<>();
    }

    public List<ItemAmbiente> listarPorAmbiente(int idAmbiente) throws Exception {
        List<Integer> ids = buscarIdsPorAmbiente(idAmbiente);
        List<ItemAmbiente> lista = new ArrayList<>();
        for (int id : ids) { ItemAmbiente i = buscar(id); if (i != null) lista.add(i); }
        return lista;
    }

    public boolean alterar(ItemAmbiente novo) throws Exception {
        ParIntLong r = hashPK.buscar(new ParIntLong(novo.getId(), 0));
        if (r == null) return false;
        ItemAmbiente antigo = arq.readPorPosicao(r.valor);
        if (antigo == null) return false;

        long novoEnd = arq.updateComPosicao(novo, r.valor);
        if (novoEnd < 0) return false;

        if (novoEnd != r.valor) hashPK.inserir(new ParIntLong(novo.getId(), novoEnd));

        if (antigo.getAmbienteId() != novo.getAmbienteId()) {
            if (antigo.getAmbienteId() > 0) removerRel(hashAmbiente, antigo.getAmbienteId(), novo.getId());
            if (novo.getAmbienteId() > 0)   inserirRel(hashAmbiente, novo.getAmbienteId(), novo.getId());
        }
        if (antigo.getAlimentoId() != novo.getAlimentoId()) {
            if (antigo.getAlimentoId() > 0) removerRel(hashAlimento, antigo.getAlimentoId(), novo.getId());
            if (novo.getAlimentoId() > 0)   inserirRel(hashAlimento, novo.getAlimentoId(), novo.getId());
        }
        return true;
    }

    public boolean excluir(int id) throws Exception {
        ParIntLong r = hashPK.buscar(new ParIntLong(id, 0));
        if (r == null) return false;
        ItemAmbiente item = arq.readPorPosicao(r.valor);
        if (item == null) return false;

        boolean ok = arq.deleteAtPosition(r.valor);
        if (ok) {
            hashPK.excluir(new ParIntLong(id, 0));
            if (item.getAmbienteId() > 0) removerRel(hashAmbiente, item.getAmbienteId(), id);
            if (item.getAlimentoId() > 0) removerRel(hashAlimento, item.getAlimentoId(), id);
        }
        return ok;
    }

    public List<ItemAmbiente> listar() throws Exception { return arq.listar(); }

    // --- helpers 1:N ---
    private void inserirRel(HashExtensivel<ParIntListaInt> h, int chave, int valor) throws Exception {
        ParIntListaInt r = h.buscar(new ParIntListaInt(chave, 0));
        if (r == null) { h.inserir(new ParIntListaInt(chave, valor)); return; }
        r.adicionar(valor);
        h.inserir(r);
    }

    private void removerRel(HashExtensivel<ParIntListaInt> h, int chave, int valor) throws Exception {
        ParIntListaInt r = h.buscar(new ParIntListaInt(chave, 0));
        if (r == null) return;
        r.remover(valor);
        if (r.qtd == 0) h.excluir(r); else h.inserir(r);
    }
}
