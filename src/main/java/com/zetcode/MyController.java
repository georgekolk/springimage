package com.zetcode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.zetcode.*;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@Controller
public class MyController {

    private static List<Blog> dirs = new ArrayList<Blog>();
    private static List<ImageFile> imageFileList = new ArrayList<ImageFile>();
    private static List<ImageFile> imageFileListToRemove = new ArrayList<ImageFile>();


    Logger logger = LoggerFactory.getLogger(MyController.class);

    private StorageService storageService;
    private InPowerWeEntrustStorageService inPowerWeEntrustStorageService;

    public MyController(StorageService storageService, InPowerWeEntrustStorageService inPowerWeEntrustStorageService) {
        this.storageService = storageService;
        this.inPowerWeEntrustStorageService = inPowerWeEntrustStorageService;
    }
 
    @Value("${welcome.message}")
    private String message;
 
    @Value("${error.message}")
    private String errorMessage;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String gallery(Model model) throws IOException {

        dirs.clear();

        for (Map.Entry<String,Path> entry : inPowerWeEntrustStorageService.getDirLocations().entrySet())
            dirs.add(new Blog(entry.getKey(), ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/getdirs/" + entry.getKey() + "/")
                    .toUriString(),"not date a gate"));

        model.addAttribute("dirs", dirs);
        return "listDirs";
    }

    @RequestMapping(value = "/getdirs/{blogname}/latest", method = RequestMethod.GET)
    public String galleryToday(@PathVariable String blogname, Model model) throws IOException {

        if (!inPowerWeEntrustStorageService.checkMap(blogname.toLowerCase())){
            model.addAttribute("message", "OMFG! NOT FOUND DIRS " + blogname);
            return "index";
        }else {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            String currentDate = sdf.format(new Date());

            List<ImageFile> imageFileList = inPowerWeEntrustStorageService.loadAll(blogname);

            for (ImageFile imageFile : imageFileList) {
                imageFile.setURI(ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/getdirs/" + blogname + "/")
                        .path(imageFile.getPath().getFileName().toString())
                        .toUriString());
                imageFile.setBlog(blogname);
                imageFile.setFileNameId(imageFile.getFileName().substring(0, imageFile.getFileName().lastIndexOf('.')));

                if (!currentDate.equals(sdf.format(imageFile.getLastModified()))){
                    imageFileListToRemove.add(imageFile);
                }
            }

            imageFileList.removeAll(imageFileListToRemove);

            model.addAttribute("files", imageFileList);

            return "listFiles";
        }
    }

    @DeleteMapping(value="/remove/{blogname}/{filename}")
    public ResponseEntity<Long> removePhotos(@PathVariable String filename, @PathVariable String blogname ){

        System.out.println(filename + " " + blogname);
        //var isRemoved = postService.delete(id);

        inPowerWeEntrustStorageService.delete(filename,blogname);

        return new ResponseEntity<>(666l, HttpStatus.OK);
    }

    @DeleteMapping(value="/approve/{blogname}/{filename}")
    public ResponseEntity<Long> approvePhotos(@PathVariable String filename, @PathVariable String blogname ){

        System.out.println(filename + " " + blogname);
        //var isRemoved = postService.delete(id);
        inPowerWeEntrustStorageService.approve(filename, blogname);
        return new ResponseEntity<>(666l, HttpStatus.OK);
    }

    @GetMapping(value="/getdirs/{blogname}")
    public String getdirs(Model model, @PathVariable String blogname) {

        logger.error("/getdirs/{"+  blogname + "}");

        if (!inPowerWeEntrustStorageService.checkMap(blogname.toLowerCase())){

                model.addAttribute("message", "OMFG! NOT FOUND DIRS " + blogname);

                return "index";

        }else {

            List<ImageFile> imageFileList =  inPowerWeEntrustStorageService.loadAll(blogname);

            for (ImageFile imageFile: imageFileList) {
                imageFile.setURI(ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/getdirs/" + blogname + "/")
                        .path(imageFile.getPath().getFileName().toString())
                        .toUriString());
                imageFile.setBlog(blogname);
                imageFile.setFileNameId(imageFile.getFileName().substring(0, imageFile.getFileName().lastIndexOf('.')));
            }

            model.addAttribute("files", imageFileList);

            return "listFiles";
        }
    }

    @GetMapping("/getdirs/{blogname}/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFileFromBlog(@PathVariable String filename, @PathVariable String blogname) {

        Resource resource = inPowerWeEntrustStorageService.loadAsResource(filename, blogname);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/download/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {

        Resource resource = storageService.loadAsResource(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @PostMapping("/upload-file")
    @ResponseBody
    public FileResponse uploadFile(@RequestParam("file") MultipartFile file) {
        String name = storageService.store(file);

        String uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/download/")
                .path(name)
                .toUriString();

        return new FileResponse(name, uri, file.getContentType(), file.getSize());
    }

    @PostMapping("/upload-multiple-files")
    @ResponseBody
    public List<FileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        return Arrays.stream(files)
                .map(file -> uploadFile(file))
                .collect(Collectors.toList());
    }


}