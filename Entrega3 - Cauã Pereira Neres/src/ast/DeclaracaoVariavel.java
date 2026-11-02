package ast;
import visitor.Visitor;
public final class DeclaracaoVariavel extends Declaracao implements ItemBloco {
    public final TipoBasico tipo; public final String nome; public final Expressao inicializador;
    public DeclaracaoVariavel(TipoBasico tipo,String nome,Expressao inicializador,int linha,int coluna){super(linha,coluna);this.tipo=tipo;this.nome=nome;this.inicializador=inicializador;}
    @Override public <T>T accept(Visitor<T> v){return v.visitDeclaracaoVariavel(this);}
}
