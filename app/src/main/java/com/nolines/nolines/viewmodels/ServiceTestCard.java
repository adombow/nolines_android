package com.nolines.nolines.viewmodels;

/**
 * Created by timot on 3/27/2018.
 */

public class ServiceTestCard {
    private String description;
    private String buttonText;

    public ServiceTestCard(String description, String buttonText){
        this.description = description;
        this.buttonText = buttonText;
    }

    public String getDescription() {
        return description;
    }
    public String getButtonText() { return buttonText; }
}
