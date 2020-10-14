package novels;

import java.util.concurrent.ConcurrentMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.beans.XMLEncoder;
import org.json.JSONObject;
import org.json.XML;

public class NovelsServlet extends HttpServlet {
    static final long serialVersionUID = 1L;
    private Novels novels; // back-end bean

    // Executed when servlet is first loaded into container.
    @Override
    public void init() {
	this.novels = new Novels();
	novels.setServletContext(this.getServletContext());
    }

    // GET /novels
    // GET /novels?id=1
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
	String param = request.getParameter("id");
	Integer key = (param == null) ? null : Integer.valueOf((param.trim()));

	// Check user preference for XML or JSON by inspecting
	// the HTTP headers for the Accept key.
	boolean json = false;
	String accept = request.getHeader("accept");
	if (accept != null && accept.contains("json")) json = true;
	
        // If no query string, assume client wants the full list.
        if (key == null) {
	    ConcurrentMap<Integer, Novel> map = novels.getConcurrentMap();
	    Object[] list = map.values().toArray();
	    Arrays.sort(list);

	    String payload = novels.toXml(list);        // defaults to Xml
	    if (json) payload = novels.toJson(payload); // Json preferred?
	    sendResponse(response, payload);
	}
	// Otherwise, return the specified Novel.
	else {
	    Novel novel = novels.getConcurrentMap().get(key);
	    if (novel == null) { // no such Novel
		String msg = key + " does not map to a novel.\n";
		sendResponse(response, novels.toXml(msg));
	    }
	    else { // requested Novel found
		if (json) sendResponse(response, novels.toJson(novels.toXml(novel)));
		else sendResponse(response, novels.toXml(novel));
	    }
	}
    }

    // POST /novels
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
	String author = request.getParameter("author");
	String title = request.getParameter("title");

	// Are the data to create a new novel present?
        if (author == null || title == null)
            throw new RuntimeException(Integer.toString(HttpServletResponse.SC_BAD_REQUEST));

	// Create a novel.
	Novel n = new Novel();
	n.setAuthor(author);
	n.setTitle(title);

	// Save the ID of the newly created Novel.
	int id = novels.addNovel(n);

	// Generate the confirmation message.
	String msg = "Novel " + id + " created.\n";
	sendResponse(response, novels.toXml(msg));
    }

    // PUT /novels
    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) {
	/* A workaround is necessary for a PUT request because Tomcat does not
	   generate a workable parameter map for the PUT verb. */
	String key = null;
	String rest = null;
	boolean author = false;
	
	/* Let the hack begin. */
	try {
	    BufferedReader br = 
		new BufferedReader(new InputStreamReader(request.getInputStream()));
	    String data = br.readLine();
	    /* To simplify the hack, assume that the PUT request has exactly
	       two parameters: the id and either author or title. Assume, further,
	       that the id comes first. From the client side, a hash character
	       # separates the id and the author/title, e.g.,
	       
	          id=33#title=War and Peace
	    */
	    String[] args = data.split("#");      // id in args[0], rest in args[1]
	    String[] parts1 = args[0].split("="); // id = parts1[1]
	    key = parts1[1];

	    String[] parts2 = args[1].split("="); // parts2[0] is key 
	    if (parts2[0].contains("author")) author = true;
	    rest = parts2[1];
	}
	catch(Exception e) { 
	    throw new RuntimeException(Integer.toString(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
	}

	// If no key, then the request is ill formed.
	if (key == null)
	    throw new RuntimeException(Integer.toString(HttpServletResponse.SC_BAD_REQUEST));

	// Look up the specified novel.
	Novel p = novels.getConcurrentMap().get(Integer.valueOf((key.trim())));
	if (p == null) { // not found
	    String msg = key + " does not map to a novel.\n";
	    sendResponse(response, novels.toXml(msg));
	}
	else { // found
	    if (rest == null) {
		throw new RuntimeException(Integer.toString(HttpServletResponse.SC_BAD_REQUEST));
	    }
	    // Do the editing.
	    else {
		if (author) p.setAuthor(rest);
		else p.setTitle(rest);

		String msg = "Novel " + key + " has been edited.\n";
		sendResponse(response, novels.toXml(msg));
	    }
	}
    }

    // DELETE /novels?id=1
    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) {
        String param = request.getParameter("id");
	Integer key = (param == null) ? null : Integer.valueOf((param.trim()));
        // Only one Novel can be deleted at a time.
        if (key == null)
            throw new RuntimeException(Integer.toString(HttpServletResponse.SC_BAD_REQUEST));
        try {
	    novels.getConcurrentMap().remove(key);
	    String msg = "Novel " + key + " removed.\n";
	    sendResponse(response, novels.toXml(msg));
        }
        catch(Exception e) {
            throw new RuntimeException(Integer.toString(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
        }
    }

    // Methods Not Allowed
    @Override
    public void doTrace(HttpServletRequest request, HttpServletResponse response) {
        throw new RuntimeException(Integer.toString(HttpServletResponse.SC_METHOD_NOT_ALLOWED));
    }

    @Override
    public void doHead(HttpServletRequest request, HttpServletResponse response) {
        throw new RuntimeException(Integer.toString(HttpServletResponse.SC_METHOD_NOT_ALLOWED));
    }

    @Override
    public void doOptions(HttpServletRequest request, HttpServletResponse response) {
        throw new RuntimeException(Integer.toString(HttpServletResponse.SC_METHOD_NOT_ALLOWED));
    }

    // Send the response payload (Xml or Json) to the client.
    private void sendResponse(HttpServletResponse response, String payload) {
	try {
	    OutputStream out = response.getOutputStream();
	    out.write(payload.getBytes());
	    out.flush();
	}
	catch(Exception e) {
	    throw new RuntimeException(Integer.toString(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
	}
    }
}     
