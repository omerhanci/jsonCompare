package com.waes.jsonCompare.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.MalformedJsonException;
import com.waes.jsonCompare.enums.DataType;
import com.waes.jsonCompare.enums.ResponseType;
import com.waes.jsonCompare.exceptions.DataNotFoundException;
import com.waes.jsonCompare.exceptions.PartsMissingException;
import com.waes.jsonCompare.service.DiffService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.waes.jsonCompare.constants.Constants.*;


/**
 * A rest controller for the application
 * @author Omer Hanci
 */
@RestController
@RequestMapping("/v1/diff/{id}")
public class DiffController {

    private static final Logger logger = LoggerFactory.getLogger(DiffController.class);

    @Autowired
    private DiffService service;

    /**
     * Saves the left side json
     * @param id : unique identifier
     * @param data : json data
     * @return ResponseEntity : response
     */
    @PostMapping(value = "/left", consumes = {MediaType.APPLICATION_JSON_VALUE})
    private ResponseEntity<String> left(@PathVariable Long id, @RequestBody String data) {
        logger.debug("Entered /left endpoint with id = '{}' and data = '{}'", id, data);

        try {
            validateJson(data).isJsonObject();
            logger.debug("Json is validated.");
        } catch (MalformedJsonException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(JSON_FORMAT_ERROR_MESSAGE);
        }
        if (service.saveData(id, data, DataType.LEFT) != ResponseType.ERRORED) {
            logger.info("Left data with id '{}' is saved/updated successfuly", id);
            return ResponseEntity.status(HttpStatus.CREATED).body(DATA_SAVED_SUCCESSFULLY_MESSAGE);
        } else {
            logger.error("left data with id '{}' cannot be saved/updated", id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(INTERNAL_SERVER_ERROR_MESSAGE);
        }
    }

    /**
     * Saves the right side json
     * @param id : unique identifier
     * @param data : json data
     * @return ResponseEntity : response
     */
    @PostMapping(value = "/right", consumes = {MediaType.APPLICATION_JSON_VALUE})
    private ResponseEntity<String> right(@PathVariable Long id, @RequestBody String data) {
        logger.debug("Entered /right endpoint with id = '{}' and data = '{}'", id, data);

        try {
            validateJson(data).isJsonObject();
        } catch (MalformedJsonException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(JSON_FORMAT_ERROR_MESSAGE);
        }
        if (service.saveData(id, data, DataType.RIGHT) != ResponseType.ERRORED) {
            logger.info("Right data with id '{}' is saved/updated successfuly", id);
            return ResponseEntity.status(HttpStatus.CREATED).body(DATA_SAVED_SUCCESSFULLY_MESSAGE);
        } else {
            logger.error("Right data with id '{}' cannot be saved/updated", id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(INTERNAL_SERVER_ERROR_MESSAGE);
        }
    }


    /**
     * To get the result of comparison for the json with given id
     * @param id : unique identifier
     * @return ResponseEntity : response
     */
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    private ResponseEntity<String> diff(@PathVariable Long id) {
        try {
            logger.debug("Entered /diff endpoint for id '{}'", id);
            String response = service.getDiff(id);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (DataNotFoundException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(NO_RECORDS_WITH_ID_MESSAGE);
        } catch (PartsMissingException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ONE_OR_TWO_PARTS_MISSING_MESSAGE);
        }
    }

    /**
     * To validate json and throw exception, if json is not valid
     * @param data
     * @return
     * @throws MalformedJsonException
     */
    private JsonObject validateJson(String data) throws MalformedJsonException {
        try {
            Gson gson = new Gson();
            return gson.fromJson(data, JsonObject.class);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new MalformedJsonException(ex.getMessage());
        }
    }


}