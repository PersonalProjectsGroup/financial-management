package br.com.lordecaio.finamgmt.common.configuration.persister;

import org.yaml.snakeyaml.DumperOptions;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class YamlPropertiesPersisterFactory {



	private YamlPropertiesPersisterFactory() { }

	public static YamlPropertiesPersisterFactory getFactory() {
		return new YamlPropertiesPersisterFactory();
	}


}
