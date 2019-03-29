/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package example.sos.rest.catalog;

import example.sos.rest.events.EnablePersistentEvents;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;

/**
 * @author Oliver Gierke
 */
@SpringBootApplication
@EnablePersistentEvents
public class RestCatalogApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestCatalogApplication.class, args);
	}

	@Bean
	CommandLineRunner run(Catalog catalog) {

		return args -> {

			Product product = catalog.save(Product.of("iPad", BigDecimal.valueOf(699.00))); //
			catalog.save(product.withPrice(BigDecimal.valueOf(799.00)));
		};
	}
}
