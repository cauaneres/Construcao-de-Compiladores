import ast.*;
import visitor.*;
import java.util.Arrays;

public final class TesteVisitors {
    public static void main(String[] args) {
        Expressao expr = new OperacaoBinaria(OperadorBinario.SUBTRACAO,
                new OperacaoBinaria(OperadorBinario.SUBTRACAO,
                        new Identificador("a",1,1), new Identificador("b",1,5),1,1),
                new OperacaoBinaria(OperadorBinario.MULTIPLICACAO,
                        new Identificador("c",1,9), new Identificador("d",1,13),1,9),1,1);
        String arvore = new ImpressoraAST().imprimir(expr);
        String codigo = new ReimpressoraCodigo().imprimir(expr);
        if (!codigo.equals("((a - b) - (c * d))")) throw new AssertionError(codigo);
        if (!arvore.contains("OperacaoBinaria(-)")) throw new AssertionError(arvore);
        System.out.println("OK - visitors executados.");
        System.out.println(arvore);
        System.out.println(codigo);
    }
}
