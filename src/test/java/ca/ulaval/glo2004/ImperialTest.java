package ca.ulaval.glo2004;

import ca.ulaval.glo2004.util.math.Imperial;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;


public class ImperialTest {

    @Test
    public void DefaultConstructorTest()
    {
        //1
        Imperial imperial = new Imperial();

        assertEquals(0, imperial.getFeet());
        assertEquals(0, imperial.getInches());
        assertEquals(0, imperial.getNumerator());
        assertEquals(1, imperial.getDenominator());
    }

    @Test
    public void ConstructorTest()
    {
        //1
        Imperial imperial = new Imperial(1, 2, 3, 4);

        assertEquals(1, imperial.getFeet());
        assertEquals(2, imperial.getInches());
        assertEquals(3, imperial.getNumerator());
        assertEquals(4, imperial.getDenominator());

        //2
        boolean exceptionThrown = false;
        try{
            imperial = new Imperial(1, 2, 3, 0);
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
    }

    @Test
    public void UpdateInchesToFeetTest()
    {
        //1
        Imperial imperial = new Imperial(1, 30);

        assertEquals(3, imperial.getFeet());
        assertEquals(6, imperial.getInches());

        //2
        imperial = new Imperial(1, 30, 5, 10);

        assertEquals(3, imperial.getFeet());
        assertEquals(6, imperial.getInches());

        //3
        imperial = new Imperial(1, 2);

        assertEquals(1, imperial.getFeet());
        assertEquals(2, imperial.getInches());
    }

    @Test
    public void ReduceFractionTest()
    {
        //1
        Imperial imperial = new Imperial(1, 30, 5, 10);

        assertEquals(1, imperial.getNumerator());
        assertEquals(2, imperial.getDenominator());

        //2
        imperial = new Imperial(1, 30, 52, 128);

        assertEquals(13, imperial.getNumerator());
        assertEquals(32, imperial.getDenominator());

        //3
        imperial = new Imperial(1, 30, 0, 128);

        assertEquals(0, imperial.getNumerator());
        assertEquals(1, imperial.getDenominator());
    }

    @Test
    public void GetTotalInchesTest()
    {
        //1
        Imperial imperial = new Imperial(1, 30, 5, 10);

        assertEquals(42, imperial.getTotalInches());

        //2
        imperial = new Imperial(10, 15);

        assertEquals(135, imperial.getTotalInches());
    }

    @Test
    public void AddTest() {
        //1
        Imperial imperial1 = new Imperial(1, 2, 5, 10);
        Imperial imperial2 = new Imperial(2, 3, 5, 10);

        imperial1.add(imperial2);

        assertEquals(3, imperial1.getFeet());
        assertEquals(6, imperial1.getInches());
        assertEquals(0, imperial1.getNumerator());
        assertEquals(1, imperial1.getDenominator());

        //2
        imperial1 = new Imperial(1, 2, 2, 10);
        imperial2 = new Imperial(2, 3, 5, 15);

        imperial1.add(imperial2);

        assertEquals(3, imperial1.getFeet());
        assertEquals(5, imperial1.getInches());
        assertEquals(8, imperial1.getNumerator());
        assertEquals(15, imperial1.getDenominator());
    }

    @Test
    public void PlusTest() {
        //1
        Imperial imperial1 = new Imperial(1, 2, 5, 10);
        Imperial imperial2 = new Imperial(2, 3, 5, 10);

        Imperial imperial3 = imperial1.plus(imperial2);

        assertEquals(3, imperial3.getFeet());
        assertEquals(6, imperial3.getInches());
        assertEquals(0, imperial3.getNumerator());
        assertEquals(1, imperial3.getDenominator());

        //2
        imperial1 = new Imperial(1, 2, 2, 10);
        imperial2 = new Imperial(2, 3, 5, 15);

        imperial3 = imperial1.plus(imperial2);

        assertEquals(3, imperial3.getFeet());
        assertEquals(5, imperial3.getInches());
        assertEquals(8, imperial3.getNumerator());
        assertEquals(15, imperial3.getDenominator());
    }

    @Test
    public void SubtractTest() {
        //1
        Imperial imperial1 = new Imperial(2, 3, 5, 10);
        Imperial imperial2 = new Imperial(1,2, 5, 10);

        imperial1.subtract(imperial2);

        assertEquals(1, imperial1.getFeet());
        assertEquals(1, imperial1.getInches());
        assertEquals(0, imperial1.getNumerator());
        assertEquals(1, imperial1.getDenominator());

        //2
        boolean exceptionThrown = false;
        try {
            imperial1 = new Imperial(1, 2, 2, 10);
            imperial2 = new Imperial(2, 3, 5, 15);

            imperial1.subtract(imperial2);
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);

        //3
        imperial1 = new Imperial(15, 5, 5, 10);
        imperial2 = new Imperial(10, 8, 8, 10);

        imperial1.subtract(imperial2);

        assertEquals(4, imperial1.getFeet());
        assertEquals(8, imperial1.getInches());
        assertEquals(7, imperial1.getNumerator());
        assertEquals(10, imperial1.getDenominator());
    }

    @Test
    public void MinusTest() {
        //1
        Imperial imperial1 = new Imperial(2, 3, 5, 10);
        Imperial imperial2 = new Imperial(1,2, 5, 10);

        Imperial imperial3 = imperial1.minus(imperial2);

        assertEquals(1, imperial3.getFeet());
        assertEquals(1, imperial3.getInches());
        assertEquals(0, imperial3.getNumerator());
        assertEquals(1, imperial3.getDenominator());

        //2
        boolean exceptionThrown = false;
        try {
            imperial1 = new Imperial(1, 2, 2, 10);
            imperial2 = new Imperial(2, 3, 5, 15);

            imperial3 = imperial1.minus(imperial2);
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);

        //3
        imperial1 = new Imperial(15, 5, 5, 10);
        imperial2 = new Imperial(10, 8, 8, 10);

        imperial3 = imperial1.minus(imperial2);

        assertEquals(4, imperial3.getFeet());
        assertEquals(8, imperial3.getInches());
        assertEquals(7, imperial3.getNumerator());
        assertEquals(10, imperial3.getDenominator());
    }

    @Test
    public void EqualsTest()
    {
        //1
        Imperial imperial1 = new Imperial(1, 2, 5, 10);
        Imperial imperial2 = new Imperial(1, 2, 5, 10);

        assertTrue(imperial1.equals(imperial2));

        //2
        imperial1 = new Imperial(1, 2, 5, 10);
        imperial2 = new Imperial(1, 2, 5, 11);

        assertFalse(imperial1.equals(imperial2));

        //3
        imperial1 = new Imperial(5, 10, 5, 10);
        imperial2 = new Imperial(4, 22, 1, 2);

        assertTrue(imperial1.equals(imperial2));
    }

    @Test
    public void LessThanTest()
    {
        //1
        Imperial imperial1 = new Imperial(1, 2, 5, 10);
        Imperial imperial2 = new Imperial(1, 2, 5, 10);

        assertFalse(imperial1.lessThan(imperial2));

        //2
        imperial1 = new Imperial(1, 2, 5, 10);
        imperial2 = new Imperial(5, 2, 5, 10);

        assertTrue(imperial1.lessThan(imperial2));

        //3
        imperial1 = new Imperial(5, 10, 5, 10);
        imperial2 = new Imperial(5, 9, 1, 2);

        assertFalse(imperial1.lessThan(imperial2));
    }

    @Test
    public void LessThanOrEqualTest()
    {
        //1
        Imperial imperial1 = new Imperial(1, 2, 5, 10);
        Imperial imperial2 = new Imperial(1, 2, 5, 10);

        assertTrue(imperial1.lessThanOrEqual(imperial2));

        //2
        imperial1 = new Imperial(1, 2, 5, 10);
        imperial2 = new Imperial(5, 2, 5, 10);

        assertTrue(imperial1.lessThanOrEqual(imperial2));

        //3
        imperial1 = new Imperial(5, 10, 5, 10);
        imperial2 = new Imperial(5, 9, 1, 2);

        assertFalse(imperial1.lessThanOrEqual(imperial2));
    }

    @Test
    public void GreaterThanTest()
    {
        //1
        Imperial imperial1 = new Imperial(1, 2, 5, 10);
        Imperial imperial2 = new Imperial(1, 2, 5, 10);

        assertFalse(imperial1.greaterThan(imperial2));

        //2
        imperial1 = new Imperial(1, 2, 5, 10);
        imperial2 = new Imperial(5, 2, 5, 10);

        assertFalse(imperial1.greaterThan(imperial2));

        //3
        imperial1 = new Imperial(5, 10, 5, 10);
        imperial2 = new Imperial(5, 9, 1, 2);

        assertTrue(imperial1.greaterThan(imperial2));
    }

    @Test
    public void GreaterThanOrEqualTest()
    {
        //1
        Imperial imperial1 = new Imperial(1, 2, 5, 10);
        Imperial imperial2 = new Imperial(1, 2, 5, 10);

        assertTrue(imperial1.greaterThanOrEqual(imperial2));

        //2
        imperial1 = new Imperial(1, 2, 5, 10);
        imperial2 = new Imperial(5, 2, 5, 10);

        assertFalse(imperial1.greaterThanOrEqual(imperial2));

        //3
        imperial1 = new Imperial(5, 10, 5, 10);
        imperial2 = new Imperial(5, 9, 1, 2);

        assertTrue(imperial1.greaterThanOrEqual(imperial2));
    }

    @Test
    public void GetRawIchValueTest()
    {
        //1
        Imperial imperial = new Imperial(2, 2, 5, 10);

        assertEquals(26.5, imperial.getRawInchValue(), (double)1/128);

        //2
        imperial = new Imperial(1, 2);

        assertEquals(14, imperial.getRawInchValue(), (double)1/128);

        //3
        imperial = new Imperial(1, 2, 7, 25);

        assertEquals(14.28, imperial.getRawInchValue(), (double)1/128);
    }

    @Test
    public void GetFeetTest()
    {
        //1
        Imperial imperial = new Imperial(2, 2, 5, 10);

        assertEquals(2, imperial.getFeet());

        //2
        imperial = new Imperial(1, 14);

        assertEquals(2, imperial.getFeet());

        //3
        imperial = new Imperial(1, 2, 7, 25);

        assertEquals(1, imperial.getFeet());
    }

    @Test
    public void GetInchesTest()
    {
        //1
        Imperial imperial = new Imperial(2, 2, 5, 10);

        assertEquals(2, imperial.getInches());

        //2
        imperial = new Imperial(1, 14);

        assertEquals(2, imperial.getInches());

        //3
        imperial = new Imperial(1, 2, 7, 25);

        assertEquals(2, imperial.getInches());
    }

    @Test
    public void GetNumeratorTest()
    {
        //1
        Imperial imperial = new Imperial(2, 2, 5, 10);

        assertEquals(1, imperial.getNumerator());

        //2
        imperial = new Imperial(1, 14);

        assertEquals(0, imperial.getNumerator());

        //3
        imperial = new Imperial(1, 2, 7, 25);

        assertEquals(7, imperial.getNumerator());
    }

    @Test
    public void GetDenominatorTest()
    {
        //1
        Imperial imperial = new Imperial(2, 2, 5, 10);

        assertEquals(2, imperial.getDenominator());

        //2
        imperial = new Imperial(1, 14);

        assertEquals(1, imperial.getDenominator());

        //3
        imperial = new Imperial(1, 2, 7, 25);

        assertEquals(25, imperial.getDenominator());
    }

    @Test
    public void ToStringTest()
    {
        //1
        Imperial imperial = new Imperial(1, 2, 5, 10);

        assertEquals("1' 2\" 1/2", imperial.toString());

        //2
        imperial = new Imperial(1, 2, 0, 10);

        assertEquals("1' 2\" 0/1", imperial.toString());
    }
}
