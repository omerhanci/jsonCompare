package com.waes.jsonCompare.service;

import com.waes.jsonCompare.entity.Data;
import com.waes.jsonCompare.enums.DataType;
import com.waes.jsonCompare.enums.ResponseType;
import com.waes.jsonCompare.exceptions.DataNotFoundException;
import com.waes.jsonCompare.exceptions.PartsMissingException;
import com.waes.jsonCompare.repository.DiffRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DiffService {

    @Autowired
    public DiffRepository repository;

    private static final Logger logger = LoggerFactory.getLogger(DiffService.class);

    public ResponseType saveData(Long id, String jsonData, DataType dataType) {
        Optional<Data> queryResponse = repository.findById(id);
        Data data = null;
        ResponseType response;

        if (!queryResponse.equals(Optional.empty())) {
            data = queryResponse.get();
            response = ResponseType.UPDATED;
        } else {
            data = new Data();
            data.setId(id);
            response = ResponseType.CREATED;
        }

        if (dataType == DataType.LEFT) {
            data.setLeftPart(jsonData);
        } else {
            data.setRightPart(jsonData);
        }
        try {
            repository.save(data);
            return response;
        } catch (Exception ex) {
            return ResponseType.ERRORED;
        }
    }

    public String getDiff(Long id) throws DataNotFoundException, PartsMissingException {
        Optional<Data> queryResponse = repository.findById(id);
        Data data = null;

        if (!queryResponse.equals(Optional.empty())) {
            data = queryResponse.get();
        } else {
            logger.error("Data with id : '{}' is not found", id);
            throw new DataNotFoundException("Data with id : " + id + " is not found");
        }

        String left = data.getLeftPart();
        String right = data.getRightPart();
        if (left == null || right == null) {
            logger.error("One of the parts is missing");
            throw new PartsMissingException("One of the parts is missing");
        }


        if (left.equals(right)) {
            return "Same json!";
        } else if (left.length() != right.length()) {
            return "Not same size!";
        } else {
            String diffsMessage = "Two json is not same, the differences are at the following positions: ";
            for (int i = 0; i < left.length(); i++) {
                if (left.charAt(i) != right.charAt(i)) {
                    diffsMessage += i + ", ";
                }
            }
            return diffsMessage.substring(0, diffsMessage.length() - 2);
        }

    }
}
