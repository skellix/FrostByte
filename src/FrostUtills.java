

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;


public class FrostUtills {

	public FrostUtills() {
		
	}
	public String readFile(File file) {
		String out = "";
		try {
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				out += scanner.nextLine()+"\n";
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			System.err.println("File '"+file.getAbsolutePath()+"' does not exist!");
			System.exit(0);
		}
		return out;
	}
	public void printFile(String filename, String out) {
		File file = new File(filename);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write(out);
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public String fixTabs(String code) {
		String out = "";
		String[] lines = code.split("\\s*\n\\s*");
		int count = 0;
		for (String line : lines) {
			for (char c : line.toCharArray()) {
				if (c == '}') {
					count --;
				}
			}
			line = times("\t",count)+line;
			for (char c : line.toCharArray()) {
				if (c == '{') {
					count ++;
				}
			}
			out += line+"\n";
		}
		return out;
	}
	private String times(String string, int count) {
		String out = "";
		for (int i = 0 ; i < count ; i ++) {
			out += string;
		}
		return out;
	}
}
