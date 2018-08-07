package com.mycompany.myproject.automation.tests.web.inheritance.parentexample;

public class Anthony extends Parents {

    public int howTall;
    public String personLikes;

    public Anthony(int howTall, String likesToDo, String homeland) {
        super(homeland);
        this.howTall = howTall;
        this.personLikes = likesToDo;
    }

    public int getHowTall() {
        return howTall;
    }

    public void setHowTall(int howTall) {
        this.howTall = howTall;
    }

    public String getPersonLikes() {
        return personLikes;
    }

    public void setPersonLikes(String personLikes) {
        this.personLikes = personLikes;
    }


}
