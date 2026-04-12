package stockit.dao;
import java.util.List;

import stockit.model.ItemAmbiente;

public class ItemAmbienteDAO {
    private Arquivo<ItemAmbiente> arq;

    public ItemAmbienteDAO() throws Exception {
        arq = new Arquivo<>("ItemAmbiente", ItemAmbiente.class.getConstructor());
    }

    public ItemAmbiente buscar(int id) throws Exception {
        return arq.read(id);
    }

    public int inserir(ItemAmbiente a) throws Exception {
        return arq.create(a);
    }

    public boolean alterar(ItemAmbiente a) throws Exception {
        return arq.update(a);
    }

    public boolean excluir(int id) throws Exception {
        return arq.delete(id);
    }

    public List<ItemAmbiente> listar() throws Exception {
    return arq.listar();
}
}

