package osu.xinyuan.deploySystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import osu.xinyuan.deploySystem.domains.JavaProjectInfo;


public interface JavaProjectInfoRepo extends JpaRepository<JavaProjectInfo, Integer> {

    JavaProjectInfo findById(int id);
}
