/*
 * Copyright 2015 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.opencga.storage.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by imedina on 30/04/15.
 */
public class StorageConfiguration {

    private String defaultStorageEngineId;
    private String logLevel;
    private String logFile;
    private String studyMetadataManager;

    private CellBaseConfiguration cellbase;
    private QueryServerConfiguration server;

    private List<StorageEngineConfiguration> storageEngines;

    protected static Logger logger = LoggerFactory.getLogger(StorageConfiguration.class);

    public StorageConfiguration() {
        this("", new ArrayList<>());
    }

    public StorageConfiguration(String defaultStorageEngineId, List<StorageEngineConfiguration> storageEngines) {
        this.defaultStorageEngineId = defaultStorageEngineId;
        this.storageEngines = storageEngines;

        this.cellbase = new CellBaseConfiguration();
        this.server = new QueryServerConfiguration();
    }

    /**
     * This method attempts to find and load the configuration from installation directory,
     * if not exists then loads JAR storage-configuration.yml
     * @throws IOException
     */
    @Deprecated
    public static StorageConfiguration load() throws IOException {
        String appHome = System.getProperty("app.home", System.getenv("OPENCGA_HOME"));
        Path path = Paths.get(appHome + "/conf/storage-configuration.yml");
        if (appHome != null && Files.exists(path)) {
            logger.debug("Loading configuration from '{}'", appHome + "/conf/storage-configuration.yml");
            return StorageConfiguration
                    .load(new FileInputStream(new File(appHome + "/conf/storage-configuration.yml")));
        } else {
            logger.debug("Loading configuration from '{}'",
                    StorageConfiguration.class.getClassLoader()
                            .getResourceAsStream("storage-configuration.yml")
                            .toString());
            return StorageConfiguration
                    .load(StorageConfiguration.class.getClassLoader().getResourceAsStream("storage-configuration.yml"));
        }
    }

    public static StorageConfiguration load(InputStream configurationInputStream) throws IOException {
        return load(configurationInputStream, "yaml");
    }

    public static StorageConfiguration load(InputStream configurationInputStream, String format) throws IOException {
        StorageConfiguration storageConfiguration;
        ObjectMapper objectMapper;
        switch (format) {
            case "json":
                objectMapper = new ObjectMapper();
                storageConfiguration = objectMapper.readValue(configurationInputStream, StorageConfiguration.class);
                break;
            case "yml":
            case "yaml":
            default:
                objectMapper = new ObjectMapper(new YAMLFactory());
                storageConfiguration = objectMapper.readValue(configurationInputStream, StorageConfiguration.class);
                break;
        }

        if(storageConfiguration != null) {
//            if (storageConfiguration.getDefaultStorageEngineId() != null) {
//                this.defaultStorageEngineId = storageConfiguration.getDefaultStorageEngineId();
//            }
//            if (storageConfiguration.get) {
//                this.include = sto
//                  for(conf.d) ...
//            }
//            for (StorageEngineConfiguration storageEngineConfiguration : storageConfiguration.getStorageEngines()) {
//                this.storageEngines.add(storageEngineConfiguration);
//            }
        }
        return storageConfiguration;
    }

    public void serialize(OutputStream configurationOututStream) throws IOException {
        ObjectMapper jsonMapper = new ObjectMapper(new YAMLFactory());
        jsonMapper.writerWithDefaultPrettyPrinter().writeValue(configurationOututStream, this);
    }

    public StorageEngineConfiguration getStorageEngine() {
        return getStorageEngine(defaultStorageEngineId);
    }

    public StorageEngineConfiguration getStorageEngine(String storageEngine) {
        StorageEngineConfiguration storageEngineConfiguration = null;
        for (StorageEngineConfiguration engine : storageEngines) {
            if (engine.getId().equals(storageEngine)) {
                storageEngineConfiguration = engine;
                break;
            }
        }

        if(storageEngineConfiguration == null && storageEngines.size() > 0) {
            storageEngineConfiguration = storageEngines.get(0);
        }

        return storageEngineConfiguration;
    }

    public void addStorageEngine(InputStream configurationInputStream) throws IOException {
        addStorageEngine(configurationInputStream, "yaml");
    }

    public void addStorageEngine(InputStream configurationInputStream, String format) throws IOException {
        StorageEngineConfiguration storageEngineConfiguration = null;
        ObjectMapper objectMapper;
        switch (format) {
            case "json":
                objectMapper = new ObjectMapper();
                storageEngineConfiguration = objectMapper.readValue(configurationInputStream, StorageEngineConfiguration.class);
                break;
            case "yml":
            case "yaml":
            default:
                objectMapper = new ObjectMapper(new YAMLFactory());
                storageEngineConfiguration = objectMapper.readValue(configurationInputStream, StorageEngineConfiguration.class);
                break;
        }

        if(storageEngineConfiguration != null) {
            this.storageEngines.add(storageEngineConfiguration);
        }
    }

    @Override
    public String toString() {
        return "StorageConfiguration{" +
                "defaultStorageEngineId='" + defaultStorageEngineId + '\'' +
                ", logLevel='" + logLevel + '\'' +
                ", logFile='" + logFile + '\'' +
                ", studyMetadataManager='" + studyMetadataManager + '\'' +
                ", cellbase=" + cellbase +
                ", server=" + server +
                ", storageEngines=" + storageEngines +
                '}';
    }

    public String getDefaultStorageEngineId() {
        return defaultStorageEngineId;
    }

    public void setDefaultStorageEngineId(String defaultStorageEngineId) {
        this.defaultStorageEngineId = defaultStorageEngineId;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public String getLogFile() {
        return logFile;
    }

    public void setLogFile(String logFile) {
        this.logFile = logFile;
    }

    public String getStudyMetadataManager() {
        return studyMetadataManager;
    }

    public void setStudyMetadataManager(String studyMetadataManager) {
        this.studyMetadataManager = studyMetadataManager;
    }

    public CellBaseConfiguration getCellbase() {
        return cellbase;
    }

    public void setCellbase(CellBaseConfiguration cellbase) {
        this.cellbase = cellbase;
    }

    public QueryServerConfiguration getServer() {
        return server;
    }

    public void setServer(QueryServerConfiguration server) {
        this.server = server;
    }

    public List<StorageEngineConfiguration> getStorageEngines() {
        return storageEngines;
    }

    public void setStorageEngines(List<StorageEngineConfiguration> storageEngines) {
        this.storageEngines = storageEngines;
    }

    //    public String getDefaultStorageEngineId() {
//        return defaultStorageEngineId;
//    }
//
//    public void setDefaultStorageEngineId(String defaultStorageEngineId) {
//        this.defaultStorageEngineId = defaultStorageEngineId;
//    }
//
//    public String getLogLevel() {
//        return logLevel;
//    }
//
//    public void setLogLevel(String logLevel) {
//        this.logLevel = logLevel;
//    }
//
//    public String getLogFile() {
//        return logFile;
//    }
//
//    public void setLogFile(String logFile) {
//        this.logFile = logFile;
//    }
//
//    public CellBaseConfiguration getCellbase() {
//        return cellbase;
//    }
//
//    public void setCellbase(CellBaseConfiguration cellbase) {
//        this.cellbase = cellbase;
//    }
//
//    public QueryServerConfiguration getServer() {
//        return server;
//    }
//
//    public void setServer(QueryServerConfiguration server) {
//        this.server = server;
//    }
//
//    public List<StorageEngineConfiguration> getStorageEngines() {
//        return storageEngines;
//    }
//
//    public void setStorageEngines(List<StorageEngineConfiguration> storageEngines) {
//        this.storageEngines = storageEngines;
//    }
//
//    public String getStudyMetadataManager() {
//        return studyMetadataManager;
//    }
//
//    public void setStudyMetadataManager(String studyMetadataManager) {
//        this.studyMetadataManager = studyMetadataManager;
//    }
}
