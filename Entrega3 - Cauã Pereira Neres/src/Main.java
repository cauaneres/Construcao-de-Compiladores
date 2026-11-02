import ast.*;
import visitor.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/** Executa o parser com construção da AST e demonstra os dois visitors. */
public class Main {
    public static void main(String[] args) throws Exception {
        String fonte;
        if (args.length > 0) fonte = new String(Files.readAllBytes(Paths.get(args[0])), StandardCharsets.UTF_8);
        else fonte = "int dobro(int n) { return n * 2; }\nvoid inicio() { int x = dobro(10); }";

        ParserGrupo parser = new ParserGrupo(fonte);
        ParserBase.ParseResult resultado = parser.parse();
        if (!resultado.accepted()) {
            System.out.println("Programa rejeitado:");
            for (String erro : resultado.errors()) System.out.println("- " + erro);
            System.exit(1);
        }

        Programa ast = parser.getPrograma();
        System.out.println("=== IMPRESSÃO DA AST ===");
        System.out.print(new ImpressoraAST().imprimir(ast));
        System.out.println("\n=== CÓDIGO-FONTE CANÔNICO ===");
        System.out.print(new ReimpressoraCodigo().imprimir(ast));
    }
}
