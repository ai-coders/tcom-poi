package net.aicoder.tcom.poi.config;

import static org.junit.Assert.*;

import org.junit.Test;

import net.aicoder.tcom.poi.config.Position;

public class CellPositionTest {
	@Test
	public void testNull() {
		String posStr = "";
		Position cellPosition = new Position(posStr);
		int rowNo = cellPosition.getRow();
		int colNo = cellPosition.getColumn();
		assertEquals(0,rowNo);
		assertEquals(0,colNo);
	}

	@Test
	public void testB() {
		String posStr = "B";
		Position cellPosition = new Position(posStr);
		int rowNo = cellPosition.getRow();
		int colNo = cellPosition.getColumn();
		assertEquals(0,rowNo);
		assertEquals(2,colNo);
	}

	@Test
	public void testBB() {
		String posStr = "BB";
		Position cellPosition = new Position(posStr);
		int rowNo = cellPosition.getRow();
		int colNo = cellPosition.getColumn();
		assertEquals(0,rowNo);
		assertEquals(2*26+2,colNo);
	}

	@Test
	public void test_4() {
		String posStr = "4";
		Position cellPosition = new Position(posStr);
		int rowNo = cellPosition.getRow();
		int colNo = cellPosition.getColumn();
		assertEquals(4,rowNo);
		assertEquals(0,colNo);
	}
	
	@Test
	public void test_44() {
		String posStr = "44";
		Position cellPosition = new Position(posStr);
		int rowNo = cellPosition.getRow();
		int colNo = cellPosition.getColumn();
		assertEquals(44,rowNo);
		assertEquals(0,colNo);
	}

	
	@Test
	public void testB4() {
		String posStr = "B4";
		Position cellPosition = new Position(posStr);
		int rowNo = cellPosition.getRow();
		int colNo = cellPosition.getColumn();
		assertEquals(4,rowNo);
		assertEquals(2,colNo);
	}
	
	@Test
	public void testBB4() {
		String posStr = "BB4";
		Position cellPosition = new Position(posStr);
		int rowNo = cellPosition.getRow();
		int colNo = cellPosition.getColumn();
		assertEquals(4,rowNo);
		assertEquals(2*26+2,colNo);
	}
	
	@Test
	public void testB44() {
		String posStr = "B44";
		Position cellPosition = new Position(posStr);
		int rowNo = cellPosition.getRow();
		int colNo = cellPosition.getColumn();
		assertEquals(44,rowNo);
		assertEquals(2,colNo);
	}
	
	@Test
	public void testBB44() {
		String posStr = "BB44";
		Position cellPosition = new Position(posStr);
		int rowNo = cellPosition.getRow();
		int colNo = cellPosition.getColumn();
		assertEquals(44,rowNo);
		assertEquals(2*26+2,colNo);
	}

/*	
	@Test(expected = IllegalArgumentException.class)
	public void test$B() {
		String posStr = "$B";
		CellPosition cellPosition = new CellPosition(posStr);
		int rowNo = cellPosition.getRow();
		int colNo = cellPosition.getColumn();
		assertEquals(0,rowNo);
		assertEquals(2,colNo);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void test_$4() {
		String posStr = "$4";
		CellPosition cellPosition = new CellPosition(posStr);
		int rowNo = cellPosition.getRow();
		int colNo = cellPosition.getColumn();
		assertEquals(4,rowNo);
		assertEquals(0,colNo);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_$B4() {
		String posStr = "$B4";
		CellPosition cellPosition = new CellPosition(posStr);
		int rowNo = cellPosition.getRow();
		int colNo = cellPosition.getColumn();
		assertEquals(4,rowNo);
		assertEquals(2,colNo);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_$B$4() {
		String posStr = "$B$4";
		CellPosition cellPosition = new CellPosition(posStr);
		int rowNo = cellPosition.getRow();
		int colNo = cellPosition.getColumn();
		assertEquals(4,rowNo);
		assertEquals(2,colNo);
	}
*/
}
