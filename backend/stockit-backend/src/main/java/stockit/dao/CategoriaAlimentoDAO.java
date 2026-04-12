package stockit.dao;

import java.util.List;
import stockit.model.CategoriaAlimento;

public class CategoriaAlimentoDAO {

    private Arquivo<CategoriaAlimento> arq;

    public CategoriaAlimentoDAO() throws Exception {
        arq = new Arquivo<>("CategoriaAlimento", CategoriaAlimento.class.getConstructor());
    }

    public CategoriaAlimento buscar(int id) throws Exception {
        return arq.read(id);
    }

    public int inserir(CategoriaAlimento c) throws Exception {
        return arq.create(c);
    }

    public boolean alterar(CategoriaAlimento c) throws Exception {
        return arq.update(c);
    }

    public boolean excluir(int id) throws Exception {
        return arq.delete(id);
    }

    public List<CategoriaAlimento> listar() throws Exception {
        return arq.listar();
    }
}