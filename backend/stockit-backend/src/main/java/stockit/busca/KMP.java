package stockit.busca;

/**
 * Algoritmo de casamento de padrões de Knuth–Morris–Pratt (KMP).
 *
 * Ideia central: pré-processa o padrão construindo a tabela de "prefixo-sufixo"
 * (também chamada de função de falha ou LPS — Longest Proper Prefix which is
 * also Suffix). Quando ocorre uma divergência durante a comparação, em vez de
 * voltar o ponteiro do texto, o KMP usa essa tabela para saber quantos
 * caracteres do padrão já casaram e reposiciona apenas o ponteiro do padrão.
 * Assim o texto é varrido uma única vez.
 *
 * Complexidade: O(m) para construir a tabela + O(n) para a busca = O(n + m),
 * onde n = tamanho do texto e m = tamanho do padrão.
 *
 * A busca é case-insensitive (texto e padrão são normalizados para minúsculas),
 * o que é mais adequado para pesquisa de nomes na base de dados.
 */
public final class KMP {

    private KMP() {}

    /**
     * Constrói a tabela de falha (LPS) do padrão.
     * lps[i] = comprimento do maior prefixo próprio de padrao[0..i] que também
     * é sufixo desse mesmo trecho.
     */
    public static int[] tabelaFalha(char[] padrao) {
        int m = padrao.length;
        int[] lps = new int[m];
        if (m == 0) return lps;

        lps[0] = 0;     // o primeiro caractere nunca tem prefixo próprio
        int tamanho = 0; // comprimento do prefixo-sufixo do trecho anterior
        int i = 1;

        while (i < m) {
            if (padrao[i] == padrao[tamanho]) {
                tamanho++;
                lps[i] = tamanho;
                i++;
            } else if (tamanho > 0) {
                // recua para o prefixo-sufixo anterior, sem mexer em i
                tamanho = lps[tamanho - 1];
            } else {
                lps[i] = 0;
                i++;
            }
        }
        return lps;
    }

    /**
     * Retorna true se o padrão ocorre pelo menos uma vez no texto.
     * Busca case-insensitive.
     */
    public static boolean contem(String texto, String padrao) {
        return indiceDe(texto, padrao) >= 0;
    }

    /**
     * Retorna o índice da primeira ocorrência do padrão no texto, ou -1.
     * Busca case-insensitive.
     */
    public static int indiceDe(String texto, String padrao) {
        if (texto == null || padrao == null) return -1;
        if (padrao.isEmpty()) return 0;
        if (padrao.length() > texto.length()) return -1;

        char[] t = texto.toLowerCase().toCharArray();
        char[] p = padrao.toLowerCase().toCharArray();
        int n = t.length, m = p.length;
        int[] lps = tabelaFalha(p);

        int i = 0; // índice no texto
        int j = 0; // índice no padrão
        while (i < n) {
            if (t[i] == p[j]) {
                i++;
                j++;
                if (j == m) {
                    return i - j; // casamento completo
                }
            } else if (j > 0) {
                // aproveita o que já casou: pula para lps[j-1]
                j = lps[j - 1];
            } else {
                i++;
            }
        }
        return -1;
    }

    /**
     * Quantidade total de ocorrências do padrão no texto (com sobreposição).
     * Busca case-insensitive.
     */
    public static int contarOcorrencias(String texto, String padrao) {
        if (texto == null || padrao == null || padrao.isEmpty()) return 0;
        char[] t = texto.toLowerCase().toCharArray();
        char[] p = padrao.toLowerCase().toCharArray();
        int n = t.length, m = p.length;
        if (m > n) return 0;
        int[] lps = tabelaFalha(p);

        int total = 0, i = 0, j = 0;
        while (i < n) {
            if (t[i] == p[j]) {
                i++;
                j++;
                if (j == m) {
                    total++;
                    j = lps[j - 1]; // continua buscando ocorrências sobrepostas
                }
            } else if (j > 0) {
                j = lps[j - 1];
            } else {
                i++;
            }
        }
        return total;
    }
}
