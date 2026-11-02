package ast;
import visitor.Visitor;
public final class LiteralBool extends Expressao { public final boolean valor; public LiteralBool(boolean valor,int linha,int coluna){super(linha,coluna);this.valor=valor;} @Override public <T>T accept(Visitor<T> v){return v.visitLiteralBool(this);} }
