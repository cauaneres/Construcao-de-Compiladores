package ast;
import java.util.*;
import visitor.Visitor;
public final class ChamadaFuncao extends Expressao { public final String nome; public final List<Expressao> argumentos; public ChamadaFuncao(String nome,List<Expressao> argumentos,int linha,int coluna){super(linha,coluna);this.nome=nome;this.argumentos=Collections.unmodifiableList(new ArrayList<>(argumentos));} @Override public <T>T accept(Visitor<T> v){return v.visitChamadaFuncao(this);} }
