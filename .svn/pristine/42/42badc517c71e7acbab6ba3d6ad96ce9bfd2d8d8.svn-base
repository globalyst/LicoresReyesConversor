package licoresreyes.model;

import licoresreyes.model.XmlFilter.Type;

public class XmlField {

	public enum Type {TEXT,BOOLEAN,FLOAT, MANY2ONE, MANY2MANY, ONE2MANY,INTEGER}
	
	private String OBColumn;
	private String ODColumn; 
	private String prefix = "";	
	private String defaultvalue = "";
	private String filter = "";
	private String exportable = "";
	private Type type = Type.TEXT;
	
	public XmlField(String OBColumn,String ODColumn, String prefix,String defaultvalue,String filter, String exportable,String type) {
		this.OBColumn = OBColumn;
		this.ODColumn = ODColumn;
		this.prefix = prefix;
		this.defaultvalue = defaultvalue;
		this.filter = filter;
		this.exportable = exportable;

		if (type.equals("bool")) {
			this.type = Type.BOOLEAN;
		} else if (type.equals("float")) {
			this.type = Type.FLOAT;
		} else if (type.equals("m2o")) {
			this.type = Type.MANY2ONE;
		} else if (type.equals("m2m")) {
			this.type = Type.MANY2MANY;
		} else if (type.equals("o2m")) {
			this.type = Type.ONE2MANY;
		} else if (type.equals("int")) {
			this.type = Type.INTEGER;
		} else {
			this.type = Type.TEXT;
		}
		
	}

	public boolean isRel() {
		return type == Type.MANY2MANY ||  type == Type.MANY2ONE || type == Type.ONE2MANY;
	}
	public boolean isExportable() {
		return exportable.equals("1");
	}
	public String getOBColumn() {
		return OBColumn;
	}

	public String getODColumn() {
		return ODColumn;
	}
	
	public String getPrefix() {
		return prefix;
	}

	public String getDefaultvalue() {
		return defaultvalue;
	}

	
	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	
	public String getExportable() {
		return exportable;
	}

	public void setExportable(String exportable) {
		this.exportable = exportable;
	}

	public void print() {
		System.out.println("      FIELD Prefix : " + prefix + " ; DEFAULT: " + defaultvalue);
		System.out.println("      OB : " + OBColumn + " ; OD : " + ODColumn);
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
	
}
