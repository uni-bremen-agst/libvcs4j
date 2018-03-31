package de.unibremen.st.libvcs4j.data;

import de.unibremen.st.libvcs4j.Issue;
import org.apache.commons.lang3.Validate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IssueImpl implements Issue {

    private String id;
    private String author;
    private String title;
    private LocalDateTime dateTime;
    private List<Comment> comments = Collections.emptyList();

    @Override
    public String getId() {
        return id;
    }

    public void setId(final String pId) {
        id = Validate.notNull(pId);
    }

    @Override
    public String getAuthor() {
        return author;
    }

    public void setAuthor(final String pAuthor) {
        author = Validate.notNull(pAuthor);
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(final String pTitle) {
        title = Validate.notNull(pTitle);
    }

    @Override
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(final LocalDateTime pDateTime) {
        dateTime = Validate.notNull(pDateTime);
    }

    @Override
    public List<Comment> getComments() {
        return new ArrayList<>(comments);
    }

    public void setComments(final List<Comment> pComments) {
        Validate.noNullElements(pComments);
        comments = new ArrayList<>(pComments);
    }
}
