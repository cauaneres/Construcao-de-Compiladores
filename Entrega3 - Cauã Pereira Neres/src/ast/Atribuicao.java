package ast;
import visitor.Visitor;
public final class Atribuicao extends Comando {
    public final Identificador alvo; public final Expressao valor;
    public Atribuicao(Identificador alvo,Expressao valor,int linha,int coluna){super(linha,coluna);this.alvo=alvo;this.valor=valor;}
    @Override public <T>T accept(Visitor<T> v){return v.visitAtribuicao(this);}
}
