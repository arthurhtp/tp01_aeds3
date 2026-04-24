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
            arquivo.writeInt(0); // Último ID usado
        }
    }

    public int create(T obj) throws Exception {
        arquivo.seek(0);
        int novoID = arquivo.readInt() + 1;
        arquivo.seek(0);
        arquivo.writeInt(novoID);
        obj.setId(novoID);
        byte[] dados = obj.toByteArray();

        arquivo.seek(arquivo.length());
        arquivo.writeByte(' '); // Lápide
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



    /** Cria registro e retorna {id, posicaoNoArquivo}. */
    public long[] createComPosicao(T obj) throws Exception {
        arquivo.seek(0);
        int novoID = arquivo.readInt() + 1;
        arquivo.seek(0);
        arquivo.writeInt(novoID);
        obj.setId(novoID);
        byte[] dados = obj.toByteArray();

        arquivo.seek(arquivo.length());
        long endereco = arquivo.getFilePointer();
        arquivo.writeByte(' ');
        arquivo.writeShort(dados.length);
        arquivo.write(dados);
        return new long[]{ obj.getId(), endereco };
    }

    /** Lê registro diretamente pela posição física no arquivo — O(1). */
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

    /** Atualiza registro a partir da posição conhecida; retorna a nova posição (pode mudar). */
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
            arquivo.seek(arquivo.length());
            long novoEndereco = arquivo.getFilePointer();
            arquivo.writeByte(' ');
            arquivo.writeShort(novoTam);
            arquivo.write(novosDados);
            return novoEndereco;
        }
    }

    /** Marca como excluído pela posição física — O(1). */
    public boolean deleteAtPosition(long posicao) throws Exception {
        arquivo.seek(posicao);
        byte lapide = arquivo.readByte();
        if (lapide == '*') return false;
        arquivo.seek(posicao);
        arquivo.writeByte('*');
        return true;
    }

    public void close() throws Exception {
        arquivo.close();
    }

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

}
