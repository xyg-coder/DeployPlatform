package osu.xinyuan.deploySystem.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import osu.xinyuan.deploySystem.domains.JavaProjectInfo;
import osu.xinyuan.deploySystem.services.JavaProjectService;
import osu.xinyuan.deploySystem.util.Util;

import java.io.IOException;

@Controller
@RequestMapping("/java-project")
public class JavaProjectController {

    private Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private JavaProjectService javaProjectService;

    @PutMapping(value = "/status")
    public ResponseEntity deployJavaProject(@RequestParam("id") int id,
                                            @RequestParam("action") String action) {
        if (action.equals("deploy")) {
            try {
                javaProjectService.deploy(id);
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body("start depolying");
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(e.toString());
            }
        } else if (action.equals("run")) {
            try {
                javaProjectService.start(id);
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body("start running");
            } catch (IOException e) {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(e.getMessage());
            }
        } else if (action.equals("stopRun")) {
            try {
                javaProjectService.stop(id);
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body("stop running");
            } catch (IOException e) {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(e.getMessage());
            }
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Unsupported action");
        }
    }

    @GetMapping(value = "/{id}")
    public String getJavaProjectDetail(@PathVariable("id") int id, Model model) {
        model.addAttribute("javaProject", javaProjectService.getJavaProjectById(id));
        return "java-project-detail";
    }

    @PostMapping(value = "/{id}")
    public String updateJavaProject(@PathVariable("id") int id, @ModelAttribute JavaProjectInfo info) {
        javaProjectService.update(info);
        return "redirect:/java-project/" + id;
    }

    @GetMapping(value = "status")
    public ResponseEntity getProjectStatus(@RequestParam("id") int id) {
        try {
            return ResponseEntity.ok(javaProjectService.getProjectStatus(id).name());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("no such id");
        }
    }

    /**
     * receive the status change of Java Project, send the new status to client through logWebsocket
     * @param msg
     */
    @JmsListener(destination = "javaProjectStatus", containerFactory = "listenerContainerFactory")
    public void receiveJMS(String msg) {
        logger.info("receiveJMS get msg: " + msg);

        Util.IdAndJavaStatus idAndJavaStatus = Util.parseStr(msg);
        javaProjectService.updateStatus(idAndJavaStatus.id, idAndJavaStatus.javaProjectStatus);
        simpMessagingTemplate.convertAndSend("/topic/status-change", msg);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteJavaProject(@PathVariable("id") int id) {
        try {
            javaProjectService.delete(id);
            return ResponseEntity.ok("Delete Successfully");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }
}
