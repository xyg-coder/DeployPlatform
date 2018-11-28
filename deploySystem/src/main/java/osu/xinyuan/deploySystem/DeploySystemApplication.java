package osu.xinyuan.deploySystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import osu.xinyuan.deploySystem.domains.JavaProjectInfo;
import osu.xinyuan.deploySystem.repositories.JavaProjectInfoRepo;

@SpringBootApplication
public class DeploySystemApplication {
	public static void main(String[] args) {
		SpringApplication.run(DeploySystemApplication.class, args);
	}
}
