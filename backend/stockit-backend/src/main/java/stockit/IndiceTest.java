package stockit;

import stockit.dao.CategoriaAlimentoDAO;
import stockit.model.CategoriaAlimento;
import java.util.List;
import java.io.IOException;

public class IndiceTest{

    public static void main(String[] args) throws Exception {

        CategoriaAlimentoDAO dao = new CategoriaAlimentoDAO();

        // 1. Inserir
        int id1 = dao.inserir(new CategoriaAlimento("Grãos"));
        int id2 = dao.inserir(new CategoriaAlimento("Laticínios"));
        int id3 = dao.inserir(new CategoriaAlimento("Verduras"));
        System.out.println("Inseridos: " + id1 + ", " + id2 + ", " + id3);

        // 2. Buscar
        System.out.println("Buscado id2: " + dao.buscar(id2).getNome());

        // 3. Alterar
        CategoriaAlimento c = dao.buscar(id2);
        c.setNome("Laticínios e Ovos");
        dao.alterar(c);
        System.out.println("Alterado id2: " + dao.buscar(id2).getNome());

        // 4. Excluir
        dao.excluir(id1);
        System.out.println("Buscando excluído: " + dao.buscar(id1)); // null

        // 5. Listar
        List<CategoriaAlimento> lista = dao.listar();
        System.out.println("Total ativo: " + lista.size()); // deve ser 2
        for (CategoriaAlimento cat : lista) {
            System.out.println("  - " + cat.getNome());
        }
    }
}