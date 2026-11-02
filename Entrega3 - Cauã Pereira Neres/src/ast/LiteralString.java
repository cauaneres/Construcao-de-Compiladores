package ast;
import visitor.Visitor;
public final class LiteralString extends Expressao { public final String valor; public LiteralString(String valor,int linha,int coluna){super(linha,coluna);this.valor=valor;} @Override public <T>T accept(Visitor<T> v){return v.visitLiteralString(this);} }
