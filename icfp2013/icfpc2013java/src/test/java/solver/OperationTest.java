package solver;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Ignore;
import org.junit.Test;

import domain.Operator;

public class OperationTest {
    @Test
    @Ignore
    public void simpleTest() {
        // ( lambda (x) 1 )
        Operation program = new Operation(Operator.one, false);
        BigInteger []context = new BigInteger[] {
                BigInteger.TEN
        };
        assertEquals(BigInteger.ONE, program.evaluate(context));
    }

    @Test
    @Ignore
    public void testEvalProblem5() {
        // Program program = new Program("(lambda (x_4137) (shr4 (shr4 (shr1 x_4137))))");
        Operation prog = new Operation(Operator.lambda, false);
        Operation op1 = new Operation(Operator.shr4, false);
        Operation op2 = new Operation(Operator.shr4, false);
        Operation op3 = new Operation(Operator.shr1, false);
        Operation op4 = new Operation(Operator.id0, false);
        prog.setParam(0, op1);
        op1.setParam(0, op2);
        op2.setParam(0, op3);
        op3.setParam(0, op4);

        assertEquals("32A", prog.evaluate(new BigInteger[] {new BigInteger("65535", 16) }).toString(16).toUpperCase());
        assertEquals("21B", prog.evaluate(new BigInteger[] {new BigInteger("43690",16) }).toString(16).toUpperCase());
        assertEquals("214A4B39", prog.evaluate(new BigInteger[]{new BigInteger("4294967295",16)}).toString(16).toUpperCase());
        assertEquals("(lambda (a0) (shr4 (shr4 (shr1 a0))))", prog.toString());
    }

}
