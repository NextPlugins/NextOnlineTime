package com.nextplugins.onlinetime.manager;

import com.google.inject.Singleton;
import com.nextplugins.onlinetime.api.conversion.Conversor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Singleton
public class ConversorManager {

    private final List<Conversor> conversors = new ArrayList<>();

    public Conversor getByName(String name) {

        return conversors.stream()
                .filter(conversor -> conversor.getConversorName().equalsIgnoreCase(name))
                .findAny()
                .orElse(null);

    }

    public List<String> avaliableConversors() {

        return conversors.stream()
                .map(Conversor::getConversorName)
                .collect(Collectors.toList());

    }

    public void registerConversor(Conversor conversor) {
        conversors.add(conversor);
    }

}
