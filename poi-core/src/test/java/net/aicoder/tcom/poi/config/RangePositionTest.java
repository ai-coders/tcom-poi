package net.aicoder.tcom.poi.config;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import net.aicoder.tcom.poi.TcomPoiApplication;
import net.aicoder.tcom.poi.config.RangePosition;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TcomPoiApplication.class)
public class RangePositionTest {

	@Test
	public void testB12() {
		String pos;
		RangePosition rp;

		pos = "B12";
		rp = new RangePosition(pos);
		assertEquals(12, rp.getBeginCell().getRow());
		assertEquals(2, rp.getBeginCell().getColumn());
		assertEquals(0, rp.getEndCell().getRow());
		assertEquals(0, rp.getEndCell().getColumn());
	}

	@Test
	public void testB12_() {
		String pos;
		RangePosition rp;

		pos = "B12:";
		rp = new RangePosition(pos);
		assertEquals(12, rp.getBeginCell().getRow());
		assertEquals(2, rp.getBeginCell().getColumn());
		assertEquals(0, rp.getEndCell().getRow());
		assertEquals(0, rp.getEndCell().getColumn());
	}

	@Test
	public void test_B12() {
		String pos;
		RangePosition rp;

		pos = ":B12";
		rp = new RangePosition(pos);
		assertEquals(0, rp.getBeginCell().getRow());
		assertEquals(0, rp.getBeginCell().getColumn());
		assertEquals(12, rp.getEndCell().getRow());
		assertEquals(2, rp.getEndCell().getColumn());
	}

	@Test
	public void testB12_AB36() {
		String pos;
		RangePosition rp;

		pos = "B12:BB36";
		rp = new RangePosition(pos);
		assertEquals(12, rp.getBeginCell().getRow());
		assertEquals(2, rp.getBeginCell().getColumn());
		assertEquals(36, rp.getEndCell().getRow());
		assertEquals(2 * 26 + 2, rp.getEndCell().getColumn());
	}

	@Test
	public void testR12() {
		RangePosition rp;
		int begRow = 12;

		rp = new RangePosition();
		rp.setRowPosition(begRow);
		assertEquals(begRow, rp.getBeginCell().getRow());
		assertEquals(0, rp.getBeginCell().getColumn());
		assertEquals(begRow, rp.getEndCell().getRow());
		assertEquals(0, rp.getEndCell().getColumn());
	}
	
	@Test
	public void testR12_36() {
		RangePosition rp;
		int begRow = 12;
		int endRow = 36;

		rp = new RangePosition();
		rp.setRowPosition(begRow,endRow);
		assertEquals(begRow, rp.getBeginCell().getRow());
		assertEquals(0, rp.getBeginCell().getColumn());
		assertEquals(endRow, rp.getEndCell().getRow());
		assertEquals(0, rp.getEndCell().getColumn());
	}
}
