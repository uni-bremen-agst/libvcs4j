package de.unibremen.st.libvcs4j;

/**
 * Represents a change of a single line of text.
 */
@SuppressWarnings("unused")
public interface LineChange {

    /**
     * The type of a {@link LineChange}.
     */
    enum Type {
        /**
         * The {@link LineChange} is an insertion.
         */
        INSERT,

        /**
         * The {@link LineChange} is a deletion.
         */
        DELETE
    }

    /**
     * Returns the {@link Type} of this line change.
     *
     * @return
     *      The {@link Type} of this line change.
     */
    Type getType();

    /**
     * Returns the changed line (1 origin).
     *
     * @return
     *      The changed line (1 origin).
     */
    int getLine();

    /**
     * Returns the content of the changed line.
     *
     * @return
     *      The content of the changed line.
     */
    String getContent();

    /**
     * Returns the file this line change belongs to.
     *
     * @return
     *      The file this line change belongs to
     */
    VCSFile getFile();
}
