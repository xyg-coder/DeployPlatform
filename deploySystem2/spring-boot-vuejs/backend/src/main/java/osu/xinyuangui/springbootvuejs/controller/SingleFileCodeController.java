package osu.xinyuangui.springbootvuejs.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import osu.xinyuangui.springbootvuejs.domain.SingleFileCode;
import osu.xinyuangui.springbootvuejs.domain.SingleFileCodeBrief;
import osu.xinyuangui.springbootvuejs.domain.SingleFileCodeType;
import osu.xinyuangui.springbootvuejs.service.SingleFileCodeService;

import java.io.IOException;
import java.util.List;

/**
 * This controller will handle the request for the single file controller
 */
@RestController
@RequestMapping("/api/single-file-code")
public class SingleFileCodeController {

    private static Logger logger = LoggerFactory.getLogger(SingleFileCodeController.class);

    @Autowired
    private SingleFileCodeService singleFileCodeService;

    @GetMapping(value = "")
    public @ResponseBody List<SingleFileCodeBrief> getSingleFileCodeInfos(
            @RequestParam("pageIndex") int pageIndex,
            @RequestParam("countPerPage") int countPerPage) {
        logger.info("Get Single File Code Infos");
        Pageable pageable = PageRequest.of(pageIndex, countPerPage);
        return singleFileCodeService.getSingleFileCodeInfosByPage(pageable);
    }

    @GetMapping("/count")
    public @ResponseBody int getTotalCount() {
        logger.info("Get total count of single file codes");
        return singleFileCodeService.getTotalCount();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity getSingleFileCode(@PathVariable("id")int id) {
        logger.info("Get Single File Code, id=" + id);
        try {
            SingleFileCode code = singleFileCodeService.getSingleFileCodeById(id);
            return ResponseEntity.ok(code);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @PostMapping("")
    public ResponseEntity createNewCode(SingleFileCode createdCode) {
        logger.info("Create new single file code");
        try {
            createdCode = singleFileCodeService.updateCode(createdCode);
            return ResponseEntity.ok(createdCode);
        } catch (IOException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity updateSingleFileCode(SingleFileCode newCode) {
        logger.info("update new code");
        try {
            newCode = singleFileCodeService.updateCode(newCode);
            return ResponseEntity.ok(newCode);
        } catch (IOException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @PostMapping(value = "run", params = {"id", "type", "stdin"})
    public ResponseEntity runCode(@RequestParam("id") int id,
                                  @RequestParam("type") String type,
                                  @RequestParam("stdin") String stdin) {
        logger.info("run code is called");
        return singleFileCodeService.runCode(id, type, stdin);
    }

    @GetMapping("mock")
    public ResponseEntity getMockData() {
        SingleFileCode code = new SingleFileCode();
        code.setType(SingleFileCodeType.JAVA);
        code.setName("code");
        code.setDescription("code");
        code.setCode("public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Hello world\");\n" +
                "    }\n" +
                "}");
        try {
            singleFileCodeService.updateCode(code);
        } catch (IOException e) {
            logger.error(e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
        return ResponseEntity.ok("saved");
    }

    @GetMapping(value = "mock", params = "id")
    public ResponseEntity mockWithId(@RequestParam("id") int id) {
        SingleFileCode code = new SingleFileCode();
        code.setId(id);
        code.setType(SingleFileCodeType.JAVA);
        code.setName("code " + id);
        code.setDescription("code");
        code.setCode("public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Hello world\");\n" +
                "    }\n" +
                "}");
        try {
            singleFileCodeService.updateCode(code);
        } catch (IOException e) {
            logger.error(e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
        return ResponseEntity.ok("saved");
    }
}
