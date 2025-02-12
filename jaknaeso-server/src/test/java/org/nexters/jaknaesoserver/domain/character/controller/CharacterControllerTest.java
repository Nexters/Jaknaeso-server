package org.nexters.jaknaesoserver.domain.character.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.SimpleType;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.domain.character.service.dto.CharactersResponse;
import org.nexters.jaknaesocore.domain.character.service.dto.CharactersResponse.CharacterResponse;
import org.nexters.jaknaesoserver.common.support.ControllerTest;
import org.nexters.jaknaesoserver.common.support.WithMockCustomUser;

class CharacterControllerTest extends ControllerTest {

  @WithMockCustomUser
  @Test
  void 캐릭터_목록을_조회한다() throws Exception {

    given(characterService.getCharacters(1L))
        .willReturn(new CharactersResponse(List.of(new CharacterResponse("첫번째", 1L))));

    mockMvc
        .perform(
            get("/api/v1/characters")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .queryParam("memberId", "1")
                .with(csrf()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "character-list-success",
                resource(
                    ResourceSnippetParameters.builder()
                        .description("캐릭터 목록 반환")
                        .tag("Character Domain")
                        .queryParameters(
                            parameterWithName("memberId")
                                .type(SimpleType.NUMBER)
                                .description("멤버 아이디"))
                        .responseFields(
                            fieldWithPath("result")
                                .type(SimpleType.STRING)
                                .description("API 요청 결과 (성공/실패)"),
                            fieldWithPath("data.characters[].characterNo")
                                .type(SimpleType.NUMBER)
                                .description("캐릭터 회차"),
                            fieldWithPath("data.characters[].bundleId")
                                .type(SimpleType.NUMBER)
                                .description("설문 번들 아이디"),
                            fieldWithPath("error").description("에러").optional())
                        .responseSchema(schema("CharactersResponse"))
                        .build())));
  }
}
