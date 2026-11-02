package ast;
public enum OperadorUnario { POSITIVO("+"), NEGATIVO("-");
    private final String simbolo;
    OperadorUnario(String simbolo) { this.simbolo = simbolo; }
    public String simbolo() { return simbolo; }
}
