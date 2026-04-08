package br.com.lordecaio.finamgmt.common.annotation.listener;

import br.com.lordecaio.finamgmt.common.annotation.ResourceId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class ResourceIdListenerTest {

	static class ResourceIdWrapper {
		@ResourceId
		private UUID resourceId;

		public UUID getResourceId() {
			return resourceId;
		}
	}

	@Test
	void shouldHandleResourceIdAnnotation() {
		var wrapper = new ResourceIdWrapper();
		new ResourceIdListener().handle(wrapper);
		assertNotNull(wrapper.getResourceId());
	}

}
