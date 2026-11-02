package ast;
import visitor.Visitor;
/** Nó previsto no projeto; o scanner/gramática atuais ainda não produzem literais char. */
public final class LiteralChar extends Expressao { public final char valor; public LiteralChar(char valor,int linha,int coluna){super(linha,coluna);this.valor=valor;} @Override public <T>T accept(Visitor<T> v){return v.visitLiteralChar(this);} }
