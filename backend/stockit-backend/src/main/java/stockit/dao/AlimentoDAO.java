package stockit.dao;

import java.util.List;
import stockit.model.Alimento;

public class AlimentoDAO {

    private Arquivo<Alimento> arq;

    public AlimentoDAO() throws Exception {
        arq = new Arquivo<>("Alimento", Alimento.class.getConstructor());
    }

    public Alimento buscar(int id) throws Exception {
        return arq.read(id);
    }

    public int inserir(Alimento a) throws Exception {
        return arq.create(a);
    }

    public boolean alterar(Alimento a) throws Exception {
        return arq.update(a);
    }

    public boolean excluir(int id) throws Exception {
        return arq.delete(id);
    }

    public List<Alimento> listar() throws Exception {
        return arq.listar();
    }
}