package osu.xinyuan.deploySystem;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.HtmlUtils;
import osu.xinyuan.deploySystem.domains.JavaProjectInfo;
import osu.xinyuan.deploySystem.domains.JavaProjectStatus;
import osu.xinyuan.deploySystem.repositories.JavaProjectInfoRepo;
import osu.xinyuan.deploySystem.util.ShellUtil;
import osu.xinyuan.deploySystem.util.Util;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
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

	@Test
	public void checkGitCheckFalse() throws IOException {
		assertFalse(Util.isValidRepo("https://github.com/XinyuanGui/Linux-Kernel-dev"));
	}

//	@Test
	public void checkGitCheckFalse2() throws IOException {
		assertFalse(Util.isValidRepo("ss"));
	}

//	@Test
	public void checkGitCheckTrue() throws IOException {
		Util.isValidRepo("https://github.com/wucao/JDeploy.git");
		assertTrue(Util.isValidRepo("https://github.com/wucao/JDeploy.git"));
	}

	@Test
	public void testPackageFile() throws IOException {
		JavaProjectInfo info = new JavaProjectInfo();
		info.setId(11);
		info.setUrl("https://github.com/xinyuangui2/test.git");
		info.setRootPath("test/my-app/");
		info.setMainName("com.mycompany.app.App");
		ShellUtil.deployJavaProject(info, new MockJmsTemplate());
//		ShellUtil.startJavaProject(info);
	}


	@Test
	public void testDeployLogAndRunningLog() throws IOException {
		JavaProjectInfo info = new JavaProjectInfo();
		info.setId(11);
		info.setUrl("https://github.com/xinyuangui2/test.git");
		info.setRootPath("test/my-app/");
		info.setMainName("com.mycompany.app.App");

		ShellUtil.deployJavaProject(info, new MockJmsTemplate());
//		ShellUtil.startJavaProject(info);

//		String deployedLog = ShellUtil.readJavaDeployLogDynamically(info);
//		System.out.println(">>> deployedlog\n" + deployedLog);

//		String runningLog = ShellUtil.getRunningLog(info);
//		System.out.println(">>> runninglog\n" + runningLog);
	}

	@Test
	public void testPathJoin() {
		Path filePath = Paths.get("/codes/deploy/", "/test/a/", "/3", "test");
		System.out.println(">>>path:\n" + filePath.toString());
	}

	@Test
	public void testJGitToCheckUrl() {
		assertTrue(Util.isValidRepo("https://github.com/XinyuanGui/newstar-blog-system"));

	}

	@Test
	public void testRunningAndStop() {
		JavaProjectInfo info = new JavaProjectInfo();
		info.setId(11);
		info.setUrl("https://github.com/xinyuangui2/test.git");
		info.setRootPath("test/my-app/");
		info.setMainName("com.mycompany.app.App");
		info.setStatus(JavaProjectStatus.RUNNING);

		try {
			ShellUtil.startJavaProject(info, new MockJmsTemplate());
			Thread.sleep(15000);
			ShellUtil.killJavaProject(info, new MockJmsTemplate());
			Thread.sleep(3000);
		} catch (Exception e) {
			System.err.println(e.toString());
			e.printStackTrace();
		}
	}

	private class MockJmsTemplate extends JmsTemplate {
		@Override
		public void send(String destinationName, MessageCreator messageCreator) throws JmsException {
			System.out.println(destinationName);
		}
	}

	@Test
	public void testDeployLog() {
		JavaProjectInfo info = new JavaProjectInfo();
		info.setId(1);
		info.setUrl("https://github.com/xinyuangui2/test.git");
		info.setRootPath("test/my-app/");
		info.setMainName("com.mycompany.app.App");
		info.setStatus(JavaProjectStatus.RUNNING);

		String path =
				Paths.get("codes/deploy/", Integer.toString(info.getId()), info.getRootPath(), "package_log.log")
						.toString();
		File f = new File(path);
		System.out.println(">>> " + path + ", " + f.exists() + ", " + f.isDirectory());
		try {
			Process process = ShellUtil.readJavaDeployLogDynamically(info);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(HtmlUtils.htmlEscape(line));
            }
		} catch (IOException e) {
		    System.err.println(">>> errors happen");
			e.printStackTrace();
		}
	}
}
