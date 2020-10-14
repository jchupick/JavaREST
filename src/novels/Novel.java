package novels;

import java.io.Serializable;

public class Novel implements Serializable, Comparable<Novel> {
    static final long serialVersionUID = 1L;
    private String author;
    private String title;
    private int id;

    public Novel() { }
    
    public void setAuthor(final String author) { this.author = author; }
    public String getAuthor() { return this.author; }
    public void setTitle(final String title) { this.title = title; }
    public String getTitle() { return this.title; }
    public void setId(final int id) { this.id = id; }
    public int getId() { return this.id; }

    public int compareTo(final Novel other) { return this.id - other.id; }
}
