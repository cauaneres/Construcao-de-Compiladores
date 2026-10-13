import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {
        String codigo;

        if (args.length > 0) {
            codigo = new String(Files.readAllBytes(Paths.get(args[0])), StandardCharsets.UTF_8);
        } else {
            codigo = "int idade = 20;\n"
                    + "double nota = 9.5;\n"
                    + "bool ativo = true;\n"
                    + "string nome = \"Ana\";\n";
            System.out.println("Nenhum arquivo informado; usando um exemplo de declarações.\n");
        }

        ParserGrupo parser = new ParserGrupo(codigo);
        ParserBase.ParseResult resultado = parser.parse();

        if (resultado.accepted()) {
            System.out.println("Programa aceito pela gramática implementada.");
        } else {
            System.out.println("Programa rejeitado. Erros sintáticos:");
            for (String erro : resultado.errors()) {
                System.out.println("- " + erro);
            }
        }
    }
}
