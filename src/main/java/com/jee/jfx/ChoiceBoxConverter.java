package com.jee.jfx;

import javafx.util.Pair;
import javafx.util.StringConverter;

/**
 * @program: WebVideoDownloader
 * @description:
 * @author: animal
 * @create: 2023-06-20 14:07
 **/
public class ChoiceBoxConverter extends StringConverter<Pair<Integer, String>> {
    @Override
    public String toString(Pair<Integer, String> pair) {
        return pair.getValue();
    }

    @Override
    public Pair<Integer, String> fromString(String str) {
        return null;
    }
}
