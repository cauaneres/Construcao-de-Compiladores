package ast;
import visitor.Visitor;
public final class Retorno extends Comando {
    public final Expressao valor;
    public Retorno(Expressao valor,int linha,int coluna){super(linha,coluna);this.valor=valor;}
    @Override public <T>T accept(Visitor<T> v){return v.visitRetorno(this);}
}
