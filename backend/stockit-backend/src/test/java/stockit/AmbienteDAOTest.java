package stockit;

import org.junit.jupiter.api.*;
import stockit.dao.AmbienteDAO;
import stockit.model.Ambiente;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AmbienteDAOTest {

    static AmbienteDAO dao;
    static int idInserido;

    @BeforeAll
    static void setup() throws Exception {
        dao = new AmbienteDAO();
    }

    @Test
    @Order(1)
    @DisplayName("1. Inserir Ambiente")
    void testInserir() throws Exception {
        // tipo: 0 = geladeira, 1 = freezer, 2 = despensa (convenção do sistema)
        Ambiente a = new Ambiente("Geladeira da Cozinha", (byte) 0);

        idInserido = dao.inserir(a);

        assertTrue(idInserido > 0, "ID deve ser maior que 0");
        System.out.println("✔ Inserido com ID: " + idInserido);
    }

    @Test
    @Order(2)
    @DisplayName("2. Buscar Ambiente por ID")
    void testBuscar() throws Exception {
        Ambiente a = dao.buscar(idInserido);

        assertNotNull(a, "Ambiente não encontrado");
        assertEquals("Geladeira da Cozinha", a.getNome());
        assertEquals((byte) 0, a.getTipo());

        System.out.println("✔ Encontrado: " + a);
    }

    @Test
    @Order(3)
    @DisplayName("3. Listar Ambientes")
    void testListar() throws Exception {
        List<Ambiente> lista = dao.listar();

        assertNotNull(lista);
        assertFalse(lista.isEmpty(), "Lista não deve estar vazia");

        System.out.println("✔ Total de ambientes: " + lista.size());
        lista.forEach(a -> System.out.println(a));
    }

    @Test
    @Order(4)
    @DisplayName("4. Alterar Ambiente (mesmo tamanho)")
    void testAlterarMesmoTamanho() throws Exception {
        Ambiente a = dao.buscar(idInserido);
        assertNotNull(a, "Ambiente não encontrado para alterar");

        a.setNome("Geladeira da Sala");
        a.setTipo((byte) 1);

        boolean alterado = dao.alterar(a);
        assertTrue(alterado, "Alteração deve retornar true");

        Ambiente atualizado = dao.buscar(idInserido);
        assertEquals("Geladeira da Sala", atualizado.getNome());
        assertEquals((byte) 1, atualizado.getTipo());

        System.out.println("✔ Alterado (mesmo tamanho): " + atualizado);
    }

    @Test
    @Order(5)
    @DisplayName("5. Alterar Ambiente (registro maior — força realocação)")
    void testAlterarMaior() throws Exception {
        Ambiente a = dao.buscar(idInserido);
        assertNotNull(a, "Ambiente não encontrado para alterar");

        a.setNome("Freezer Vertical Duplex da Área de Serviço");
        a.setTipo((byte) 2);

        boolean alterado = dao.alterar(a);
        assertTrue(alterado, "Alteração deve retornar true");

        Ambiente atualizado = dao.buscar(idInserido);
        assertEquals("Freezer Vertical Duplex da Área de Serviço", atualizado.getNome());
        assertEquals((byte) 2, atualizado.getTipo());

        System.out.println("✔ Alterado (registro maior): " + atualizado);
    }

    @Test
    @Order(6)
    @DisplayName("6. Buscar ID inexistente")
    void testBuscarInexistente() throws Exception {
        Ambiente a = dao.buscar(99999);

        assertNull(a, "Busca de ID inexistente deve retornar null");

        System.out.println("✔ Busca de ID inexistente retornou null corretamente");
    }

    @Test
    @Order(7)
    @DisplayName("7. Excluir Ambiente")
    void testExcluir() throws Exception {
        boolean excluido = dao.excluir(idInserido);

        assertTrue(excluido, "Exclusão deve retornar true");

        Ambiente a = dao.buscar(idInserido);
        assertNull(a, "Ambiente excluído não deve ser encontrado");

        System.out.println("✔ Excluído com sucesso. ID: " + idInserido);
    }

    @Test
    @Order(8)
    @DisplayName("8. Excluir ID inexistente")
    void testExcluirInexistente() throws Exception {
        boolean excluido = dao.excluir(99999);

        assertFalse(excluido, "Excluir ID inexistente deve retornar false");

        System.out.println("✔ Excluir ID inexistente retornou false corretamente");
    }
}