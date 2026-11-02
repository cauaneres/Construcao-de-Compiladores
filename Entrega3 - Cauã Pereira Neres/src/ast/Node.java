package ast;
import visitor.Visitor;
/** Raiz da hierarquia da AST. A posição é a do primeiro token que originou o nó. */
public abstract class Node {
    public final int linha;
    public final int coluna;
    protected Node(int linha, int coluna) { this.linha = linha; this.coluna = coluna; }
    public abstract <T> T accept(Visitor<T> visitor);
}
