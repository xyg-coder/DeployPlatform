package osu.xinyuangui.springbootvuejs.service;

import org.springframework.http.ResponseEntity;
import osu.xinyuangui.springbootvuejs.domain.SingleFileCode;
import osu.xinyuangui.springbootvuejs.domain.SingleFileCodeBrief;
import osu.xinyuangui.springbootvuejs.domain.SingleFileCodeType;

import java.io.IOException;
import java.util.List;

public interface SingleFileCodeService {
    List<SingleFileCodeBrief> getSingleFileCodeInfos();

    SingleFileCode getSingleFileCodeById(int id);

    SingleFileCode updateCode(SingleFileCode code) throws IOException;

    ResponseEntity runCode(int id, String type, String stdin);
}
