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
package example.sos.modulith;

import example.sos.modulith.catalog.Catalog;
import example.sos.modulith.catalog.Product;
import example.sos.modulith.inventory.Inventory;
import example.sos.modulith.orders.OrderManager;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author Oliver Gierke
 */
@EnableAsync(proxyTargetClass = false)
@SpringBootApplication
public class ModulithApplication {

	public static void main(String... args) {
		SpringApplication.run(ModulithApplication.class, args);
	}

	@Bean
	CommandLineRunner onStartup(Catalog catalog, Inventory inventory, OrderManager orders) {

		return args -> {

			Product iPad = catalog.save(new Product("iPad Pro", new BigDecimal(799.99)));
			Product iPhone = catalog.save(new Product("iPhone X", new BigDecimal(999.99)));

			inventory.registerShipment(iPad, 10);
			inventory.registerShipment(iPhone, 15);
		};
	}
}
