package com.foodexpress.order.service;

import com.foodexpress.order.client.*;
import com.foodexpress.order.dto.OrderFullResponse;
import com.foodexpress.order.dto.OrderRequest;
import com.foodexpress.order.dto.PaginatedResponse;
import com.foodexpress.order.entity.Order;
import com.foodexpress.order.entity.Order.OrderStatus;
import com.foodexpress.order.entity.OrderItem;
import com.foodexpress.order.exception.*;
import com.foodexpress.order.repository.OrderRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class OrderService {

    @Inject
    OrderRepository orderRepository;

    @Inject
    @RestClient
    CustomerRestClient customerClient;

    @Inject
    @RestClient
    RestaurantRestClient restaurantClient;

    @Inject
    @RestClient
    DeliveryRestClient deliveryClient;

    @Inject
    OrderConfig orderConfig;

    private static final Set<OrderStatus> VALID_TRANSITIONS_FROM_CREATED = Set.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED);
    private static final Set<OrderStatus> VALID_TRANSITIONS_FROM_CONFIRMED = Set.of(OrderStatus.PREPARING, OrderStatus.CANCELLED);
    private static final Set<OrderStatus> VALID_TRANSITIONS_FROM_PREPARING = Set.of(OrderStatus.READY);
    private static final Set<OrderStatus> VALID_TRANSITIONS_FROM_READY = Set.of(OrderStatus.PICKED_UP);
    private static final Set<OrderStatus> VALID_TRANSITIONS_FROM_PICKED_UP = Set.of(OrderStatus.DELIVERED);

    public PaginatedResponse<Order> listAll(int page, int size) {
        long total = orderRepository.count();
        List<Order> data = orderRepository.findAll().page(Page.of(page, size)).list();
        return new PaginatedResponse<>(data, page, size, total);
    }

    public Order findById(Long id) {
        Order order = orderRepository.findById(id);
        if (order == null) {
            throw new ResourceNotFoundException("Commande avec l'id " + id + " non trouvée");
        }
        return order;
    }

    @Transactional
    public Order create(OrderRequest request) {
        // 1. Verifie customer exists
        verifyCustomerExists(request.customerId);

        // 2. Verifie restaurant exists et get info
        RestaurantDTO restaurant = getRestaurant(request.restaurantId);

        // 3. Verifie dishes
        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        List<String> errors = new ArrayList<>();

        for (OrderRequest.OrderItemRequest itemReq : request.items) {
            try {
                DishDTO dish = restaurantClient.getDish(request.restaurantId, itemReq.dishId);
                if (!dish.available) {
                    errors.add("Le plat '" + dish.name + "' (id=" + itemReq.dishId + ") n'est pas disponible");
                    continue;
                }
                OrderItem item = new OrderItem();
                item.dishId = dish.id;
                item.dishName = dish.name;
                item.unitPrice = dish.price;
                item.quantity = itemReq.quantity;
                items.add(item);
                total = total.add(dish.price.multiply(BigDecimal.valueOf(itemReq.quantity)));
            } catch (WebApplicationException e) {
                if (e.getResponse().getStatus() == 404) {
                    errors.add("Le plat avec l'id " + itemReq.dishId + " n'existe pas dans le restaurant " + request.restaurantId);
                } else {
                    throw e;
                }
            } catch (Exception e) {
                handleServiceException(e, "restaurant-service");
            }
        }

        if (!errors.isEmpty()) {
            throw new BusinessRuleException(String.join(" | ", errors));
        }

        // 4. Check minimum order montant
        if (total.compareTo(orderConfig.minimumAmount()) < 0) {
            throw new BusinessRuleException("Le montant minimum de commande est de " + orderConfig.minimumAmount() + "€. Montant actuel : " + total + "€");
        }

        // 5. Persist order
        Order order = new Order();
        order.customerId = request.customerId;
        order.restaurantId = request.restaurantId;
        order.deliveryAddress = request.deliveryAddress;
        order.totalAmount = total;
        order.status = OrderStatus.CREATED;
        order.items = items;
        items.forEach(item -> item.order = order);

        orderRepository.persist(order);

        // 6. Create delivery
        try {
            deliveryClient.create(new DeliveryCreateRequest(
                    order.id, restaurant.address, request.deliveryAddress));
        } catch (Exception e) {
            // a finir
        }

        return order;
    }

    @Transactional
    public Order updateStatus(Long id, OrderStatus newStatus) {
        Order order = findById(id);
        validateStatusTransition(order.status, newStatus);
        order.status = newStatus;
        return order;
    }

    public List<Order> findByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    @Transactional
    public void cancel(Long id) {
        Order order = findById(id);
        if (order.status == OrderStatus.DELIVERED) {
            throw new InvalidStatusTransitionException("Impossible d'annuler une commande déjà livrée");
        }
        if (order.status == OrderStatus.CANCELLED) {
            throw new InvalidStatusTransitionException("La commande est déjà annulée");
        }
        order.status = OrderStatus.CANCELLED;
    }

    public OrderFullResponse getFullOrder(Long id) {
        Order order = findById(id);
        CustomerDTO customer = null;
        RestaurantDTO restaurant = null;
        DeliveryDTO delivery = null;

        try { customer = customerClient.getById(order.customerId); } catch (Exception ignored) {}
        try { restaurant = restaurantClient.getById(order.restaurantId); } catch (Exception ignored) {}
        try { delivery = deliveryClient.getById(id); } catch (Exception ignored) {}

        return new OrderFullResponse(order, customer, restaurant, delivery);
    }

    private void verifyCustomerExists(Long customerId) {
        try {
            boolean exists = customerClient.exists(customerId);
            if (!exists) {
                throw new BusinessRuleException("Impossible de créer la commande : le client avec l'id " + customerId + " n'existe pas");
            }
        } catch (BusinessRuleException e) {
            throw e;
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == 404) {
                throw new BusinessRuleException("Impossible de créer la commande : le client avec l'id " + customerId + " n'existe pas");
            }
            throw e;
        } catch (Exception e) {
            handleServiceException(e, "customer-service");
        }
    }

    private RestaurantDTO getRestaurant(Long restaurantId) {
        try {
            return restaurantClient.getById(restaurantId);
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == 404) {
                throw new BusinessRuleException("Impossible de créer la commande : le restaurant avec l'id " + restaurantId + " n'existe pas");
            }
            throw e;
        } catch (Exception e) {
            handleServiceException(e, "restaurant-service");
            return null; // unreachable
        }
    }

    private void handleServiceException(Exception e, String serviceName) {
        Throwable cause = e;
        while (cause != null) {
            if (cause instanceof ConnectException) {
                throw new ServiceUnavailableException("Le service " + serviceName + " est indisponible. Veuillez réessayer plus tard.");
            }
            cause = cause.getCause();
        }
        if (e instanceof RuntimeException re) throw re;
        throw new ServiceUnavailableException("Erreur de communication avec " + serviceName + " : " + e.getMessage());
    }

    private void validateStatusTransition(OrderStatus current, OrderStatus target) {
        Set<OrderStatus> allowed = switch (current) {
            case CREATED -> VALID_TRANSITIONS_FROM_CREATED;
            case CONFIRMED -> VALID_TRANSITIONS_FROM_CONFIRMED;
            case PREPARING -> VALID_TRANSITIONS_FROM_PREPARING;
            case READY -> VALID_TRANSITIONS_FROM_READY;
            case PICKED_UP -> VALID_TRANSITIONS_FROM_PICKED_UP;
            default -> Set.of();
        };
        if (!allowed.contains(target)) {
            throw new InvalidStatusTransitionException(
                    "Transition de statut invalide : " + current + " → " + target +
                    ". Transitions autorisées depuis " + current + " : " + allowed);
        }
    }
}
