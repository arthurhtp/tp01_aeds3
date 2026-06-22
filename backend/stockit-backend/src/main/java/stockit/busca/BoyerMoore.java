package stockit.busca;

import java.util.HashMap;
import java.util.Map;

/**
 * Algoritmo de casamento de padrões de Boyer–Moore.
 *
 * Diferente do KMP, a comparação do padrão com o texto é feita da DIREITA para
 * a ESQUERDA. Quando há divergência, o algoritmo decide o quanto pode deslizar
 * o padrão para a direita usando duas heurísticas, escolhendo sempre o maior
 * salto entre elas:
 *
 *  1) Bad character (caractere ruim) — OBRIGATÓRIA:
 *     ao falhar no caractere c do texto, alinha-se o padrão de modo que a última
 *     ocorrência de c no padrão fique sob c. Se c não existe no padrão, pula-se
 *     o padrão inteiro além de c.
 *
 *  2) Good suffix (bom sufixo) — OPCIONAL (implementada):
 *     aproveita o sufixo que JÁ casou. Se esse sufixo aparece em outra posição
 *     do padrão (ou um prefixo do padrão coincide com um sufixo do bom sufixo),
 *     desliza-se o padrão para reaproveitar esse trecho.
 *
 * Na prática o Boyer–Moore costuma examinar bem menos que n caracteres, sendo
 * sublinear no caso médio. O pior caso é O(n·m), mas com a heurística de bom
 * sufixo aproxima-se de O(n + m).
 *
 * Busca case-insensitive (texto e padrão normalizados para minúsculas).
 */
public final class BoyerMoore {

    private BoyerMoore() {}

    // ===================== HEURÍSTICA 1: BAD CHARACTER =====================

    /**
     * Tabela do "último índice" de cada caractere no padrão.
     * Usa HashMap para suportar todo o conjunto Unicode (acentos, etc.),
     * em vez de limitar a 256 posições ASCII.
     */
    public static Map<Character, Integer> tabelaBadCharacter(char[] padrao) {
        Map<Character, Integer> ultima = new HashMap<>();
        for (int i = 0; i < padrao.length; i++) {
            ultima.put(padrao[i], i); // sobrescreve: fica o índice mais à direita
        }
        return ultima;
    }

    // ===================== HEURÍSTICA 2: GOOD SUFFIX =====================

    /**
     * Pré-processamento da heurística do bom sufixo.
     * Retorna o vetor de deslocamentos `shift` de tamanho m+1, onde shift[i] é
     * o quanto deslizar quando ocorre divergência na posição i do padrão
     * (o sufixo padrao[i+1..m-1] já havia casado).
     */
    public static int[] tabelaGoodSuffix(char[] padrao) {
        int m = padrao.length;
        int[] shift = new int[m + 1];
        int[] borda = new int[m + 1]; // posição inicial da borda mais larga

        // --- Caso 1: o bom sufixo ocorre em outro lugar do padrão ---
        int i = m, j = m + 1;
        borda[i] = j;
        while (i > 0) {
            while (j <= m && padrao[i - 1] != padrao[j - 1]) {
                if (shift[j] == 0) shift[j] = j - i;
                j = borda[j];
            }
            i--;
            j--;
            borda[i] = j;
        }

        // --- Caso 2: apenas parte do bom sufixo (um prefixo do padrão) casa ---
        j = borda[0];
        for (i = 0; i <= m; i++) {
            if (shift[i] == 0) shift[i] = j;
            if (i == j) j = borda[j];
        }
        return shift;
    }

    // ===================== BUSCA =====================

    /** Retorna true se o padrão ocorre pelo menos uma vez no texto. */
    public static boolean contem(String texto, String padrao) {
        return indiceDe(texto, padrao) >= 0;
    }

    /**
     * Índice da primeira ocorrência do padrão no texto, ou -1.
     * Combina bad character + good suffix, escolhendo o maior salto.
     */
    public static int indiceDe(String texto, String padrao) {
        if (texto == null || padrao == null) return -1;
        if (padrao.isEmpty()) return 0;
        if (padrao.length() > texto.length()) return -1;

        char[] t = texto.toLowerCase().toCharArray();
        char[] p = padrao.toLowerCase().toCharArray();
        int n = t.length, m = p.length;

        Map<Character, Integer> bad = tabelaBadCharacter(p);
        int[] good = tabelaGoodSuffix(p);

        int s = 0; // deslocamento do padrão em relação ao texto
        while (s <= n - m) {
            int j = m - 1; // compara da direita para a esquerda
            while (j >= 0 && p[j] == t[s + j]) {
                j--;
            }
            if (j < 0) {
                return s; // casamento completo
            }
            // salto pela heurística do bad character
            int ultima = bad.getOrDefault(t[s + j], -1);
            int saltoBad = Math.max(1, j - ultima);
            // salto pela heurística do good suffix
            int saltoGood = good[j + 1];
            s += Math.max(saltoBad, saltoGood);
        }
        return -1;
    }

    /** Quantidade total de ocorrências do padrão no texto (com sobreposição). */
    public static int contarOcorrencias(String texto, String padrao) {
        if (texto == null || padrao == null || padrao.isEmpty()) return 0;
        char[] t = texto.toLowerCase().toCharArray();
        char[] p = padrao.toLowerCase().toCharArray();
        int n = t.length, m = p.length;
        if (m > n) return 0;

        Map<Character, Integer> bad = tabelaBadCharacter(p);
        int[] good = tabelaGoodSuffix(p);

        int total = 0, s = 0;
        while (s <= n - m) {
            int j = m - 1;
            while (j >= 0 && p[j] == t[s + j]) {
                j--;
            }
            if (j < 0) {
                total++;
                // após casar, desliza pelo bom sufixo do início (good[0])
                s += good[0];
            } else {
                int ultima = bad.getOrDefault(t[s + j], -1);
                int saltoBad = Math.max(1, j - ultima);
                int saltoGood = good[j + 1];
                s += Math.max(saltoBad, saltoGood);
            }
        }
        return total;
    }
}
