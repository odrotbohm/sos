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
package example.sos.monolith;

import example.sos.monolith.catalog.Catalog;
import example.sos.monolith.catalog.Product;
import example.sos.monolith.inventory.Inventory;
import example.sos.monolith.orders.OrderManager;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author Oliver Gierke
 */
@SpringBootApplication
public class MonolithApplication {

	public static void main(String... args) {
		SpringApplication.run(MonolithApplication.class, args);
	}

	@Bean
	CommandLineRunner onStartup(Catalog catalog, Inventory inventory, OrderManager orders) {

		return args -> {

			Product iPad = catalog.save(new Product("iPad", new BigDecimal(699.99)));
			Product iPhone = catalog.save(new Product("iPhone", new BigDecimal(899.99)));

			inventory.addItemFor(iPad, 10);
			inventory.addItemFor(iPhone, 15);
		};
	}
}
