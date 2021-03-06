package messages;

public class MessageQuit implements Message{
	
	private static final long serialVersionUID = 1012840551935795973L;
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