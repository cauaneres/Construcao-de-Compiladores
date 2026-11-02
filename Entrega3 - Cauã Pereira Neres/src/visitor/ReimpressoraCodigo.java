package visitor;

import ast.*;

/** Gera uma forma canônica da linguagem, com parênteses em toda operação. */
public final class ReimpressoraCodigo implements Visitor<String> {
    private int nivel;

    public String imprimir(Node raiz) {
        nivel = 0;
        return raiz == null ? "" : raiz.accept(this);
    }

    private String indentacao() { return "    ".repeat(nivel); }
    private static String tipo(TipoBasico t) {
        switch (t) {
            case INT: return "int";
            case DOUBLE: return "double";
            case BOOL: return "bool";
            case CHAR: return "char";
            case STRING: return "string";
            default: throw new IllegalStateException("Tipo não tratado: " + t);
        }
    }
    private static String escaparString(String s) {
        // A gramática/scanner atuais não definem escapes; preserva o conteúdo literal.
        return s;
    }
    private static String escaparChar(char c) {
        if (c == '\\') return "\\\\";
        if (c == '\'') return "\\'";
        if (c == '\n') return "\\n";
        if (c == '\r') return "\\r";
        if (c == '\t') return "\\t";
        return String.valueOf(c);
    }

    @Override public String visitPrograma(Programa n) {
        StringBuilder s = new StringBuilder();
        for (Declaracao d : n.declaracoes) s.append(d.accept(this)).append('\n');
        return s.toString();
    }
    @Override public String visitDeclaracaoVariavel(DeclaracaoVariavel n) {
        return indentacao() + tipo(n.tipo) + " " + n.nome
                + (n.inicializador == null ? "" : " = " + n.inicializador.accept(this)) + ";";
    }
    @Override public String visitDeclaracaoFuncao(DeclaracaoFuncao n) {
        StringBuilder s = new StringBuilder(indentacao()).append(tipo(n.tipoRetorno)).append(' ').append(n.nome).append('(');
        parametros(s, n.parametros);
        return s.append(") ").append(n.corpo.accept(this)).toString();
    }
    @Override public String visitDeclaracaoProcedimento(DeclaracaoProcedimento n) {
        StringBuilder s = new StringBuilder(indentacao()).append("void ").append(n.nome).append('(');
        parametros(s, n.parametros);
        return s.append(") ").append(n.corpo.accept(this)).toString();
    }
    private void parametros(StringBuilder s, java.util.List<Parametro> parametros) {
        for (int i = 0; i < parametros.size(); i++) {
            if (i > 0) s.append(", ");
            s.append(parametros.get(i).accept(this));
        }
    }
    @Override public String visitParametro(Parametro n) { return tipo(n.tipo) + " " + n.nome; }
    @Override public String visitBloco(Bloco n) {
        StringBuilder s = new StringBuilder("{\n");
        nivel++;
        for (ItemBloco item : n.itens) s.append(((Node)item).accept(this)).append('\n');
        nivel--;
        return s.append(indentacao()).append('}').toString();
    }
    @Override public String visitAtribuicao(Atribuicao n) {
        return indentacao() + n.alvo.accept(this) + " = " + n.valor.accept(this) + ";";
    }
    @Override public String visitComandoChamada(ComandoChamada n) { return indentacao() + n.chamada.accept(this) + ";"; }
    @Override public String visitSelecao(Selecao n) {
        String s = indentacao() + "if (" + n.condicao.accept(this) + ") " + n.entao.accept(this);
        if (n.senao != null) s += " else " + n.senao.accept(this);
        return s;
    }
    @Override public String visitRepeticao(Repeticao n) {
        return indentacao() + "while (" + n.condicao.accept(this) + ") " + n.corpo.accept(this);
    }
    @Override public String visitRetorno(Retorno n) {
        return indentacao() + "return" + (n.valor == null ? "" : " " + n.valor.accept(this)) + ";";
    }
    @Override public String visitLiteralInt(LiteralInt n) { return Integer.toString(n.valor); }
    @Override public String visitLiteralDouble(LiteralDouble n) {
        String s = Double.toString(n.valor); return s.indexOf('.') < 0 ? s + ".0" : s;
    }
    @Override public String visitLiteralBool(LiteralBool n) { return Boolean.toString(n.valor); }
    @Override public String visitLiteralChar(LiteralChar n) { return "'" + escaparChar(n.valor) + "'"; }
    @Override public String visitLiteralString(LiteralString n) { return "\"" + escaparString(n.valor) + "\""; }
    @Override public String visitIdentificador(Identificador n) { return n.nome; }
    @Override public String visitChamadaFuncao(ChamadaFuncao n) {
        StringBuilder s = new StringBuilder(n.nome).append('(');
        for (int i = 0; i < n.argumentos.size(); i++) {
            if (i > 0) s.append(", ");
            s.append(n.argumentos.get(i).accept(this));
        }
        return s.append(')').toString();
    }
    @Override public String visitOperacaoBinaria(OperacaoBinaria n) {
        return "(" + n.esquerda.accept(this) + " " + n.operador.simbolo() + " " + n.direita.accept(this) + ")";
    }
    @Override public String visitOperacaoUnaria(OperacaoUnaria n) {
        return "(" + n.operador.simbolo() + n.operando.accept(this) + ")";
    }
}
