package ar.com.concentrador.extractor.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.poi.hssf.record.EOFRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.RecordFormatException;
import org.apache.poi.util.StringUtil;

import ar.com.concentrador.extractor.BaseExtractor;
import ar.com.concentrador.model.Quotes;

public class MecadoCentralBSASExtractor extends BaseExtractor {
	private static final String CODE_EXTRACTOR = "02";
	private static final String URL = "http://www.mercadocentral.gob.ar/sites/default/files/precios_mayoristas/PM-Hortalizas-%s.zip";
	private static final int BUFFER = 2048;
	private static final char[] CHAR_TO_REMOVE = {};
	private static final String CODE_MARKET = "BSAS";
	
	private Map<String, String> mapPackage;
	private Map<Integer, String> mapMonth;

	public MecadoCentralBSASExtractor() {
		this.mapPackage = new HashMap<>();
		this.mapPackage.put("AP", "ARGEN-POOL");
		this.mapPackage.put("A", "ATADO");
		this.mapPackage.put("BA", "BANDEJA");
		this.mapPackage.put("BO", "BOLSA");
		this.mapPackage.put("CA", "CAJA");
		this.mapPackage.put("CJ", "CAJON");
		this.mapPackage.put("CT", "CAJA/Telescop");
		this.mapPackage.put("GR", "GRANEL");
		this.mapPackage.put("IF", "IFCO");
		this.mapPackage.put("JA", "JAULA");
		this.mapPackage.put("MA", "MARK 4");
		this.mapPackage.put("PE", "PERDIDO");
		this.mapPackage.put("PL", "PLAFOM");
		this.mapPackage.put("PQ", "PAQUETE");
		this.mapPackage.put("RT", "RISTRA 100");
		this.mapPackage.put("SM", "SAN MARTIN");
		this.mapPackage.put("ST", "STANDARTD");
		this.mapPackage.put("SU", "SUDAFRICANO");
		this.mapPackage.put("TO", "TORO");
		this.mapPackage.put("TT", "TORITO");

		this.mapMonth = new HashMap<>();
		this.mapMonth.put(1, "Ene");
		this.mapMonth.put(2, "Feb");
		this.mapMonth.put(3, "Marzo");
		this.mapMonth.put(4, "Abr");
		this.mapMonth.put(5, "May");
		this.mapMonth.put(6, "Jun");
		this.mapMonth.put(7, "Jul");
		this.mapMonth.put(8, "Ago");
		this.mapMonth.put(9, "Set");
		this.mapMonth.put(10, "Oct");
		this.mapMonth.put(11, "Nov");
		this.mapMonth.put(12, "Dic");
	}

	@Override
	public String getCodeExtractor() {
		return CODE_EXTRACTOR;
	}
	
	@Override
	public String getMarket() {
		return CODE_MARKET;
	}

	@Override
	public List<Quotes> getQuotes() {
		return this.extract();
	}

	private List<Quotes> extract() {
		List<Quotes> information = new ArrayList<>();

		byte[] data = this.call(Calendar.getInstance());
		Workbook workbook = convertBIFF2To8(this.extractCompress(data));

		try {

			Date date = new Date();
			Sheet firstSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = firstSheet.iterator();
			
			/* Saltea Titulos */
			iterator.next();
			
			while (iterator.hasNext()) {
				Row nextRow = iterator.next();
				int initCol = nextRow.getFirstCellNum();

				Quotes q = createQuotes();
				q.setDate(date);
				q.setCode(formatCodeValue(formatValueFromCell(nextRow.getCell(initCol)) + " " + formatValueFromCell(nextRow.getCell(initCol + 1))));
				q.setSource(formatDescriptionValue(formatValueFromCell(nextRow.getCell(initCol + 2))));
				q.setPackageDes(this.formatPackage(formatValueFromCell(nextRow.getCell(initCol + 3))));
				q.setValue(formatDescriptionValue(formatValueFromCell(nextRow.getCell(initCol + 4)).replace(".0", "")));
				q.setMaxValue(formatMoneyValue(formatValueFromCell(nextRow.getCell(initCol + 8)), CHAR_TO_REMOVE));
				q.setMinValue(formatMoneyValue(formatValueFromCell(nextRow.getCell(initCol + 10)), CHAR_TO_REMOVE));
				q.setDescription(formatDescriptionValue(q.getCode(), q.getPackageDes(), q.getValue()));

				information.add(q);
			}

		} catch (Exception e) {
			throw new RuntimeException("Error al extraer informacion del xls: " + workbook.getSheetName(0), e);

		} finally {
			if (workbook != null)
				try {
					workbook.close();
				} catch (Exception e) {
					/* nothing todo */
				}
		}

		return information;
	}
	
	private byte[] call(Calendar c) {
		List<String> urls = new ArrayList<>();
		List<Exception> exs = new ArrayList<>();
		
		byte[] data = null;
		
		int i = 1;
		while (i < 4 && data == null) {
			c.add(Calendar.DAY_OF_MONTH, -1);

			String year = String.valueOf(c.get(Calendar.YEAR));
			String month = mapMonth.get(c.get(Calendar.MONTH) + 1);
			String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
			
			if (day.length() == 1) {
				day = "0" + day; 
			}

			urls.add( String.format(URL, day + "-" + month + "-" + year) );
			try {
				data = this.call(urls.get(urls.size()-1) );
			} catch (Exception e) {
				exs.add(e);
			}
			
			i++;
		}
		
		if (data == null) {
			Exception e = new Exception("No se tuvo acceso a los siguientes archivos...");
			for(Exception ee: exs) { 
				e.addSuppressed(ee);
			}
			throw new RuntimeException("No se pudo recuperar la informacion de: " + urls, e);
		}
		
		return data;
	}

	private String formatPackage(String code) {
		return formatDescriptionValue(this.mapPackage.get(code));
	}

	private static String formatValueFromCell(Cell cell) {
		String value = "";
		if (CellType.STRING.equals(cell.getCellTypeEnum())) {
			value = cell.getStringCellValue();
		} else if (CellType.NUMERIC.equals(cell.getCellTypeEnum())) {
			value = "" + cell.getNumericCellValue();
		} else if (CellType.BOOLEAN.equals(cell.getCellTypeEnum())) {
			value = "" + cell.getNumericCellValue();
		} else if (CellType.NUMERIC.equals(cell.getCellTypeEnum())) {
			value = "" + cell.getBooleanCellValue();
		} else {
			throw new RuntimeException("Tipo de valor no valido");
		}
		return value.trim();
	}

	private Object[] extractCompress(byte[] data) {
		ZipInputStream zip = null;
		ZipEntry entry = null;
		ByteArrayOutputStream target = null;

		try {
			zip = new ZipInputStream(new ByteArrayInputStream(data));

			if ((entry = zip.getNextEntry()) != null) {
				int count;
				byte dataZip[] = new byte[BUFFER];

				target = new ByteArrayOutputStream();
				while ((count = zip.read(dataZip, 0, BUFFER)) != -1) {
					target.write(dataZip, 0, count);
				}
				
				target.flush();
				
				return new Object[] { new ByteArrayInputStream(target.toByteArray()), entry.getName()};
			}
			
			throw new RuntimeException("Archivo corrupto.");

		} catch (IOException e) {
			if (entry != null) {
				throw new RuntimeException("Error al descomprimir informacion del zip de datos de " + entry.getName(), e);
			}
			throw new RuntimeException("Error al recuperar informacion del zip de datos.", e);

		} finally {
			try { if (target != null)	target.close(); } catch (IOException e) {}
			try { if (zip != null) zip.close(); } catch (IOException e) {}
		}
	}

	public static HSSFWorkbook convertBIFF2To8(Object[] data) {
		InputStream biff2stream = (InputStream)data[0];
		String name = data[1].toString();
		
		try {
			return convert(read(biff2stream), name);

		} catch (Exception e) {
			throw new RuntimeException("Error al convertir el xml biff2 a biff8.", e);
		}
	}

	public static HSSFWorkbook convert(Record[] biff2Records, String sheetName) {
		HSSFWorkbook result = new HSSFWorkbook();
		HSSFSheet sheet = result.createSheet(sheetName);
		if (!(biff2Records[0] instanceof BOFRecord)) {
			throw new RecordFormatException("Expected BOF record");
		}
		int lastRecIx = biff2Records.length - 1;
		if (!(biff2Records[lastRecIx] instanceof EOFRecord)) {
			throw new RecordFormatException("Expected EOF record");
		}
		for (int i = 1; i < lastRecIx; i++) {
			convertRecord(sheet, biff2Records[i]);
		}
		return result;
	}

	private static void convertRecord(HSSFSheet sheet, Record record) {
		if (record instanceof CellBaseRecord) {
			convertCellRecord(sheet, (CellBaseRecord) record);
			return;
		}
		switch (record.getSid()) {
		case DimensionRecord.sid:
			return;
		}
		throw new RuntimeException("Unexpected record (" + record.getClass().getName() + ")");
	}

	private static void convertCellRecord(HSSFSheet sheet, CellBaseRecord cr) {
		HSSFRow row = sheet.getRow(cr.getRowIndex());
		if (row == null) {
			row = sheet.createRow(cr.getRowIndex());
		}
		HSSFCell cell = row.getCell(cr.getColumnIndex());
		if (cell == null) {
			cell = row.createCell(cr.getColumnIndex());
		}
		convertCellValue(cell, cr);
	}

	private static void convertCellValue(HSSFCell cell, CellBaseRecord cr) {
		if (cr instanceof NumberRecord) {
			NumberRecord nr = (NumberRecord) cr;
			cell.setCellValue(nr.getValue());
			return;
		}
		if (cr instanceof LabelRecord) {
			LabelRecord lr = (LabelRecord) cr;
			cell.setCellValue(new HSSFRichTextString(lr.getText()));
			return;
		}
		throw new RuntimeException("Unexpected record (" + cr.getClass().getName() + ")");
	}

	public static Record[] read(InputStream is) {
		RecordInputStream in = new RecordInputStream(is);
		List<Record> temp = new ArrayList<Record>();
		while (in.hasNextRecord()) {
			in.nextRecord();
			Record r = createRecord(in);
			if (r.getSid() != UnknowRecord.sid) {
				temp.add(r);
			}
		}

		Record[] result = new Record[temp.size()];
		temp.toArray(result);
		return result;
	}

	private static Record createRecord(RecordInputStream in) {
		int sid = in.getSid();
		switch (sid) {
		case UnknowRecord.sid:
			return new UnknowRecord(in);
		case DimensionRecord.sid:
			return new DimensionRecord(in);
		case NumberRecord.sid:
			return new NumberRecord(in);
		case LabelRecord.sid:
			return new LabelRecord(in);
		case BOFRecord.sid:
			return new BOFRecord(in);
		case EOFRecord.sid:
			return new EOFRecord(in);
		}
		throw new IllegalArgumentException("Unexpected sid (" + new String(HexDump.shortToHex(sid)) + ")");
	}

	public static final class CellAttributes {
		public static final int ENCODED_SIZE = 3;

		private static final BitField xfIndexMask = bf(0x3F);
		private static final BitField cellLockedFlag = bf(0x40);
		private static final BitField formulaHidden = bf(0x80);

		private static final BitField formatIndexMask = bf(0x3F);
		private static final BitField fontIndexMask = bf(0xC0);

		private static final BitField horizontalAlignMask = bf(0x07);
		private static final BitField leftBorderFlag = bf(0x08);
		private static final BitField rightBorderFlag = bf(0x10);
		private static final BitField topBorderFlag = bf(0x20);
		private static final BitField bottomBorderFlag = bf(0x40);
		private static final BitField shadedBackgroundFlag = bf(0x80);

		private static BitField bf(int i) {
			return BitFieldFactory.getInstance(i);
		}

		private int _protectionAndXF;
		private int _formatAndFont;
		private int _style;

		public CellAttributes(LittleEndianInput in) {
			_protectionAndXF = in.readUByte();
			_formatAndFont = in.readUByte();
			_style = in.readUByte();
		}

		public void serialize(LittleEndianOutput out) {
			out.writeByte(_protectionAndXF);
			out.writeByte(_formatAndFont);
			out.writeByte(_style);
		}

		public int getXFIndex() {
			return xfIndexMask.getValue(_protectionAndXF);
		}

		public boolean isCellLocked() {
			return cellLockedFlag.isSet(_protectionAndXF);
		}

		public boolean isFormulaHidden() {
			return formulaHidden.isSet(_protectionAndXF);
		}

		public int getFormatIndex() {
			return formatIndexMask.getValue(_formatAndFont);
		}

		public int getFontIndex() {
			return fontIndexMask.getValue(_formatAndFont);
		}

		public int getHorizontalAlignmentCode() {
			return horizontalAlignMask.getValue(_style);
		}

		public boolean isTopBorderSet() {
			return topBorderFlag.isSet(_style);
		}

		public boolean isBottomBorderSet() {
			return bottomBorderFlag.isSet(_style);
		}

		public boolean isLeftBorderSet() {
			return leftBorderFlag.isSet(_style);
		}

		public boolean isRightBorderSet() {
			return rightBorderFlag.isSet(_style);
		}

		public boolean isBackgroundShaded() {
			return shadedBackgroundFlag.isSet(_style);
		}
	}

	public static final class BOFRecord extends StandardRecord {
		public static final int sid = 0x0009;
		private int _version;
		private int _type;

		public BOFRecord(LittleEndianInput in) {
			_version = in.readUShort();
			_type = in.readUShort();
		}

		@Override
		protected void serialize(LittleEndianOutput out) {
			out.writeShort(_version);
			out.writeShort(_type);
		}

		@Override
		protected int getDataSize() {
			return 4;
		}

		@Override
		public short getSid() {
			return sid;
		}
	}

	public static final class UnknowRecord extends StandardRecord {
		public static final int sid = 0x0024;

		public UnknowRecord(LittleEndianInput in) {
			in.readInt();
		}

		@Override
		protected void serialize(LittleEndianOutput out) {
			/* Nothing todo */
		}

		@Override
		protected int getDataSize() {
			return 4;
		}

		@Override
		public short getSid() {
			return sid;
		}
	}

	public static final class DimensionRecord extends StandardRecord {
		public static final int sid = 0x0000;
		private int _firstRowIndex;
		private int _lastRowIndex;
		private int _firstColumnIndex;
		private int _lastColumnIndex;

		public DimensionRecord(LittleEndianInput in) {
			_firstRowIndex = in.readUShort();
			_lastRowIndex = in.readUShort() - 1;
			_firstColumnIndex = in.readUShort();
			_lastColumnIndex = in.readUShort() - 1;
		}

		@Override
		protected void serialize(LittleEndianOutput out) {
			out.writeShort(_firstRowIndex);
			out.writeShort(_lastRowIndex + 1);
			out.writeShort(_firstColumnIndex);
			out.writeShort(_lastColumnIndex + 1);
		}

		@Override
		protected int getDataSize() {
			return 8;
		}

		@Override
		public short getSid() {
			return sid;
		}
	}

	public static abstract class CellBaseRecord extends StandardRecord {
		private int _rowIndex;
		private int _columnIndex;
		private CellAttributes _cellAttributes;

		protected CellBaseRecord(LittleEndianInput in) {
			_rowIndex = in.readUShort();
			_columnIndex = in.readUShort();
			_cellAttributes = new CellAttributes(in);
		}

		@Override
		protected final void serialize(LittleEndianOutput out) {
			out.writeShort(_rowIndex);
			out.writeShort(_columnIndex);
			_cellAttributes.serialize(out);
			serializeSpecificData(out);
		}

		protected abstract void serializeSpecificData(LittleEndianOutput out);

		@Override
		protected final int getDataSize() {
			return 2 + 2 + CellAttributes.ENCODED_SIZE + getSpecificDataSize();
		}

		protected abstract int getSpecificDataSize();

		public final int getRowIndex() {
			return _rowIndex;
		}

		public final int getColumnIndex() {
			return _columnIndex;
		}

		public final CellAttributes getCellAttributes() {
			return _cellAttributes;
		}
	}

	public static final class NumberRecord extends CellBaseRecord {
		public static final int sid = 0x0003;
		private Double _value;

		public NumberRecord(LittleEndianInput in) {
			super(in);
			_value = in.readDouble();
		}

		@Override
		protected void serializeSpecificData(LittleEndianOutput out) {
			out.writeDouble(_value);
		}

		@Override
		protected int getSpecificDataSize() {
			return 8;
		}

		@Override
		public short getSid() {
			return sid;
		}

		public double getValue() {
			return _value;
		}
	}

	public static final class LabelRecord extends CellBaseRecord {
		public static final int sid = 0x0004;
		private String _text;

		public LabelRecord(LittleEndianInput in) {
			super(in);
			int nChars = in.readUByte();
			_text = StringUtil.readCompressedUnicode(in, nChars);
		}

		@Override
		protected void serializeSpecificData(LittleEndianOutput out) {
			out.writeByte(_text.length());
			StringUtil.putCompressedUnicode(_text, out);
		}

		@Override
		protected int getSpecificDataSize() {
			return 1 + _text.length();
		}

		public String getText() {
			return _text;
		}

		@Override
		public short getSid() {
			return sid;
		}
	}
}
