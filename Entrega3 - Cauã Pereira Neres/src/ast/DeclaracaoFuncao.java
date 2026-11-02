package ast;
import java.util.*;
import visitor.Visitor;
public final class DeclaracaoFuncao extends Declaracao {
    public final TipoBasico tipoRetorno; public final String nome; public final List<Parametro> parametros; public final Bloco corpo;
    public DeclaracaoFuncao(TipoBasico tipoRetorno,String nome,List<Parametro> parametros,Bloco corpo,int linha,int coluna){super(linha,coluna);this.tipoRetorno=tipoRetorno;this.nome=nome;this.parametros=Collections.unmodifiableList(new ArrayList<>(parametros));this.corpo=corpo;}
    @Override public <T>T accept(Visitor<T> v){return v.visitDeclaracaoFuncao(this);}
}
