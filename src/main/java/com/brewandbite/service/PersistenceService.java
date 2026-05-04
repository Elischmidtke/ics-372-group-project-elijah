package com.brewandbite.service;

import com.brewandbite.model.AppData;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class PersistenceService {

    private static final String DATA_DIR  =
            System.getProperty("user.home") + File.separator + ".brewandbite";
    private static final String DATA_FILE = DATA_DIR + File.separator + "appdata.json";
    private static final String SEED_PATH = "/com/brewandbite/data/seed_data.json";

    private final ObjectMapper mapper;

    public PersistenceService() {
        mapper = new ObjectMapper();

        // Pretty JSON output
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        // IMPORTANT: allow older/newer saved JSON files to load even if fields changed.
        // Fixes: Unrecognized field "displayType" (and similar future schema changes).
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        new File(DATA_DIR).mkdirs();
    }

    /**
     * Loads persisted data from disk. Falls back to the bundled seed file
     * if no saved data exists, and returns an empty AppData if the seed is also missing.
     */
    public AppData loadData() {
        File saved = new File(DATA_FILE);
        if (saved.exists()) {
            try {
                return mapper.readValue(saved, AppData.class);
            } catch (IOException e) {
                System.err.println("[Persistence] Saved data corrupt, loading seed: " + e.getMessage());
            }
        }
        return loadSeedData();
    }

    /** Loads the bundled seed_data.json from the classpath. */
    public AppData loadSeedData() {
        URL resource = getClass().getResource(SEED_PATH);
        if (resource != null) {
            try {
                return mapper.readValue(resource, AppData.class);
            } catch (IOException e) {
                System.err.println("[Persistence] Seed load failed: " + e.getMessage());
            }
        }
        System.err.println("[Persistence] Seed resource not found: " + SEED_PATH);
        return new AppData();
    }

    /** Writes the current application state to disk. */
    public void saveData(AppData data) {
        try {
            mapper.writeValue(new File(DATA_FILE), data);
        } catch (IOException e) {
            System.err.println("[Persistence] Save failed: " + e.getMessage());
        }
    }
}
