import java.util.HashSet;
import java.util.Set;

public class Scanner {

    private final String fonte;
    private int posicao;
    private int linha;
    private int coluna;

    private final Set<String> palavrasReservadas;


    public Scanner(String fonte) {
        this.fonte = fonte;
        this.posicao = 0;
        this.linha = 1;
        this.coluna = 1;

        palavrasReservadas = new HashSet<>();

        palavrasReservadas.add("int");
        palavrasReservadas.add("double");
        palavrasReservadas.add("bool");
        palavrasReservadas.add("char");
        palavrasReservadas.add("string");
        palavrasReservadas.add("if");
        palavrasReservadas.add("else");
        palavrasReservadas.add("while");
        palavrasReservadas.add("return");
        palavrasReservadas.add("void");
        palavrasReservadas.add("float");
    }

    public boolean hasNext() {
        return posicao < fonte.length();
    }

    public char peek() {
        if (!hasNext()) {
            return '\0';
        }

        return fonte.charAt(posicao);
    }

    public char advance() {

        if (!hasNext()) {
            return '\0';
        }

        char c = fonte.charAt(posicao);

        posicao++;

        if (c == '\n') {
            linha++;
            coluna = 1;
        } else {
            coluna++;
        }

        return c;
    }

    public Token nextToken() {

        ignorarEspacosEComentarios();

        if (!hasNext()) {
            return new Token(
                    TokenType.EOF,
                    "",
                    linha,
                    coluna
            );
        }


        int linhaInicial = linha;
        int colunaInicial = coluna;

        char c = peek();

        if (ehLetra(c)) {
            return lerIdentificador(
                    linhaInicial,
                    colunaInicial
            );
        }

        if (ehDigito(c)) {
            return lerNumero(
                    linhaInicial,
                    colunaInicial
            );
        }

        if (c == '"') {
            return lerString(
                    linhaInicial,
                    colunaInicial
            );
        }

        if (ehInicioDeOperador(c)) {
            return lerOperador(
                    linhaInicial,
                    colunaInicial
            );
        }

        if (ehDelimitador(c)) {

            char delimitador = advance();

            return new Token(
                    TokenType.DELIMITADOR,
                    String.valueOf(delimitador),
                    linhaInicial,
                    colunaInicial
            );
        }

        reportarErro(
                linhaInicial,
                colunaInicial,
                "Caractere inválido: '" + c + "'"
        );

        advance();

        return nextToken();
    }

    private Token lerIdentificador(
            int linhaInicial,
            int colunaInicial) {

        // q0 -> q1: o primeiro caractere deve ser uma letra
        StringBuilder lexema = new StringBuilder();

        if (ehLetra(peek())) {
            lexema.append(advance());
        }

        // q1 -> q1: continua enquanto for letra, dígito ou _
        while (ehLetra(peek()) || ehDigito(peek()) || peek() == '_') {
            lexema.append(advance());
        }

        // Ao terminar, q1 é um estado final.
        // Agora verificamos se o lexema é palavra reservada.
        String texto = lexema.toString();

        if (palavrasReservadas.contains(texto)) {
            return new Token(
                    TokenType.PALAVRA_RESERVADA,
                    texto,
                    linhaInicial,
                    colunaInicial
            );
        }

        return new Token(
                TokenType.IDENTIFICADOR,
                texto,
                linhaInicial,
                colunaInicial
        );
    }

    private Token lerNumero(
            int linhaInicial,
            int colunaInicial) {

        StringBuilder lexema = new StringBuilder();

        while (hasNext() && ehDigito(peek())) {
            lexema.append(advance());
        }

        if (hasNext() && peek() == '.') {

            if (posicao + 1 < fonte.length()
                    && ehDigito(fonte.charAt(posicao + 1))) {

                lexema.append(advance());

                while (hasNext() && ehDigito(peek())) {
                    lexema.append(advance());
                }
            }
        }


        return new Token(
                TokenType.LITERAL_NUMERICO,
                lexema.toString(),
                linhaInicial,
                colunaInicial
        );
    }

    private Token lerString(
            int linhaInicial,
            int colunaInicial) {

        StringBuilder lexema = new StringBuilder();

        // q0 -> q1: encontra a aspas inicial
        if (peek() == '"') {
            lexema.append(advance());
        }

        // q1: estamos dentro da string
        while (hasNext() && peek() != '"' && peek() != '\n') {
            lexema.append(advance());
        }

        // q1 -> q2: encontra a aspas final
        if (peek() == '"') {
            lexema.append(advance());

            return new Token(
                    TokenType.STRING,
                    lexema.toString(),
                    linhaInicial,
                    colunaInicial
            );
        }

        // Se chegou aqui, q1 terminou de maneira inválida:
        // EOF ou fim de linha antes da aspas final.
        reportarErro(
                linhaInicial,
                colunaInicial,
                "String não fechada"
        );

        return new Token(
                TokenType.STRING,
                lexema.toString(),
                linhaInicial,
                colunaInicial
        );
    }

    private Token recuperarString() {

        while (hasNext() && peek() != '\n') {
            advance();
        }

        return nextToken();
    }

    private Token lerOperador(
            int linhaInicial,
            int colunaInicial) {

        char primeiro = peek();


        if (primeiro == '=') {

            advance();

            if (peek() == '=') {
                advance();

                return novoOperador(
                        "==",
                        linhaInicial,
                        colunaInicial
                );
            }

            return novoOperador(
                    "=",
                    linhaInicial,
                    colunaInicial
            );
        }


        if (primeiro == '<') {

            advance();

            if (peek() == '=') {
                advance();

                return novoOperador(
                        "<=",
                        linhaInicial,
                        colunaInicial
                );
            }

            return novoOperador(
                    "<",
                    linhaInicial,
                    colunaInicial
            );
        }


        if (primeiro == '>') {

            advance();

            if (peek() == '=') {
                advance();

                return novoOperador(
                        ">=",
                        linhaInicial,
                        colunaInicial
                );
            }

            return novoOperador(
                    ">",
                    linhaInicial,
                    colunaInicial
            );
        }


        if (primeiro == '!') {

            advance();

            if (peek() == '=') {
                advance();

                return novoOperador(
                        "!=",
                        linhaInicial,
                        colunaInicial
                );
            }

            reportarErro(
                    linhaInicial,
                    colunaInicial,
                    "Operador '!' não é válido isoladamente"
            );

            return nextToken();
        }


        if (primeiro == '&') {

            advance();

            if (peek() == '&') {
                advance();

                return novoOperador(
                        "&&",
                        linhaInicial,
                        colunaInicial
                );
            }

            reportarErro(
                    linhaInicial,
                    colunaInicial,
                    "Operador '&' não é válido isoladamente"
            );

            return nextToken();
        }


        if (primeiro == '|') {

            advance();

            if (peek() == '|') {
                advance();

                return novoOperador(
                        "||",
                        linhaInicial,
                        colunaInicial
                );
            }

            reportarErro(
                    linhaInicial,
                    colunaInicial,
                    "Operador '|' não é válido isoladamente"
            );

            return nextToken();
        }

        if (primeiro == '+'
                || primeiro == '-'
                || primeiro == '*'
                || primeiro == '/'
                || primeiro == '%') {

            advance();

            return novoOperador(
                    String.valueOf(primeiro),
                    linhaInicial,
                    colunaInicial
            );
        }

        reportarErro(
                linhaInicial,
                colunaInicial,
                "Operador inválido"
        );

        advance();

        return nextToken();
    }


    private Token novoOperador(
            String operador,
            int linha,
            int coluna) {

        return new Token(
                TokenType.OPERADOR,
                operador,
                linha,
                coluna
        );
    }

    private void ignorarEspacosEComentarios() {

        boolean encontrouAlgo;


        do {

            encontrouAlgo = false;


            /*
             * Espaços.
             */
            while (hasNext() && Character.isWhitespace(peek())) {
                advance();
                encontrouAlgo = true;
            }

            if (peek() == '/'
                    && proximoCaractere() == '/') {

                encontrouAlgo = true;

                advance();
                advance();


                while (hasNext() && peek() != '\n') {
                    advance();
                }
            }

            if (peek() == '/'
                    && proximoCaractere() == '*') {

                encontrouAlgo = true;

                ignorarComentarioBloco();
            }

        } while (encontrouAlgo);
    }


    private void ignorarComentarioBloco() {

        int linhaInicial = linha;
        int colunaInicial = coluna;

        advance();
        advance();


        while (hasNext()) {

            if (peek() == '*'
                    && proximoCaractere() == '/') {

                advance();
                advance();

                return;
            }

            advance();
        }

        reportarErro(
                linhaInicial,
                colunaInicial,
                "Comentário de bloco não fechado até EOF"
        );
    }

    private char proximoCaractere() {

        if (posicao + 1 >= fonte.length()) {
            return '\0';
        }

        return fonte.charAt(posicao + 1);
    }

    private boolean ehLetra(char c) {

        return (c >= 'a' && c <= 'z')
                || (c >= 'A' && c <= 'Z');
    }

    private boolean ehDigito(char c) {

        return c >= '0' && c <= '9';
    }

    private boolean ehInicioDeOperador(char c) {

        return c == '+'
                || c == '-'
                || c == '*'
                || c == '/'
                || c == '%'
                || c == '='
                || c == '<'
                || c == '>'
                || c == '!'
                || c == '&'
                || c == '|';
    }

    private boolean ehDelimitador(char c) {

        return c == '('
                || c == ')'
                || c == '{'
                || c == '}'
                || c == ';'
                || c == ',';
    }

    private void reportarErro(
            int linha,
            int coluna,
            String mensagem) {

        System.err.println(
                "Erro léxico na linha "
                        + linha
                        + ", coluna "
                        + coluna
                        + ": "
                        + mensagem
        );
    }
}