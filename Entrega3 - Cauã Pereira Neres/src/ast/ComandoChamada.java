package ast;
import visitor.Visitor;
public final class ComandoChamada extends Comando {
    public final ChamadaFuncao chamada;
    public ComandoChamada(ChamadaFuncao chamada,int linha,int coluna){super(linha,coluna);this.chamada=chamada;}
    @Override public <T>T accept(Visitor<T> v){return v.visitComandoChamada(this);}
}
