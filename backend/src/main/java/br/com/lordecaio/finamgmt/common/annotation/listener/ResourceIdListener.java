package br.com.lordecaio.finamgmt.common.annotation.listener;

import br.com.lordecaio.finamgmt.common.annotation.ResourceId;
import br.com.lordecaio.finamgmt.common.util.UUIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import jakarta.persistence.PrePersist;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ResourceIdListener {

	private static final Logger log = LoggerFactory.getLogger(ResourceIdListener.class);

	private static final Map<Class<?>, Optional<Field>> FIELD_CACHE = new ConcurrentHashMap<>();

	@PrePersist
	public void handle(Object entity) {
		Class<?> entityClass = entity.getClass();

		FIELD_CACHE.computeIfAbsent(entityClass, this::findAnnotatedField)
			  .ifPresent(field -> injectResourceId(entity, field));
	}

	private Optional<Field> findAnnotatedField(Class<?> clazz) {
		return Arrays.stream(clazz.getDeclaredFields())
			  .filter(f -> f.isAnnotationPresent(ResourceId.class))
			  .peek(f -> {
				  log.debug("Ripping open the guts of {} to find @ResourceId", clazz.getSimpleName());
				  f.setAccessible(true);
			  })
			  .findFirst();
	}

	private void injectResourceId(Object entity, Field field) {
		try {
			if (field.get(entity) == null) {
				field.set(entity, UUIDUtils.nextV7());
			}
		} catch (IllegalAccessException e) {
			log.error("Failed to access entity {} guts with magic. The seal is too strong!",
				  entity.getClass().getSimpleName(), e
			);
			throw new RuntimeException("Magic injection failed", e);
		}
	}
}
