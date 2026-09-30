public class Token {

    private TokenType tipo;
    private String lexema;
    private int linha;
    private int coluna;

    public Token(TokenType tipo, String lexema, int linha, int coluna) {
        this.tipo = tipo;
        this.lexema = lexema;
        this.linha = linha;
        this.coluna = coluna;
    }

    public TokenType getTipo() {
        return tipo;
    }

    public String getLexema() {
        return lexema;
    }

    public int getLinha() {
        return linha;
    }

    public int getColuna() {
        return coluna;
    }

    @Override
    public String toString() {
        return "Token{" +
                "tipo=" + tipo +
                ", lexema='" + lexema + '\'' +
                ", linha=" + linha +
                ", coluna=" + coluna +
                '}';
    }
}