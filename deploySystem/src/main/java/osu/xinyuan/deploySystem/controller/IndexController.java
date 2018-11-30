package osu.xinyuan.deploySystem.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import osu.xinyuan.deploySystem.domains.JavaProjectInfo;
import osu.xinyuan.deploySystem.services.JavaProjectService;
import osu.xinyuan.deploySystem.util.ShellUtil;

import java.io.IOException;

@Controller
@RequestMapping("/")
public class IndexController {
    @Autowired
    private JavaProjectService javaProjectService;

    private Logger logger = LoggerFactory.getLogger(IndexController.class);


    public JavaProjectService getJavaProjectService() {
        return javaProjectService;
    }

    public void setJavaProjectService(JavaProjectService javaProjectService) {
        this.javaProjectService = javaProjectService;
    }

    @GetMapping("/")
    public String projectList(Model model) {
        model.addAttribute("javaProjects", javaProjectService.getAllJavaProjects());
        return "project-list";
    }

    @GetMapping("/new-java-project")
    public String newJavaProject() {
        return "new-java-project";
    }

    @PostMapping(value = "/new-java-project", consumes = {"application/x-www-form-urlencoded"})
    public String createNewJavaProject(JavaProjectInfo info) {
        try {
            javaProjectService.addJavaProject(info);
        } catch (IOException e) {
            logger.error("createNewJavaProject", e.getMessage());
        }
        return "redirect:/";
    }

    /**
     * check whether the url is valid
     * @param url
     * @return
     */
    @GetMapping(value = "/new-java-project", params = "url")
    public ResponseEntity checkUrl(@RequestParam("url") String url) {
        try {
            boolean isValid = ShellUtil.isValidRepo(url);
            if (isValid) {
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body("valid url");
            } else {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("invalid url");
            }
        } catch (IOException e) {
            logger.error("checkUrl", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("server error");
        }
    }


}
