package org.nexters.jaknaesoserver.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.SimpleType;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesoserver.common.support.ControllerTest;
import org.springframework.restdocs.payload.JsonFieldType;

class SampleControllerTest extends ControllerTest {

    @Test
    void testGetSampleById() throws Exception {
        Long sampleId = 1L;

        given(sampleService.getBy(anyLong())).willReturn(sampleId);

        this.mockMvc
                .perform(get("/api/samples/{sampleId}", sampleId).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document(
                        "sample-get-by-id",
                        resource(ResourceSnippetParameters.builder()
                                .description("ID를 통해 샘플 조회")
                                .tags("Sample Domain")
                                .pathParameters(parameterWithName("sampleId")
                                        .type(SimpleType.NUMBER)
                                        .description("샘플 ID"))
                                .responseFields(fieldWithPath("sampleId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("샘플 ID"))
                                .build())));
    }
}
