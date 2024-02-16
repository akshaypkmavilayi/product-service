package com.microservices.productservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.productservice.dto.ProductRequest;
import com.microservices.productservice.dto.ProductResponse;
import com.microservices.productservice.model.Product;
import com.microservices.productservice.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers //To inform junit 5 that we are using TestContainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

	static DockerImageName myImage = DockerImageName.parse("mongo:latest").asCompatibleSubstituteFor("mongo");

	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer(myImage);

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ProductRepository productRepository;

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry){
		dynamicPropertyRegistry.add("spring.data.mongodb.uri",mongoDBContainer::getReplicaSetUrl);
	}
	@BeforeEach
	void shouldCreateProduct() throws Exception {
		ProductRequest productRequest = getProductRequest();
		String productRequestString = objectMapper.writeValueAsString(productRequest);
		 mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
				 .contentType(MediaType.APPLICATION_JSON)
				 .content(productRequestString))
				 .andExpect(status().isCreated());

        Assertions.assertEquals(1, productRepository.findAll().size());

	}

	@Test
	void getAllProductTest() throws Exception {
		List<Product> pr =  productRepository.findAll();
		List<ProductResponse> productResponses = pr.stream().map(p -> ProductResponse.builder().name(p.getName()).id(p.getId()).description(p.getDescription()).price(p.getPrice()).build()).collect(Collectors.toList());

		mockMvc.perform(MockMvcRequestBuilders.get("/api/product")
				.content(String.valueOf(MediaType.APPLICATION_JSON))
				.content(productResponses.toString()))
				.andExpect(status().isOk());

        Assertions.assertFalse(productRepository.findAll().isEmpty());

    }

	private ProductRequest getProductRequest() {
		return ProductRequest.builder()
				.name("iPhone 13")
				.description("iPhone 13")
				.price(BigDecimal.valueOf(1200)).name("Samsung Galaxy").description("Samsung Galaxy").price(BigDecimal.valueOf(2400))
				.build();
	}

}
