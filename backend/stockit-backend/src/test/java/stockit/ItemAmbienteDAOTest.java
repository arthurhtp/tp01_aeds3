package stockit;

import org.junit.jupiter.api.*;
import stockit.dao.ItemAmbienteDAO;
import stockit.model.ItemAmbiente;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ItemAmbienteDAOTest {

    static ItemAmbienteDAO dao;
    static int idInserido;

    // Datas no formato YYYYMMDD como int
    static final int DATA_CADASTRO   = 20250101;
    static final int DATA_VENCIMENTO = 20250601;

    @BeforeAll
    static void setup() throws Exception {
        dao = new ItemAmbienteDAO();
    }

    @Test
    @Order(1)
    @DisplayName("1. Inserir ItemAmbiente")
    void testInserir() throws Exception {
        // alimentoId=1, ambienteId=1 (assumindo que já existem no sistema)
        ItemAmbiente item = new ItemAmbiente(1, 1, (short) 10, DATA_CADASTRO, DATA_VENCIMENTO);

        idInserido = dao.inserir(item);

        assertTrue(idInserido > 0, "ID deve ser maior que 0");
        System.out.println("✔ Inserido com ID: " + idInserido);
    }

    @Test
    @Order(2)
    @DisplayName("2. Buscar ItemAmbiente por ID")
    void testBuscar() throws Exception {
        ItemAmbiente item = dao.buscar(idInserido);

        assertNotNull(item, "ItemAmbiente não encontrado");
        assertEquals(1, item.getAlimentoId());
        assertEquals(1, item.getAmbienteId());
        assertEquals((short) 10, item.getQuantidade());
        assertEquals(DATA_CADASTRO, item.getDataCadastro());
        assertEquals(DATA_VENCIMENTO, item.getDataVencimento());

        System.out.println("✔ Encontrado: " + item);
    }

    @Test
    @Order(3)
    @DisplayName("3. Listar ItemAmbientes")
    void testListar() throws Exception {
        List<ItemAmbiente> lista = dao.listar();

        assertNotNull(lista);
        assertFalse(lista.isEmpty(), "Lista não deve estar vazia");

        System.out.println("✔ Total de itens: " + lista.size());
        lista.forEach(i -> System.out.println(i));
    }

    @Test
    @Order(4)
    @DisplayName("4. Alterar ItemAmbiente (quantidade e vencimento)")
    void testAlterar() throws Exception {
        ItemAmbiente item = dao.buscar(idInserido);
        assertNotNull(item, "ItemAmbiente não encontrado para alterar");

        item.setQuantidade((short) 25);
        item.setDataVencimento(20251231);
        item.setAmbienteId(2);

        boolean alterado = dao.alterar(item);
        assertTrue(alterado, "Alteração deve retornar true");

        ItemAmbiente atualizado = dao.buscar(idInserido);
        assertEquals((short) 25, atualizado.getQuantidade());
        assertEquals(20251231, atualizado.getDataVencimento());
        assertEquals(2, atualizado.getAmbienteId());

        System.out.println("✔ Alterado: " + atualizado);
    }

    @Test
    @Order(5)
    @DisplayName("5. Buscar ID inexistente")
    void testBuscarInexistente() throws Exception {
        ItemAmbiente item = dao.buscar(99999);

        assertNull(item, "Busca de ID inexistente deve retornar null");

        System.out.println("✔ Busca de ID inexistente retornou null corretamente");
    }

    @Test
    @Order(6)
    @DisplayName("6. Excluir ItemAmbiente")
    void testExcluir() throws Exception {
        boolean excluido = dao.excluir(idInserido);

        assertTrue(excluido, "Exclusão deve retornar true");

        ItemAmbiente item = dao.buscar(idInserido);
        assertNull(item, "ItemAmbiente excluído não deve ser encontrado");

        System.out.println("✔ Excluído com sucesso. ID: " + idInserido);
    }

    @Test
    @Order(7)
    @DisplayName("7. Excluir ID inexistente")
    void testExcluirInexistente() throws Exception {
        boolean excluido = dao.excluir(99999);

        assertFalse(excluido, "Excluir ID inexistente deve retornar false");

        System.out.println("✔ Excluir ID inexistente retornou false corretamente");
    }
}