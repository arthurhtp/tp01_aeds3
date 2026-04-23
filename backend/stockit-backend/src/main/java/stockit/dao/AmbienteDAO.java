package stockit.dao;

import java.util.List;
import stockit.model.Ambiente;

public class AmbienteDAO {

    private Arquivo<Ambiente> arq;
    private IndicePrimario indice;

    public AmbienteDAO() throws Exception {
        arq = new Arquivo<>("Ambiente", Ambiente.class.getConstructor());
        indice = new IndicePrimario("Ambiente");
    }

    public int inserir(Ambiente a) throws Exception {
        long[] resultado = arq.createComPosicao(a);
        indice.inserir((int) resultado[0], resultado[1]);
        return (int) resultado[0];
    }

    public Ambiente buscar(int id) throws Exception {
        long posicao = indice.buscar(id);
        if (posicao == -1) return null;
        return arq.readPorPosicao(posicao);
    }

    public boolean alterar(Ambiente a) throws Exception {
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

    public List<Ambiente> listar() throws Exception {
        return arq.listar();
    }
}