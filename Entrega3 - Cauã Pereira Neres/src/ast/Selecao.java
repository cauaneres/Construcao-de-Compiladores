package ast;
import visitor.Visitor;
public final class Selecao extends Comando {
    public final Expressao condicao; public final Bloco entao; public final Bloco senao;
    public Selecao(Expressao condicao,Bloco entao,Bloco senao,int linha,int coluna){super(linha,coluna);this.condicao=condicao;this.entao=entao;this.senao=senao;}
    @Override public <T>T accept(Visitor<T> v){return v.visitSelecao(this);}
}
