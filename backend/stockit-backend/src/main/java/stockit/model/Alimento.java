package stockit.model;

import stockit.model.Registro;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Alimento implements Registro {

    private int id;
    private String nome;
    private List<String> rotulos;
    private int idCategoriaAlimento;

    // Construtores
    public Alimento() {
        this(-1, "", new ArrayList<>(), -1);
    }

    public Alimento(String nome, List<String> rotulos, int idCategoriaAlimento) {
        this(0, nome, rotulos, idCategoriaAlimento);
    }

    public Alimento(int id, String nome, List<String> rotulos, int idCategoriaAlimento) {
        this.id = id;
        this.nome = nome;
        this.rotulos = rotulos != null ? rotulos : new ArrayList<>();
        this.idCategoriaAlimento = idCategoriaAlimento;
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

    public List<String> getRotulos() {
        return rotulos;
    }

    public void setRotulos(List<String> rotulos) {
        this.rotulos = rotulos;
    }

    public int getIdCategoriaAlimento() {
        return idCategoriaAlimento;
    }

    public void setIdCategoriaAlimento(int idCategoriaAlimento) {
        this.idCategoriaAlimento = idCategoriaAlimento;
    }

    // Serialização
    @Override
    public byte[] toByteArray() throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // ID (4 bytes)
        dos.writeInt(this.id);

        // Nome (variável)
        byte[] nomeBytes = this.nome.getBytes("UTF-8");
        dos.writeShort(nomeBytes.length);
        dos.write(nomeBytes);

        // Rótulos: quantidade + cada string (variável)
        dos.writeShort(this.rotulos.size());
        for (String rotulo : this.rotulos) {
            byte[] rotuloBytes = rotulo.getBytes("UTF-8");
            dos.writeShort(rotuloBytes.length);
            dos.write(rotuloBytes);
        }

        // ID da Categoria (4 bytes)
        dos.writeInt(this.idCategoriaAlimento);

        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] b) throws IOException {

        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);

        // ID
        this.id = dis.readInt();

        // Nome (variável)
        short tamNome = dis.readShort(); 
        byte[] nomeBytes = new byte[tamNome];
        dis.readFully(nomeBytes);
        this.nome = new String(nomeBytes, "UTF-8");

        // Rótulos
        short qtdRotulos = dis.readShort();
        this.rotulos = new ArrayList<>();
        for (int i = 0; i < qtdRotulos; i++) {
            short tamRotulo = dis.readShort();
            byte[] rotuloBytes = new byte[tamRotulo];
            dis.readFully(rotuloBytes);
            this.rotulos.add(new String(rotuloBytes, "UTF-8"));
        }

        // ID da Categoria
        this.idCategoriaAlimento = dis.readInt();
    }

    @Override
    public String toString() {
        return "\nID................: " + this.id +
               "\nNome..............: " + this.nome +
               "\nRótulos...........: " + this.rotulos +
               "\nID Categoria......: " + this.idCategoriaAlimento;
    }

}