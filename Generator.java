interface PasswordGenerator {
	String[] generatePassword();

	void printPasswords(String[] passwords);
}

public class Generator implements PasswordGenerator {
	private int total;
	private int length;

	// Generator(int total,int length) {
	// this.total=total;
	// this.length=length;
	// }
	public void setLength(int length) {
		this.length = length;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public void getLength() {
		System.out.println(length);
	}

	public void getTotal() {
		System.out.println(total);
	}

	public String[] generatePassword() {
		String[] randomPasswords = new String[total];
		try {
			for (int i = 0; i < total; i++) {
				StringBuilder randomPassword = new StringBuilder();
				for (int j = 0; j < length; j++) {
					randomPassword.append(randomCharacter());
				}
				randomPasswords[i] = randomPassword.toString();
			}

			if (randomPasswords.length==1){
				printPasswords(randomPasswords[0]);
			} else {
				printPasswords(randomPasswords);
			}
		} catch (Exception e) {
			System.out.println("An error occurred during password generation: " + e.getMessage());
		}
		return randomPasswords;
	}

	public void printPasswords(String password) {
		System.out.println(password);
	}

	public void printPasswords(String[] arr) {
		for (int i = 0; i < arr.length; i++) {
			System.out.println(arr[i]);
		}
	}

	public static char randomCharacter() {
		int rand = (int) (Math.random() * 62);
		if (rand <= 9) {
			int ascii = rand + 48;
			return (char) (ascii);
		} else if (rand <= 35) {
			int ascii = rand + 55;
			return (char) (ascii);
		} else {
			int ascii = rand + 61;
			return (char) (ascii);
		}
	}
}