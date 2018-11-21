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

import static com.waes.jsonCompare.constants.Constants.*;

/**
 * A service class for the application, logic of the application is here
 * @author Omer Hanci
 */
@Service
public class DiffService {

    @Autowired
    public DiffRepository repository;

    private static final Logger logger = LoggerFactory.getLogger(DiffService.class);

    /**
     * saves left/right data to db to be compared later
     * @param id
     * @param jsonData
     * @param dataType
     * @return
     */
    public ResponseType saveData(Long id, String jsonData, DataType dataType) {
        logger.debug("Entered saveData() in DiffService with '{}", id);
        Optional<Data> queryResponse = repository.findById(id);
        Data data = null;
        ResponseType response;

        if (!queryResponse.equals(Optional.empty())) {
            logger.info("Data found in the db with id: '{}'", id);
            data = queryResponse.get();
            response = ResponseType.UPDATED;
        } else {
            logger.info("Data not found in the db with id: '{}'", id);
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
            logger.info("Data : '{}' is saved succesfully", data.toString());
            return response;
        } catch (Exception ex) {
            logger.error("Data : '{}' cannot be saved", data.toString());
            return ResponseType.ERRORED;
        }
    }

    /**
     * get the diffs between two jsons
     * @param id
     * @return
     * @throws DataNotFoundException
     * @throws PartsMissingException
     */
    public String getDiff(Long id) throws DataNotFoundException, PartsMissingException {
        logger.debug("Entered getDiff() in DiffService with id : '{}'", id);
        Optional<Data> queryResponse = repository.findById(id);
        Data data = null;

        if (!queryResponse.equals(Optional.empty())) {
            logger.info("Data found in the db with id: '{}'", id);
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
            logger.info(JSONS_ARE_SAME_MESSAGE);
            return JSONS_ARE_SAME_MESSAGE;
        } else if (left.length() != right.length()) {
            logger.info(JSONS_ARE_NOT_IN_SAME_SIZE_MESSAGE);
            return JSONS_ARE_NOT_IN_SAME_SIZE_MESSAGE;
        } else {
            String diffsMessage = TWO_JSONS_ARE_NOT_SAME_MESSAGE;
            for (int i = 0; i < left.length(); i++) {
                if (left.charAt(i) != right.charAt(i)) {
                    diffsMessage += i + ", ";
                }
            }
            logger.info(diffsMessage.substring(0, diffsMessage.length() - 2));
            return diffsMessage.substring(0, diffsMessage.length() - 2);
        }

    }
}
