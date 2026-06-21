package stockit.seguranca;

import java.io.UnsupportedEncodingException;

/**
 * Criptografia simétrica por XOR (cifra de Vernam com chave repetida).
 *
 * Cada byte do dado é combinado com um byte da chave através da operação
 * bit-a-bit XOR (^). A chave é repetida ciclicamente ao longo do dado.
 *
 * Propriedade fundamental do XOR:  (D ^ K) ^ K == D
 * Ou seja, a MESMA operação cifra e decifra — basta aplicar duas vezes com a
 * mesma chave para recuperar o texto original. Por isso há um único método
 * `aplicar(...)` usado tanto para cifrar quanto para decifrar.
 *
 * É um método didático (não é criptografia forte), adequado para demonstrar a
 * proteção de um campo sensível na base de dados conforme pedido na Fase V.
 */
public final class XORCipher {

    /**
     * Chave de cifragem. Em um sistema real viria de configuração/segredo;
     * aqui é fixa para manter o exemplo autocontido e reprodutível.
     */
    private static final byte[] CHAVE = "StockIt-XOR-2024".getBytes();

    private XORCipher() {}

    /**
     * Aplica XOR byte-a-byte entre os dados e a chave (repetida ciclicamente).
     * Serve tanto para cifrar quanto para decifrar (operação involutiva).
     */
    public static byte[] aplicar(byte[] dados) {
        return aplicar(dados, CHAVE);
    }

    /** Versão com chave explícita (facilita testes). */
    public static byte[] aplicar(byte[] dados, byte[] chave) {
        if (dados == null) return null;
        if (chave == null || chave.length == 0) {
            throw new IllegalArgumentException("Chave XOR não pode ser vazia");
        }
        byte[] saida = new byte[dados.length];
        for (int i = 0; i < dados.length; i++) {
            saida[i] = (byte) (dados[i] ^ chave[i % chave.length]);
        }
        return saida;
    }

    /** Cifra uma String (UTF-8) e devolve os bytes cifrados. */
    public static byte[] cifrar(String texto) throws UnsupportedEncodingException {
        if (texto == null) return new byte[0];
        return aplicar(texto.getBytes("UTF-8"));
    }

    /** Decifra bytes cifrados de volta para String (UTF-8). */
    public static String decifrar(byte[] cifrado) throws UnsupportedEncodingException {
        if (cifrado == null) return "";
        return new String(aplicar(cifrado), "UTF-8");
    }

    /**
     * Cifra um texto e devolve o resultado como string hexadecimal
     * (ex.: "0C 10 1B 09 ..."), para visualização do conteúdo cifrado.
     */
    public static String cifrarParaHex(String texto) throws UnsupportedEncodingException {
        return paraHex(cifrar(texto));
    }

    /**
     * Decifra um texto a partir da sua representação hexadecimal
     * (aceita com ou sem espaços entre os bytes).
     */
    public static String decifrarDeHex(String hex) throws UnsupportedEncodingException {
        return decifrar(deHex(hex));
    }

    /** Converte bytes em string hexadecimal separada por espaços. */
    public static String paraHex(byte[] dados) {
        if (dados == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < dados.length; i++) {
            if (i > 0) sb.append(' ');
            sb.append(String.format("%02X", dados[i]));
        }
        return sb.toString();
    }

    /** Converte uma string hexadecimal (com ou sem espaços) em bytes. */
    public static byte[] deHex(String hex) {
        if (hex == null) return new byte[0];
        String limpo = hex.replaceAll("\\s+", "");
        if (limpo.length() % 2 != 0) {
            throw new IllegalArgumentException("Hex inválido: número ímpar de dígitos");
        }
        byte[] out = new byte[limpo.length() / 2];
        for (int i = 0; i < out.length; i++) {
            out[i] = (byte) Integer.parseInt(limpo.substring(i * 2, i * 2 + 2), 16);
        }
        return out;
    }
}
