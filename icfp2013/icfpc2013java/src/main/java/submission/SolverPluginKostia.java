package submission;

import domain.Identifier;
import domain.Operator;
import search.ConstraintStore;
import search.SearchEngine;
import search.SearchEngineMode;

import java.math.BigInteger;

public class SolverPluginKostia implements SolverPlugin {

    private String searchMode;

    public SolverPluginKostia(String searchMode) {
        this.searchMode = searchMode;
    }

    @Override
    public void doSearch(GuessVerifier verifier, String problemId, BigInteger[] inputs, BigInteger[] outputs, int size, Operator... ops) {
        ConstraintStore constraints = new ConstraintStore(Operator.toStrings(ops), inputs, outputs);
        SearchEngine engine = new SearchEngine(new Identifier("x"), verifier, SearchEngineMode.valueOf(searchMode));
        engine.search(size, constraints);
    }
}
