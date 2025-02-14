package org.nexters.jaknaesoserver.domain.character.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.SimpleType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.model.ScaledBigDecimal;
import org.nexters.jaknaesocore.domain.character.model.CharacterType;
import org.nexters.jaknaesocore.domain.character.model.ValueReport;
import org.nexters.jaknaesocore.domain.character.service.dto.CharacterCommand;
import org.nexters.jaknaesocore.domain.character.service.dto.CharacterResponse;
import org.nexters.jaknaesocore.domain.character.service.dto.CharacterValueReportCommand;
import org.nexters.jaknaesocore.domain.character.service.dto.CharacterValueReportResponse;
import org.nexters.jaknaesocore.domain.character.service.dto.CharactersResponse;
import org.nexters.jaknaesocore.domain.character.service.dto.CharactersResponse.SimpleCharacterResponse;
import org.nexters.jaknaesocore.domain.survey.model.Keyword;
import org.nexters.jaknaesoserver.common.support.ControllerTest;
import org.nexters.jaknaesoserver.common.support.WithMockCustomUser;

class CharacterControllerTest extends ControllerTest {

  @WithMockCustomUser
  @Test
  void 캐릭터_목록을_조회한다() throws Exception {

    given(characterService.getCharacters(1L))
        .willReturn(
            new CharactersResponse(
                List.of(
                    SimpleCharacterResponse.builder()
                        .characterNo("첫번째")
                        .characterId(1L)
                        .bundleId(1L)
                        .build())));

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
                            fieldWithPath("data.characters[].characterId")
                                .type(SimpleType.NUMBER)
                                .description("캐릭터 아이디"),
                            fieldWithPath("data.characters[].bundleId")
                                .type(SimpleType.NUMBER)
                                .description("설문 번들 아이디"),
                            fieldWithPath("error").description("에러").optional())
                        .responseSchema(schema("CharactersResponse"))
                        .build())));
  }

  @WithMockCustomUser
  @Test
  void 특정_캐릭터_정보를_조회한다() throws Exception {

    given(characterService.getCharacter(new CharacterCommand(1L, 1L)))
        .willReturn(
            CharacterResponse.builder()
                .characterNo("첫번째")
                .type(CharacterType.SUCCESS.getName())
                .description(CharacterType.SUCCESS.getDescription())
                .startDate(LocalDate.now().minusDays(15))
                .endDate(LocalDate.now())
                .build());

    mockMvc
        .perform(
            get("/api/v1/characters/{characterId}", 1)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .queryParam("memberId", "1")
                .with(csrf()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "character-success",
                resource(
                    ResourceSnippetParameters.builder()
                        .description("캐릭터 목록 반환")
                        .tag("Character Domain")
                        .pathParameters(
                            parameterWithName("characterId")
                                .type(SimpleType.NUMBER)
                                .description("캐릭터 아이디"))
                        .queryParameters(
                            parameterWithName("memberId")
                                .type(SimpleType.NUMBER)
                                .description("멤버 아이디"))
                        .responseFields(
                            fieldWithPath("result")
                                .type(SimpleType.STRING)
                                .description("API 요청 결과 (성공/실패)"),
                            fieldWithPath("data.characterNo")
                                .type(SimpleType.NUMBER)
                                .description("캐릭터 회차"),
                            fieldWithPath("data.characterType")
                                .type(SimpleType.NUMBER)
                                .description("캐릭터 타입"),
                            fieldWithPath("data.description")
                                .type(SimpleType.STRING)
                                .description("캐릭터 설명"),
                            fieldWithPath("data.startDate")
                                .type(SimpleType.STRING)
                                .description("시작 일자"),
                            fieldWithPath("data.endDate")
                                .type(SimpleType.STRING)
                                .description("종료 일자"),
                            fieldWithPath("error").description("에러").optional())
                        .responseSchema(schema("CharacterResponse"))
                        .build())));
  }

  @WithMockCustomUser
  @Test
  void 특정_캐릭터의_가치관_분석_정보를_조회한다() throws Exception {

    given(characterService.getCharacterReport(any(CharacterValueReportCommand.class)))
        .willReturn(
            CharacterValueReportResponse.of(
                List.of(
                    ValueReport.of(
                        Keyword.SUCCESS, ScaledBigDecimal.of(BigDecimal.valueOf(33.33))))));
    mockMvc
        .perform(
            get("/api/v1/characters/{characterId}/report", 1)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .queryParam("memberId", "1")
                .with(csrf()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "character-value-report-success",
                resource(
                    ResourceSnippetParameters.builder()
                        .description("캐릭터 분석 정보 반환")
                        .tag("Character Domain")
                        .pathParameters(
                            parameterWithName("characterId")
                                .type(SimpleType.NUMBER)
                                .description("캐릭터 아이디"))
                        .queryParameters(
                            parameterWithName("memberId")
                                .type(SimpleType.NUMBER)
                                .description("멤버 아이디"))
                        .responseFields(
                            fieldWithPath("result")
                                .type(SimpleType.STRING)
                                .description("API 요청 결과 (성공/실패)"),
                            fieldWithPath("data.valueReports[].keyword").description("가치관"),
                            fieldWithPath("data.valueReports[].percentage")
                                .description("해당 가치관 비율"),
                            fieldWithPath("error").description("에러").optional())
                        .responseSchema(schema("CharacterValueReportResponse"))
                        .build())));
  }
}
