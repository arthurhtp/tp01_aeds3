package stockit.dao;

import java.io.File;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import stockit.model.Registro;

public class Arquivo<T extends Registro> {
    private static final int TAM_CABECALHO = 4;
    private RandomAccessFile arquivo;
    private String nomeArquivo;
    private Constructor<T> construtor;
    private RandomAccessFile arquivoFree;
    private static final int TAM_ENTRADA_FREE = 10; // 2 + 8

    public Arquivo(String nomeArquivo, Constructor<T> construtor) throws Exception {
        File diretorio = new File("./data");
        if (!diretorio.exists())
            diretorio.mkdir();

        diretorio = new File("./data/" + nomeArquivo);
        if (!diretorio.exists())
            diretorio.mkdir();

        this.nomeArquivo = "./data/" + nomeArquivo + "/" + nomeArquivo + ".dat";
        this.construtor = construtor;
        this.arquivo = new RandomAccessFile(this.nomeArquivo, "rw");

        if (arquivo.length() < TAM_CABECALHO) {
            arquivo.writeInt(0);
        }

        String nomeFree = "./data/" + nomeArquivo + "/" + nomeArquivo + ".free";
        this.arquivoFree = new RandomAccessFile(nomeFree, "rw");
        if (arquivoFree.length() == 0) {
            arquivoFree.writeInt(0);
        }
    }

    // ==============================
    // MÉTODOS ORIGINAIS
    // ==============================

    public int create(T obj) throws Exception {
        arquivo.seek(0);
        int novoID = arquivo.readInt() + 1;
        arquivo.seek(0);
        arquivo.writeInt(novoID);
        obj.setId(novoID);
        byte[] dados = obj.toByteArray();

        arquivo.seek(arquivo.length());
        arquivo.writeByte(' ');
        arquivo.writeShort(dados.length);
        arquivo.write(dados);
        return obj.getId();
    }

    public T read(int id) throws Exception {
        arquivo.seek(TAM_CABECALHO);
        while (arquivo.getFilePointer() < arquivo.length()) {
            long posicao = arquivo.getFilePointer();
            byte lapide = arquivo.readByte();
            short tamanho = arquivo.readShort();
            byte[] dados = new byte[tamanho];
            arquivo.read(dados);

            if (lapide == ' ') {
                T obj = construtor.newInstance();
                obj.fromByteArray(dados);
                if (obj.getId() == id) {
                    return obj;
                }
            }
        }
        return null;
    }

    public boolean delete(int id) throws Exception {
        arquivo.seek(TAM_CABECALHO);
        while (arquivo.getFilePointer() < arquivo.length()) {
            long posicao = arquivo.getFilePointer();
            byte lapide = arquivo.readByte();
            short tamanho = arquivo.readShort();
            byte[] dados = new byte[tamanho];
            arquivo.read(dados);

            if (lapide == ' ') {
                T obj = construtor.newInstance();
                obj.fromByteArray(dados);
                if (obj.getId() == id) {
                    arquivo.seek(posicao);
                    arquivo.writeByte('*');
                    addDeleted(tamanho, posicao); // registra espaço livre
                    return true;
                }
            }
        }
        return false;
    }

    public boolean update(T novoObj) throws Exception {
        arquivo.seek(TAM_CABECALHO);
        while (arquivo.getFilePointer() < arquivo.length()) {
            long posicao = arquivo.getFilePointer();
            byte lapide = arquivo.readByte();
            short tamanho = arquivo.readShort();
            byte[] dados = new byte[tamanho];
            arquivo.read(dados);

            if (lapide == ' ') {
                T obj = construtor.newInstance();
                obj.fromByteArray(dados);
                if (obj.getId() == novoObj.getId()) {
                    byte[] novosDados = novoObj.toByteArray();
                    short novoTam = (short) novosDados.length;

                    if (novoTam <= tamanho) {
                        arquivo.seek(posicao + 3);
                        arquivo.write(novosDados);
                    } else {
                        arquivo.seek(posicao);
                        arquivo.writeByte('*');
                        addDeleted(tamanho, posicao); // registra espaço livre

                        arquivo.seek(arquivo.length());
                        arquivo.writeByte(' ');
                        arquivo.writeShort(novoTam);
                        arquivo.write(novosDados);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    // ==============================
    // MÉTODOS COM POSIÇÃO (para índice)
    // ==============================

    public long[] createComPosicao(T obj) throws Exception {
        arquivo.seek(0);
        int novoID = arquivo.readInt() + 1;
        arquivo.seek(0);
        arquivo.writeInt(novoID);
        obj.setId(novoID);
        byte[] dados = obj.toByteArray();

        long endereco = getDeleted(dados.length);
        if (endereco == -1) {
            arquivo.seek(arquivo.length());
            endereco = arquivo.getFilePointer();
            arquivo.writeByte(' ');
            arquivo.writeShort(dados.length);
            arquivo.write(dados);
        } else {
            arquivo.seek(endereco);
            arquivo.writeByte(' ');
            arquivo.skipBytes(2);
            arquivo.write(dados);
        }
        return new long[]{ obj.getId(), endereco };
    }

    public T readPorPosicao(long posicao) throws Exception {
        arquivo.seek(posicao);
        byte lapide = arquivo.readByte();
        if (lapide == '*') return null;
        short tamanho = arquivo.readShort();
        byte[] dados = new byte[tamanho];
        arquivo.read(dados);
        T obj = construtor.newInstance();
        obj.fromByteArray(dados);
        return obj;
    }

    public long updateComPosicao(T novoObj, long posicaoAntiga) throws Exception {
        arquivo.seek(posicaoAntiga);
        byte lapide = arquivo.readByte();
        if (lapide == '*') return -1;
        short tamanhoAntigo = arquivo.readShort();
        byte[] novosDados = novoObj.toByteArray();
        short novoTam = (short) novosDados.length;

        if (novoTam <= tamanhoAntigo) {
            arquivo.seek(posicaoAntiga + 3);
            arquivo.write(novosDados);
            return posicaoAntiga;
        } else {
            arquivo.seek(posicaoAntiga);
            arquivo.writeByte('*');
            addDeleted(tamanhoAntigo, posicaoAntiga);

            long novoEndereco = getDeleted(novosDados.length);
            if (novoEndereco == -1) {
                arquivo.seek(arquivo.length());
                novoEndereco = arquivo.getFilePointer();
                arquivo.writeByte(' ');
                arquivo.writeShort(novoTam);
                arquivo.write(novosDados);
            } else {
                arquivo.seek(novoEndereco);
                arquivo.writeByte(' ');
                arquivo.skipBytes(2);
                arquivo.write(novosDados);
            }
            return novoEndereco;
        }
    }

    public boolean deleteAtPosition(long posicao) throws Exception {
        arquivo.seek(posicao);
        byte lapide = arquivo.readByte();
        if (lapide == '*') return false;
        short tamanho = arquivo.readShort();
        arquivo.seek(posicao);
        arquivo.writeByte('*');
        addDeleted(tamanho, posicao);
        return true;
    }

    // ==============================
    // MÉTODOS AUXILIARES
    // ==============================

    private void addDeleted(short tamanho, long posicao) throws Exception {
        arquivoFree.seek(0);
        int quantidade = arquivoFree.readInt();

        arquivoFree.seek(arquivoFree.length());
        arquivoFree.writeShort(tamanho);
        arquivoFree.writeLong(posicao);

        arquivoFree.seek(0);
        arquivoFree.writeInt(quantidade + 1);
    }

    private long getDeleted(int tamanhoNecessario) throws Exception {
        arquivoFree.seek(0);
        int quantidade = arquivoFree.readInt();

        for (int i = 0; i < quantidade; i++) {
            long posEntrada = 4 + (i * TAM_ENTRADA_FREE);
            arquivoFree.seek(posEntrada);
            short tamanhoLivre = arquivoFree.readShort();
            long posicaoLivre  = arquivoFree.readLong();

            if (tamanhoLivre >= tamanhoNecessario) {
                int ultimaPos = 4 + ((quantidade - 1) * TAM_ENTRADA_FREE);
                arquivoFree.seek(ultimaPos);
                short ultimoTam  = arquivoFree.readShort();
                long  ultimaPos2 = arquivoFree.readLong();

                arquivoFree.seek(posEntrada);
                arquivoFree.writeShort(ultimoTam);
                arquivoFree.writeLong(ultimaPos2);

                arquivoFree.setLength(arquivoFree.length() - TAM_ENTRADA_FREE);
                arquivoFree.seek(0);
                arquivoFree.writeInt(quantidade - 1);

                return posicaoLivre;
            }
        }
        return -1;
    }

    // ==============================
    // LISTAR e CLOSE
    // ==============================

    public List<T> listar() throws Exception {
        List<T> lista = new ArrayList<>();
        arquivo.seek(TAM_CABECALHO);

        while (arquivo.getFilePointer() < arquivo.length()) {
            byte lapide = arquivo.readByte();
            short tamanho = arquivo.readShort();
            byte[] dados = new byte[tamanho];
            arquivo.read(dados);

            if (lapide == ' ') {
                T obj = construtor.newInstance();
                obj.fromByteArray(dados);
                lista.add(obj);
            }
        }
        return lista;
    }

    public void close() throws Exception {
        arquivo.close();
        arquivoFree.close(); // ← corrigido
    }
}