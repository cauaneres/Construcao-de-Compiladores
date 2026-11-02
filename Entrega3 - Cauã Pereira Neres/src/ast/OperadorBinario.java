package ast;
public enum OperadorBinario {
    OU("||"), E("&&"), IGUAL("=="), DIFERENTE("!="), MENOR("<"), MENOR_IGUAL("<="),
    MAIOR(">"), MAIOR_IGUAL(">="), SOMA("+"), SUBTRACAO("-"),
    MULTIPLICACAO("*"), DIVISAO("/"), MODULO("%");
    private final String simbolo;
    OperadorBinario(String simbolo) { this.simbolo = simbolo; }
    public String simbolo() { return simbolo; }
}
