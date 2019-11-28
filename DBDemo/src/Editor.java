
public class Editor extends User {
private int editorId;
	
	public Editor(String name, String email, int id) {
		super(name, email);
		editorId = id;
	}
	
	public int getId() {
		return editorId;
	}
}
