package esprit.tn.entities;

public class Quote {
    private String quote;
    private String author;

    // Constructeur
    public Quote(String quote, String author) {
        this.quote = quote;
        this.author = author;
    }

    // Getters et Setters
    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "\"" + quote + "\" - " + author;
    }
}