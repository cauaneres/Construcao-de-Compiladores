package ast;
import visitor.Visitor;
public final class LiteralDouble extends Expressao { public final double valor; public LiteralDouble(double valor,int linha,int coluna){super(linha,coluna);this.valor=valor;} @Override public <T>T accept(Visitor<T> v){return v.visitLiteralDouble(this);} }
