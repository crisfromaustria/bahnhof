package at.fontain.bahnhof.exception;

public class BahnhofNotFoundException extends Exception {
	private static final long serialVersionUID = 3845257784700711277L;
	
	public BahnhofNotFoundException(String message) {
		super(message);
	}
}
