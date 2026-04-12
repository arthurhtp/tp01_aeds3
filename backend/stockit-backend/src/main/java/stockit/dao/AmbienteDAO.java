package stockit.dao;
import java.util.List;

import stockit.model.Ambiente;

public class AmbienteDAO {
    private Arquivo<Ambiente> arq;

    public AmbienteDAO() throws Exception {
        arq = new Arquivo<>("Ambiente", Ambiente.class.getConstructor());
    }

    public Ambiente buscar(int id) throws Exception {
        return arq.read(id);
    }

    public int inserir(Ambiente a) throws Exception {
        return arq.create(a);
    }

    public boolean alterar(Ambiente a) throws Exception {
        return arq.update(a);
    }

    public boolean excluir(int id) throws Exception {
        return arq.delete(id);
    }

    public List<Ambiente> listar() throws Exception {
    return arq.listar();
}
}

