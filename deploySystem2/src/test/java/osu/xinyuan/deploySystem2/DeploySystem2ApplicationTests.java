package osu.xinyuan.deploySystem2;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import osu.xinyuan.deploySystem2.repository.SingleFileCodeRepo;
import osu.xinyuan.deploySystem2.util.SingleFileCodeDefault;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DeploySystem2ApplicationTests {

	@Test
	public void contextLoads() {
	}

	@Test
	public void testDefaultJavaCode() {
		System.out.println(SingleFileCodeDefault.defaultJavaCode());
	}
}

