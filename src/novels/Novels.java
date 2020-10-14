package novels;

import java.io.IOException;
import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.util.stream.Stream;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Collections;
import java.beans.XMLEncoder;
import javax.servlet.ServletContext; // not in JavaSE
import org.json.JSONObject;
import org.json.XML;

public class Novels {
    private final String fileName = "/WEB-INF/data/novels.db";
    private ConcurrentMap<Integer, Novel> novels;
    private ServletContext sctx;
    private AtomicInteger mapKey;

    public Novels() {
	novels = new ConcurrentHashMap<Integer, Novel>();
	mapKey = new AtomicInteger(); 
    }

    public void setServletContext(ServletContext sctx) { this.sctx = sctx; }
    public ServletContext getServletContext() { return this.sctx; }

    public ConcurrentMap<Integer, Novel> getConcurrentMap() {
	if (getServletContext() == null) return null; // not initialized
	if (novels.size() < 1) populate();
	return this.novels;
    }

    public String toXml(Object obj) { // default encoding
	String xml = null;
	try {
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    XMLEncoder encoder = new XMLEncoder(out);
	    encoder.writeObject(obj);
	    encoder.close();
	    xml = out.toString();
	}
	catch(Exception e) { }
	return xml;
    }

    public String toJson(String xml) { // option for requester
	try {
	    JSONObject jobt = XML.toJSONObject(xml);
	    return jobt.toString(3); // 3 is indentation level
	}
	catch(Exception e) { }
	return null;
    }

    public int addNovel(Novel novel) {
	int id = mapKey.incrementAndGet();
	novel.setId(id);
	novels.put(id, novel);
	return id;
    }

    private void populate() {
	InputStream in = sctx.getResourceAsStream(this.fileName);
	// Convert novel.db string data into novels.
	if (in != null) {
	    try {
		InputStreamReader isr = new InputStreamReader(in);
		BufferedReader reader = new BufferedReader(isr);

		String record = null;
		while ((record = reader.readLine()) != null) {
		    String[] parts = record.split("!");
		    if (parts.length == 2) {
			Novel novel = new Novel();
			novel.setAuthor(parts[0]);
			novel.setTitle(parts[1]);
			addNovel(novel); // sets the Id, adds to map
		    }
		}
		in.close();
	    }
	    catch (IOException e) { }
	}
    }
}
