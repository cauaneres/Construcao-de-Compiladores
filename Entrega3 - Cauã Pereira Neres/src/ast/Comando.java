package ast;
public abstract class Comando extends Node implements ItemBloco {
    protected Comando(int linha, int coluna) { super(linha, coluna); }
}
