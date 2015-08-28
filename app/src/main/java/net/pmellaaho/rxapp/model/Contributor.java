package net.pmellaaho.rxapp.model;

public class Contributor {
    private String login;
    private long contributions;

    public Contributor() {}

    public Contributor(String login, long contributions) {
        this.login = login;
        this.contributions = contributions;
    }

    public String login() {
        return login;
    }

    public long contributions() {
        return contributions;
    }

}
