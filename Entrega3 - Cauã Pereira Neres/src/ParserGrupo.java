import ast.*;
import java.util.ArrayList;
import java.util.List;

/** Parser recursivo descendente da Entrega 2, agora construindo a AST. */
public final class ParserGrupo extends ParserGrupoTemplate {

    private static final SyncSet FOLLOW_PROGRAMA =
            syncLexemesAndTypes(
                    new String[]{"void", "int", "double", "bool", "char", "string", ";", "}"},
                    TokenType.IDENTIFICADOR);
    private static final SyncSet FOLLOW_DECLARACAO =
            syncLexemesAndTypes(
                    new String[]{";", "}", "else", "return", "if", "while", "int", "double", "bool", "char", "string"},
                    TokenType.IDENTIFICADOR);
    private static final SyncSet FOLLOW_PARAMETROS = syncLexemes(")", "{", "}", ";");
    private static final SyncSet FOLLOW_FECHA_PARENTESE =
            syncLexemesAndTypes(
                    new String[]{"{", ";", "}", "else", "return", "if", "while", "int", "double", "bool", "char", "string"},
                    TokenType.IDENTIFICADOR);
    private static final SyncSet FOLLOW_BLOCO =
            syncLexemesAndTypes(
                    new String[]{"}", "else", "return", "if", "while", "{", "int", "double", "bool", "char", "string"},
                    TokenType.IDENTIFICADOR);
    private static final SyncSet FOLLOW_COMANDO =
            syncLexemesAndTypes(
                    new String[]{";", "}", "else", "return", "if", "while", "{", "int", "double", "bool", "char", "string"},
                    TokenType.IDENTIFICADOR);
    private static final SyncSet FOLLOW_EXPRESSAO = syncLexemes(";", ")", ",", "}", "else");
    private static final SyncSet FOLLOW_CONDICAO =
            syncLexemesAndTypes(
                    new String[]{")", "{", "}", "else", "return", "if", "while", "int", "double", "bool", "char", "string"},
                    TokenType.IDENTIFICADOR);
    private static final SyncSet FOLLOW_ELSE =
            syncLexemesAndTypes(
                    new String[]{"}", "return", "if", "while", "{", "int", "double", "bool", "char", "string"},
                    TokenType.IDENTIFICADOR);

    private Programa programa;

    public ParserGrupo(String fonte) { super(fonte); }

    /** AST construída durante parse(); será null até a primeira análise. */
    public Programa getPrograma() { return programa; }

    @Override
    protected void parsePrograma() {
        Token inicio = lookahead();
        List<Declaracao> declaracoes = new ArrayList<>();
        while (tokenType() != TokenType.EOF) {
            int antes = position();
            if (isInicioTipoRetorno()) {
                Declaracao declaracao = parseDeclaracaoTopo();
                if (declaracao != null) declaracoes.add(declaracao);
            } else {
                erroESincronizar("Programa", FOLLOW_PROGRAMA);
            }
            if (position() == antes && tokenType() != TokenType.EOF) advance();
        }
        programa = new Programa(declaracoes, inicio.getLinha(), inicio.getColuna());
    }

    private boolean isInicioTipoRetorno() { return isTipoVariavel() || isLexeme("void"); }
    private boolean isTipoVariavel() { return isOneOfLexemes("int", "double", "bool", "char", "string"); }

    private Declaracao parseDeclaracaoTopo() {
        Token inicio = lookahead();
        boolean tipoVoid = isLexeme("void");
        TipoBasico tipo = null;
        if (isInicioTipoRetorno()) {
            if (tipoVoid) advance();
            else tipo = parseTipo();
        } else {
            report("esperado um tipo de retorno");
            synchronize(FOLLOW_PROGRAMA);
            return null;
        }

        Token nome = lookahead();
        if (!matchType(TokenType.IDENTIFICADOR, FOLLOW_PROGRAMA)) return null;

        if (isLexeme("(")) {
            advance();
            List<Parametro> parametros = parseParametrosOpt();
            matchLexeme(")", FOLLOW_FECHA_PARENTESE);
            Bloco corpo = parseBloco();
            if (tipoVoid) return new DeclaracaoProcedimento(nome.getLexema(), parametros, corpo, inicio.getLinha(), inicio.getColuna());
            return new DeclaracaoFuncao(tipo, nome.getLexema(), parametros, corpo, inicio.getLinha(), inicio.getColuna());
        }

        if (tipoVoid) {
            report("procedimento void deve possuir lista de parâmetros entre parênteses");
            synchronize(FOLLOW_PROGRAMA);
            return null;
        }
        Expressao inicializador = parseInicializacaoOpt();
        matchLexeme(";", FOLLOW_PROGRAMA);
        return new DeclaracaoVariavel(tipo, nome.getLexema(), inicializador, inicio.getLinha(), inicio.getColuna());
    }

    private DeclaracaoVariavel parseDeclaracaoVariavel() {
        Token inicio = lookahead();
        TipoBasico tipo = parseTipo();
        Token nome = lookahead();
        if (!matchType(TokenType.IDENTIFICADOR, FOLLOW_DECLARACAO)) return null;
        Expressao inicializador = parseInicializacaoOpt();
        matchLexeme(";", FOLLOW_DECLARACAO);
        return new DeclaracaoVariavel(tipo, nome.getLexema(), inicializador, inicio.getLinha(), inicio.getColuna());
    }

    private TipoBasico parseTipo() {
        if (!isTipoVariavel()) {
            report("esperado um tipo: int, double, bool, char ou string");
            synchronize(FOLLOW_DECLARACAO);
            return TipoBasico.INT;
        }
        String lexema = lexeme();
        advance();
        switch (lexema) {
            case "double": return TipoBasico.DOUBLE;
            case "bool": return TipoBasico.BOOL;
            case "char": return TipoBasico.CHAR;
            case "string": return TipoBasico.STRING;
            default: return TipoBasico.INT;
        }
    }

    private Expressao parseInicializacaoOpt() {
        if (isLexeme("=")) {
            advance();
            return parseExpressao();
        } else if (isLexeme(";")) {
            epsilonOuRecuperar("InicializacaoOpt", syncLexemes(";"));
            return null;
        }
        report("esperado '=' ou ';' depois da declaração");
        synchronize(FOLLOW_DECLARACAO);
        return null;
    }

    private List<Parametro> parseParametrosOpt() {
        List<Parametro> parametros = new ArrayList<>();
        if (isLexeme(")")) {
            epsilonOuRecuperar("ParametrosOpt", syncLexemes(")"));
            return parametros;
        }
        if (!isTipoVariavel()) {
            report("esperado tipo de parâmetro ou ')'");
            synchronize(FOLLOW_PARAMETROS);
            return parametros;
        }
        Parametro primeiro = parseParametro();
        if (primeiro != null) parametros.add(primeiro);
        while (isLexeme(",")) {
            advance();
            if (isLexeme(")")) {
                report("esperado parâmetro depois da vírgula");
                return parametros;
            }
            if (!isTipoVariavel()) {
                report("esperado tipo de parâmetro depois da vírgula");
                synchronize(FOLLOW_PARAMETROS);
                return parametros;
            }
            Parametro parametro = parseParametro();
            if (parametro != null) parametros.add(parametro);
        }
        return parametros;
    }

    private Parametro parseParametro() {
        Token inicio = lookahead();
        TipoBasico tipo = parseTipo();
        Token nome = lookahead();
        if (!matchType(TokenType.IDENTIFICADOR, FOLLOW_PARAMETROS)) return null;
        return new Parametro(tipo, nome.getLexema(), inicio.getLinha(), inicio.getColuna());
    }

    private Bloco parseBloco() {
        Token inicio = lookahead();
        if (!matchLexeme("{", FOLLOW_BLOCO)) return new Bloco(new ArrayList<>(), inicio.getLinha(), inicio.getColuna());
        List<ItemBloco> itens = new ArrayList<>();
        while (tokenType() != TokenType.EOF && !isLexeme("}")) {
            int antes = position();
            ItemBloco item = null;
            if (isTipoVariavel()) {
                item = parseDeclaracaoVariavel();
            } else if (isLexeme("if")) {
                item = parseIf();
            } else if (isLexeme("while")) {
                item = parseWhile();
            } else if (isLexeme("return")) {
                item = parseRetorno();
            } else if (tokenType() == TokenType.IDENTIFICADOR) {
                item = parseComandoIdentificador();
            } else if (isLexeme("{")) {
                item = parseBloco();
            } else {
                erroESincronizar("Bloco/Comando", FOLLOW_COMANDO);
            }
            if (item != null) itens.add(item);
            if (position() == antes && tokenType() != TokenType.EOF && !isLexeme("}")) advance();
        }
        matchLexeme("}", FOLLOW_COMANDO);
        return new Bloco(itens, inicio.getLinha(), inicio.getColuna());
    }

    private Comando parseComandoIdentificador() {
        Token nome = lookahead();
        advance();
        if (isLexeme("=")) {
            advance();
            Expressao valor = parseExpressao();
            matchLexeme(";", FOLLOW_COMANDO);
            Identificador alvo = new Identificador(nome.getLexema(), nome.getLinha(), nome.getColuna());
            return new Atribuicao(alvo, valor, nome.getLinha(), nome.getColuna());
        }
        if (isLexeme("(")) {
            advance();
            List<Expressao> argumentos = parseArgumentosOpt();
            matchLexeme(")", FOLLOW_FECHA_PARENTESE);
            matchLexeme(";", FOLLOW_COMANDO);
            ChamadaFuncao chamada = new ChamadaFuncao(nome.getLexema(), argumentos, nome.getLinha(), nome.getColuna());
            return new ComandoChamada(chamada, nome.getLinha(), nome.getColuna());
        }
        report("esperado '=' ou '(' depois do identificador");
        synchronize(FOLLOW_COMANDO);
        if (isLexeme(";")) advance();
        return null;
    }

    private Selecao parseIf() {
        Token inicio = lookahead();
        matchLexeme("if", FOLLOW_CONDICAO);
        matchLexeme("(", FOLLOW_CONDICAO);
        Expressao condicao = parseExpressao();
        matchLexeme(")", FOLLOW_CONDICAO);
        Bloco entao = parseBloco();
        Bloco senao = null;
        if (isLexeme("else")) {
            advance();
            senao = parseBloco();
        } else {
            epsilonOuRecuperar("ElseOpt", FOLLOW_ELSE);
        }
        return new Selecao(condicao, entao, senao, inicio.getLinha(), inicio.getColuna());
    }

    private Repeticao parseWhile() {
        Token inicio = lookahead();
        matchLexeme("while", FOLLOW_CONDICAO);
        matchLexeme("(", FOLLOW_CONDICAO);
        Expressao condicao = parseExpressao();
        matchLexeme(")", FOLLOW_CONDICAO);
        Bloco corpo = parseBloco();
        return new Repeticao(condicao, corpo, inicio.getLinha(), inicio.getColuna());
    }

    private Retorno parseRetorno() {
        Token inicio = lookahead();
        matchLexeme("return", FOLLOW_COMANDO);
        Expressao valor;
        if (isLexeme(";")) {
            epsilonOuRecuperar("ExpressaoOpt", syncLexemes(";"));
            valor = null;
        } else {
            valor = parseExpressao();
        }
        matchLexeme(";", FOLLOW_COMANDO);
        return new Retorno(valor, inicio.getLinha(), inicio.getColuna());
    }

    private Expressao parseExpressao() { return parseDisjuncao(); }

    private Expressao parseDisjuncao() { return parseBinaria(this::parseConjuncao, "||"); }
    private Expressao parseConjuncao() { return parseBinaria(this::parseIgualdade, "&&"); }
    private Expressao parseIgualdade() { return parseBinaria(this::parseRelacional, "==", "!="); }
    private Expressao parseRelacional() { return parseBinaria(this::parseAditiva, "<", ">", "<=", ">="); }
    private Expressao parseAditiva() { return parseBinaria(this::parseMultiplicativa, "+", "-"); }
    private Expressao parseMultiplicativa() { return parseBinaria(this::parseUnaria, "*", "/", "%"); }

    @FunctionalInterface
    private interface ParserExpressao { Expressao parse(); }

    private Expressao parseBinaria(ParserExpressao proximo, String... operadores) {
        Token inicio = lookahead();
        Expressao acumulada = proximo.parse();
        while (isOneOfLexemes(operadores)) {
            String simbolo = lexeme();
            advance();
            Expressao direita = proximo.parse();
            acumulada = new OperacaoBinaria(operadorDe(simbolo), acumulada, direita,
                    inicio.getLinha(), inicio.getColuna());
        }
        return acumulada;
    }

    private OperadorBinario operadorDe(String s) {
        switch (s) {
            case "||": return OperadorBinario.OU;
            case "&&": return OperadorBinario.E;
            case "==": return OperadorBinario.IGUAL;
            case "!=": return OperadorBinario.DIFERENTE;
            case "<": return OperadorBinario.MENOR;
            case "<=": return OperadorBinario.MENOR_IGUAL;
            case ">": return OperadorBinario.MAIOR;
            case ">=": return OperadorBinario.MAIOR_IGUAL;
            case "+": return OperadorBinario.SOMA;
            case "-": return OperadorBinario.SUBTRACAO;
            case "*": return OperadorBinario.MULTIPLICACAO;
            case "/": return OperadorBinario.DIVISAO;
            default: return OperadorBinario.MODULO;
        }
    }

    private Expressao parseUnaria() {
        if (isOneOfLexemes("+", "-")) {
            Token operador = lookahead();
            advance();
            OperadorUnario op = operador.getLexema().equals("+") ? OperadorUnario.POSITIVO : OperadorUnario.NEGATIVO;
            return new OperacaoUnaria(op, parseUnaria(), operador.getLinha(), operador.getColuna());
        }
        return parsePrimaria();
    }

    private Expressao parsePrimaria() {
        Token t = lookahead();
        if (tokenType() == TokenType.LITERAL_NUMERICO) {
            advance();
            if (t.getLexema().contains(".")) return new LiteralDouble(Double.parseDouble(t.getLexema()), t.getLinha(), t.getColuna());
            return new LiteralInt(Integer.parseInt(t.getLexema()), t.getLinha(), t.getColuna());
        }
        if (tokenType() == TokenType.STRING) {
            advance();
            String valor = t.getLexema();
            if (valor.length() >= 2 && valor.charAt(0) == '"' && valor.charAt(valor.length() - 1) == '"') {
                valor = valor.substring(1, valor.length() - 1);
            }
            return new LiteralString(valor, t.getLinha(), t.getColuna());
        }
        if (isOneOfLexemes("true", "false")) {
            advance();
            return new LiteralBool(t.getLexema().equals("true"), t.getLinha(), t.getColuna());
        }
        if (tokenType() == TokenType.IDENTIFICADOR) {
            advance();
            if (isLexeme("(")) {
                advance();
                List<Expressao> argumentos = parseArgumentosOpt();
                matchLexeme(")", FOLLOW_EXPRESSAO);
                return new ChamadaFuncao(t.getLexema(), argumentos, t.getLinha(), t.getColuna());
            }
            return new Identificador(t.getLexema(), t.getLinha(), t.getColuna());
        }
        if (isLexeme("(")) {
            advance();
            Expressao expressao = parseExpressao();
            matchLexeme(")", FOLLOW_EXPRESSAO);
            return expressao;
        }
        report("esperada expressão; encontrado '" + lexeme() + "'");
        if (!isIn(FOLLOW_EXPRESSAO) && tokenType() != TokenType.EOF) synchronize(FOLLOW_EXPRESSAO);
        // Nó de recuperação: a análise já está marcada como inválida pelos erros registrados.
        return new LiteralInt(0, t.getLinha(), t.getColuna());
    }

    private List<Expressao> parseArgumentosOpt() {
        List<Expressao> argumentos = new ArrayList<>();
        if (isLexeme(")")) {
            epsilonOuRecuperar("ArgumentosOpt", syncLexemes(")"));
            return argumentos;
        }
        argumentos.add(parseExpressao());
        while (isLexeme(",")) {
            advance();
            if (isLexeme(")")) {
                report("esperado argumento depois da vírgula");
                return argumentos;
            }
            argumentos.add(parseExpressao());
        }
        return argumentos;
    }
}
