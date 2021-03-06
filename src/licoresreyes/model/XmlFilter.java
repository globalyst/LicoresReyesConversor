package licoresreyes.model;

public class XmlFilter {

	public enum Type {FILTER,FILTER_EX, REMOVE, REPLACE, ADVANCED,BYNAME,BYDATE,EAN,ACCOUNTING}
	
	private Type type = Type.FILTER;
	private String column = "";
	private String value = "";
	private String by ="";
	private String set1 ="";
	private String set2 ="";
	
	public XmlFilter(String type, String column, String value, String by,String set1, String set2) {
		if (type.equals("FILTER_EX")) {
				this.type = Type.FILTER_EX;
		}  else if (type.equals("REMOVE")) {
			this.type = Type.REMOVE;
		} else if (type.equals("REPLACE")) {
			this.type = Type.REPLACE;
		}else if (type.equals("ADVANCED")) {
			this.type = Type.ADVANCED;
		} else if (type.equals("BYNAME")) {
			this.type = Type.BYNAME;
		} else if (type.equals("BYDATE")) {
			this.type = Type.BYDATE;
		}else if (type.equals("EAN")) {
			this.type = Type.EAN;
		}else if (type.equals("ACCOUNTING")) {
			this.type = Type.ACCOUNTING;
		}
		this.column = column;
		this.value = value;
		this.by = by;
		this.set1 = set1;
		this.set2 = set2;
		
	}

	public Type getType() {
		return type;
	}

	public String getColumn() {
		return column;
	}

	public String getValue() {
		return value;
	}

	public String getBy() {
		return by;
	}

	public String getSet1() {
		return set1;
	}

	public void setSet1(String set1) {
		this.set1 = set1;
	}

	public String getSet2() {
		return set2;
	}

	public void setSet2(String set2) {
		this.set2 = set2;
	}
	
	
	
	
}
