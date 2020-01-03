package com.zetcode.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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


//@RestController
@Controller
public class MyController {

   private static List<Person> persons = new ArrayList<Person>();

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
 
    /*@RequestMapping(value = { "/index" }, method = RequestMethod.GET)
    public String index(Model model) {
 
        model.addAttribute("message", message);
 
        return "index";
    }*/
 
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
/*-------------------------------------------------------------------------------------------*/

    @RequestMapping(value = "/sid", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<InputStreamResource> getImage() throws IOException {

        var imgFile = new ClassPathResource("image/sid.jpg");

        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(new InputStreamResource(imgFile.getInputStream()));
    }

    @RequestMapping(value = "/nancy", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<InputStreamResource> getImage2() throws IOException {

          var kek = new ClassPathResource("file:C:/Documents and Settings/admin/AppData/Local/Temp/a456eaa5-5d96-4250-b2f4-cc1efeac0baf.jpg");
        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(new InputStreamResource(kek.getInputStream()));
    }                                                   

    @RequestMapping(value = "/gallery", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<InputStreamResource> gallery() throws IOException {

          var kek = new ClassPathResource("file:C:/Documents and Settings/admin/AppData/Local/Temp/a456eaa5-5d96-4250-b2f4-cc1efeac0baf.jpg");
        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(new InputStreamResource(kek.getInputStream()));
    }

    @GetMapping("/image")
    public String findPhotos(Model model)  {
        List<String> userImages = new ArrayList<>();
        //userImages.add("image/sid.jpg");
        //userImages.add("file:C:/Documents and Settings/admin/AppData/Local/Temp/a456eaa5-5d96-4250-b2f4-cc1efeac0baf.jpg");

        File dir = new File("C:\\KKK\\springimage\\test");

        /*for (File file:dir.listFiles()) {
            if (file.getName().endsWith(".jpg")) {
                try {
                    FileInputStream fileInputStreamReader = new FileInputStream(file);
                    byte[] bytes = new byte[(int)file.length()];
                    fileInputStreamReader.read(bytes);
                    userImages.add(new String(Base64.encodeBase64(bytes), "UTF-8"));

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }*/

        try {
        //    new InputStreamResource(kK.getInputStream());
            File file = new File("C:\\Documents and Settings\\admin\\AppData\\Local\\Temp\\a456eaa5-5d96-4250-b2f4-cc1efeac0baf.jpg");
            FileInputStream fileInputStreamReader = new FileInputStream(file);
            byte[] bytes = new byte[(int)file.length()];
            fileInputStreamReader.read(bytes);
            userImages.add(new String(Base64.encodeBase64(bytes), "UTF-8"));

        } catch (IOException e) {
            e.printStackTrace();
        }

        model.addAttribute("files", userImages);

        return "image";

    }

    @GetMapping(value="/hello", produces = MediaType.TEXT_PLAIN_VALUE)
    public String sayHello() {

        return "Hello there!";
    }

    @GetMapping(value="/")
    public String index(Model model) {
        model.addAttribute("files", storageService.loadAll().map(
                path -> ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/download/")
                        .path(path.getFileName().toString())
                        .toUriString())
                .collect(Collectors.toList()));

        return "listFiles";
    }

    @GetMapping(value="/getdirs/{blogname}")
    public String getdirs(Model model, @PathVariable String blogname) {

        logger.error("/getdirs/{"+  blogname + "}");

        model.addAttribute("files", inPowerWeEntrustStorageService.loadAll(blogname).map(
                path -> ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/getdirs/" + blogname  + "/")
                        .path(path.getFileName().toString())
                        .toUriString())
                .collect(Collectors.toList()));

        return "listFiles";
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