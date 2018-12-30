package osu.xinyuangui.springbootvuejs.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import osu.xinyuangui.springbootvuejs.domain.SingleFileCode;
import osu.xinyuangui.springbootvuejs.domain.SingleFileCodeBrief;

public interface SingleFileCodeRepository extends JpaRepository<SingleFileCode, Integer> {
    @Query("SELECT new osu.xinyuangui.springbootvuejs.domain.SingleFileCodeBrief" +
            "(code.id, code.type, code.name, code.description) " +
            "from SingleFileCode code")
    Page<SingleFileCodeBrief> findAllInfosByPage(Pageable pageable);
}
