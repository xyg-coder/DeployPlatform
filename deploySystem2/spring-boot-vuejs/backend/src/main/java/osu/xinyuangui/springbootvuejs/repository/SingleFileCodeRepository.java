package osu.xinyuangui.springbootvuejs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import osu.xinyuangui.springbootvuejs.domain.SingleFileCode;
import osu.xinyuangui.springbootvuejs.domain.SingleFileCodeBrief;

import java.util.List;

public interface SingleFileCodeRepository extends JpaRepository<SingleFileCode, Integer> {
    @Query("SELECT new osu.xinyuangui.springbootvuejs.domain.SingleFileCodeBrief" +
            "(code.id, code.type, code.name, code.description) " +
            "from SingleFileCode code")
    List<SingleFileCodeBrief> findAllInfos();
}
