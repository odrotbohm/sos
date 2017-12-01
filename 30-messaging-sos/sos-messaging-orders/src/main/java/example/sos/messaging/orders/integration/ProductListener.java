package example.sos.messaging.orders.integration;

import example.sos.messaging.orders.ProductInfo;
import example.sos.messaging.orders.ProductInfo.ProductNumber;
import example.sos.messaging.orders.ProductInfoRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.web.JsonPath;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class ProductListener {

	private final @NonNull ProductInfoRepository productInfos;

	@KafkaListener(topics = "products")
	void on(ProductAdded event) {

		UUID productNumber = event.getProductNumber();

		productInfos.save(ProductInfo.of(ProductNumber.of(productNumber), event.getDescription(), event.getPrice()));
	}

	interface ProductAdded {

		@JsonPath("$.product.id")
		UUID getProductNumber();

		@JsonPath("$.product.name")
		String getDescription();

		@JsonPath("$.product.price")
		BigDecimal getPrice();
	}
}
