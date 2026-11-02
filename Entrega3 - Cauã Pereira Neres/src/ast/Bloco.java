package ast;
import java.util.*;
import visitor.Visitor;
public final class Bloco extends Comando {
    public final List<ItemBloco> itens;
    public Bloco(List<ItemBloco> itens,int linha,int coluna){super(linha,coluna);this.itens=Collections.unmodifiableList(new ArrayList<>(itens));}
    @Override public <T>T accept(Visitor<T> v){return v.visitBloco(this);}
}
