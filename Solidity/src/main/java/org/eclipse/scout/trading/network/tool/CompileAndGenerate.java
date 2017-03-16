package org.eclipse.scout.trading.network.tool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import org.web3j.codegen.SolidityFunctionWrapperGenerator;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthCompileSolidity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;



public class CompileAndGenerate {
	
	public static final String BASE_PACKAGE = "org.eclipse.scout.trading.network";
	public static final String CONTRACT = "OrderBook";
	
	public static final String FOLDER_SOURCE = "C:\\Users\\mzi\\Desktop\\oss\\github\\trading-network\\Solidity\\src\\main\\resources";
	public static final String FOLDER_TARGET = "C:\\Users\\mzi\\Desktop\\oss\\github\\trading-network\\Solidity\\src\\main\\java";
	
	public static final String EXT_SOLIDITY = "sol";
	public static final String EXT_BINARY = "bin";
	public static final String EXT_ABI = "abi";
	
	public static void main(String argv []) throws Exception {
		CompileAndGenerate cag = new CompileAndGenerate();
		cag.run();
	}

	
	public void run() throws Exception {
		Web3jHelper.getWeb3j();
		compileSolidity();
		generateJava();
	}
	
	public void generateJava() throws Exception {
		String binaryFile = getBinaryFileName(CONTRACT, FOLDER_SOURCE);
		String abiFile = getAbiFileName(CONTRACT, FOLDER_SOURCE);
		String [] cmdLine = {binaryFile, abiFile, "-p", BASE_PACKAGE, "-o", FOLDER_TARGET};

		System.out.printf("-- running SolidityFunctionWrapperGenerator " + String.join(" ", cmdLine) + " ... ");
        SolidityFunctionWrapperGenerator.main(cmdLine);
        System.out.println("done");
	}
	
	public void compileSolidity() throws Exception {
		String sourceCode = readSolidity(String.format("%s\\%s.%s", FOLDER_SOURCE, CONTRACT, EXT_SOLIDITY));
		JsonObject response = compileSolidity(sourceCode, CONTRACT, FOLDER_SOURCE);		
	}

	private String readSolidity(String fileName) {
		try {
			File file = new File(fileName);
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			StringBuffer text = new StringBuffer();

			br.lines().forEach(line -> {
				if(text.length() > 0) {
					text.append(" ");
				}

				if(line.contains("//")) {
					line = line.substring(0, line.indexOf("//"));
				}

				text.append(line);
			});
			
			br.close();

			String sourceCode = text.toString();
			return sourceCode.replaceAll("\\s+", " ");
		} 
		catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public void compileNotWorkding() {
		String sourceCode = readSolidity("FXTrading.sol");
		Request<?, EthCompileSolidity> result = Web3jHelper.getWeb3j().ethCompileSolidity(sourceCode);
		System.out.println(result.toString());
	}
	
	/**
	 * hack to compile solditiy source code (needed as long testrpc and web3j do not play along nicely
	 * see https://github.com/web3j/web3j/issues/53
	 */
	private JsonObject compileSolidity(String source, String contractName, String path) throws Exception {
		String compileCommandTemplate = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_compileSolidity\",\"params\":[\"%s\"],\"id\":1}";
		String compileCommand = String.format(compileCommandTemplate, source);

		System.out.println("compile command " + compileCommand);
		System.out.printf(String.format("sending compile request to %s:%s", Web3jHelper.IP_ADDRESS, Web3jHelper.PORT));

		StringEntity requestEntity = new StringEntity(compileCommand, ContentType.create("text/plain").withCharset(Charset.forName("UTF-8")));
		HttpUriRequest request = RequestBuilder
				.post(getServerAddress())
				.setEntity(requestEntity)
				.build();

		ResponseHandler<JsonObject> rh = new ResponseHandler<JsonObject>() {

			@Override
			public JsonObject handleResponse(final HttpResponse response) throws IOException {
				StatusLine statusLine = response.getStatusLine();
				HttpEntity entity = response.getEntity();

				if (statusLine.getStatusCode() >= 300) {
					throw new HttpResponseException(
							statusLine.getStatusCode(),
							statusLine.getReasonPhrase());
				}

				if (entity == null) {
					throw new ClientProtocolException("Response contains no content");
				}

				Gson gson = new GsonBuilder().create();
				Reader reader = new InputStreamReader(entity.getContent(), Charset.forName("UTF-8"));
				return gson.fromJson(reader, JsonObject.class);
			}
		};

		JsonObject response = HttpClients.createDefault().execute(request, rh);
		System.out.println(" done");
		checkForErrors(response);
		checkForResult(response, contractName, path);

		return response;
	}
	
	private String getServerAddress() {
		return String.format("http://%s:%s", Web3jHelper.IP_ADDRESS, Web3jHelper.PORT); 
	}

	private void checkForErrors(JsonObject response) {
		String ERROR = "error";
		if(response.has(ERROR)) {
			JsonObject error = response.get(ERROR).getAsJsonObject();
			System.out.println("error code: " + error.get("code").toString());
			System.out.println("error message:\n" + error.get("message").getAsString());
		}
	}

	private void checkForResult(JsonObject response, String contractName, String path) throws Exception {
		String RESULT = "result";
		if(response.has(RESULT)) {
			printJsonElement("response: ", response, "", contractName, path);
		}
	}

	/**
	 * prints content to console
	 * for abiDefinition and code elements the value is written to the file system.
	 * TODO extract file writing ops to separate method which are called from checkForResult --> processResult 
	 */
	private void printJsonElement(String id, JsonElement e, String ident, String contractName, String path)
		throws Exception
	{
		if(id.equals("abiDefinition:") || id.equals("code:")) {
			File file = null;
			String value = e.toString();
			verifyValue(id, value);
			
			if(id.equals("abiDefinition:")) {
				file = new File(getAbiFileName(contractName, path));
			}
			else {
				// strip initial '"'
				if(value.startsWith("\"")) {
					value = value.substring(1);
				}
				
				// strip tailing '"'
				if(value.endsWith("\"")) {
					value = value.substring(0, value.length() - 1);
				}
				
				file = new File(getBinaryFileName(contractName, path));
			}
			
			System.out.printf("-- writing " + id + "value output to '" + file.getAbsolutePath() + "' ... ");
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			bw.write(value);
			bw.close();
			System.out.println("done");
			
			if(value.length() >= 3000) {
				System.out.println(ident + id + value.substring(0,  3000) + " ...");
			}
			else {
				System.out.println(ident + id + value);
			}
		}
		else if(e.isJsonPrimitive()) {
			String value = e.getAsString();
			value = verifyValue(id, value);
			System.out.println(ident + id + value);
		}
		else if(e.isJsonObject()) {
			System.out.println(ident + id + " {");
			JsonObject o = (JsonObject)e;
			for(Entry<String, JsonElement> entry: o.entrySet()) {
				String key = entry.getKey();
				printJsonElement(key + ":", entry.getValue(), ident + "  ", contractName, path);
			}
			System.out.println(ident + "}");
		}
		else if(e.isJsonArray()) {
			System.out.println(ident + id + " [");
			JsonArray a= (JsonArray)e;
			a.forEach(child -> {
				try {
					printJsonElement("", child, ident + "  ", contractName, path);
				} 
				catch (Exception e1) {
					e1.printStackTrace();
				}
			});
			System.out.println(ident + "}");
		}
		else {
			System.out.println(ident + id + "<!!!> " + e.toString());
		}
	}
	
	private String getAbiFileName(String contractName, String path) {
		return String.format("%s\\%s.%s", path, contractName, EXT_ABI);
	}
	
	private String getBinaryFileName(String contractName, String path) {
		return String.format("%s\\%s.%s", path, contractName, EXT_BINARY);
	}

	private String verifyValue(String id, String s) {
		if(s.startsWith("0x")) {
			String allowedChars = "0123456789abcdefg";
			int unexpectedChars = 0;
			for(int i = 2; i < s.length(); i++) {
				if(allowedChars.indexOf(s.charAt(i)) < 0) {
					unexpectedChars++;
				}
			}

			if(unexpectedChars != 0) {
				System.out.println("// " + unexpectedChars + " unexpected chars found");
			}
		}

		if(id.equals("code:") || id.equals("abiDefinition:")) {
			try {
				File tmp = null;

				if(id.equals("code:")) { tmp = File.createTempFile("contract", ".bin");}
				else                   { tmp = File.createTempFile("contract", ".abi");}

				BufferedWriter bw = new BufferedWriter(new FileWriter(tmp));
				bw.write(s);
				bw.close();

				System.out.println("// wrote content of '" + id + "' to file " + tmp.getAbsolutePath());
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}

		return s.length() <= 3000 ? s : s.substring(0, 3000); 
	}
	
}
