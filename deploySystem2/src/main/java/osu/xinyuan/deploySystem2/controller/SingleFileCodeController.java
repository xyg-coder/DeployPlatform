package osu.xinyuan.deploySystem2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import osu.xinyuan.deploySystem2.domain.SingleFileCode;
import osu.xinyuan.deploySystem2.service.SingleFileCodeService;

@Controller
@RequestMapping("/singleFileCode")
public class SingleFileCodeController {

    @Autowired
    private SingleFileCodeService singleFileCodeService;

    /**
     * get the single file code list
     * @return
     */
    @GetMapping("/")
    public ResponseEntity getSingleFileCodeList() {
        return ResponseEntity.ok(singleFileCodeService.getSingleFileCodeList());
    }

    /**
     * get the code detail page for specific id
     * @param id
     * @param model
     * @return
     * @throws IllegalArgumentException
     */
    @GetMapping("/{id}")
    public String getCodeDetail(@PathVariable("id") int id, Model model) throws IllegalArgumentException {
        SingleFileCode code = singleFileCodeService.findCodeById(id);
        if (code == null) {
            throw new IllegalArgumentException("No code with such id");
        }
        model.addAttribute("code_detail", code);
        return "single-file-code-detail";
    }

    @PostMapping("/{id}")
    public ResponseEntity updateCodeDetail(@ModelAttribute SingleFileCode codeDetail) {
        codeDetail = singleFileCodeService.updateSingleFileCode(codeDetail);
        return ResponseEntity.ok("Save success");
    }

    /**
     * create one new code
     * @param codeDetail
     * @return
     */
    @PostMapping("/")
    public String createCodeDetail(@ModelAttribute SingleFileCode codeDetail) {
        codeDetail = singleFileCodeService.updateSingleFileCode(codeDetail);
        return "redirect:/" + codeDetail.getId();
    }

    @GetMapping(value = "/{id}/result")
    public ResponseEntity runCode(@PathVariable("id") int id, @RequestParam("stdin") String stdin) {
        try {
            singleFileCodeService.runCode(id, stdin);
            return ResponseEntity.ok("Running code");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
