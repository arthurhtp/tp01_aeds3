package stockit.dao;

import java.util.List;
import stockit.model.CategoriaAlimento;

public class CategoriaAlimentoDAO {

    private Arquivo<CategoriaAlimento> arq;
    private IndicePrimario indice;

    public CategoriaAlimentoDAO() throws Exception {
        arq = new Arquivo<>("CategoriaAlimento", CategoriaAlimento.class.getConstructor());
        indice = new IndicePrimario("CategoriaAlimento");
    }

    public int inserir(CategoriaAlimento c) throws Exception {
        long[] resultado = arq.createComPosicao(c);
        indice.inserir((int) resultado[0], resultado[1]);
        return (int) resultado[0];
    }

    public CategoriaAlimento buscar(int id) throws Exception {
        long posicao = indice.buscar(id);
        if (posicao == -1) return null;
        return arq.readPorPosicao(posicao);
    }

    public boolean alterar(CategoriaAlimento c) throws Exception {
        long posicaoAntiga = indice.buscar(c.getId());
        if (posicaoAntiga == -1) return false;
        long novaPosicao = arq.updateComPosicao(c, posicaoAntiga);
        if (novaPosicao != posicaoAntiga) {
            indice.atualizar(c.getId(), novaPosicao);
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

    public List<CategoriaAlimento> listar() throws Exception {
        return arq.listar();
    }
}