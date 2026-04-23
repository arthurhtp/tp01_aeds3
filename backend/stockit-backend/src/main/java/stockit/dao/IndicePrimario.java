package stockit.dao;

import java.io.File;
import java.io.RandomAccessFile;

public class IndicePrimario {
    private static final int TAM_CABECALHO = 4;
    private static final int TAM_ENTRADA   = 12; // 4 (id) + 8 (posição)
    private RandomAccessFile arquivo;

    public IndicePrimario(String nomeEntidade) throws Exception {
        File dir = new File("./data/" + nomeEntidade);
        if (!dir.exists()) dir.mkdir();

        String caminho = "./data/" + nomeEntidade + "/" + nomeEntidade + ".idx";
        this.arquivo = new RandomAccessFile(caminho, "rw");

        if (arquivo.length() == 0) {
            arquivo.writeInt(0); // quantidade inicial = 0
        }
    }

    public void inserir(int id, long posicao) throws Exception {
        // lê quantidade atual
        arquivo.seek(0);
        int quantidade = arquivo.readInt();

        // vai para o final e escreve a nova entrada
        arquivo.seek(arquivo.length());
        arquivo.writeInt(id);
        arquivo.writeLong(posicao);

        // atualiza o cabeçalho
        arquivo.seek(0);
        arquivo.writeInt(quantidade + 1);
    }

    public long buscar(int id) throws Exception {
        arquivo.seek(0);
        int quantidade = arquivo.readInt();

        for (int i = 0; i < quantidade; i++) {
            arquivo.seek(TAM_CABECALHO + (i * TAM_ENTRADA));
            int idAtual = arquivo.readInt();
            long posicao = arquivo.readLong();

            if (idAtual == id) {
                return posicao;
            }
        }
        return -1; // não encontrado
    }

    public boolean deletar(int id) throws Exception {
        arquivo.seek(0);
        int quantidade = arquivo.readInt();

        for (int i = 0; i < quantidade; i++) {
            long posEntrada = TAM_CABECALHO + (i * TAM_ENTRADA);
            arquivo.seek(posEntrada);
            int idAtual = arquivo.readInt();
            arquivo.readLong(); // pula a posição

            if (idAtual == id) {
                // substitui esta entrada pela última
                long posUltima = TAM_CABECALHO + ((quantidade - 1) * TAM_ENTRADA);
                arquivo.seek(posUltima);
                int ultimoId  = arquivo.readInt();
                long ultimaPos = arquivo.readLong();

                arquivo.seek(posEntrada);
                arquivo.writeInt(ultimoId);
                arquivo.writeLong(ultimaPos);

                // encurta o arquivo removendo a última entrada
                arquivo.setLength(arquivo.length() - TAM_ENTRADA);

                // atualiza o cabeçalho
                arquivo.seek(0);
                arquivo.writeInt(quantidade - 1);
                return true;
            }
        }
        return false;
    }

    public boolean atualizar(int id, long novaPosicao) throws Exception {
        arquivo.seek(0);
        int quantidade = arquivo.readInt();

        for (int i = 0; i < quantidade; i++) {
            long posEntrada = TAM_CABECALHO + (i * TAM_ENTRADA);
            arquivo.seek(posEntrada);
            int idAtual = arquivo.readInt();

            if (idAtual == id) {
                // sobrescreve só a posição
                arquivo.writeLong(novaPosicao);
                return true;
            }
        }
        return false;
    }

    public void close() throws Exception {
        arquivo.close();
    }

}