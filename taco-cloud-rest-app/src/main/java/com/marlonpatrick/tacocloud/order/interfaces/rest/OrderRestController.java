package com.marlonpatrick.tacocloud.order.interfaces.rest;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.marlonpatrick.tacocloud.order.Order;
import com.marlonpatrick.tacocloud.order.OrderApplicationService;
import com.marlonpatrick.tacocloud.taco.Ingredient;
import com.marlonpatrick.tacocloud.taco.Taco;

import reactor.core.publisher.Mono;

@RestController
@CrossOrigin("*")
@RequestMapping(path = "/orders", produces = "application/json")
public class OrderRestController {

	@Autowired
	private OrderApplicationService orderApplicationService;

	@PostMapping(consumes = "application/json")
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Order> postOrder(@RequestBody Order order) {
		return orderApplicationService.saveOrder(order);
	}


	@PostMapping(path = "fromEmail", consumes = "application/json")
	@ResponseStatus(HttpStatus.CREATED)//fake
	@Transactional(readOnly=true)
	public Order postOrderFromEmail(@RequestBody String emailOrder) {
		
		System.out.println("EmailOrder\n" + emailOrder);
		
		//TODO: implement reactively
		Order order = orderApplicationService.findById(UUID.fromString("get uuid")).block();
		
		for (Taco taco : order.getTacos()) {
			for (Ingredient ingredient : taco.getIngredients()) {
				System.out.println(ingredient.getName());
			}
		}
		
		return order;
	}

	@PutMapping(path = "/{orderId}", consumes = "application/json")
	public Mono<Order> putOrder(@RequestBody Order order) {
		return orderApplicationService.saveOrder(order);
	}

	@PatchMapping(path = "/{orderId}", consumes = "application/json")
	public Mono<Order> patchOrder(@PathVariable("orderId") UUID orderId, @RequestBody Order patchOrder) {

		//implement reactively
		Order order = orderApplicationService.findById(orderId).block();

		if (patchOrder.getDeliveryName() != null) {
			order.setDeliveryName(patchOrder.getDeliveryName());
		}
		if (patchOrder.getDeliveryStreet() != null) {
			order.setDeliveryStreet(patchOrder.getDeliveryStreet());
		}
		if (patchOrder.getDeliveryCity() != null) {
			order.setDeliveryCity(patchOrder.getDeliveryCity());
		}
		if (patchOrder.getDeliveryState() != null) {
			order.setDeliveryState(patchOrder.getDeliveryState());
		}
		if (patchOrder.getDeliveryZip() != null) {
			order.setDeliveryZip(patchOrder.getDeliveryState());
		}
		if (patchOrder.getCcNumber() != null) {
			order.setCcNumber(patchOrder.getCcNumber());
		}
		if (patchOrder.getCcExpiration() != null) {
			order.setCcExpiration(patchOrder.getCcExpiration());
		}
		if (patchOrder.getCcCVV() != null) {
			order.setCcCVV(patchOrder.getCcCVV());
		}

		return orderApplicationService.saveOrder(order);

	}

	@DeleteMapping("/{orderId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removeOrder(@PathVariable("orderId") UUID orderId) {
		try {
			//TODO; return Mono?
			orderApplicationService.removeOrder(orderId);
		} catch (EmptyResultDataAccessException ex) {
			//
		}
	}
	
	@GetMapping(path="/send/{id}", produces = "text/plain")
	@Transactional(readOnly=true)
	public String send(@PathVariable("id") UUID id) {
		this.orderApplicationService.sendOrder(id);
		return "Message sent successfully";
	}
}