	import java.util.Scanner;
public class Generator {
	public static void main(String[] args) {
		Scanner inp= new Scanner(System.in);
		System.out.println("How many passwords do you want : ");
		int total=inp.nextInt();
		System.out.println("How many characters do you want that password to be : ");
		int length=inp.nextInt();
		
		String[] randomPasswords=new String[total];
		for (int i=0;i<total;i++) {
			String randomPassword="";
			for (int j=0;j<length;j++) {
				randomPassword+=randomCharacter();
			}
			randomPasswords[i]=randomPassword;
		}
		printPasswords(randomPasswords);
		
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