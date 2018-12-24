package osu.xinyuan.deploySystem2.service;

import osu.xinyuan.deploySystem2.domain.SingleFileCode;

import java.util.List;

public interface SingleFileCodeService {
    List<SingleFileCode> getSingleFileCodeList();

    SingleFileCode findCodeById(int id);

    SingleFileCode updateSingleFileCode(SingleFileCode codeDetail);

    List<String> getFileList(int id) throws IllegalArgumentException;

    void runCode(int id, String stdin) throws Exception;
}
