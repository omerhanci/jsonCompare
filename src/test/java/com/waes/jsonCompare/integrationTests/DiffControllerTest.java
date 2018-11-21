package com.waes.jsonCompare.integrationTests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.waes.jsonCompare.constants.Constants.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DiffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldReturnErrorIfContentIsNotInJSONFormat() throws Exception {
        String fakeId = "100001";
        String fakeBrokenJson = "{\n" +
                "\t\"tes: 20,\n" +
                "\t\"testAttr2\": \"\"\n" +
                "}";
        this.mockMvc.perform(post("/v1/diff/" + fakeId + "/left").accept(MediaType.APPLICATION_JSON_VALUE)
                .content(fakeBrokenJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(JSON_FORMAT_ERROR_MESSAGE)));

        this.mockMvc.perform(post("/v1/diff/" + fakeId + "/right").accept(MediaType.APPLICATION_JSON_VALUE)
                .content(fakeBrokenJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(JSON_FORMAT_ERROR_MESSAGE)));
    }

    @Test
    public void shouldReturnOkAndSaveIfDataIsProperFormat() throws Exception {
        String fakeId = "100001";
        String fakeJson = "{\n" +
                "\t\"testAttr1\": 20,\n" +
                "\t\"testAttr2\": \"\"\n" +
                "}";
        this.mockMvc.perform(post("/v1/diff/" + fakeId + "/left").accept(MediaType.APPLICATION_JSON_VALUE)
                .content(fakeJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString(DATA_SAVED_SUCCESSFULLY_MESSAGE)));

        this.mockMvc.perform(post("/v1/diff/" + fakeId + "/right").accept(MediaType.APPLICATION_JSON_VALUE)
                .content(fakeJson).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString(DATA_SAVED_SUCCESSFULLY_MESSAGE)));
    }

    @Test
    public void shouldReturnNoContentIfNoRecordWithGivenId() throws Exception {
        String fakeId = "100002";
        this.mockMvc.perform(get("/v1/diff/" + fakeId).accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(NO_RECORDS_WITH_ID_MESSAGE)));
    }

    @Test
    public void shouldReturnOneOrTwoPartsMissingWhenEitherLeftOrRightIsNotSubmitted() throws Exception {
        String fakeId1 = "100002";
        String fakeId2 = "100003";
        String fakeJson = "{\n" +
                            "\t\"testAttr1\": 20,\n" +
                            "\t\"testAttr2\": \"\"\n" +
                            "}";

        // first submit left part
        this.mockMvc.perform(post("/v1/diff/" + fakeId1 + "/left").accept(MediaType.APPLICATION_JSON_VALUE)
                .content(fakeJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString(DATA_SAVED_SUCCESSFULLY_MESSAGE)));

        // try to get difference
        this.mockMvc.perform(get("/v1/diff/" + fakeId1).accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // lets try it also for right
        this.mockMvc.perform(post("/v1/diff/" + fakeId2 + "/right").accept(MediaType.APPLICATION_JSON_VALUE)
                .content(fakeJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString(DATA_SAVED_SUCCESSFULLY_MESSAGE)));

        // try to get difference with second id
        this.mockMvc.perform(get("/v1/diff/" + fakeId2).accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

}
