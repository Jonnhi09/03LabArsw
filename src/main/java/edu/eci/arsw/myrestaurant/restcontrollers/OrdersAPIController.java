/*
 * Copyright (C) 2016 Pivotal Software, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.eci.arsw.myrestaurant.restcontrollers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.eci.arsw.myrestaurant.model.Order;
import edu.eci.arsw.myrestaurant.model.ProductType;
import edu.eci.arsw.myrestaurant.model.RestaurantProduct;
import edu.eci.arsw.myrestaurant.services.OrderServicesException;
import edu.eci.arsw.myrestaurant.services.RestaurantOrderServices;
import edu.eci.arsw.myrestaurant.services.RestaurantOrderServicesStub;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author hcadavid
 */
@Service
@RestController
@RequestMapping(value = "/orders")
public class OrdersAPIController {

    @Autowired
    private RestaurantOrderServices ros;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> manejadorGetRecursoOrders() {
        try {
            //obtener datos que se enviarán a través del API
            Set<Integer> set = ros.getTablesWithOrders();
            Map<Integer, Order> ordersMap = new ConcurrentHashMap<>();
            for (Integer i : set) {
                ordersMap.put(i, ros.getTableOrder(i));
            }
            ObjectMapper objectMap = new ObjectMapper();
            String json = objectMap.writeValueAsString(ordersMap);
            return new ResponseEntity<>(json, HttpStatus.ACCEPTED);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(OrdersAPIController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("Error json", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{tableId}")
    public ResponseEntity<?> manejadorGetRecursoTableOrder(@PathVariable int tableId) {
        try {
            //obtener datos que se enviarán a través del API
            HttpStatus respuesta;
            String json;
            Order order = ros.getTableOrder(tableId);
            if (order != null) {
                Map<Integer, Order> ordersMap = new ConcurrentHashMap<>();
                ordersMap.put(tableId, order);
                ObjectMapper objectMap = new ObjectMapper();
                json = objectMap.writeValueAsString(ordersMap);
                respuesta = HttpStatus.ACCEPTED;
            } else {
                json = "La mesa no existe o no tiene ordenes";
                respuesta = HttpStatus.NOT_FOUND;
            }
            return new ResponseEntity<>(json, respuesta);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(OrdersAPIController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("Error json", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> manejadorPostRecursoOrder(@RequestBody String o) {
        try {
            //registrar dato
            ObjectMapper objectMap = new ObjectMapper();
            Order order = objectMap.readValue(o, Order.class);
            ros.addNewOrderToTable(order);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (OrderServicesException ex) {
            Logger.getLogger(OrdersAPIController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("Error al adicionar una nueva orden", HttpStatus.FORBIDDEN);
        } catch (IOException ex) {
            Logger.getLogger(OrdersAPIController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("Error al abrir", HttpStatus.FORBIDDEN);
        }
    }
}
