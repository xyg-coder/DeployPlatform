package osu.xinyuangui.springbootvuejs.service;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import osu.xinyuangui.springbootvuejs.domain.SingleFileCode;
import osu.xinyuangui.springbootvuejs.domain.SingleFileCodeBrief;
import osu.xinyuangui.springbootvuejs.util.StorageException;
import osu.xinyuangui.springbootvuejs.util.StorageFileNotFoundException;

import java.io.IOException;
import java.util.List;

public interface SingleFileCodeService {
    List<SingleFileCodeBrief> getSingleFileCodeInfosByPage(Pageable pageable);

    SingleFileCode getSingleFileCodeById(int id);

    SingleFileCode updateCode(SingleFileCode code) throws IOException;

    ResponseEntity runCode(int id, String type, String stdin);

    Process getFileReadingProcess(int id, String fileName) throws IOException;

    String[] getKillFileReadingProcessCommand(int id, String fileName);

    int getTotalCount();

    void deleteCode(int id) throws IOException;

    void saveFile(int id, MultipartFile file) throws StorageException;

    List<String> getFileNames(int id) throws StorageFileNotFoundException;

    List<String> deleteFiles(int id, List<String> files) throws StorageFileNotFoundException;

    Resource getFileResource(int id, String file) throws StorageFileNotFoundException, IOException;
}
