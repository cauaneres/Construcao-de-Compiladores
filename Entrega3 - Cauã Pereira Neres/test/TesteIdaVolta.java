import ast.Programa;
import visitor.ReimpressoraCodigo;

/** Teste de ida e volta: fonte -> parser/AST -> código canônico -> parser/AST. */
public final class TesteIdaVolta {
    public static void main(String[] args) {
        String fonte = "int somar(int n) {\n"
                + "  int soma = 0;\n"
                + "  int i = 1;\n"
                + "  while (i <= n) {\n"
                + "    if (i % 2 == 0) { soma = soma + i; }\n"
                + "    i = i + 1;\n"
                + "  }\n"
                + "  return soma;\n"
                + "}\n"
                + "void principal() { int x = somar(10); }";

        ParserGrupo primeiro = new ParserGrupo(fonte);
        ParserBase.ParseResult r1 = primeiro.parse();
        exigir(r1.accepted(), "A fonte original foi rejeitada: " + r1.errors());
        Programa ast1 = primeiro.getPrograma();

        String canonico = new ReimpressoraCodigo().imprimir(ast1);
        ParserGrupo segundo = new ParserGrupo(canonico);
        ParserBase.ParseResult r2 = segundo.parse();
        exigir(r2.accepted(), "O código reimpresso foi rejeitado: " + r2.errors() + "\n" + canonico);
        exigir(segundo.getPrograma() != null, "A segunda AST não foi construída.");

        String canonico2 = new ReimpressoraCodigo().imprimir(segundo.getPrograma());
        exigir(canonico.equals(canonico2), "A reimpressão não foi determinística.");
        System.out.println("OK - ida e volta concluída.");
        System.out.println("\nCódigo canônico:\n" + canonico);
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
}
