package ast;
import java.util.*;
import visitor.Visitor;
public final class DeclaracaoProcedimento extends Declaracao {
    public final String nome; public final List<Parametro> parametros; public final Bloco corpo;
    public DeclaracaoProcedimento(String nome,List<Parametro> parametros,Bloco corpo,int linha,int coluna){super(linha,coluna);this.nome=nome;this.parametros=Collections.unmodifiableList(new ArrayList<>(parametros));this.corpo=corpo;}
    @Override public <T>T accept(Visitor<T> v){return v.visitDeclaracaoProcedimento(this);}
}
