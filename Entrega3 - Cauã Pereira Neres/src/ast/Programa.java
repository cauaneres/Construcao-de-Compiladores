package ast;
import java.util.*;
import visitor.Visitor;
public final class Programa extends Node {
    public final List<Declaracao> declaracoes;
    public Programa(List<Declaracao> declaracoes, int linha, int coluna) { super(linha,coluna); this.declaracoes=Collections.unmodifiableList(new ArrayList<>(declaracoes)); }
    @Override public <T>T accept(Visitor<T> v){ return v.visitPrograma(this); }
}
