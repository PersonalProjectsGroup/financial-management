package br.com.lordecaio.finamgmt.common.configuration;

import br.com.lordecaio.finamgmt.common.configuration.persister.YamlPropertiesPersister;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class MessageConfiguration {

	@Bean
	public MessageSource messageSource() {
		var messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasenames("i18n/messages");
		messageSource.setDefaultEncoding("UTF-8");
		messageSource.setPropertiesPersister(YamlPropertiesPersister.createDefault());
		return messageSource;
	}

}
