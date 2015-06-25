package util;

import frontend.Problem;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

/**
 * Misc functions
 */
public class Util {
    private Util() {}

    private static Random random = new Random();

    private static final int DATA_PACK_SIZE = 256;

    public static Problem getProblemById(String id) {
        Problem[] problems = getAllProblems();
        for(Problem problem : problems) {
            if (id.equals(problem.getId())) return problem;
        }
        throw new RuntimeException("Problem can't be found in file");
    }

    public static Problem[] getAllProblems() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(Util.class.getResourceAsStream("/tasks/problems.json"), Problem[].class);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to fetch task from file");
        }
    }

    public static Problem[] getAllProblems(String file) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(new File(file), Problem[].class);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to fetch task from file");
        }
    }

    public static String[] getTestDataPack() {
        String[] testData = new String[DATA_PACK_SIZE];
        int index = 0;
        String[] walk = getWalkingBitData();
        System.arraycopy(walk, 0, testData, index, walk.length);
        index += walk.length;
        String[] ladder = getLadderData();
        System.arraycopy(ladder, 0, testData, index, ladder.length);
        index += ladder.length;
        String[] revLadder = getReverseLadderData();
        System.arraycopy(revLadder, 0, testData, index, revLadder.length);
        index += revLadder.length;
        String[] randomData = getRandomTestData(DATA_PACK_SIZE-index);
        System.arraycopy(randomData, 0, testData, index, randomData.length);
        return testData;
    }
    
    public static void main(String[] args) {
        System.out.println(Arrays.toString(getTestDataPack()));
    }

    // reverse conversion is used (!) don't reuse code from here
    public static String getRandomNumber() {
        byte[] buf = new byte[8];
        random.nextBytes(buf);
        StringBuilder result = new StringBuilder(64);
        for(byte data : buf) {
            result.append((char)encodeHalf(data&0xf));
            result.append((char)encodeHalf((data>>4)&0xf));
        }
        return result.toString();
    }

    public static String[] getRandomTestData(int size) {
        String[] data = new String[size];
        for(int i=0;i<size;i++) {
            data[i] = getRandomNumber();
        }
        return data;
    }

    public static String[] getWalkingBitData() {
        String[] result = new String[64];
        BigInteger base = BigInteger.ONE;
        for(int i=0;i<64;i++) {
            result[i] = base.toString(16);
            base = base.shiftLeft(1);
        }
        return result;
    }

    public static String[] getLadderData() {
        String[] result = new String[64];
        BigInteger base = BigInteger.ONE;
        result[0] = BigInteger.ZERO.toString(16);
        for(int i=1;i<64;i++) {
            result[i] = base.toString(16);
            base = base.shiftLeft(1).add(BigInteger.ONE);
        }
        return result;
    }

    public static String[] getReverseLadderData() {
        String[] result = new String[63];
        BigInteger allBits = new BigInteger("ffffffffffffffff", 16);
        BigInteger base = allBits;
        for(int i=0;i<63;i++) {
            result[i] = base.toString(16);
            base = base.shiftLeft(1).and(allBits);
        }
        return result;
    }

    /**
     * Encodes a single nibble.
     *
     * @param decoded the nibble to encode.
     *
     * @return the encoded half octet.
     */
    private static int encodeHalf(final int decoded) {

        switch (decoded) {
            case 0x00:
            case 0x01:
            case 0x02:
            case 0x03:
            case 0x04:
            case 0x05:
            case 0x06:
            case 0x07:
            case 0x08:
            case 0x09:
                return decoded + 0x30; // 0x30('0') - 0x39('9')
            case 0x0A:
            case 0x0B:
            case 0x0C:
            case 0x0D:
            case 0x0E:
            case 0x0F:
                return decoded + 0x57; // 0x41('a') - 0x46('f')
            default:
                throw new IllegalArgumentException("illegal half: " + decoded);
        }
    }

    public static BigInteger[] prepareData(String[] serverData) {
        BigInteger[] result = new BigInteger[serverData.length];
        int i=0;
        for(String number : serverData) {
            result[i++] = parseServerNumber(number);
        }
        return result;
    }

    public static BigInteger parseServerNumber(String number) {
        return number.startsWith("0x")?new BigInteger(number.substring(2), 16):new BigInteger(number, 16);
    }
}
