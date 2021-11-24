/*
 * Copyright 2017-2021 the original author or authors.
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
package example.sos.messaging.orders;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * @author Oliver Gierke
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductInfo {

	ObjectId id;
	ProductNumber productNumber;
	@With String description;
	@With BigDecimal price;

	LocalDateTime created;

	public static ProductInfo of(String description, BigDecimal price) {
		return of(ProductNumber.of(UUID.randomUUID()), description, price);
	}

	public static ProductInfo of(ProductNumber number, String description, BigDecimal price) {
		return new ProductInfo(new ObjectId(), number, description, price, LocalDateTime.now());
	}

	/**
	 * @author Oliver Gierke
	 */
	@Value(staticConstructor = "of")
	@JsonSerialize(using = ToStringSerializer.class)
	public static class ProductNumber {

		@NonNull UUID value;

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return value.toString();
		}
	}
}
