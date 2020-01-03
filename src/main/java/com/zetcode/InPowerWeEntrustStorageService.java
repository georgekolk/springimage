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
        return null;
    }

    @Override
    public Path load(String filename) {
        return null;
    }

    @Override
    public Resource loadAsResource(String filename) {
        return null;
    }

    //@Override
    public Stream<Path> loadAll(String blogname) {
        try {
            logger.error("loadAll InPower Service" + Paths.get("./" + blogname));

            return Files.walk(Paths.get("./"+blogname), 1)
                    .filter(path -> !path.equals(Paths.get("./"+blogname)))
                    .map(Paths.get("./"+blogname)::relativize);

        } catch (IOException e) {

            e.printStackTrace();

            throw new StorageException("Failed to read stored files", e);
        }

    }


    public Path load(String filename, String blogname) {
        return Paths.get("./"+blogname).resolve(filename);
    }

    public Resource loadAsResource(String filename, String blogname) {
        try {
            Path file = load(filename, blogname);
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
