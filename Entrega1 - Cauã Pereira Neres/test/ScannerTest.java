public class ScannerTest {

    public static void main(String[] args) {

        testeIdentificador();
        testePalavraReservada();
        testeString();
        testeOperador();
        testeNumero();

        testeStringEOF();
        testeStringFimDeLinha();
        testeCaractereInvalido();

        testeCodigoRealista();

        testeMaximalMunch();

        System.out.println();
        System.out.println("=================================");
        System.out.println("TODOS OS TESTES PASSARAM!");
        System.out.println("=================================");
    }


    // 1. Identificador
    private static void testeIdentificador() {

        Scanner scanner = new Scanner("idade");

        Token token = scanner.nextToken();

        assert token.getTipo() == TokenType.IDENTIFICADOR;
        assert token.getLexema().equals("idade");

        System.out.println("OK - Identificador");
    }


    // 2. Palavra reservada
    private static void testePalavraReservada() {

        Scanner scanner = new Scanner("int");

        Token token = scanner.nextToken();

        assert token.getTipo() == TokenType.PALAVRA_RESERVADA;
        assert token.getLexema().equals("int");

        System.out.println("OK - Palavra reservada");
    }


    // 3. String
    private static void testeString() {

        Scanner scanner = new Scanner("\"Olá mundo\"");

        Token token = scanner.nextToken();

        assert token.getTipo() == TokenType.STRING;
        assert token.getLexema().equals("\"Olá mundo\"");

        System.out.println("OK - String");
    }


    // 4. Operador
    private static void testeOperador() {

        Scanner scanner = new Scanner("==");

        Token token = scanner.nextToken();

        assert token.getTipo() == TokenType.OPERADOR;
        assert token.getLexema().equals("==");

        System.out.println("OK - Operador");
    }


    // 5. Literal numérico
    private static void testeNumero() {

        Scanner scanner = new Scanner("3.14");

        Token token = scanner.nextToken();

        assert token.getTipo() == TokenType.LITERAL_NUMERICO;
        assert token.getLexema().equals("3.14");

        System.out.println("OK - Literal numérico");
    }


    // 6. String não fechada até EOF
    private static void testeStringEOF() {

        Scanner scanner = new Scanner("\"Olá mundo");

        Token token = scanner.nextToken();

        // O importante neste teste é que o scanner
        // reporte o erro e não pare por exception.
        assert token != null;

        Token eof = scanner.nextToken();

        assert eof.getTipo() == TokenType.EOF;

        System.out.println("OK - String não fechada até EOF");
    }


    // 7. String não fechada até fim de linha
    private static void testeStringFimDeLinha() {

        Scanner scanner = new Scanner("\"Olá\nint idade");

        Token token = scanner.nextToken();

        assert token != null;

        Token proximo = scanner.nextToken();

        assert proximo.getTipo() == TokenType.PALAVRA_RESERVADA;
        assert proximo.getLexema().equals("int");

        System.out.println("OK - String não fechada até fim de linha");
    }


    // 8. Caractere fora do alfabeto
    private static void testeCaractereInvalido() {

        Scanner scanner = new Scanner("@ idade");

        Token token = scanner.nextToken();

        assert token.getTipo() == TokenType.IDENTIFICADOR;
        assert token.getLexema().equals("idade");

        System.out.println("OK - Caractere inválido + recuperação");
    }


    // 9. Código realista
    private static void testeCodigoRealista() {

        String codigo =
                "int idade = 20; // idade do aluno\n" +
                        "double media = 7.5;\n" +
                        "if (idade >= 18) { media = media + 1; }";

        Scanner scanner = new Scanner(codigo);

        int quantidadeTokens = 0;

        while (true) {

            Token token = scanner.nextToken();

            quantidadeTokens++;

            if (token.getTipo() == TokenType.EOF) {
                break;
            }
        }

        assert quantidadeTokens > 10;

        System.out.println("OK - Código realista");
    }

    private static void testeMaximalMunch() {

        Scanner scanner = new Scanner("== <= >= != && ||");

        String[] esperados = {
                "==",
                "<=",
                ">=",
                "!=",
                "&&",
                "||"
        };

        for (String esperado : esperados) {

            Token token = scanner.nextToken();

            assert token.getTipo() == TokenType.OPERADOR;
            assert token.getLexema().equals(esperado);
        }

        System.out.println("OK - Maximal munch");
    }
}

