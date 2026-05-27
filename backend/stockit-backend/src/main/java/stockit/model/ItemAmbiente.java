package stockit.model;

import java.io.*;

// Tabela intermediaria N:N entre Alimento e Ambiente. PK composta: (alimentoId, ambienteId)
public class ItemAmbiente implements Registro {

    private int id;
    private int alimentoId;
    private int ambienteId;
    private short quantidade;
    private int dataCadastro;
    private int dataVencimento;

    public ItemAmbiente() {
        this(-1, -1, -1, (short) 0, 0, 0);
    }

    public ItemAmbiente(int alimentoId, int ambienteId, short quantidade, int dataCadastro, int dataVencimento) {
        this(0, alimentoId, ambienteId, quantidade, dataCadastro, dataVencimento);
    }

    public ItemAmbiente(int id, int alimentoId, int ambienteId, short quantidade, int dataCadastro, int dataVencimento) {
        this.id = id;
        this.alimentoId = alimentoId;
        this.ambienteId = ambienteId;
        this.quantidade = quantidade;
        this.dataCadastro = dataCadastro;
        this.dataVencimento = dataVencimento;
    }

    // Chave composta: alimentoId * 100000 + ambienteId
    public int getChaveComposta() {
        return alimentoId * 100000 + ambienteId;
    }

    @Override
    public void setId(int id) { this.id = id; }

    @Override
    public int getId() { return this.id; }

    public int getAlimentoId() { return alimentoId; }
    public void setAlimentoId(int alimentoId) { this.alimentoId = alimentoId; }

    public int getAmbienteId() { return ambienteId; }
    public void setAmbienteId(int ambienteId) { this.ambienteId = ambienteId; }

    public short getQuantidade() { return quantidade; }
    public void setQuantidade(short quantidade) { this.quantidade = quantidade; }

    public int getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(int dataCadastro) { this.dataCadastro = dataCadastro; }

    public int getDataVencimento() { return dataVencimento; }
    public void setDataVencimento(int dataVencimento) { this.dataVencimento = dataVencimento; }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.id);
        dos.writeInt(this.alimentoId);
        dos.writeInt(this.ambienteId);
        dos.writeShort(this.quantidade);
        dos.writeInt(this.dataCadastro);
        dos.writeInt(this.dataVencimento);
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);
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
               "\nChave Composta....: (" + this.alimentoId + "," + this.ambienteId + ")" +
               "\nQuantidade........: " + this.quantidade +
               "\nData de Cadastro..: " + this.dataCadastro +
               "\nData de Vencimento: " + this.dataVencimento;
    }
}
