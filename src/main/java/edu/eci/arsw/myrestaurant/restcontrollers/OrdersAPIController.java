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

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.eci.arsw.myrestaurant.model.Order;
import edu.eci.arsw.myrestaurant.services.OrderServicesException;
import edu.eci.arsw.myrestaurant.services.RestaurantOrderServices;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
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

    /**
     * Parte I - Primer punto.
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> manejadorGetRecursoOrders() {
        //obtener datos que se enviarán a través del API
        Set<Integer> set = ros.getTablesWithOrders();
        Map<Integer, Order> ordersMap = new ConcurrentHashMap<>();
        for (Integer i : set) {
            ordersMap.put(i, ros.getTableOrder(i));
        }
        return new ResponseEntity<>(ordersMap, HttpStatus.ACCEPTED);
    }

    /**
     * Parte I - Tercer punto.
     *
     * @param tableId
     * @return
     */
    @GetMapping("/{tableId}")
    public ResponseEntity<?> manejadorGetRecursoTableOrder(@PathVariable int tableId) {
        //obtener datos que se enviarán a través del API
        Order order = ros.getTableOrder(tableId);
        if (order != null) {
            Map<Integer, Order> ordersMap = new ConcurrentHashMap<>();
            ordersMap.put(tableId, order);
            return new ResponseEntity<>(ordersMap, HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>("La mesa no existe o no tiene ordenes", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Parte II - Primer punto.
     *
     * @param o Order en formato JSON
     * @return
     */
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

    /**
     * Parte III - Cuarto punto.
     *
     * @param tableId
     * @return
     */
    @GetMapping("/{tableId}/total")
    public ResponseEntity<?> manejadorGetRecursoTotalBill(@PathVariable int tableId) {
        try {
            //obtener datos que se enviarán a través del API
            int totalBill = ros.calculateTableBill(tableId);
            String json = "La cuenta total de la mesa " + tableId + " es: " + totalBill;
            return new ResponseEntity<>(json, HttpStatus.ACCEPTED);
        } catch (OrderServicesException ex) {
            Logger.getLogger(OrdersAPIController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("La mesa: " + tableId + " ya fue liberada o no tiene ordenes.", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Parte IV - Primer punto.
     *
     * @param tableId
     * @param p Producto
     * @return
     */
    @PutMapping("/{tableId}")
    public ResponseEntity<?> manejadorPutAddProduct(@ModelAttribute("tableId") int tableId, @RequestBody String p) {
        try {
            //registrar dato
            ObjectMapper objectMap = new ObjectMapper();
            Map<String, Integer> product = objectMap.readValue(p, ConcurrentHashMap.class);
            Set<String> keys = product.keySet();
            Order order = ros.getTableOrder(tableId);
            for (String k : keys) {
                order.addDish(k, product.get(k));
            }
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (IOException ex) {
            Logger.getLogger(OrdersAPIController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("Error al abrir", HttpStatus.FORBIDDEN);
        }
    }

    //Posible implementacion.
    /*@DeleteMapping("/{tableId}")
    public ResponseEntity<?> manejadorDeleteReleaseTable(@PathVariable int tableId) {
        try {
            ros.releaseTable(tableId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (OrderServicesException ex) {
            Logger.getLogger(OrdersAPIController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("La mesa: " + tableId + " ya fue liberada.", HttpStatus.NOT_FOUND);
        }
    }*/
    
    /**
     * Parte IV - Segundo punto.
     *
     * @param tableId
     * @return
     */
    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<?> manejadorDeleteReleaseTable(@RequestBody int tableId) {
        try {
            ros.releaseTable(tableId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (OrderServicesException ex) {
            Logger.getLogger(OrdersAPIController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("La mesa: " + tableId + " ya fue liberada.", HttpStatus.NOT_FOUND);
        }
    }
}
