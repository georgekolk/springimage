package com.zetcode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class InPowerWeEntrustStorageService implements StorageService{

    private final Path rootLocation;
    private final List dirs;
    Logger logger = LoggerFactory.getLogger(InPowerWeEntrustStorageService.class);


    @Autowired
    public InPowerWeEntrustStorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
        this.dirs = properties.getDirs();
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootLocation);

            for (Object dir:dirs) {
                logger.error("dirs " + dir.toString());
            }

        } catch (IOException e) {
            throw new StorageException("Could not initialize storage location", e);
        }
    }

    @Override
    public String store(MultipartFile file) {
        return null;
    }

    @Override
    public Stream<Path> loadAll() {
        try {

            return Files.walk(Paths.get("./uploads"), 1)
                    .filter(path -> !path.equals(Paths.get("./uploads")))
                    .map(Paths.get("./uploads")::relativize);

        } catch (IOException e) {

            e.printStackTrace();

            throw new StorageException("Failed to read stored files", e);
        }

    }


    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new FileNotFoundException(
                        "Could not read file: " + filename);
            }
        }
        catch (MalformedURLException e) {
            throw new FileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {

    }

}
