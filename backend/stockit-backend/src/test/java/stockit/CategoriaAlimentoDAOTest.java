package stockit;

import org.junit.jupiter.api.*;
import stockit.dao.CategoriaAlimentoDAO;
import stockit.model.CategoriaAlimento;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CategoriaAlimentoDAOTest {

    static CategoriaAlimentoDAO dao;
    static int idInserido;

    @BeforeAll
    static void setup() throws Exception {
        dao = new CategoriaAlimentoDAO();
    }

    @Test
    @Order(1)
    @DisplayName("1. Inserir CategoriaAlimento")
    void testInserir() throws Exception {
        CategoriaAlimento c = new CategoriaAlimento("Frutas");

        idInserido = dao.inserir(c);

        assertTrue(idInserido > 0, "ID deve ser maior que 0");
        System.out.println("✔ Inserido com ID: " + idInserido);
    }

    @Test
    @Order(2)
    @DisplayName("2. Buscar CategoriaAlimento por ID")
    void testBuscar() throws Exception {
        CategoriaAlimento c = dao.buscar(idInserido);

        assertNotNull(c, "Categoria não encontrada");
        assertEquals("Frutas", c.getNome());

        System.out.println("✔ Encontrado: " + c);
    }

    @Test
    @Order(3)
    @DisplayName("3. Listar Categorias")
    void testListar() throws Exception {
        List<CategoriaAlimento> lista = dao.listar();

        assertNotNull(lista);
        assertFalse(lista.isEmpty(), "Lista não deve estar vazia");

        System.out.println("✔ Total de categorias: " + lista.size());
        lista.forEach(c -> System.out.println(c));
    }

    @Test
    @Order(4)
    @DisplayName("4. Alterar CategoriaAlimento")
    void testAlterar() throws Exception {
        CategoriaAlimento c = dao.buscar(idInserido);
        assertNotNull(c, "Categoria não encontrada para alterar");

        c.setNome("Frutas Frescas");

        boolean alterado = dao.alterar(c);

        assertTrue(alterado, "Alteração deve retornar true");

        CategoriaAlimento atualizado = dao.buscar(idInserido);
        assertEquals("Frutas Frescas", atualizado.getNome());

        System.out.println("✔ Alterado: " + atualizado);
    }

    @Test
    @Order(5)
    @DisplayName("5. Buscar ID inexistente")
    void testBuscarInexistente() throws Exception {
        CategoriaAlimento c = dao.buscar(99999);

        assertNull(c, "Busca de ID inexistente deve retornar null");

        System.out.println("✔ Busca de ID inexistente retornou null corretamente");
    }

    @Test
    @Order(6)
    @DisplayName("6. Excluir CategoriaAlimento")
    void testExcluir() throws Exception {
        boolean excluido = dao.excluir(idInserido);

        assertTrue(excluido, "Exclusão deve retornar true");

        CategoriaAlimento c = dao.buscar(idInserido);
        assertNull(c, "Categoria excluída não deve ser encontrada");

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