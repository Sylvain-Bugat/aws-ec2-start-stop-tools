package com.github.sbugat.ec2tools.service.update;

import org.eclipse.egit.github.core.service.RepositoryService;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.sbugat.GenericMockitoTest;

@RunWith(MockitoJUnitRunner.class)
public class UpdateServiceTest extends GenericMockitoTest {

	@Mock
	private RepositoryService repositoryService;

	@InjectMocks
	private UpdateService updateService;

	@After
	public void cleanDirectory() {

	}

	@Ignore
	@Test
	public void test() throws Exception {

		updateService.run();

		final InOrder inOrder = Mockito.inOrder(repositoryService);

		inOrder.verify(repositoryService).getRepository(UpdateService.gitHubUser, UpdateService.gitHubRepository);
		inOrder.verify(repositoryService).getTags(null);
	}
}
