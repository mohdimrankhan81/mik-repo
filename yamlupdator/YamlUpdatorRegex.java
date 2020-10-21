package yamlupdator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
		//String fileName = "c:\\temp\\env-descriptor.yaml";
		//String fileName = "c:\\my-projs\\autoadoption\\yamlupdator\\src\\main\\resources\\test.yaml";
		validateArguments(args);
		String fileName = args[0];
		String outFileName = ".\\temp.yaml";
		
		Map<String, String> newPropertyMap = extractProperties(args); 
				//new HashMap<>();
		//newPropertyMap.put("account-management-subdomain-release", "0.37.47");
		
		FileReader fr = new FileReader(fileName);
		FileWriter fw = new FileWriter(outFileName);
		
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
		return false;
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
