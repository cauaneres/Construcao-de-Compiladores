public abstract class ParserGrupoTemplate extends ParserBase {

    protected ParserGrupoTemplate(String fonte) {
        super(fonte);
    }

    protected final boolean epsilonOuRecuperar(String naoTerminal, SyncSet followA) {
        if (isIn(followA)) {
            return true;
        }

        report("não é possível escolher ε para <" + naoTerminal
                + ">: lookahead '" + lookahead().getLexema()
                + "' não pertence a FOLLOW(<" + naoTerminal + ">)");
        synchronize(followA);
        return false;
    }

    protected final void erroESincronizar(String naoTerminal, SyncSet followEAncoras) {
        report("token inesperado em <" + naoTerminal + ">: '" + lookahead().getLexema() + "'");
        synchronize(followEAncoras);
    }
}
