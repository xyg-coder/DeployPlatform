package osu.xinyuangui.springbootvuejs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import osu.xinyuangui.springbootvuejs.domain.SingleFileCodeInfo;
import osu.xinyuangui.springbootvuejs.domain.SingleFileCodeBrief;
import osu.xinyuangui.springbootvuejs.repository.SingleFileCodeRepository;

import java.util.List;

@Service
public class SingleFileCodeServiceImpl implements SingleFileCodeService {
    @Autowired
    private SingleFileCodeRepository codeRepository;

    @Override
    public List<SingleFileCodeBrief> getSingleFileCodeInfos() {
        return codeRepository.findAllInfos();
    }

    @Override
    public SingleFileCodeInfo getSingleFileCodeById(int id) {
        return codeRepository.getOne(id);
    }

    @Override
    public void updateCode(SingleFileCodeInfo code) {
        codeRepository.save(code);
    }
}
