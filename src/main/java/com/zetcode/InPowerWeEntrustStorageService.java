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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class InPowerWeEntrustStorageService implements StorageService{

    private final Path rootLocation;
    private final List<String> dirs;
    Logger logger = LoggerFactory.getLogger(InPowerWeEntrustStorageService.class);
    private final HashMap<String, Path> dirLocations;

    @Autowired
    public InPowerWeEntrustStorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
        this.dirs = properties.getDirs();
        dirLocations = new HashMap<String, Path>();
    }

    @PostConstruct
    public void init() {

            for (String dir:dirs) {
                logger.error("dirs " + dir.toString());
                dirLocations.put(dir.substring(dir.lastIndexOf("/")+1), Paths.get(dir));
            }

    }

    public HashMap<String, Path> getDirLocations(){
        return this.dirLocations;
    }

    public boolean checkMap(String mapKey){
        return dirLocations.containsKey(mapKey);
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

    public List<ImageFile> loadAll(String blogname) {
        try {

            return Files.walk(dirLocations.get(blogname), 1)
                    .filter(path -> !path.equals(dirLocations.get(blogname)))
                    .filter(p -> p.toString().endsWith(".jpg"))
                    .map(ImageFile::new).collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
            throw new StorageException("Failed to read stored files", e);
        }
    }

    public List<ImageFile> loadAllmp4(String blogname) {
        try {

            return Files.walk(dirLocations.get(blogname), 1)
                    .filter(path -> !path.equals(dirLocations.get(blogname)))
                    .filter(p -> p.toString().endsWith(".mp4"))
                    .map(ImageFile::new).collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
            throw new StorageException("Failed to read stored files", e);
        }
    }

    public List<ImageFile> loadToday(String blogname) {
        try {

            return Files.walk(dirLocations.get(blogname), 1)
                    .filter(path -> !path.equals(dirLocations.get(blogname)))
                    .filter(p -> p.toString().endsWith(".jpg"))
                    //.map(Paths.get("C:/jHateSMMTemp/asdasd")::relativize)
                    .map(ImageFile::new).collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
            throw new StorageException("Failed to read stored files", e);
        }
    }

    public Path load(String filename, String blogname) {
        return dirLocations.get(blogname).resolve(filename);
    }

    public Resource loadAsResource(String filename, String blogname) {
        try {
            Path file = load(filename, blogname);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new FileNotFoundException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new FileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {

    }

}
