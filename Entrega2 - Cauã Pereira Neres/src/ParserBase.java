import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

public abstract class ParserBase {
    private final Scanner scanner;
    private Token lookahead;
    private int position;
    private final List<String> errors = new ArrayList<>();

    protected ParserBase(String fonte) {
        this.scanner = new Scanner(Objects.requireNonNull(fonte, "fonte não pode ser null"));
        this.lookahead = scanner.nextToken();
        this.position = 0;
    }

    public final ParseResult parse() {
        parsePrograma();
        if (lookahead.getTipo() != TokenType.EOF) {
            report("tokens inesperados após o fim do programa: '" + display(lookahead) + "'");
            while (lookahead.getTipo() != TokenType.EOF) {
                advance();
            }
        }
        return new ParseResult(errors);
    }

    protected abstract void parsePrograma();

    protected final Token lookahead() { return lookahead; }
    protected final String lexeme() { return lookahead.getLexema(); }
    protected final TokenType tokenType() { return lookahead.getTipo(); }
    protected final int position() { return position; }

    protected final boolean isLexeme(String expected) {
        return lookahead.getLexema().equals(expected);
    }

    protected final boolean isOneOfLexemes(String... expected) {
        for (String item : expected) {
            if (isLexeme(item)) return true;
        }
        return false;
    }

    protected final boolean isType(TokenType expected) {
        return lookahead.getTipo() == expected;
    }

    protected final boolean isIn(SyncSet set) {
        return set != null && set.contains(lookahead);
    }

    protected final void advance() {
        if (lookahead.getTipo() != TokenType.EOF) {
            lookahead = scanner.nextToken();
            position++;
        }
    }

    protected final boolean matchLexeme(String expected, SyncSet synchronization) {
        if (isLexeme(expected)) {
            advance();
            return true;
        }

        report("esperado '" + expected + "', encontrado '" + display(lookahead) + "'");
        recoverUntil(t -> t.getLexema().equals(expected) || isIn(synchronization));

        if (isLexeme(expected)) {
            advance();
            return true;
        }
        return false;
    }

    protected final boolean matchType(TokenType expected, SyncSet synchronization) {
        if (isType(expected)) {
            advance();
            return true;
        }

        report("esperado token " + expected + ", encontrado " + lookahead.getTipo()
                + " ('" + display(lookahead) + "')");
        recoverUntil(t -> t.getTipo() == expected || isIn(synchronization));

        if (isType(expected)) {
            advance();
            return true;
        }
        return false;
    }

    protected final void synchronize(SyncSet synchronization) {
        if (synchronization == null) return;
        while (lookahead.getTipo() != TokenType.EOF && !synchronization.contains(lookahead)) {
            advance();
        }
    }

    protected final void ensureProgress(int positionBefore, SyncSet saidasValidas) {
        if (lookahead.getTipo() != TokenType.EOF
                && position == positionBefore
                && !isIn(saidasValidas)) {
            advance();
        }
    }

    protected final void ensureProgress(int positionBefore) {
        ensureProgress(positionBefore, null);
    }

    protected final void report(String message) {
        errors.add("Erro sintático na linha " + lookahead.getLinha()
                + ", coluna " + lookahead.getColuna() + ": " + message);
    }

    protected final List<String> errorsSoFar() {
        return Collections.unmodifiableList(errors);
    }

    private void recoverUntil(Predicate<Token> stopCondition) {
        while (lookahead.getTipo() != TokenType.EOF && !stopCondition.test(lookahead)) {
            advance();
        }
    }

    private static String display(Token token) {
        return token.getTipo() == TokenType.EOF ? "EOF" : token.getLexema();
    }

    protected static final class SyncSet {
        private final Set<String> lexemes;
        private final Set<TokenType> types;

        private SyncSet(Set<String> lexemes, Set<TokenType> types) {
            this.lexemes = Collections.unmodifiableSet(new LinkedHashSet<>(lexemes));
            this.types = Collections.unmodifiableSet(new LinkedHashSet<>(types));
        }

        private boolean contains(Token token) {
            return lexemes.contains(token.getLexema()) || types.contains(token.getTipo());
        }

        public SyncSet union(SyncSet other) {
            Set<String> combinedLexemes = new LinkedHashSet<>(lexemes);
            Set<TokenType> combinedTypes = new LinkedHashSet<>(types);
            combinedLexemes.addAll(other.lexemes);
            combinedTypes.addAll(other.types);
            return new SyncSet(combinedLexemes, combinedTypes);
        }
    }

    protected static SyncSet syncLexemes(String... lexemes) {
        return new SyncSet(new LinkedHashSet<>(Arrays.asList(lexemes)), new LinkedHashSet<>());
    }

    protected static SyncSet syncTypes(TokenType... types) {
        return new SyncSet(new LinkedHashSet<>(), new LinkedHashSet<>(Arrays.asList(types)));
    }

    protected static SyncSet syncLexemesAndTypes(String[] lexemes, TokenType... types) {
        return new SyncSet(new LinkedHashSet<>(Arrays.asList(lexemes)), new LinkedHashSet<>(Arrays.asList(types)));
    }

    public static final class ParseResult {
        private final List<String> errors;

        private ParseResult(List<String> errors) {
            this.errors = Collections.unmodifiableList(new ArrayList<>(errors));
        }

        public boolean accepted() { return errors.isEmpty(); }
        public List<String> errors() { return errors; }
    }
}
