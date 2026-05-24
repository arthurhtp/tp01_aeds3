package stockit.dao;

import java.util.ArrayList;
import java.util.List;
import stockit.model.ItemAmbiente;
import stockit.dao.HashExtensivel.*;

// Tabela intermediaria N:N entre Alimento e Ambiente. Chave composta: (alimentoId, ambienteId)
public class ItemAmbienteDAO {

    private Arquivo<ItemAmbiente> arq;
    private HashExtensivel<ParIntLong>     hashPK;
    private HashExtensivel<ParIntLong>     hashComposta;
    private HashExtensivel<ParIntListaInt> hashAmbiente;
    private HashExtensivel<ParIntListaInt> hashAlimento;
    private ArvoreBMais arvoreBMais;

    public ItemAmbienteDAO() throws Exception {
        arq          = new Arquivo<>("ItemAmbiente", ItemAmbiente.class.getConstructor());
        hashPK       = new HashExtensivel<>("ItemAmbiente", "ItemAmbientePK",       new ParIntLong());
        hashComposta = new HashExtensivel<>("ItemAmbiente", "ItemAmbienteComposta", new ParIntLong());
        hashAmbiente = new HashExtensivel<>("ItemAmbiente", "ItemAmbienteAmbiente", new ParIntListaInt());
        hashAlimento = new HashExtensivel<>("ItemAmbiente", "ItemAmbienteAlimento", new ParIntListaInt());
        arvoreBMais  = new ArvoreBMais("ItemAmbiente", "ItemAmbienteOrdem");
    }

    // Insere verificando unicidade da chave composta
    public int inserir(ItemAmbiente item) throws Exception {
        // Verificar se já existe com essa chave composta
        int chaveComp = item.getChaveComposta();
        ParIntLong existente = hashComposta.buscar(new ParIntLong(chaveComp, 0));
        if (existente != null) {
            throw new Exception("Já existe um registro com chave composta (" 
                + item.getAlimentoId() + "," + item.getAmbienteId() + ")");
        }

        long[] res = arq.createComPosicao(item);
        int id = (int) res[0]; long end = res[1];

        hashPK.inserir(new ParIntLong(id, end));
        hashComposta.inserir(new ParIntLong(chaveComp, end));
        if (item.getAmbienteId() > 0) inserirRel(hashAmbiente, item.getAmbienteId(), id);
        if (item.getAlimentoId() > 0) inserirRel(hashAlimento, item.getAlimentoId(), id);

        // Inserir na árvore B+ (chave composta → ID interno)
        arvoreBMais.inserir(chaveComp, id);

        return id;
    }

    /** Busca por ID interno. */
    public ItemAmbiente buscar(int id) throws Exception {
        ParIntLong r = hashPK.buscar(new ParIntLong(id, 0));
        return r == null ? null : arq.readPorPosicao(r.valor);
    }

    /** Busca pela chave composta (alimentoId, ambienteId). */
    public ItemAmbiente buscarPorChaveComposta(int alimentoId, int ambienteId) throws Exception {
        int chaveComp = alimentoId * 100000 + ambienteId;
        ParIntLong r = hashComposta.buscar(new ParIntLong(chaveComp, 0));
        return r == null ? null : arq.readPorPosicao(r.valor);
    }

    /** Lista todos os IDs de itens de um ambiente. */
    public List<Integer> buscarIdsPorAmbiente(int idAmbiente) throws Exception {
        ParIntListaInt r = hashAmbiente.buscar(new ParIntListaInt(idAmbiente, 0));
        return r != null ? r.listar() : new ArrayList<>();
    }

    /** Lista todos os IDs de itens de um alimento. */
    public List<Integer> buscarIdsPorAlimento(int idAlimento) throws Exception {
        ParIntListaInt r = hashAlimento.buscar(new ParIntListaInt(idAlimento, 0));
        return r != null ? r.listar() : new ArrayList<>();
    }

    /** Lista itens de um ambiente (N:N — a partir do Ambiente). */
    public List<ItemAmbiente> listarPorAmbiente(int idAmbiente) throws Exception {
        List<Integer> ids = buscarIdsPorAmbiente(idAmbiente);
        List<ItemAmbiente> lista = new ArrayList<>();
        for (int id : ids) { ItemAmbiente i = buscar(id); if (i != null) lista.add(i); }
        return lista;
    }

    /** Lista itens de um alimento (N:N — a partir do Alimento). */
    public List<ItemAmbiente> listarPorAlimento(int idAlimento) throws Exception {
        List<Integer> ids = buscarIdsPorAlimento(idAlimento);
        List<ItemAmbiente> lista = new ArrayList<>();
        for (int id : ids) { ItemAmbiente i = buscar(id); if (i != null) lista.add(i); }
        return lista;
    }

    // Lista ordenada via Arvore B+ (sem sort em memoria)
    public List<ItemAmbiente> listarOrdenado() throws Exception {
        List<long[]> pares = arvoreBMais.listarOrdenado();
        List<ItemAmbiente> lista = new ArrayList<>();
        for (long[] par : pares) {
            int id = (int) par[1];
            ItemAmbiente item = buscar(id);
            if (item != null) lista.add(item);
        }
        return lista;
    }

    /** Retorna a estrutura da árvore B+ para visualização. */
    public ArvoreBMais.NoVisualizacao getEstruturaArvore() throws Exception {
        return arvoreBMais.getEstrutura();
    }

    public boolean alterar(ItemAmbiente novo) throws Exception {
        ParIntLong r = hashPK.buscar(new ParIntLong(novo.getId(), 0));
        if (r == null) return false;
        ItemAmbiente antigo = arq.readPorPosicao(r.valor);
        if (antigo == null) return false;

        long novoEnd = arq.updateComPosicao(novo, r.valor);
        if (novoEnd < 0) return false;

        // Atualizar hash PK
        if (novoEnd != r.valor) hashPK.inserir(new ParIntLong(novo.getId(), novoEnd));

        // Se a chave composta mudou, atualizar índices
        int chaveAntigaComp = antigo.getChaveComposta();
        int chaveNovaComp = novo.getChaveComposta();

        if (chaveAntigaComp != chaveNovaComp) {
            // Verificar se nova chave composta já existe
            ParIntLong existente = hashComposta.buscar(new ParIntLong(chaveNovaComp, 0));
            if (existente != null) {
                throw new Exception("Já existe um registro com chave composta (" 
                    + novo.getAlimentoId() + "," + novo.getAmbienteId() + ")");
            }

            hashComposta.excluir(new ParIntLong(chaveAntigaComp, 0));
            hashComposta.inserir(new ParIntLong(chaveNovaComp, novoEnd));

            // Atualizar árvore B+
            arvoreBMais.excluir(chaveAntigaComp);
            arvoreBMais.inserir(chaveNovaComp, novo.getId());
        } else if (novoEnd != r.valor) {
            hashComposta.inserir(new ParIntLong(chaveNovaComp, novoEnd));
        }

        // Atualizar relacionamentos
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
            hashComposta.excluir(new ParIntLong(item.getChaveComposta(), 0));
            if (item.getAmbienteId() > 0) removerRel(hashAmbiente, item.getAmbienteId(), id);
            if (item.getAlimentoId() > 0) removerRel(hashAlimento, item.getAlimentoId(), id);
            arvoreBMais.excluir(item.getChaveComposta());
        }
        return ok;
    }

    /** Exclui pela chave composta. */
    public boolean excluirPorChaveComposta(int alimentoId, int ambienteId) throws Exception {
        int chaveComp = alimentoId * 100000 + ambienteId;
        ParIntLong r = hashComposta.buscar(new ParIntLong(chaveComp, 0));
        if (r == null) return false;
        ItemAmbiente item = arq.readPorPosicao(r.valor);
        if (item == null) return false;
        return excluir(item.getId());
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
