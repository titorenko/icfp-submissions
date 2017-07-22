package model;

import org.junit.Test;

import static org.junit.Assert.*;

public class MoveEncodingTest {
    @Test
    public void shouldReturnCorrectMove() {
        assertEquals(MoveEncoding.SE, MoveEncoding.encodingOf('o'));
    }
    @Test
    public void shouldBeCaseInsensitivee() {
        assertEquals(MoveEncoding.SE, MoveEncoding.encodingOf('O'));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwAnExceptionIfNotSupported() {
        MoveEncoding.encodingOf(';');
    }

    @Test
    public void shouldReturnFirstLetterAsNormalizedEncoding() {
        assertEquals('l', MoveEncoding.SE.getEncoding());
    }
}