package licoresreyes.model;

import java.util.ArrayList;
import java.util.List;

public class XmlTable {

	private String id = "";
	private String relation = "";
	private String relationOD = "";
	private String from = "";
	private String fuse = "";
	private List<XmlField> fields;

	public XmlTable(String id, String relation, String relOD, String from, String fuse) {
		this.id = id;
		this.relation = relation;
		this.relationOD = relOD;
		this.from = from;
		this.fuse = fuse;
		fields = new ArrayList<XmlField>();
	}
	
	public void addField(XmlField xmlField) {
		fields.add(xmlField);
	}
	
	public void print() {
		System.out.println("    TABLE id: " + id + " ; rel : " + relation );
		for (int i = 0;i < fields.size(); i++) {
			fields.get(i).print();
		}
	}

	public String getId() {
		return id;
	}
	
	public String getRelation() {
		return relation;
	}

	public List<XmlField> getFields() {
		return fields;
	}

	public String getFuse() {
		return fuse;
	}

	public String getRelationOD() {
		return relationOD;
	}

	public String getFrom() {
		return from;
	}
	
	public String getRelationFrom() {
		if (from.isEmpty()) {
			return relation;
		} else {
			return from;
		}
	}
	
}
