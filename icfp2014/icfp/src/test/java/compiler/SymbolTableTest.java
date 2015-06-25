package compiler;

import org.junit.Before;
import org.junit.Test;
import parser.ParsingException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 *
 */
public class SymbolTableTest {

    private SymbolTable st;

    @Before
    public void createST() {
        st = new SymbolTable();
    }

    @Test
    public void addFrame() {
        st.pushFrame("a", "b", "c");
        SymbolTable.VarPtr vp = st.getVariable("a", 0);
        assertThat(vp.frame, is(0));
        assertThat(vp.index, is(0));
        vp = st.getVariable("c", 0);
        assertThat(vp.frame, is(0));
        assertThat(vp.index, is(2));
    }

    @Test
    public void addFrameLocal() {
        st.pushFrame("a", "b", "c");
        st.pushFrame("d", "e", "f");
        SymbolTable.VarPtr vp = st.getVariable("a", 0);
        assertThat(vp.frame, is(1));
        assertThat(vp.index, is(0));
        vp = st.getVariable("e", 0);
        assertThat(vp.frame, is(0));
        assertThat(vp.index, is(1));
    }

    @Test
    public void removeFrame() {
        st.pushFrame("a", "b", "c");
        st.pushFrame("d", "e", "f");
        st.popFrame();
        SymbolTable.VarPtr vp = st.getVariable("a", 0);
        assertThat(vp.frame, is(0));
        assertThat(vp.index, is(0));
        vp = st.getVariable("c", 0);
        assertThat(vp.frame, is(0));
        assertThat(vp.index, is(2));
    }

    @Test(expected = ParsingException.class)
    public void getNonExistent() {
        st.pushFrame("a", "b", "c");
        SymbolTable.VarPtr vp = st.getVariable("d", 0);
    }

    @Test(expected = ParsingException.class)
    public void removeVar() {
        st.pushFrame("a", "b", "c");
        st.pushFrame("d", "e", "f");
        st.popFrame();
        SymbolTable.VarPtr vp = st.getVariable("d", 0);
    }
}
