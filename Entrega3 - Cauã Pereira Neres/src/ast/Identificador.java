package ast;
import visitor.Visitor;
public final class Identificador extends Expressao { public final String nome; public Identificador(String nome,int linha,int coluna){super(linha,coluna);this.nome=nome;} @Override public <T>T accept(Visitor<T> v){return v.visitIdentificador(this);} }
