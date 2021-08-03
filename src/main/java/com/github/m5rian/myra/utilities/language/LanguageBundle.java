package com.github.m5rian.myra.utilities.language;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class LanguageBundle {
    private final ResourceBundle bundle;

    public LanguageBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public String get(String target) {
        try {
            return this.bundle.getString(target);
        }
        // Couldn't find string
        catch (MissingResourceException e) {
            return new LanguageBundle(ResourceBundle.getBundle("languages/" + Lang.Country.ENGLISH.getIsoCode())).get(target);
        }

    }

    public String[] getArray(String target) {
        return this.bundle.getString(target).split(",");
    }
}
