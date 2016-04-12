package messages;

public class MessageQuit implements Message{
	
	private String message;

	public MessageQuit() {
		super();
		this.message = "Quit";
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "MessageQuit [message=" + message + "]";
	}

}