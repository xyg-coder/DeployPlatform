package osu.xinyuan.deploySystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import osu.xinyuan.deploySystem.services.JavaProjectService;
import osu.xinyuan.deploySystem.util.DeployFailureException;

@Controller
@RequestMapping("/java-project")
public class JavaProjectController {

    @Autowired
    private JavaProjectService javaProjectService;

    @PutMapping(value = "/{id}/deploy")
    public ResponseEntity deployJavaProject(@PathVariable("id") int id) {
        try {
            javaProjectService.deploy(id);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("start depolying");
        } catch (DeployFailureException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Deploy failure");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.toString());
        }
    }
}
