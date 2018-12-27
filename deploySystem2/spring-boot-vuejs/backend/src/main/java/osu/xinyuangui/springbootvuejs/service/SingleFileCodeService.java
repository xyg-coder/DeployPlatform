package osu.xinyuangui.springbootvuejs.service;

import osu.xinyuangui.springbootvuejs.domain.SingleFileCodeInfo;
import osu.xinyuangui.springbootvuejs.domain.SingleFileCodeBrief;

import java.util.List;

public interface SingleFileCodeService {
    List<SingleFileCodeBrief> getSingleFileCodeInfos();

    SingleFileCodeInfo getSingleFileCodeById(int id);

    void updateCode(SingleFileCodeInfo code);
}
