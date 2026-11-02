package ast;
import visitor.Visitor;
public final class LiteralInt extends Expressao { public final int valor; public LiteralInt(int valor,int linha,int coluna){super(linha,coluna);this.valor=valor;} @Override public <T>T accept(Visitor<T> v){return v.visitLiteralInt(this);} }
