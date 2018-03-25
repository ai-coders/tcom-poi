package net.aicoder.tcom.poi.config;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import net.aicoder.tcom.poi.TcomPoiApplication;
import net.aicoder.tcom.poi.config.Position;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TcomPoiApplication.class)
public class CellPositionUtilTest {

	@Test
	public void testColStr2ColNo() {
		String colStr;
		int colNo;
		colStr = "A";
		colNo = Position.colStr2ColNo(colStr);
		assertEquals(1,colNo);
		
		colStr = "L";
		colNo = Position.colStr2ColNo(colStr);
		assertEquals(12,colNo);
		
		colStr = "Z";
		colNo = Position.colStr2ColNo(colStr);
		assertEquals(26,colNo);
		
		colStr = "AA";
		colNo = Position.colStr2ColNo(colStr);
		assertEquals(27,colNo);
		
		colStr = "AZ";
		colNo = Position.colStr2ColNo(colStr);
		assertEquals(26+26,colNo);
	}

	@Test
	public void testColNo2ColStr() {
		String colStr;
		int colNo;
		colNo = 1;
		colStr = Position.colNo2ColStr(colNo);
		assertEquals("A",colStr);

		colNo = 12;
		colStr = Position.colNo2ColStr(colNo);
		assertEquals("L",colStr);

		colNo = 26;
		colStr = Position.colNo2ColStr(colNo);
		assertEquals("Z",colStr);

		colNo = 27;
		colStr = Position.colNo2ColStr(colNo);
		assertEquals("AA",colStr);

		colNo = 26+26;
		colStr = Position.colNo2ColStr(colNo);
		assertEquals("AZ",colStr);
	}

}
