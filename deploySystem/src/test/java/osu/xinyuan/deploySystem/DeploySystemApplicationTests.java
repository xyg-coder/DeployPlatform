package osu.xinyuan.deploySystem;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import osu.xinyuan.deploySystem.domains.JavaProjectInfo;
import osu.xinyuan.deploySystem.repositories.JavaProjectInfoRepo;
import osu.xinyuan.deploySystem.util.DeployFailureException;
import osu.xinyuan.deploySystem.util.ShellUtil;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class DeploySystemApplicationTests {
	@Autowired
	private JavaProjectInfoRepo repo;

//	@Test
	public void testDatabase() {
		JavaProjectInfo info = new JavaProjectInfo();
		info.setDescription("test, test");
		info.setUrl("www.google.com");
		JavaProjectInfo saved = repo.save(info);

		JavaProjectInfo projectInfo = repo.findById(saved.getId());
		System.out.println(projectInfo.getDescription() + ", " + projectInfo.getUrl());

	}

//	@Test
	public void testExec() throws IOException {
		Process process = Runtime.getRuntime().exec("ls -al");
		InputStream inputStream = process.getInputStream();
		InputStream errorStream = process.getErrorStream();
		try {
			String err = IOUtils.toString(errorStream, "UTF-8");
			String out = IOUtils.toString(inputStream, "UTF-8");
			System.out.println(">>> err: " + err + "\n>>>out: " + out);
			System.out.println(out.isEmpty());
		} finally {
			inputStream.close();
			errorStream.close();
		}
	}

//	@Test
	public void checkGitCheckFalse() throws IOException {
		assertFalse(ShellUtil.isValidRepo("https://github.com/XinyuanGui/DeployPlatform.git"));
	}

//	@Test
	public void checkGitCheckFalse2() throws IOException {
		assertFalse(ShellUtil.isValidRepo("ss"));
	}

//	@Test
	public void checkGitCheckTrue() throws IOException {
		ShellUtil.isValidRepo("https://github.com/wucao/JDeploy.git");
		assertTrue(ShellUtil.isValidRepo("https://github.com/wucao/JDeploy.git"));
	}

	@Test
	public void testPackageFile() throws IOException, DeployFailureException {
		JavaProjectInfo info = new JavaProjectInfo();
		info.setId(11);
		info.setUrl("https://github.com/xinyuangui2/test.git");
		info.setRootPath("test/my-app/");
		info.setMainName("com.mycompany.app.App");
		ShellUtil.deployJavaProject(info);
		ShellUtil.startJavaProject(info);
	}

	@Test
	public void testIsRunning() throws IOException {
		JavaProjectInfo info = new JavaProjectInfo();
		info.setId(110);
		info.setUrl("https://github.com/xinyuangui2/test.git");
		info.setRootPath("test/my-app/");
		info.setMainName("com.mycompany.app.App");
		assertTrue(ShellUtil.javaProjectIsRunning(info));
	}

	@Test
	public void testDeployLogAndRunningLog() throws IOException, DeployFailureException {
		JavaProjectInfo info = new JavaProjectInfo();
		info.setId(11);
		info.setUrl("https://github.com/xinyuangui2/test.git");
		info.setRootPath("test/my-app/");
		info.setMainName("com.mycompany.app.App");

//		ShellUtil.deployJavaProject(info);
//		ShellUtil.startJavaProject(info);

		String deployedLog = ShellUtil.getDeployedLog(info);
		System.out.println(">>> deployedlog\n" + deployedLog);

		String runningLog = ShellUtil.getRunningLog(info);
		System.out.println(">>> runninglog\n" + runningLog);
	}

	@Test
	public void testPathJoin() {
		Path filePath = Paths.get("codes/deploy/", "///../test", "3", "test");
		System.out.println(">>>path:\n" + filePath.toString());
	}
}
