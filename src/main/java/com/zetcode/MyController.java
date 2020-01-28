package com.zetcode.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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

   private static List<Person> persons = new ArrayList<Person>();
   private static List<Blog> dirs = new ArrayList<Blog>();
    private static List<ImageFile> imageFileList = new ArrayList<ImageFile>();


    Logger logger = LoggerFactory.getLogger(MyController.class);

    private StorageService storageService;
    private InPowerWeEntrustStorageService inPowerWeEntrustStorageService;
    private SQLiteService sqLiteService;

    public MyController(StorageService storageService, InPowerWeEntrustStorageService inPowerWeEntrustStorageService, SQLiteService sqLiteService) {
        this.storageService = storageService;
        this.inPowerWeEntrustStorageService = inPowerWeEntrustStorageService;
        this.sqLiteService = sqLiteService;


    }
 
    static {
        persons.add(new Person("Bill", "Gates"));
        persons.add(new Person("Steve", "Jobs"));
    }
 
    @Value("${welcome.message}")
    private String message;
 
    @Value("${error.message}")
    private String errorMessage;

    @GetMapping("/lol")
    public String index() {
        return "ajax";
    }


    @RequestMapping(value = { "/personList" }, method = RequestMethod.GET)
    public String personList(Model model) {
 
        model.addAttribute("persons", persons);
 
        return "personList";
    }
 
    @RequestMapping(value = { "/addPerson" }, method = RequestMethod.GET)
    public String showAddPersonPage(Model model) {
 
        PersonForm personForm = new PersonForm();
        model.addAttribute("personForm", personForm);
 
        return "addPerson";
    }
 
    @RequestMapping(value = { "/addPerson" }, method = RequestMethod.POST)
    public String savePerson(Model model, @ModelAttribute("personForm") PersonForm personForm) {
 
        String firstName = personForm.getFirstName();
        String lastName = personForm.getLastName();
 
        if (firstName != null && firstName.length() > 0 //
                && lastName != null && lastName.length() > 0) {
            Person newPerson = new Person(firstName, lastName);
            persons.add(newPerson);
 
            return "redirect:/personList";
        }
 
        model.addAttribute("errorMessage", errorMessage);
        return "addPerson";
    }

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

    @RequestMapping(value = "/getdirs/{blogname}/mp4", method = RequestMethod.GET)
    public String galleryToday(@PathVariable String blogname, Model model) throws IOException {

        logger.error("/getdirs/{"+  blogname + "}");

        if (!inPowerWeEntrustStorageService.checkMap(blogname.toLowerCase())){

            model.addAttribute("message", "OMFG! NOT FOUND DIRS " + blogname);

            return "index";

        }else {

            List<ImageFile> imageFileList = inPowerWeEntrustStorageService.loadAllmp4(blogname);

            for (ImageFile imageFile : imageFileList) {
                imageFile.setURI(ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/getdirs/" + blogname + "/")
                        .path(imageFile.getPath().getFileName().toString())
                        .toUriString());
                imageFile.setBlog(blogname);
                imageFile.setFileNameId(imageFile.getFileName().substring(0, imageFile.getFileName().lastIndexOf('.')));
            }

            model.addAttribute("files", imageFileList);

            return "listVids";
        }
    }

    @DeleteMapping(value="/remove/{blogname}/{filename}")
    public ResponseEntity<Long> removePhotos(@PathVariable String filename, @PathVariable String blogname ){

        System.out.println(filename + " " + blogname);
        //var isRemoved = postService.delete(id);
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