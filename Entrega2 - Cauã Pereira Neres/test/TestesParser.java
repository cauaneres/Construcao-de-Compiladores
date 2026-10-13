import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class TestesParser {
    private static final long LIMITE_SEGUNDOS = 2;

    @FunctionalInterface
    public interface FabricaParser {
        ParserBase criar(String fonte);
    }

    private TestesParser() { }

    public static void executar(FabricaParser fabrica) throws Exception {
        int total = 0;

        deveAceitar(fabrica, "V1 - tipos básicos",
                "void tipos() {\n" +
                "  int idade;\n" +
                "  double media;\n" +
                "  bool aprovado;\n" +
                "  char inicial;\n" +
                "  string nome;\n" +
                "  return;\n" +
                "}");
        total++;

        deveAceitar(fabrica, "V2 - controle de fluxo",
                "void controle() {\n" +
                "  int contador;\n" +
                "  contador = 0;\n" +
                "  while (contador < 3) {\n" +
                "    if (contador < 2) {\n" +
                "      contador = contador + 1;\n" +
                "    } else {\n" +
                "      contador = contador + 1;\n" +
                "    }\n" +
                "  }\n" +
                "  return;\n" +
                "}");
        total++;

        deveAceitar(fabrica, "V3 - função, procedimento e chamadas",
                "int somar(int a, int b) {\n" +
                "  return a + b;\n" +
                "}\n" +
                "void mostrar(string texto) {\n" +
                "  return;\n" +
                "}\n" +
                "int principal() {\n" +
                "  int resultado;\n" +
                "  resultado = somar(2, 3);\n" +
                "  mostrar(\"ok\");\n" +
                "  return resultado;\n" +
                "}");
        total++;

        deveRejeitar(fabrica, "I1 - ponto e vírgula ausente",
                "void erro1() {\n" +
                "  int idade\n" +
                "  return;\n" +
                "}");
        total++;

        deveRejeitar(fabrica, "I2 - parêntese de fechamento ausente",
                "void erro2() {\n" +
                "  int x;\n" +
                "  if (x < 10 {\n" +
                "    x = 1;\n" +
                "  }\n" +
                "  return;\n" +
                "}");
        total++;

        deveRejeitar(fabrica, "I3 - lista de parâmetros incompleta",
                "int somar(int a, ) {\n" +
                "  return a;\n" +
                "}");
        total++;

        deveReportarMultiplosErros(fabrica, "I4 - múltiplos erros e recuperação",
                "int principal() {\n" +
                "  int x;\n" +
                "  x = ;\n" +
                "  if (x < ) {\n" +
                "    x = 1;\n" +
                "  }\n" +
                "  return x;\n" +
                "}");
        total++;

        System.out.println("Suíte concluída: " + total + " cenários executados.");
    }

    private static ParserBase.ParseResult analisarComLimite(
            FabricaParser fabrica, String fonte) throws Exception {
        FutureTask<ParserBase.ParseResult> tarefa = new FutureTask<>(
                () -> fabrica.criar(fonte).parse());
        Thread thread = new Thread(tarefa, "teste-parser-timeout");
        thread.setDaemon(true);
        thread.start();
        try {
            return tarefa.get(LIMITE_SEGUNDOS, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            tarefa.cancel(true);
            throw new AssertionError("O parser não terminou em "
                    + LIMITE_SEGUNDOS + " segundos; possível loop infinito.", e);
        }
    }

    private static void deveAceitar(FabricaParser fabrica, String nome,
                                    String fonte) throws Exception {
        ParserBase.ParseResult resultado = analisarComLimite(fabrica, fonte);
        if (!resultado.accepted()) {
            throw new AssertionError(nome + " deveria ser aceito, mas teve erros:\n"
                    + formatarErros(resultado));
        }
        System.out.println("[OK] " + nome + " — aceito");
    }

    private static void deveRejeitar(FabricaParser fabrica, String nome,
                                     String fonte) throws Exception {
        ParserBase.ParseResult resultado = analisarComLimite(fabrica, fonte);
        if (resultado.accepted()) {
            throw new AssertionError(nome + " deveria ser rejeitado, mas foi aceito.");
        }
        verificarPosicaoDosErros(nome, resultado);
        System.out.println("[OK] " + nome + " — rejeitado com "
                + resultado.errors().size() + " erro(s)");
        System.out.println(formatarErros(resultado));
    }

    private static void deveReportarMultiplosErros(FabricaParser fabrica,
                                                    String nome, String fonte)
            throws Exception {
        ParserBase.ParseResult resultado = analisarComLimite(fabrica, fonte);
        if (resultado.accepted()) {
            throw new AssertionError(nome + " deveria ser rejeitado.");
        }
        if (resultado.errors().size() < 2) {
            throw new AssertionError(nome + " deveria reportar pelo menos dois erros, "
                    + "mas reportou " + resultado.errors().size() + ":\n"
                    + formatarErros(resultado));
        }
        verificarPosicaoDosErros(nome, resultado);
        System.out.println("[OK] " + nome + " — "
                + resultado.errors().size() + " erros na mesma análise");
        System.out.println(formatarErros(resultado));
    }

    private static void verificarPosicaoDosErros(String nome,
                                                  ParserBase.ParseResult resultado) {
        for (String erro : resultado.errors()) {
            if (!erro.contains("linha") || !erro.contains("coluna")) {
                throw new AssertionError(nome + ": diagnóstico sem linha/coluna: " + erro);
            }
        }
    }

    private static String formatarErros(ParserBase.ParseResult resultado) {
        StringBuilder sb = new StringBuilder();
        for (String erro : resultado.errors()) {
            sb.append("  - ").append(erro).append(System.lineSeparator());
        }
        return sb.toString();
    }
}
