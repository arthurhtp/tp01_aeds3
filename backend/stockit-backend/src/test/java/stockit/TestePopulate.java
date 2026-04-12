package stockit;

import stockit.dao.ItemAmbienteDAO;
import stockit.model.ItemAmbiente;

public class TestePopulate {

    public static void main(String[] args) throws Exception {

        ItemAmbienteDAO dao = new ItemAmbienteDAO();

        System.out.println("Populando itens nos ambientes...");

        // (alimentoId, ambienteId, quantidade, dataCadastro, dataVencimento)
        dao.inserir(new ItemAmbiente(1, 1, (short) 15, 20260304, 20261231));
        dao.inserir(new ItemAmbiente(2, 2, (short) 5, 20260304, 20260615));
        dao.inserir(new ItemAmbiente(3, 3, (short) 30, 20260304, 20270101));

        System.out.println("Itens inseridos com sucesso!");

        System.out.println("\nListando registros do estoque:");
        dao.listar().forEach(System.out::println);
    }
}