package com.myra.dev.marian.utilities.language;

import java.util.ResourceBundle;

public class LanguageBundle {
    private final ResourceBundle bundle;

    public LanguageBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public String get(String target) {
        return this.bundle.getString(target);
    }

    public String[] getArray(String target) {
        return this.bundle.getString(target).split(",");
    }
}
