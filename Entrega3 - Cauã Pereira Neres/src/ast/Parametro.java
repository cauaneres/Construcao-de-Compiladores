package ast;
import visitor.Visitor;
public final class Parametro extends Declaracao {
    public final TipoBasico tipo; public final String nome;
    public Parametro(TipoBasico tipo,String nome,int linha,int coluna){super(linha,coluna);this.tipo=tipo;this.nome=nome;}
    @Override public <T>T accept(Visitor<T> v){return v.visitParametro(this);}
}
