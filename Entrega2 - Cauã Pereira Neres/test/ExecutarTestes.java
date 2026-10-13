public final class ExecutarTestes {
    private ExecutarTestes() { }

    public static void main(String[] args) throws Exception {
        TestesParser.executar(ParserGrupo::new);
    }
}
