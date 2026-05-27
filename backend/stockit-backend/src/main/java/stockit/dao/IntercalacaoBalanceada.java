package stockit.dao;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.*;

import stockit.model.Registro;

// Ordenacao por Intercalacao Balanceada em memoria secundaria
public class IntercalacaoBalanceada<T extends Registro> {

    private static final int BLOCO = 4;
    private static final int NUM_FITAS = 4;

    private final Constructor<T> construtor;
    private final Comparator<T> comparador;
    private final String pastaTemp;

    public IntercalacaoBalanceada(Constructor<T> construtor, Comparator<T> comparador, String entidade) {
        this.construtor = construtor;
        this.comparador = comparador;
        this.pastaTemp = "./data/" + entidade + "/temp_sort/";
    }

    // Resultado com log de passos
    public static class ResultadoOrdenacao<T> {
        public List<T> registrosOrdenados;
        public List<String> log; // passos do algoritmo
        public int totalPassos;
        public int blocosGerados;

        public ResultadoOrdenacao() {
            this.registrosOrdenados = new ArrayList<>();
            this.log = new ArrayList<>();
            this.totalPassos = 0;
            this.blocosGerados = 0;
        }
    }

    // Executa a ordenacao
    public ResultadoOrdenacao<T> ordenar(List<T> registros) throws Exception {
        ResultadoOrdenacao<T> resultado = new ResultadoOrdenacao<>();

        if (registros == null || registros.isEmpty()) {
            resultado.log.add("Nenhum registro para ordenar.");
            return resultado;
        }

        if (registros.size() == 1) {
            resultado.registrosOrdenados = registros;
            resultado.log.add("Apenas 1 registro — já está ordenado.");
            return resultado;
        }

        // Criar pasta temporária
        File pasta = new File(pastaTemp);
        if (!pasta.exists()) pasta.mkdirs();

        try {
            // FASE 1: Distribuição — criar blocos ordenados
            resultado.log.add("=== FASE 1: DISTRIBUIÇÃO ===");
            resultado.log.add("Total de registros: " + registros.size());
            resultado.log.add("Tamanho do bloco (M): " + BLOCO + " registros");

            List<List<T>> blocos = new ArrayList<>();
            for (int i = 0; i < registros.size(); i += BLOCO) {
                int fim = Math.min(i + BLOCO, registros.size());
                List<T> bloco = new ArrayList<>(registros.subList(i, fim));
                bloco.sort(comparador);
                blocos.add(bloco);
            }

            resultado.blocosGerados = blocos.size();
            resultado.log.add("Blocos ordenados gerados: " + blocos.size());

            for (int i = 0; i < blocos.size(); i++) {
                resultado.log.add("  Bloco " + i + ": " + descricaoBloco(blocos.get(i)));
            }

            // Gravar blocos em fitas (arquivos temporários)
            // Distribuir alternadamente entre fita0 e fita1
            List<List<List<T>>> fitas = new ArrayList<>();
            fitas.add(new ArrayList<>()); // fita 0
            fitas.add(new ArrayList<>()); // fita 1

            for (int i = 0; i < blocos.size(); i++) {
                fitas.get(i % 2).add(blocos.get(i));
            }

            resultado.log.add("Distribuição nas fitas:");
            resultado.log.add("  Fita 0: " + fitas.get(0).size() + " blocos");
            resultado.log.add("  Fita 1: " + fitas.get(1).size() + " blocos");

            // FASE 2: Intercalação
            resultado.log.add("");
            resultado.log.add("=== FASE 2: INTERCALAÇÃO ===");

            int passo = 0;
            while (fitas.get(0).size() + fitas.get(1).size() > 1) {
                passo++;
                resultado.log.add("--- Passo " + passo + " ---");

                List<List<T>> fitaEntrada0 = fitas.get(0);
                List<List<T>> fitaEntrada1 = fitas.get(1);
                List<List<List<T>>> fitasSaida = new ArrayList<>();
                fitasSaida.add(new ArrayList<>()); // saída 0
                fitasSaida.add(new ArrayList<>()); // saída 1

                int maxBlocos = Math.max(fitaEntrada0.size(), fitaEntrada1.size());
                int saidaIdx = 0;

                for (int i = 0; i < maxBlocos; i++) {
                    List<T> mesclado;
                    if (i < fitaEntrada0.size() && i < fitaEntrada1.size()) {
                        mesclado = mesclar(fitaEntrada0.get(i), fitaEntrada1.get(i));
                        resultado.log.add("  Mesclando bloco " + i + " de fita0 (" + fitaEntrada0.get(i).size() + " reg) com bloco " + i + " de fita1 (" + fitaEntrada1.get(i).size() + " reg) → " + mesclado.size() + " reg");
                    } else if (i < fitaEntrada0.size()) {
                        mesclado = fitaEntrada0.get(i);
                        resultado.log.add("  Copiando bloco " + i + " de fita0 (" + mesclado.size() + " reg) sem par");
                    } else {
                        mesclado = fitaEntrada1.get(i);
                        resultado.log.add("  Copiando bloco " + i + " de fita1 (" + mesclado.size() + " reg) sem par");
                    }

                    fitasSaida.get(saidaIdx % 2).add(mesclado);
                    saidaIdx++;
                }

                fitas = fitasSaida;
                resultado.log.add("  Resultado: Fita 0 = " + fitas.get(0).size() + " blocos, Fita 1 = " + fitas.get(1).size() + " blocos");
            }

            resultado.totalPassos = passo;

            // Resultado final
            List<T> ordenados;
            if (!fitas.get(0).isEmpty()) {
                ordenados = fitas.get(0).get(0);
            } else if (!fitas.get(1).isEmpty()) {
                ordenados = fitas.get(1).get(0);
            } else {
                ordenados = new ArrayList<>();
            }

            resultado.registrosOrdenados = ordenados;
            resultado.log.add("");
            resultado.log.add("=== RESULTADO FINAL ===");
            resultado.log.add("Registros ordenados: " + ordenados.size());
            resultado.log.add("Total de passos de intercalação: " + passo);

        } finally {
            // Limpar pasta temporária
            limparPasta(pasta);
        }

        return resultado;
    }

    private List<T> mesclar(List<T> a, List<T> b) {
        List<T> resultado = new ArrayList<>();
        int i = 0, j = 0;
        while (i < a.size() && j < b.size()) {
            if (comparador.compare(a.get(i), b.get(j)) <= 0) {
                resultado.add(a.get(i++));
            } else {
                resultado.add(b.get(j++));
            }
        }
        while (i < a.size()) resultado.add(a.get(i++));
        while (j < b.size()) resultado.add(b.get(j++));
        return resultado;
    }

    private String descricaoBloco(List<T> bloco) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < bloco.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(getNome(bloco.get(i)));
        }
        sb.append("]");
        return sb.toString();
    }

    private String getNome(T registro) {
        // Usa reflexão para pegar o nome
        try {
            var metodo = registro.getClass().getMethod("getNome");
            return (String) metodo.invoke(registro);
        } catch (Exception e) {
            return "id:" + registro.getId();
        }
    }

    private void limparPasta(File pasta) {
        if (pasta.exists()) {
            File[] arquivos = pasta.listFiles();
            if (arquivos != null) {
                for (File f : arquivos) f.delete();
            }
            pasta.delete();
        }
    }
}
