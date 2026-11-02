package ast;
import visitor.Visitor;
public final class OperacaoBinaria extends Expressao { public final OperadorBinario operador; public final Expressao esquerda; public final Expressao direita; public OperacaoBinaria(OperadorBinario operador,Expressao esquerda,Expressao direita,int linha,int coluna){super(linha,coluna);this.operador=operador;this.esquerda=esquerda;this.direita=direita;} @Override public <T>T accept(Visitor<T> v){return v.visitOperacaoBinaria(this);} }
