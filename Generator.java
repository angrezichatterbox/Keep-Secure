public class Generator {
	int total;
	int length;
    Generator(int total,int length) {
		this.total=total;
		this.length=length;
    }

	public String[] generatePassword(){
		String[] randomPasswords=new String[total];
		for (int i=0;i<total;i++) {
			String randomPassword="";
			for (int j=0;j<length;j++) {
				randomPassword+=randomCharacter();
			}
			randomPasswords[i]=randomPassword;
		}System.out.println("number of passwords given is : ");
		System.out.println("Length of password is : ");
		printPasswords(randomPasswords);
		return randomPasswords;
	}
	public static void printPasswords(String[] arr) {
		for(int i=0;i<arr.length;i++) {
			System.out.println(arr[i]);
		}
	}
	public static char randomCharacter() {
		int rand=(int)(Math.random()*62);
		if(rand<=9) {
			int ascii=rand+48;
			return (char)(ascii);
		}else if (rand <=35) {
			int ascii=rand+55;
			return (char)(ascii);
		} else {
			int ascii=rand+61;
			return (char)(ascii);
		}
	}
}