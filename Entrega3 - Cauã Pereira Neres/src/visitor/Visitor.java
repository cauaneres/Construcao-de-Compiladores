package visitor;
import ast.*;
/** Um método por tipo concreto: o dispatch é feito por Node.accept(). */
public interface Visitor<T> {
    T visitPrograma(Programa n);
    T visitDeclaracaoVariavel(DeclaracaoVariavel n);
    T visitDeclaracaoFuncao(DeclaracaoFuncao n);
    T visitDeclaracaoProcedimento(DeclaracaoProcedimento n);
    T visitParametro(Parametro n);
    T visitBloco(Bloco n);
    T visitAtribuicao(Atribuicao n);
    T visitComandoChamada(ComandoChamada n);
    T visitSelecao(Selecao n);
    T visitRepeticao(Repeticao n);
    T visitRetorno(Retorno n);
    T visitLiteralInt(LiteralInt n);
    T visitLiteralDouble(LiteralDouble n);
    T visitLiteralBool(LiteralBool n);
    T visitLiteralChar(LiteralChar n);
    T visitLiteralString(LiteralString n);
    T visitIdentificador(Identificador n);
    T visitChamadaFuncao(ChamadaFuncao n);
    T visitOperacaoBinaria(OperacaoBinaria n);
    T visitOperacaoUnaria(OperacaoUnaria n);
}
