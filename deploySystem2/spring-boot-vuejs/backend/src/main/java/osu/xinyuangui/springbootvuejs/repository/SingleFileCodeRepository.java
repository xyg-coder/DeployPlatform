package osu.xinyuangui.springbootvuejs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import osu.xinyuangui.springbootvuejs.domain.SingleFileCodeInfo;
import osu.xinyuangui.springbootvuejs.domain.SingleFileCodeBrief;

import java.util.List;

public interface SingleFileCodeRepository extends JpaRepository<SingleFileCodeInfo, Integer> {
    @Query("SELECT new osu.xinyuangui.springbootvuejs.domain.SingleFileCodeBrief" +
            "(code.id, code.type, code.name, code.description) " +
            "from SingleFileCodeInfo code")
    List<SingleFileCodeBrief> findAllInfos();
}
