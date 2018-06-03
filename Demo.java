import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

public class Demo {

	static List<String> stocks = new ArrayList<String>();

	public static void main(String[] args) throws IOException {

		loadStocks();
		System.out.println(stocks.size());
		deleteFile();
		createFileWIthDate();
		Iterator<String> stocksIterator = stocks.iterator();
		int howManyCompleted = 0;
		while (stocksIterator.hasNext()) {			
			readStockInfo(stocksIterator.next());
			System.out.println(++howManyCompleted+ " stock completed out of " + stocks.size() + " and "+(stocks.size() - howManyCompleted)+" remaining");
		}

	}

	private static void deleteFile() {
		String fileName = fileNameWithDate();
		File file = new File(fileName);
		if (file.delete()) {

		}

	}

	private static void createFileWIthDate() {
		String fileName = fileNameWithDate();
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
			out.write("symbol\tcompanyName\tprice\tOpenPrice\tPrClose\tChange\ttotalTradedVolume\n");
			out.close();
		} catch (IOException e) {
			System.out.println("Exception Occurred" + e);
		}

	}

	private static String fileNameWithDate() {
		StringBuffer fileName = new StringBuffer();
		fileName.append("c:\\Venkat\\StocksInfo");
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("MMddyyyy");
		fileName.append(formatter.format(date));
		fileName.append(".txt");
		return fileName.toString();
	}

	private static void loadStocks() throws FileNotFoundException, IOException {
		FileReader reader = new FileReader("stocks.properties");
		Properties p = new Properties();
		p.load(reader);
		StringTokenizer stocksTokenizer = new StringTokenizer(p.getProperty("stocks"), ",");
		while (stocksTokenizer.hasMoreTokens())
			stocks.add(stocksTokenizer.nextToken());
	}

	private static void readStockInfo(String stockName) throws MalformedURLException, IOException {
		StringBuffer urlString = new StringBuffer();
		urlString.append("https://nseindia.com/live_market/dynaContent/live_watch/get_quote/GetQuote.jsp?symbol=");
		urlString.append(stockName);
		urlString.append("&illiquid=0&smeFlag=0&itpFlag=0");
		URL url = new URL(urlString.toString());
		URLConnection urlConn = url.openConnection();
		urlConn.setRequestProperty("Content-Type", "text/html");
		InputStreamReader inStream = new InputStreamReader(urlConn.getInputStream());
		BufferedReader buff = new BufferedReader(inStream);

		String line = buff.readLine();
		String price = "not found";
		String PrClose = "not found";
		String OpenPrice = "not found";
		String companyName = "not found";
		String symbol = "not found";
		String change = "not found";
		String totalTradedVolume = "not found";
		while (line != null) {
			if (line.contains("\"lastPrice\":")) {
				price = findPrices(line, "\"lastPrice\":");
			}
			if (line.contains("\"previousClose\":")) {
				PrClose = findPrices(line, "\"previousClose\":");
			}
			if (line.contains("\"open\":")) {
				OpenPrice = findPrices(line, "\"open\":");
			}
			if (line.contains("\"companyName\":")) {
				companyName = findName(line, "\"companyName\":");
			}
			if (line.contains("\"symbol\":")) {
				symbol = findName(line, "\"symbol\":");
			}
			if (line.contains("\"change\":")) {
				change = findPrices(line, "\"change\":");
			}
			if (line.contains("\"totalTradedVolume\":")) {
				totalTradedVolume = findVolume(line);
			}
			line = buff.readLine();
		}
		
		String appendStr = symbol + "\t" + companyName + "\t" + price + "\t" + OpenPrice + "\t" + PrClose+"\t"+change+"\t"+totalTradedVolume+"\n";
		appendStrToFile(fileNameWithDate(),appendStr);
	}

	private static String findVolume(String line) {
		String volume;
		String findString = "\"totalTradedVolume\":";
		int target = line.indexOf(findString);
		int commaLocation = line.indexOf(",", target);
		int start = commaLocation;
		while (line.charAt(start) != '\"') {
			start--;
		}
		int end = commaLocation + 1;
		while (line.charAt(end) != '\"') {
			end++;
		}

		volume = line.substring(start + 1, end - 1);
		
		return volume;
	}

	public static void appendStrToFile(String fileName, String str) {
		try {

			// Open given file in append mode.
			BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true));
			out.write(str);
			out.close();
		} catch (IOException e) {
			System.out.println("exception occoured" + e);
		}
	}

	private static String findName(String line, String findString) {
		String companyName;
		int target = line.indexOf(findString);
		int start = line.indexOf(":", target) + 1;
		int end = start + 1;
		while (line.charAt(end) != '\"') {
			end++;
		}

		companyName = line.substring(start + 1, end);
		return companyName;
	}

	private static String findPrices(String line, String findString) {

		String price = "not found";
		int target = line.indexOf(findString);
		int deci = line.indexOf(".", target);
		int start = deci;
		while (line.charAt(start) != '\"') {
			start--;
		}

		price = line.substring(start + 1, deci + 3);

		return price;
	}

}
