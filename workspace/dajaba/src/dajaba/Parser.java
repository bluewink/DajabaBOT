package dajaba;

import java.io.File;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.Vector;

public class Parser {
	Logger logger = Logger.getLogger(Parser.class);

	private Document doc;
	private File input;
	private Vector<String> urlList = new Vector<String>();

	public Parser(String filePath) {
		input = new File(filePath);

	}

	public Vector<String> openDoc() {
		try {
			doc = Jsoup.parse(input, "utf-8");
			try {
				this.getUrl();
			} catch (Exception e) {
				logger.warn("error from getting url in html file", e);
				logger.info("program ends");
			}

		} catch (Exception e) {

		}
		return urlList;
	}

	public void getUrl() {
		try {
			Elements link = doc.select("td.line-content:contains(itm) a.html-external-link");
			for (Element e : link) {
				String text = e.attr("href");
				urlList.add(text); // save url into vector
				logger.debug(text + " has been parsed");
			}
		} catch (Exception e) {

		}
	}
}
