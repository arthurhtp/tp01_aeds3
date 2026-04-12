package stockit.model;

import java.io.*;

public class ItemAmbiente implements Registro {

    private int id;
    private int alimentoId; // FK para Alimento
    private int ambienteId; // FK para Ambiente
    private short quantidade;
    private int dataCadastro;
    private int dataVencimento;

    // Construtor vazio (necessário para o método getConstructor no DAO)
    public ItemAmbiente() {
        this(-1, -1, -1, (short) 0, 0, 0);
    }

    // Construtor sem ID (usado na criação de um novo registro antes de salvar)
    public ItemAmbiente(int alimentoId, int ambienteId, short quantidade, int dataCadastro, int dataVencimento) {
        this(0, alimentoId, ambienteId, quantidade, dataCadastro, dataVencimento);
    }

    // Construtor completo
    public ItemAmbiente(int id, int alimentoId, int ambienteId, short quantidade, int dataCadastro, int dataVencimento) {
        this.id = id;
        this.alimentoId = alimentoId;
        this.ambienteId = ambienteId;
        this.quantidade = quantidade;
        this.dataCadastro = dataCadastro;
        this.dataVencimento = dataVencimento;
    }

    // Getters e Setters
    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return this.id;
    }

    public int getAlimentoId() {
        return alimentoId;
    }

    public void setAlimentoId(int alimentoId) {
        this.alimentoId = alimentoId;
    }

    public int getAmbienteId() {
        return ambienteId;
    }

    public void setAmbienteId(int ambienteId) {
        this.ambienteId = ambienteId;
    }

    public short getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(short quantidade) {
        this.quantidade = quantidade;
    }

    public int getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(int dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public int getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(int dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    // Serialização (Transformar o objeto em um vetor de bytes)
    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // Escrevendo os atributos na ordem especificada [cite: 110, 173, 174]
        dos.writeInt(this.id);
        dos.writeInt(this.alimentoId);
        dos.writeInt(this.ambienteId);
        dos.writeShort(this.quantidade);
        dos.writeInt(this.dataCadastro);
        dos.writeInt(this.dataVencimento);

        return baos.toByteArray();
    }

    // Desserialização (Transformar o vetor de bytes de volta no objeto)
    @Override
    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);

        // Lendo os atributos na mesma ordem que foram escritos
        this.id = dis.readInt();
        this.alimentoId = dis.readInt();
        this.ambienteId = dis.readInt();
        this.quantidade = dis.readShort();
        this.dataCadastro = dis.readInt();
        this.dataVencimento = dis.readInt();
    }

    @Override
    public String toString() {
        return "\nID................: " + this.id +
               "\nID do Alimento....: " + this.alimentoId +
               "\nID do Ambiente....: " + this.ambienteId +
               "\nQuantidade........: " + this.quantidade +
               "\nData de Cadastro..: " + this.dataCadastro +
               "\nData de Vencimento: " + this.dataVencimento;
    }
}