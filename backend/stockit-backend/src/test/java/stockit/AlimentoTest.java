package stockit;

import org.junit.jupiter.api.*;
import stockit.dao.AlimentoDAO;
import stockit.model.Alimento;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AlimentoTest {

    static AlimentoDAO dao;
    static int idInserido;

    @BeforeAll
    static void setup() throws Exception {
        dao = new AlimentoDAO();
    }

    @Test
    @Order(1)
    @DisplayName("1. Inserir Alimento")
    void testInserir() throws Exception {
        Alimento a = new Alimento(
            "Leite Integral",
            Arrays.asList("laticínio", "proteína"),
            1
        );
        idInserido = dao.inserir(a);

        assertTrue(idInserido > 0, "ID deve ser maior que 0");
        System.out.println("✔ Inserido com ID: " + idInserido);
    }

    @Test
    @Order(2)
    @DisplayName("2. Buscar Alimento por ID")
    void testBuscar() throws Exception {
        Alimento a = dao.buscar(idInserido);

        assertNotNull(a, "Alimento não encontrado");
        assertEquals("Leite Integral", a.getNome());
        assertEquals(2, a.getRotulos().size());
        assertTrue(a.getRotulos().contains("laticínio"));
        assertTrue(a.getRotulos().contains("proteína"));
        assertEquals(1, a.getIdCategoriaAlimento());
        System.out.println("✔ Encontrado: " + a);
    }

    @Test
    @Order(3)
    @DisplayName("3. Listar Alimentos")
    void testListar() throws Exception {
        List<Alimento> lista = dao.listar();

        assertNotNull(lista);
        assertFalse(lista.isEmpty(), "Lista não deve estar vazia");
        System.out.println("✔ Total de alimentos: " + lista.size());
        lista.forEach(a -> System.out.println(a));
    }

    @Test
    @Order(4)
    @DisplayName("4. Alterar Alimento")
    void testAlterar() throws Exception {
        Alimento a = dao.buscar(idInserido);
        assertNotNull(a, "Alimento não encontrado para alterar");

        a.setNome("Leite Desnatado");
        a.setRotulos(Arrays.asList("laticínio", "light", "sem gordura"));
        a.setIdCategoriaAlimento(2);
        boolean alterado = dao.alterar(a);

        assertTrue(alterado, "Alteração deve retornar true");

        Alimento atualizado = dao.buscar(idInserido);
        assertEquals("Leite Desnatado", atualizado.getNome());
        assertEquals(3, atualizado.getRotulos().size());
        assertTrue(atualizado.getRotulos().contains("light"));
        assertEquals(2, atualizado.getIdCategoriaAlimento());
        System.out.println("✔ Alterado: " + atualizado);
    }

    @Test
    @Order(5)
    @DisplayName("5. Excluir Alimento")
    void testExcluir() throws Exception {
        boolean excluido = dao.excluir(idInserido);

        assertTrue(excluido, "Exclusão deve retornar true");

        Alimento a = dao.buscar(idInserido);
        assertNull(a, "Alimento excluído não deve ser encontrado");
        System.out.println("✔ Excluído com sucesso. ID: " + idInserido);
    }
}