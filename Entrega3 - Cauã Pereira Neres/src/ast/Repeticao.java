package ast;
import visitor.Visitor;
public final class Repeticao extends Comando {
    public final Expressao condicao; public final Bloco corpo;
    public Repeticao(Expressao condicao,Bloco corpo,int linha,int coluna){super(linha,coluna);this.condicao=condicao;this.corpo=corpo;}
    @Override public <T>T accept(Visitor<T> v){return v.visitRepeticao(this);}
}
