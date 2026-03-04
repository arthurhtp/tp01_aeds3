package stockit.model;

import stockit.model.Registro;

import java.io.*;

public class Ambiente implements Registro {

    private int id;
    private String nome;
    private byte tipo;

    // Construtores
    public Ambiente() {
        this(-1, "", (byte) 0);
    }

    public Ambiente(String nome, byte tipo) {
        this(0, nome, tipo);
    }

    public Ambiente(int id, String nome, byte tipo) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public byte getTipo() {
        return tipo;
    }

    public void setTipo(byte tipo) {
        this.tipo = tipo;
    }

    // Serialização
    @Override
    public byte[] toByteArray() throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // ID (4 bytes)
        dos.writeInt(this.id);

        // Nome variável
        byte[] nomeBytes = this.nome.getBytes("UTF-8");
        dos.writeShort(nomeBytes.length);
        dos.write(nomeBytes);

        // Tipo (1 byte)
        dos.writeByte(this.tipo);

        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] b) throws IOException {

        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);

        // ID
        this.id = dis.readInt();

        // Nome variável
        short tamNome = dis.readShort();
        byte[] nomeBytes = new byte[tamNome];
        dis.readFully(nomeBytes);
        this.nome = new String(nomeBytes, "UTF-8");

        // Tipo
        this.tipo = dis.readByte();
    }

    @Override
    public String toString() {
        return "\nID........: " + this.id +
               "\nNome......: " + this.nome +
               "\nTipo......: " + this.tipo;
    }
}
