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
package example.sos.modulith.orders;

import example.sos.modulith.orders.Order.OrderCompleted;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * @author Oliver Gierke
 */
@Slf4j
@Component
class EmailSender {

	@Async
	@TransactionalEventListener
	void on(OrderCompleted event) {

		log.info("Sending email for order {}.", event.getOrder());

		try {
			Thread.sleep(1000);
		} catch (InterruptedException o_O) {}

		log.info("Successfully sent email for order {}.", event.getOrder());
	}
}
