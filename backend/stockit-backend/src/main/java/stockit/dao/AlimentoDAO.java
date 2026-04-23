package stockit.dao;

import java.util.List;
import stockit.model.Alimento;

public class AlimentoDAO {

    private Arquivo<Alimento> arq;
    private HashExtensivel indice;

    public AlimentoDAO() throws Exception {
        arq    = new Arquivo<>("Alimento", Alimento.class.getConstructor());
        indice = new HashExtensivel("Alimento");
    }

    public int inserir(Alimento a) throws Exception {
        long[] resultado = arq.createComPosicao(a);
        indice.inserir((int) resultado[0], resultado[1]);
        return (int) resultado[0];
    }

    public Alimento buscar(int id) throws Exception {
        long posicao = indice.buscar(id);
        if (posicao == -1) return null;
        return arq.readPorPosicao(posicao);
    }

    public boolean alterar(Alimento a) throws Exception {
        long posicaoAntiga = indice.buscar(a.getId());
        if (posicaoAntiga == -1) return false;
        long novaPosicao = arq.updateComPosicao(a, posicaoAntiga);
        if (novaPosicao != posicaoAntiga) {
            indice.inserir(a.getId(), novaPosicao);
        }
        return true;
    }

    public boolean excluir(int id) throws Exception {
        long posicao = indice.buscar(id);
        if (posicao == -1) return false;
        arq.deleteAtPosition(posicao);
        indice.excluir(id);
        return true;
    }

    public List<Alimento> listar() throws Exception {
        return arq.listar();
    }
}