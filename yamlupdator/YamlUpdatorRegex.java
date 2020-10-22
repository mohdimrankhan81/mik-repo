package yamlupdator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

public class YamlUpdatorRegex {

	public static void main(String args[]) throws IOException {		
		YamlUpdatorRegex yamlUpdator = new YamlUpdatorRegex();		
		yamlUpdator.updateYaml(args);		
	}

	private boolean updateYaml(String[] args) throws IOException {
		validateArguments(args);		
		String tempFileName = ".\\temp.yaml";
		
		generateUpdatedTempFile(args, tempFileName);
		copyTempFileToExistingFile(args, tempFileName);
		deleteTempFile(tempFileName);
		return false;
	}
	
	

	private void deleteTempFile(String tempFileName) {
		File f = new File(tempFileName);
		f.delete();
		
	}

	private void copyTempFileToExistingFile(String[] args, String tempFileName) throws IOException {
		String fileName = args[0];
		FileReader fr = new FileReader(tempFileName);
		FileWriter fw = new FileWriter(fileName);
		
		BufferedReader br = new BufferedReader(fr);
		BufferedWriter bw = new BufferedWriter(fw);
		
		Stream<String> x = br.lines();
		x.forEach(s-> {
			try {
				bw.write(s);
				bw.newLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		});
		
		br.close();
		bw.close();
		fr.close();
		fw.close();		
	}

	private void generateUpdatedTempFile(String[] args, String tempFileName) throws IOException {
		String fileName = args[0];
		Map<String, String> newPropertyMap = extractProperties(args);
		
		FileReader fr = new FileReader(fileName);
		FileWriter fw = new FileWriter(tempFileName);
		
		BufferedReader br = new BufferedReader(fr);
		BufferedWriter bw = new BufferedWriter(fw);
		
		Stream<String> x = br.lines();
		x.forEach(s-> {
			try {
				s = updateLine(s, newPropertyMap);
				bw.write(s);
				bw.newLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		});
		
		br.close();
		bw.close();
		fr.close();
		fw.close();
	}

	private void validateArguments(String[] args) {
		if(args == null || args.length < 2) {
			String errorMssg = String.format("Missing mandatory parameters : %s, %s", 
					"Fully qualified Yaml file path",
					"token:value pair to be replaced");
			System.out.println(errorMssg);;
			throw new RuntimeException(errorMssg);
		}
		
	}

	private Map<String, String> extractProperties(String[] args) {
		Map<String, String> propMap = new HashMap<>();
		for (int i = 1; i < args.length; i++) {
			String[] keyValue = args[i].split(":");
			if(keyValue == null || keyValue.length < 2) {
				String errorMssg = String.format("Invalid argument %s. Failed to parse to token and value. The correct format is Key:Value", args[i]);
				System.out.println(errorMssg);
				throw new RuntimeException(errorMssg);
			}
			propMap.put(keyValue[0], keyValue[1]);
		}
		if(propMap.isEmpty()) {
			String errorMssg = "Failed to parse tokens in arguments.";
			System.out.println(errorMssg);
			throw new RuntimeException(errorMssg);
		}
		System.out.println("Property map: \n"+ propMap);
		return propMap;
	}

	private String updateLine(String line, Map<String, String> newPropertyMap) {
		//String regex = "(\\.*account-management-subdomain-release:)([\\[ | \\(]*[\\d+ | . ]*[\\] | \\)]*:)(\\.*)";
		//String newValue = "account-management-subdomain-release:987654321:";
		
		//String regex = "(\\.*targetPort: )([\\d]*)";
		//String newValue = "targetPort: 7654";
		String newLine = line;
		for(Entry<String, String> es : newPropertyMap.entrySet()) {
			//String regex = String.format("(\\.*%s: )([\\d]*)", es.getKey());
			String regex = String.format("\\.*%s: \\d+", es.getKey());
			String newValue = String.format("%s: %s", es.getKey(), es.getValue());
			newLine = line.replaceAll(regex, newValue);
			
			if(line != newLine) {
				String infomssg = String.format("Original Line : %s replaced by %s ", line, newLine);
				System.out.println(infomssg);
				break;
			}
		}		
		return newLine;
	}

}
