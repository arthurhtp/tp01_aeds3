package stockit.model;

import stockit.model.Registro;

import java.io.*;

public class CategoriaAlimento implements Registro {

    private int id;
    private String nome;

    // Construtores
    public CategoriaAlimento() {
        this(-1, "");
    }

    public CategoriaAlimento(String nome) {
        this(0, nome);
    }

    public CategoriaAlimento(int id, String nome) {
        this.id = id;
        this.nome = nome;
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

    // Serialização
    @Override
    public byte[] toByteArray() throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // ID
        dos.writeInt(this.id);

        // Nome
        byte[] nomeBytes = this.nome.getBytes("UTF-8");
        dos.writeShort(nomeBytes.length);
        dos.write(nomeBytes);

        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] b) throws IOException {

        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);

        // ID
        this.id = dis.readInt();

        // Nome
        short tamNome = dis.readShort();
        byte[] nomeBytes = new byte[tamNome];
        dis.readFully(nomeBytes);
        this.nome = new String(nomeBytes, "UTF-8");
    }

    @Override
    public String toString() {
        return "\nID........: " + this.id +
               "\nNome......: " + this.nome;
    }
}