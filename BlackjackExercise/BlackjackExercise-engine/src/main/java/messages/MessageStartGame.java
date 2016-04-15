package messages;

public class MessageStartGame implements Message{

	private static final long serialVersionUID = -4557764977432243169L;
	private String message;

	public MessageStartGame() {
		super();
		this.message = "StartGame";
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "MessageStartGame [message=" + message + "]";
	}

}