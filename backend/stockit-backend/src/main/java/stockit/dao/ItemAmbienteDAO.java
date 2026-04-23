package stockit.dao;

import java.util.List;
import stockit.model.ItemAmbiente;

public class ItemAmbienteDAO {

    private Arquivo<ItemAmbiente> arq;
    private IndicePrimario indice;

    public ItemAmbienteDAO() throws Exception {
        arq = new Arquivo<>("ItemAmbiente", ItemAmbiente.class.getConstructor());
        indice = new IndicePrimario("ItemAmbiente");
    }

    public int inserir(ItemAmbiente a) throws Exception {
        long[] resultado = arq.createComPosicao(a);
        indice.inserir((int) resultado[0], resultado[1]);
        return (int) resultado[0];
    }

    public ItemAmbiente buscar(int id) throws Exception {
        long posicao = indice.buscar(id);
        if (posicao == -1) return null;
        return arq.readPorPosicao(posicao);
    }

    public boolean alterar(ItemAmbiente a) throws Exception {
        long posicaoAntiga = indice.buscar(a.getId());
        if (posicaoAntiga == -1) return false;
        long novaPosicao = arq.updateComPosicao(a, posicaoAntiga);
        if (novaPosicao != posicaoAntiga) {
            indice.atualizar(a.getId(), novaPosicao);
        }
        return true;
    }

    public boolean excluir(int id) throws Exception {
        long posicao = indice.buscar(id);
        if (posicao == -1) return false;
        arq.deleteAtPosition(posicao);
        indice.deletar(id);
        return true;
    }

    public List<ItemAmbiente> listar() throws Exception {
        return arq.listar();
    }
}