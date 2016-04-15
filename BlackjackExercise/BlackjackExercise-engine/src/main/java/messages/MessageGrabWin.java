package messages;

public class MessageGrabWin implements Message{

	private static final long serialVersionUID = -6971428379869577087L;
	private String message;

	public MessageGrabWin() {
		super();
		this.message = "GrabWin";
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "MessageGrabWin [message=" + message + "]";
	}

}