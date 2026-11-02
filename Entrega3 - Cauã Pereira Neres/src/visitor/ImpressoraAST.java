package visitor;

import ast.*;

/**
 * Impressora determinística: um nó por linha, dois espaços por nível.
 * Os atributos escalares aparecem entre parênteses; filhos aparecem abaixo,
 * na ordem em que são armazenados na AST. Filhos opcionais nulos são omitidos.
 */
public final class ImpressoraAST implements Visitor<Void> {
    private final StringBuilder saida = new StringBuilder();
    private int nivel;

    public String imprimir(Node raiz) {
        saida.setLength(0);
        nivel = 0;
        if (raiz != null) raiz.accept(this);
        return saida.toString();
    }

    private void linha(String texto) {
        saida.append("  ".repeat(nivel)).append(texto).append('\n');
    }

    private void filho(Node no) {
        if (no == null) return;
        nivel++;
        no.accept(this);
        nivel--;
    }

    @Override public Void visitPrograma(Programa n) {
        linha("Programa"); for (Declaracao d : n.declaracoes) filho(d); return null;
    }
    @Override public Void visitDeclaracaoVariavel(DeclaracaoVariavel n) {
        linha("DeclaracaoVariavel(" + n.tipo + " " + n.nome + ")"); filho(n.inicializador); return null;
    }
    @Override public Void visitDeclaracaoFuncao(DeclaracaoFuncao n) {
        linha("DeclaracaoFuncao(" + n.tipoRetorno + " " + n.nome + ")");
        for (Parametro p : n.parametros) filho(p); filho(n.corpo); return null;
    }
    @Override public Void visitDeclaracaoProcedimento(DeclaracaoProcedimento n) {
        linha("DeclaracaoProcedimento(" + n.nome + ")");
        for (Parametro p : n.parametros) filho(p); filho(n.corpo); return null;
    }
    @Override public Void visitParametro(Parametro n) { linha("Parametro(" + n.tipo + " " + n.nome + ")"); return null; }
    @Override public Void visitBloco(Bloco n) {
        linha("Bloco"); for (ItemBloco item : n.itens) filho((Node) item); return null;
    }
    @Override public Void visitAtribuicao(Atribuicao n) { linha("Atribuicao"); filho(n.alvo); filho(n.valor); return null; }
    @Override public Void visitComandoChamada(ComandoChamada n) { linha("ComandoChamada"); filho(n.chamada); return null; }
    @Override public Void visitSelecao(Selecao n) { linha("Selecao"); filho(n.condicao); filho(n.entao); filho(n.senao); return null; }
    @Override public Void visitRepeticao(Repeticao n) { linha("Repeticao"); filho(n.condicao); filho(n.corpo); return null; }
    @Override public Void visitRetorno(Retorno n) { linha("Retorno"); filho(n.valor); return null; }
    @Override public Void visitLiteralInt(LiteralInt n) { linha("LiteralInt(" + n.valor + ")"); return null; }
    @Override public Void visitLiteralDouble(LiteralDouble n) { linha("LiteralDouble(" + Double.toString(n.valor) + ")"); return null; }
    @Override public Void visitLiteralBool(LiteralBool n) { linha("LiteralBool(" + n.valor + ")"); return null; }
    @Override public Void visitLiteralChar(LiteralChar n) { linha("LiteralChar(" + (int)n.valor + ")"); return null; }
    @Override public Void visitLiteralString(LiteralString n) { linha("LiteralString(\"" + n.valor + "\")"); return null; }
    @Override public Void visitIdentificador(Identificador n) { linha("Identificador(" + n.nome + ")"); return null; }
    @Override public Void visitChamadaFuncao(ChamadaFuncao n) {
        linha("ChamadaFuncao(" + n.nome + ")"); for (Expressao a : n.argumentos) filho(a); return null;
    }
    @Override public Void visitOperacaoBinaria(OperacaoBinaria n) {
        linha("OperacaoBinaria(" + n.operador.simbolo() + ")"); filho(n.esquerda); filho(n.direita); return null;
    }
    @Override public Void visitOperacaoUnaria(OperacaoUnaria n) {
        linha("OperacaoUnaria(" + n.operador.simbolo() + ")"); filho(n.operando); return null;
    }
}
