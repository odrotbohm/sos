package example.sos.messaging.orders.integration;

import example.sos.messaging.orders.ProductInfo;
import example.sos.messaging.orders.ProductInfo.ProductNumber;
import example.sos.messaging.orders.ProductInfoRepository;
import example.sos.messaging.orders.integration.Payloads.ProductAdded;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.springframework.data.projection.ProjectionFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class ProductListener {

	private final @NonNull ProductInfoRepository productInfos;
	private final @NonNull ProjectionFactory projectionFactory;

	@KafkaListener(topics = "products")
	void on(Message<String> message) {

		ProductAdded event = readFromMessage(message, ProductAdded.class);
		UUID productNumber = event.getProductNumber();

		productInfos.save(ProductInfo.of(ProductNumber.of(productNumber), event.getDescription(), event.getPrice()));
	}

	private <T> T readFromMessage(Message<String> message, Class<T> type) {

		try (InputStream stream = new ByteArrayInputStream(message.getPayload().getBytes())) {
			return projectionFactory.createProjection(type, stream);
		} catch (IOException o_O) {
			throw new RuntimeException(o_O);
		}
	}
}
