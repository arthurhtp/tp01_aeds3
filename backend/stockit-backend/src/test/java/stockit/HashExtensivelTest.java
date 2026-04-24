package stockit;

import org.junit.jupiter.api.*;
import stockit.dao.HashExtensivel;
import stockit.dao.HashExtensivel.*;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Hash Extensível Genérico — Testes Completos")
class HashExtensivelTest {

    // =====================================================================
    // Utilitário: apaga pasta de dados de teste
    // =====================================================================
    static void limparPasta(String nome) {
        File pasta = new File("./data/" + nome);
        if (pasta.exists()) {
            for (File f : pasta.listFiles()) f.delete();
            pasta.delete();
        }
    }

    // =====================================================================
    // 1. ParIntLong — Índice primário (int → long)
    // =====================================================================
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("1. ParIntLong (int → long)")
    class TesteParIntLong {

        static HashExtensivel<ParIntLong> hash;

        @BeforeAll
        static void setup() throws Exception {
            limparPasta("TestePK");
            hash = new HashExtensivel<>("TestePK", new ParIntLong());
        }

        @AfterAll
        static void cleanup() throws Exception {
            hash.close();
            limparPasta("TestePK");
        }

        @Test @Order(1)
        @DisplayName("Inserir e buscar uma entrada")
        void inserirEBuscar() throws Exception {
            hash.inserir(new ParIntLong(1, 100L));
            ParIntLong r = hash.buscar(new ParIntLong(1, 0));
            assertNotNull(r);
            assertEquals(100L, r.valor);
            System.out.println("✔ Inseriu ID=1 → pos=100, buscou OK");
        }

        @Test @Order(2)
        @DisplayName("Atualizar valor existente")
        void atualizar() throws Exception {
            hash.inserir(new ParIntLong(1, 999L));
            ParIntLong r = hash.buscar(new ParIntLong(1, 0));
            assertEquals(999L, r.valor);
            System.out.println("✔ Atualizou ID=1 → pos=999");
        }

        @Test @Order(3)
        @DisplayName("Buscar chave inexistente retorna null")
        void buscarInexistente() throws Exception {
            assertNull(hash.buscar(new ParIntLong(999, 0)));
            System.out.println("✔ Chave 999 não encontrada (null)");
        }

        @Test @Order(4)
        @DisplayName("Inserir várias entradas (forçar split)")
        void inserirVarias() throws Exception {
            for (int i = 2; i <= 20; i++) {
                hash.inserir(new ParIntLong(i, i * 10L));
            }
            // Verifica todas
            for (int i = 1; i <= 20; i++) {
                ParIntLong r = hash.buscar(new ParIntLong(i, 0));
                assertNotNull(r, "ID " + i + " deveria existir");
            }
            System.out.println("✔ 20 entradas inseridas com split, todas encontradas");
        }

        @Test @Order(5)
        @DisplayName("Excluir entrada (lápide)")
        void excluir() throws Exception {
            assertTrue(hash.excluir(new ParIntLong(5, 0)));
            assertNull(hash.buscar(new ParIntLong(5, 0)));
            // Outras continuam intactas
            assertNotNull(hash.buscar(new ParIntLong(4, 0)));
            assertNotNull(hash.buscar(new ParIntLong(6, 0)));
            System.out.println("✔ ID=5 excluído, vizinhos intactos");
        }

        @Test @Order(6)
        @DisplayName("Excluir chave inexistente retorna false")
        void excluirInexistente() throws Exception {
            assertFalse(hash.excluir(new ParIntLong(999, 0)));
            System.out.println("✔ Excluir chave 999 retornou false");
        }

        @Test @Order(7)
        @DisplayName("Reinserir em slot com lápide")
        void reinserir() throws Exception {
            hash.inserir(new ParIntLong(5, 555L));
            ParIntLong r = hash.buscar(new ParIntLong(5, 0));
            assertNotNull(r);
            assertEquals(555L, r.valor);
            System.out.println("✔ ID=5 reinserido no slot da lápide");
        }
    }

    // =====================================================================
    // 2. ParStringLong — Índice secundário (String → long)
    // =====================================================================
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("2. ParStringLong (String → long)")
    class TesteParStringLong {

        static HashExtensivel<ParStringLong> hash;

        @BeforeAll
        static void setup() throws Exception {
            limparPasta("TesteNome");
            hash = new HashExtensivel<>("TesteNome", new ParStringLong());
        }

        @AfterAll
        static void cleanup() throws Exception {
            hash.close();
            limparPasta("TesteNome");
        }

        @Test @Order(1)
        @DisplayName("Inserir e buscar por nome")
        void inserirEBuscar() throws Exception {
            hash.inserir(new ParStringLong("Arroz Integral", 200L));
            ParStringLong r = hash.buscar(new ParStringLong("Arroz Integral", 0));
            assertNotNull(r);
            assertEquals(200L, r.valor);
            System.out.println("✔ 'Arroz Integral' → pos=200");
        }

        @Test @Order(2)
        @DisplayName("Nomes diferentes não colidem")
        void nomesDiferentes() throws Exception {
            hash.inserir(new ParStringLong("Feijão Preto", 300L));
            hash.inserir(new ParStringLong("Leite Desnatado", 400L));

            assertEquals(200L, hash.buscar(new ParStringLong("Arroz Integral", 0)).valor);
            assertEquals(300L, hash.buscar(new ParStringLong("Feijão Preto", 0)).valor);
            assertEquals(400L, hash.buscar(new ParStringLong("Leite Desnatado", 0)).valor);
            System.out.println("✔ 3 nomes distintos, sem colisão");
        }

        @Test @Order(3)
        @DisplayName("Atualizar endereço de nome existente")
        void atualizar() throws Exception {
            hash.inserir(new ParStringLong("Arroz Integral", 888L));
            assertEquals(888L, hash.buscar(new ParStringLong("Arroz Integral", 0)).valor);
            System.out.println("✔ 'Arroz Integral' atualizado → pos=888");
        }

        @Test @Order(4)
        @DisplayName("Excluir por nome (lápide)")
        void excluir() throws Exception {
            assertTrue(hash.excluir(new ParStringLong("Feijão Preto", 0)));
            assertNull(hash.buscar(new ParStringLong("Feijão Preto", 0)));
            assertNotNull(hash.buscar(new ParStringLong("Arroz Integral", 0)));
            System.out.println("✔ 'Feijão Preto' excluído, outros intactos");
        }

        @Test @Order(5)
        @DisplayName("Inserir muitos nomes (forçar split)")
        void inserirMuitos() throws Exception {
            String[] nomes = {"Banana", "Maçã", "Uva", "Pera", "Manga",
                              "Abacaxi", "Melancia", "Morango", "Kiwi", "Laranja"};
            for (int i = 0; i < nomes.length; i++) {
                hash.inserir(new ParStringLong(nomes[i], (i + 1) * 50L));
            }
            for (int i = 0; i < nomes.length; i++) {
                ParStringLong r = hash.buscar(new ParStringLong(nomes[i], 0));
                assertNotNull(r, nomes[i] + " deveria existir");
                assertEquals((i + 1) * 50L, r.valor);
            }
            System.out.println("✔ 10 nomes inseridos com split, todos encontrados");
        }
    }

    // =====================================================================
    // 3. ParIntListaInt — Relacionamento 1:N (int → lista de ints)
    // =====================================================================
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("3. ParIntListaInt (int → lista de ints)")
    class TesteParIntListaInt {

        static HashExtensivel<ParIntListaInt> hash;

        @BeforeAll
        static void setup() throws Exception {
            limparPasta("TesteRel");
            hash = new HashExtensivel<>("TesteRel", new ParIntListaInt());
        }

        @AfterAll
        static void cleanup() throws Exception {
            hash.close();
            limparPasta("TesteRel");
        }

        @Test @Order(1)
        @DisplayName("Inserir primeiro relacionamento")
        void inserirPrimeiro() throws Exception {
            ParIntListaInt p = new ParIntListaInt(10, 1); // ambiente 10 → item 1
            hash.inserir(p);

            ParIntListaInt r = hash.buscar(new ParIntListaInt(10, 0));
            assertNotNull(r);
            assertEquals(1, r.qtd);
            assertEquals(1, r.valores[0]);
            System.out.println("✔ Ambiente 10 → [1]");
        }

        @Test @Order(2)
        @DisplayName("Adicionar mais IDs ao mesmo relacionamento")
        void adicionarMais() throws Exception {
            ParIntListaInt r = hash.buscar(new ParIntListaInt(10, 0));
            r.adicionar(2);
            r.adicionar(3);
            hash.inserir(r); // atualiza

            r = hash.buscar(new ParIntListaInt(10, 0));
            assertEquals(3, r.qtd);
            List<Integer> ids = r.listar();
            assertTrue(ids.contains(1));
            assertTrue(ids.contains(2));
            assertTrue(ids.contains(3));
            System.out.println("✔ Ambiente 10 → [1, 2, 3]");
        }

        @Test @Order(3)
        @DisplayName("Não permite duplicata")
        void semDuplicata() throws Exception {
            ParIntListaInt r = hash.buscar(new ParIntListaInt(10, 0));
            boolean adicionou = r.adicionar(2); // já existe
            assertFalse(adicionou);
            assertEquals(3, r.qtd);
            System.out.println("✔ Duplicata ignorada");
        }

        @Test @Order(4)
        @DisplayName("Remover um ID da lista")
        void removerValor() throws Exception {
            ParIntListaInt r = hash.buscar(new ParIntListaInt(10, 0));
            assertTrue(r.remover(2));
            hash.inserir(r);

            r = hash.buscar(new ParIntListaInt(10, 0));
            assertEquals(2, r.qtd);
            assertFalse(r.listar().contains(2));
            System.out.println("✔ Removeu ID=2, ficou [1, 3]");
        }

        @Test @Order(5)
        @DisplayName("Excluir relacionamento inteiro (lápide)")
        void excluirTudo() throws Exception {
            assertTrue(hash.excluir(new ParIntListaInt(10, 0)));
            assertNull(hash.buscar(new ParIntListaInt(10, 0)));
            System.out.println("✔ Relacionamento do ambiente 10 excluído");
        }

        @Test @Order(6)
        @DisplayName("Múltiplas chaves com split")
        void multiplasChaves() throws Exception {
            for (int chave = 1; chave <= 10; chave++) {
                for (int val = 1; val <= 3; val++) {
                    ParIntListaInt r = hash.buscar(new ParIntListaInt(chave, 0));
                    if (r == null) {
                        hash.inserir(new ParIntListaInt(chave, val));
                    } else {
                        r.adicionar(val);
                        hash.inserir(r);
                    }
                }
            }
            // Verifica
            for (int chave = 1; chave <= 10; chave++) {
                ParIntListaInt r = hash.buscar(new ParIntListaInt(chave, 0));
                assertNotNull(r, "Chave " + chave + " deveria existir");
                assertEquals(3, r.qtd, "Chave " + chave + " deveria ter 3 valores");
            }
            System.out.println("✔ 10 chaves × 3 valores cada, com split, tudo OK");
        }
    }
}
