/**
 * Parser recursivo descendente do grupo.
 *
 * Gramática implementada nesta etapa:
 *
 * Programa          ::= { DeclaracaoTopo } EOF
 * DeclaracaoTopo    ::= TipoRetorno IDENTIFICADOR SufixoDeclaracao
 * TipoRetorno       ::= Tipo | 'void'
 * SufixoDeclaracao  ::= '(' ParametrosOpt ')' Bloco
 *                     | InicializacaoOpt ';'
 * ParametrosOpt     ::= Parametro { ',' Parametro } | epsilon
 * Parametro         ::= Tipo IDENTIFICADOR
 * Tipo              ::= 'int' | 'double' | 'bool' | 'char' | 'string'
 * InicializacaoOpt  ::= '=' Expressao | epsilon
 *
 * Bloco             ::= '{' { DeclaracaoVariavel | Comando } '}'
 * DeclaracaoVariavel ::= Tipo IDENTIFICADOR InicializacaoOpt ';'
 * Comando           ::= Bloco
 *                     | 'if' '(' Expressao ')' Bloco ElseOpt
 *                     | 'while' '(' Expressao ')' Bloco
 *                     | 'return' ExpressaoOpt ';'
 *                     | IDENTIFICADOR SufixoComando
 * SufixoComando     ::= '=' Expressao ';'
 *                     | '(' ArgumentosOpt ')' ';'
 * ElseOpt           ::= 'else' Bloco | epsilon
 * ExpressaoOpt      ::= Expressao | epsilon
 * ArgumentosOpt     ::= Expressao { ',' Expressao } | epsilon
 *
 * Expressao         ::= Disjuncao
 * Disjuncao         ::= Conjuncao { '||' Conjuncao }
 * Conjuncao         ::= Igualdade { '&&' Igualdade }
 * Igualdade         ::= Relacional { ('==' | '!=') Relacional }
 * Relacional        ::= Aditiva { ('<' | '>' | '<=' | '>=') Aditiva }
 * Aditiva           ::= Multiplicativa { ('+' | '-') Multiplicativa }
 * Multiplicativa    ::= Unaria { ('*' | '/' | '%') Unaria }
 * Unaria            ::= ('+' | '-') Unaria | Primaria
 * Primaria          ::= LITERAL_NUMERICO | STRING | IDENTIFICADOR
 *                     | IDENTIFICADOR '(' ArgumentosOpt ')'
 *                     | '(' Expressao ')' | 'true' | 'false'
 *
 * Scanner.java, Token.java e TokenType.java permanecem sem alterações.
 */
public final class ParserGrupo extends ParserGrupoTemplate {

    private static final SyncSet FOLLOW_PROGRAMA =
            syncLexemesAndTypes(
                    new String[]{
                            "void", "int", "double", "bool", "char",
                            "string", ";", "}"
                    },
                    TokenType.IDENTIFICADOR
            );

    private static final SyncSet FOLLOW_DECLARACAO =
            syncLexemesAndTypes(
                    new String[]{
                            ";", "}", "else", "return", "if", "while",
                            "int", "double", "bool", "char", "string"
                    },
                    TokenType.IDENTIFICADOR
            );

    private static final SyncSet FOLLOW_PARAMETROS =
            syncLexemes(")", "{", "}", ";");

    private static final SyncSet FOLLOW_FECHA_PARENTESE =
            syncLexemesAndTypes(
                    new String[]{
                            "{", ";", "}", "else", "return", "if", "while",
                            "int", "double", "bool", "char", "string"
                    },
                    TokenType.IDENTIFICADOR
            );

    private static final SyncSet FOLLOW_BLOCO =
            syncLexemesAndTypes(
                    new String[]{
                            "}", "else", "return", "if", "while", "{",
                            "int", "double", "bool", "char", "string"
                    },
                    TokenType.IDENTIFICADOR
            );

    private static final SyncSet FOLLOW_COMANDO =
            syncLexemesAndTypes(
                    new String[]{
                            ";", "}", "else", "return", "if", "while", "{",
                            "int", "double", "bool", "char", "string"
                    },
                    TokenType.IDENTIFICADOR
            );

    private static final SyncSet FOLLOW_EXPRESSAO =
            syncLexemes(";", ")", ",", "}", "else");

    private static final SyncSet FOLLOW_CONDICAO =
            syncLexemesAndTypes(
                    new String[]{
                            ")", "{", "}", "else", "return", "if", "while",
                            "int", "double", "bool", "char", "string"
                    },
                    TokenType.IDENTIFICADOR
            );

    private static final SyncSet FOLLOW_ELSE =
            syncLexemesAndTypes(
                    new String[]{
                            "}", "return", "if", "while", "{",
                            "int", "double", "bool", "char", "string"
                    },
                    TokenType.IDENTIFICADOR
            );

    public ParserGrupo(String fonte) {
        super(fonte);
    }

    @Override
    protected void parsePrograma() {
        while (tokenType() != TokenType.EOF) {
            int antes = position();

            if (isInicioTipoRetorno()) {
                parseDeclaracaoTopo();
            } else {
                erroESincronizar("Programa", FOLLOW_PROGRAMA);
            }

            if (position() == antes && tokenType() != TokenType.EOF) {
                advance();
            }
        }
    }

    private boolean isInicioTipoRetorno() {
        return isTipoVariavel() || isLexeme("void");
    }

    private boolean isTipoVariavel() {
        return isOneOfLexemes(
                "int", "double", "bool", "char", "string"
        );
    }

    private void parseDeclaracaoTopo() {
        boolean tipoVoid = isLexeme("void");

        if (isInicioTipoRetorno()) {
            advance();
        } else {
            report("esperado um tipo de retorno");
            synchronize(FOLLOW_PROGRAMA);
            return;
        }

        if (!matchType(TokenType.IDENTIFICADOR, FOLLOW_PROGRAMA)) {
            return;
        }

        if (isLexeme("(")) {
            advance();

            parseParametrosOpt();

            matchLexeme(")", FOLLOW_FECHA_PARENTESE);

            parseBloco();
            return;
        }

        if (tipoVoid) {
            report("procedimento void deve possuir lista de parâmetros entre parênteses");
            synchronize(FOLLOW_PROGRAMA);
            return;
        }

        parseInicializacaoOpt();
        matchLexeme(";", FOLLOW_PROGRAMA);
    }

    private void parseDeclaracaoVariavel() {
        parseTipo();

        if (!matchType(TokenType.IDENTIFICADOR, FOLLOW_DECLARACAO)) {
            return;
        }

        parseInicializacaoOpt();

        matchLexeme(";", FOLLOW_DECLARACAO);
    }

    private void parseTipo() {
        if (isTipoVariavel()) {
            advance();
        } else {
            report("esperado um tipo: int, double, bool, char ou string");
            synchronize(FOLLOW_DECLARACAO);
        }
    }

    private void parseInicializacaoOpt() {
        if (isLexeme("=")) {
            advance();
            parseExpressao();
        } else if (isLexeme(";")) {
            // epsilon só é válido quando o lookahead pertence a FOLLOW.
            epsilonOuRecuperar(
                    "InicializacaoOpt",
                    syncLexemes(";")
            );
        } else {
            report("esperado '=' ou ';' depois da declaração");
            synchronize(FOLLOW_DECLARACAO);
        }
    }

    private void parseParametrosOpt() {
        if (isLexeme(")")) {
            epsilonOuRecuperar(
                    "ParametrosOpt",
                    syncLexemes(")")
            );
            return;
        }

        if (!isTipoVariavel()) {
            report("esperado tipo de parâmetro ou ')'");
            synchronize(FOLLOW_PARAMETROS);
            return;
        }

        parseParametro();

        while (isLexeme(",")) {
            advance();

            if (isLexeme(")")) {
                report("esperado parâmetro depois da vírgula");
                return;
            }

            if (!isTipoVariavel()) {
                report("esperado tipo de parâmetro depois da vírgula");
                synchronize(FOLLOW_PARAMETROS);
                return;
            }

            parseParametro();
        }
    }

    private void parseParametro() {
        parseTipo();
        matchType(TokenType.IDENTIFICADOR, FOLLOW_PARAMETROS);
    }

    private void parseBloco() {
        if (!matchLexeme("{", FOLLOW_BLOCO)) {
            return;
        }

        while (tokenType() != TokenType.EOF && !isLexeme("}")) {
            int antes = position();

            if (isTipoVariavel()) {
                parseDeclaracaoVariavel();
            } else if (isLexeme("if")) {
                parseIf();
            } else if (isLexeme("while")) {
                parseWhile();
            } else if (isLexeme("return")) {
                parseRetorno();
            } else if (tokenType() == TokenType.IDENTIFICADOR) {
                parseComandoIdentificador();
            } else if (isLexeme("{")) {
                parseBloco();
            } else {
                erroESincronizar("Bloco/Comando", FOLLOW_COMANDO);
            }

            // Não consome '}', pois ela encerra este bloco.
            if (position() == antes
                    && tokenType() != TokenType.EOF
                    && !isLexeme("}")) {
                advance();
            }
        }

        matchLexeme("}", FOLLOW_COMANDO);
    }

    private void parseComandoIdentificador() {
        advance(); // IDENTIFICADOR

        if (isLexeme("=")) {
            advance();
            parseExpressao();
            matchLexeme(";", FOLLOW_COMANDO);
            return;
        }

        if (isLexeme("(")) {
            advance();

            parseArgumentosOpt();

            matchLexeme(")", FOLLOW_FECHA_PARENTESE);
            matchLexeme(";", FOLLOW_COMANDO);
            return;
        }

        report("esperado '=' ou '(' depois do identificador");
        synchronize(FOLLOW_COMANDO);

        if (isLexeme(";")) {
            advance();
        }
    }

    private void parseIf() {
        matchLexeme("if", FOLLOW_CONDICAO);
        matchLexeme("(", FOLLOW_CONDICAO);

        parseExpressao();

        matchLexeme(")", FOLLOW_CONDICAO);

        parseBloco();

        if (isLexeme("else")) {
            advance();
            parseBloco();
        } else {
            epsilonOuRecuperar("ElseOpt", FOLLOW_ELSE);
        }
    }

    private void parseWhile() {
        matchLexeme("while", FOLLOW_CONDICAO);
        matchLexeme("(", FOLLOW_CONDICAO);

        parseExpressao();

        matchLexeme(")", FOLLOW_CONDICAO);

        parseBloco();
    }

    private void parseRetorno() {
        matchLexeme("return", FOLLOW_COMANDO);

        if (isLexeme(";")) {
            epsilonOuRecuperar(
                    "ExpressaoOpt",
                    syncLexemes(";")
            );
        } else {
            parseExpressao();
        }

        matchLexeme(";", FOLLOW_COMANDO);
    }

    private void parseExpressao() {
        parseDisjuncao();
    }

    private void parseDisjuncao() {
        parseConjuncao();

        while (isLexeme("||")) {
            advance();
            parseConjuncao();
        }
    }

    private void parseConjuncao() {
        parseIgualdade();

        while (isLexeme("&&")) {
            advance();
            parseIgualdade();
        }
    }

    private void parseIgualdade() {
        parseRelacional();

        while (isOneOfLexemes("==", "!=")) {
            advance();
            parseRelacional();
        }
    }

    private void parseRelacional() {
        parseAditiva();

        while (isOneOfLexemes("<", ">", "<=", ">=")) {
            advance();
            parseAditiva();
        }
    }

    private void parseAditiva() {
        parseMultiplicativa();

        while (isOneOfLexemes("+", "-")) {
            advance();
            parseMultiplicativa();
        }
    }

    private void parseMultiplicativa() {
        parseUnaria();

        while (isOneOfLexemes("*", "/", "%")) {
            advance();
            parseUnaria();
        }
    }

    private void parseUnaria() {
        if (isOneOfLexemes("+", "-")) {
            advance();
            parseUnaria();
        } else {
            parsePrimaria();
        }
    }

    private void parsePrimaria() {
        if (tokenType() == TokenType.LITERAL_NUMERICO
                || tokenType() == TokenType.STRING) {
            advance();
            return;
        }

        // O scanner do CP1 reconhece true/false como identificadores.
        if (isOneOfLexemes("true", "false")) {
            advance();
            return;
        }

        if (tokenType() == TokenType.IDENTIFICADOR) {
            advance();

            if (isLexeme("(")) {
                advance();
                parseArgumentosOpt();
                matchLexeme(")", FOLLOW_EXPRESSAO);
            }

            return;
        }

        if (isLexeme("(")) {
            advance();
            parseExpressao();
            matchLexeme(")", FOLLOW_EXPRESSAO);
            return;
        }

        report("esperada expressão; encontrado '" + lexeme() + "'");

        /*
         * Se o lookahead já pertence ao FOLLOW da expressão, preserva-o
         * para que o chamador consuma ';', ')', ',' ou '}'.
         */
        if (!isIn(FOLLOW_EXPRESSAO) && tokenType() != TokenType.EOF) {
            synchronize(FOLLOW_EXPRESSAO);
        }
    }

    private void parseArgumentosOpt() {
        if (isLexeme(")")) {
            epsilonOuRecuperar(
                    "ArgumentosOpt",
                    syncLexemes(")")
            );
            return;
        }

        parseExpressao();

        while (isLexeme(",")) {
            advance();

            if (isLexeme(")")) {
                report("esperado argumento depois da vírgula");
                return;
            }

            parseExpressao();
        }
    }
}