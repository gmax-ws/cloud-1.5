package scalable.solutions.rest.controlers;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import scalable.solutions.rest.dto.Order;
import scalable.solutions.rest.dto.OrderDTO;
import scalable.solutions.rest.dto.OrderDao;
import scalable.solutions.rest.exceptions.OrderNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A dummy REST controller used just to validate the POC. It implements a
 * potential CRUD set of operations. It provide security for endpoints and
 * Hystrix commands for monitoring.
 *
 * @author Marius
 */
@RefreshScope
@RestController
public class OrderController {

    private static final Logger LOG = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderDao orderDao;

    @HystrixCommand(fallbackMethod = "defaultOrders", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "500")}, threadPoolProperties = {
            @HystrixProperty(name = "coreSize", value = "30"),
            @HystrixProperty(name = "maxQueueSize", value = "101"),
            @HystrixProperty(name = "keepAliveTimeMinutes", value = "2"),
            @HystrixProperty(name = "queueSizeRejectionThreshold", value = "15"),
            @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "12"),
            @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "1440")})
    @ApiOperation(value = "Get all orders", hidden = false)
    @Async
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_DEVELOPER')")
    @RequestMapping(value = "/orders", method = RequestMethod.GET)
    public AsyncResult<ResponseEntity<Collection<OrderDTO>>> orders() {
        List<Order> orders = orderDao.findAll();
        List<OrderDTO> result = new ArrayList<>(orders.size());
        for (Order order : orders) {
            result.add(new OrderDTO(order.getId(), order.getDescription()));
        }
        return new AsyncResult<>(new ResponseEntity<Collection<OrderDTO>>(result, HttpStatus.OK));
    }

    @Async
    public AsyncResult<ResponseEntity<Collection<OrderDTO>>> defaultOrders() {
        LOG.info("Hystrix fallbackMethod = defaultOrders");
        return new AsyncResult<>(new ResponseEntity<>((Collection<OrderDTO>) null, HttpStatus.BAD_GATEWAY));
    }

    @HystrixCommand(fallbackMethod = "defaultOrder", ignoreExceptions = {
            OrderNotFoundException.class}, commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "500")}, threadPoolProperties = {
            @HystrixProperty(name = "coreSize", value = "30"),
            @HystrixProperty(name = "maxQueueSize", value = "101"),
            @HystrixProperty(name = "keepAliveTimeMinutes", value = "2"),
            @HystrixProperty(name = "queueSizeRejectionThreshold", value = "15"),
            @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "12"),
            @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "1440")})
    @ApiOperation(value = "Get order", response = OrderDTO.class, hidden = false)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_DEVELOPER')")
    @RequestMapping(value = "/order/{orderId}", method = RequestMethod.GET)
    public ResponseEntity<OrderDTO> order(@PathVariable(required = true) long orderId) {
        Order order = orderDao.findOne(orderId);
        if (order == null)
            throw new OrderNotFoundException(orderId);
        return new ResponseEntity<>(new OrderDTO(order.getId(), order.getDescription()), HttpStatus.OK);
        // throw new HystrixBadRequestException("Bad request.", new
        // OrderNotFoundException(orderId));
    }

    public ResponseEntity<OrderDTO> defaultOrder(long param) {
        LOG.info("Hystrix fallbackMethod = defaultOrder");
        return new ResponseEntity<>((OrderDTO) null, HttpStatus.BAD_GATEWAY);
    }

    @HystrixCommand(fallbackMethod = "defaultCreateOrder", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "500")}, threadPoolProperties = {
            @HystrixProperty(name = "coreSize", value = "30"),
            @HystrixProperty(name = "maxQueueSize", value = "101"),
            @HystrixProperty(name = "keepAliveTimeMinutes", value = "2"),
            @HystrixProperty(name = "queueSizeRejectionThreshold", value = "15"),
            @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "12"),
            @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "1440")})
    @ApiOperation(value = "Create new order", response = OrderDTO.class, hidden = false)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_DEVELOPER')")
    @RequestMapping(value = "/order", method = RequestMethod.POST)
    public ResponseEntity<OrderDTO> createOrder(@RequestBody(required = true) OrderDTO order) {
        Order entity = orderDao.save(new Order(order.getDescription()));
        return new ResponseEntity<>(new OrderDTO(entity.getId(), entity.getDescription()), HttpStatus.CREATED);
    }

    public ResponseEntity<OrderDTO> defaultCreateOrder(OrderDTO param) {
        LOG.info("Hystrix fallbackMethod = defaultCreateOrder");
        return new ResponseEntity<>((OrderDTO) null, HttpStatus.BAD_GATEWAY);
    }

    @HystrixCommand(fallbackMethod = "defaultUpdateOrder", ignoreExceptions = {
            OrderNotFoundException.class}, commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "500")}, threadPoolProperties = {
            @HystrixProperty(name = "coreSize", value = "30"),
            @HystrixProperty(name = "maxQueueSize", value = "101"),
            @HystrixProperty(name = "keepAliveTimeMinutes", value = "2"),
            @HystrixProperty(name = "queueSizeRejectionThreshold", value = "15"),
            @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "12"),
            @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "1440")})
    @ApiOperation(value = "Update order", response = OrderDTO.class, hidden = false)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_DEVELOPER')")
    @RequestMapping(value = "/order", method = RequestMethod.PUT)
    public ResponseEntity<OrderDTO> updateOrder(@RequestBody(required = true) OrderDTO order) {
        Order entity = orderDao.findOne(order.getId());
        if (entity == null)
            throw new OrderNotFoundException(order.getId());
        entity.setDescription(order.getDescription());
        orderDao.save(entity);
        return new ResponseEntity<>(new OrderDTO(entity.getId(), entity.getDescription()), HttpStatus.OK);
    }

    public ResponseEntity<OrderDTO> defaultUpdateOrder(OrderDTO order) {
        LOG.info("Hystrix fallbackMethod = defaultUpdateOrder");
        return new ResponseEntity<>((OrderDTO) null, HttpStatus.BAD_GATEWAY);
    }

    @HystrixCommand(fallbackMethod = "defaultDeleteOrder", ignoreExceptions = {
            OrderNotFoundException.class}, commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "500")}, threadPoolProperties = {
            @HystrixProperty(name = "coreSize", value = "30"),
            @HystrixProperty(name = "maxQueueSize", value = "101"),
            @HystrixProperty(name = "keepAliveTimeMinutes", value = "2"),
            @HystrixProperty(name = "queueSizeRejectionThreshold", value = "15"),
            @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "12"),
            @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "1440")})
    @ApiOperation(value = "Delete order", response = OrderDTO.class, hidden = false)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/order/{orderId}", method = RequestMethod.DELETE)
    public ResponseEntity<OrderDTO> deleteOrder(@PathVariable(required = true) long orderId) {
        Order order = orderDao.findOne(orderId);
        if (order == null)
            throw new OrderNotFoundException(orderId);
        orderDao.delete(order);
        return new ResponseEntity<>(new OrderDTO(order.getId(), order.getDescription()), HttpStatus.OK);
    }

    public ResponseEntity<OrderDTO> defaultDeleteOrder(long param) {
        LOG.info("Hystrix fallbackMethod = defaultDeleteOrder");
        return new ResponseEntity<>((OrderDTO) null, HttpStatus.BAD_GATEWAY);
    }
}
