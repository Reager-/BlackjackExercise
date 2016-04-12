package messages;

public class MessageRegisterPlayer implements Message{
	
	private String message;

	public MessageRegisterPlayer() {
		super();
		this.message = "RegisterPlayer";
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "MessageRegisterPlayer [message=" + message + "]";
	}
	
}