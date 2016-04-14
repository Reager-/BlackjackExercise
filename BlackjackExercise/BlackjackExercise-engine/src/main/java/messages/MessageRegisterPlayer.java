package messages;

public class MessageRegisterPlayer implements Message{

	private static final long serialVersionUID = -3461651859586665756L;
	private String message;

	public MessageRegisterPlayer() {
		super();
		this.message = "RegisterPlayer";
		System.out.println("MessageRegisterPlayer");
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