package chatt;

import java.io.Serializable;
import java.util.List;

public class Data implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private int command;
	private String message;
	// 벡터뿐 아니라 List 구현한 모든 객체 수용 가능
	private List<String> users = null;
	
	public Data (String id, int command, String message) {
		this.id = id;
		this.command = command;
		this.message = message;
	}
	
	public Data (String id, int command, String message, List<String> users) {
		this.id = id;
		this.command = command;
		this.message = message;
		this.users = users;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getCommand() {
		return command;
	}

	public void setCommand(int command) {
		this.command = command;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<String> getUsers() {
		return users;
	}

	public void setUsers(List<String> users) {
		this.users = users;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
