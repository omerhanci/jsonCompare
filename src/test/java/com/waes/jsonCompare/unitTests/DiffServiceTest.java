package com.waes.jsonCompare.unitTests;

import com.waes.jsonCompare.entity.Data;
import com.waes.jsonCompare.enums.DataType;
import com.waes.jsonCompare.enums.ResponseType;
import com.waes.jsonCompare.exceptions.DataNotFoundException;
import com.waes.jsonCompare.exceptions.PartsMissingException;
import com.waes.jsonCompare.repository.DiffRepository;
import com.waes.jsonCompare.service.DiffService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static com.waes.jsonCompare.constants.Constants.JSONS_ARE_NOT_IN_SAME_SIZE_MESSAGE;
import static com.waes.jsonCompare.constants.Constants.JSONS_ARE_SAME_MESSAGE;
import static com.waes.jsonCompare.constants.Constants.TWO_JSONS_ARE_NOT_SAME_MESSAGE;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DiffServiceTest {


    @InjectMocks
    private DiffService service;

    @Mock
    private DiffRepository repository;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldCreateIfDataDoesNotExists() {
        String fakeJson = "test";
        Mockito.doReturn(Optional.empty()).when(repository).findById(Mockito.eq(1L));
        ResponseType responseType = service.saveData(1L, fakeJson, DataType.LEFT);
        Assert.assertEquals(responseType, ResponseType.CREATED);
    }

    @Test
    public void shouldUpdateIfDataExists() {
        String fakeJson = "test";
        Data data = new Data(1L, fakeJson, null);
        Mockito.doReturn(Optional.of(data)).when(repository).findById(Mockito.eq(1L));
        ResponseType responseType = service.saveData(1L, fakeJson, DataType.RIGHT);
        Assert.assertEquals(responseType, ResponseType.UPDATED);
    }

    @Test(expected = Exception.class)
    public void shouldErroredIfRepositoryFails() {
        String fakeJson = "test";
        Data data = new Data(1L, fakeJson, null);
        Mockito.doReturn(Optional.of(data)).when(repository).findById(Mockito.eq(1L));
        Mockito.doThrow(new Exception()).when(repository).save(Mockito.any(Data.class));
        ResponseType responseType = service.saveData(1L, fakeJson, DataType.RIGHT);
        Assert.assertEquals(responseType, ResponseType.ERRORED);
    }

    @Test
    public void shouldThrowDataNotFoundExceptionIfDataIsNotFound() throws PartsMissingException {
        Mockito.doReturn(Optional.empty()).when(repository).findById(Mockito.eq(1L));
        try{
            service.getDiff(1L);
        } catch (DataNotFoundException ex){
            Assert.assertEquals(ex.getMessage(), "Data with id : " + 1L + " is not found");
        }
    }

    @Test
    public void shouldThrowPartMissingIfRightPartIsMissing() throws DataNotFoundException{
        String fakeJson = "test";
        Data data = new Data(1L, fakeJson, null);
        Mockito.doReturn(Optional.of(data)).when(repository).findById(Mockito.eq(1L));
        try{
            service.getDiff(1L);
        } catch (PartsMissingException ex){
            Assert.assertEquals(ex.getMessage(), "One of the parts is missing");
        }
    }

    @Test
    public void shouldThrowPartMissingIfLeftPartIsMissing() throws DataNotFoundException{
        String fakeJson = "test";
        Data data = new Data(1L, null, fakeJson);
        Mockito.doReturn(Optional.of(data)).when(repository).findById(Mockito.eq(1L));
        try{
            service.getDiff(1L);
        } catch (PartsMissingException ex){
            Assert.assertEquals(ex.getMessage(), "One of the parts is missing");
        }
    }

    @Test
    public void shouldReturnSameJsonIfJsonsAreTheSame() throws DataNotFoundException, PartsMissingException{
        String fakeJson = "test";
        Data data = new Data(1L, fakeJson, fakeJson);
        Mockito.doReturn(Optional.of(data)).when(repository).findById(Mockito.eq(1L));
        String result = service.getDiff(1L);
        Assert.assertEquals(result, JSONS_ARE_SAME_MESSAGE);
    }

    @Test
    public void shouldReturnDifferentLengthIfLengthAreDifferent() throws DataNotFoundException, PartsMissingException{
        String fakeJson = "test";
        String fakeJson2 = "test2";
        Data data = new Data(1L, fakeJson, fakeJson2);
        Mockito.doReturn(Optional.of(data)).when(repository).findById(Mockito.eq(1L));
        String result = service.getDiff(1L);
        Assert.assertEquals(result, JSONS_ARE_NOT_IN_SAME_SIZE_MESSAGE);
    }

    @Test
    public void shouldReturnDifferenceOffsetIfLengthSameJsonDifferent() throws DataNotFoundException, PartsMissingException{
        String fakeJson = "test1test";
        String fakeJson2 = "test2test";
        Data data = new Data(1L, fakeJson, fakeJson2);
        Mockito.doReturn(Optional.of(data)).when(repository).findById(Mockito.eq(1L));
        String result = service.getDiff(1L);
        Assert.assertEquals(result, TWO_JSONS_ARE_NOT_SAME_MESSAGE + "4");
    }

    @Test
    public void shouldReturnMultipleDifferenceOffsetIfLengthSameJsonDifferent() throws DataNotFoundException, PartsMissingException{
        String fakeJson = "test1test4test";
        String fakeJson2 = "test2test3test";
        Data data = new Data(1L, fakeJson, fakeJson2);
        Mockito.doReturn(Optional.of(data)).when(repository).findById(Mockito.eq(1L));
        String result = service.getDiff(1L);
        Assert.assertEquals(result, TWO_JSONS_ARE_NOT_SAME_MESSAGE + "4, 9");
    }
}
