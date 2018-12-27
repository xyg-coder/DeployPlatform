package osu.xinyuangui.springbootvuejs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import osu.xinyuangui.springbootvuejs.domain.SingleFileCodeInfo;
import osu.xinyuangui.springbootvuejs.domain.SingleFileCodeBrief;
import osu.xinyuangui.springbootvuejs.domain.SingleFileCodeType;
import osu.xinyuangui.springbootvuejs.service.SingleFileCodeService;

import java.util.List;

/**
 * This controller will handle the request for the single file controller
 */
@RestController
@RequestMapping("/api/single-file-code")
public class SingleFileCodeController {

    @Autowired
    private SingleFileCodeService singleFileCodeService;

    @GetMapping("")
    public @ResponseBody List<SingleFileCodeBrief> getSingleFileCodeInfos() {
        return singleFileCodeService.getSingleFileCodeInfos();
    }

    @GetMapping(value = "", params = "id")
    public @ResponseBody
    SingleFileCodeInfo getSingleFileCode(@RequestParam("id")int id) {
        SingleFileCodeInfo code = singleFileCodeService.getSingleFileCodeById(id);
        return code;
    }

    @GetMapping("mock")
    public ResponseEntity getMockData() {
        SingleFileCodeInfo code = new SingleFileCodeInfo();
        code.setType(SingleFileCodeType.JAVA);
        code.setName("code");
        code.setDescription("code");
        code.setCode("Hello world ");
        singleFileCodeService.updateCode(code);
        return ResponseEntity.ok("saved");
    }
}
