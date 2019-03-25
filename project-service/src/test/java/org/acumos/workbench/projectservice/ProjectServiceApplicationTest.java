package org.acumos.workbench.projectservice;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProjectServiceApplication.class)
public class ProjectServiceApplicationTest {
	@Test
	public void contextLoads() throws Exception {
		ProjectServiceApplication.main(new String[0]);
	}
	
	
}
