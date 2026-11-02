package ast;
import visitor.Visitor;
public final class OperacaoUnaria extends Expressao { public final OperadorUnario operador; public final Expressao operando; public OperacaoUnaria(OperadorUnario operador,Expressao operando,int linha,int coluna){super(linha,coluna);this.operador=operador;this.operando=operando;} @Override public <T>T accept(Visitor<T> v){return v.visitOperacaoUnaria(this);} }
