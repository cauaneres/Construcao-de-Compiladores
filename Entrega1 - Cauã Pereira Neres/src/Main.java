public class Main {

    public static void main(String[] args) {

        String codigo =
                "int idade = 20;\n" +
                        "double nota = 9.5;\n" +
                        "if (idade >= 18) {\n" +
                        "    // comentário\n" +
                        "    idade = idade + 1;\n" +
                        "}\n";

        Scanner scanner = new Scanner(codigo);

        Token token;

        do {

            token = scanner.nextToken();

            System.out.println(token);

        } while (token.getTipo() != TokenType.EOF);
    }
}
